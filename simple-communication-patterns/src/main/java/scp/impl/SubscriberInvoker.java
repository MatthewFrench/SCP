/**
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scp.api.SubscriberConfiguration;
import scp.util.NonNull;
import scp.util.TimestampedBox;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * Subscriber invoker.
 * @param <T>  the type of subscribed data.
 */
public final class SubscriberInvoker<T extends Serializable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriberInvoker.class);
    private final SubscriberConfiguration<T> configuration;
    private final ExecutorService executorService;
    private final Timer timer;
    private int messageCount;
    private long currConsumptionTime;
    private int numUnconsumedMessages;
    private TimerTask timerTask;

    /**
     * Instantiates a new SubscriberInvoker.
     *
     * @param configuration the configuration
     */
    public SubscriberInvoker(@NonNull SubscriberConfiguration<T> configuration) {
        assert configuration != null;

        this.configuration = configuration;
        this.executorService = Executors.newCachedThreadPool();
        this.timer = new Timer();
        this.messageCount = 0;
        this.currConsumptionTime = 0;
    }

    /**
     * Process data.
     *
     * @param data the data
     */
    public final void processData(@NonNull final TimestampedBox<T> data) {
        assert data != null;

        this.messageCount++;
        final long _lastConsTime = this.currConsumptionTime;
        final long _tmp5 = System.currentTimeMillis();
        if (_lastConsTime == 0 || _tmp5 - _lastConsTime >= this.configuration.minimumSeparation) {
            // handling publication
            this.currConsumptionTime = _tmp5;
            resetMaximumSeparationTracker();
            invokeHandler(data);
        } else {
            // drop fast publication
            LOGGER.warn("Dropping {}-th message", this.messageCount);
        }
    }

    private void invokeHandler(@NonNull final TimestampedBox<T> msg) {
        assert msg != null;

        long _arrTime = System.currentTimeMillis();
        final long _remLifeTime = msg.remainingLifetime - (_arrTime - msg.timestamp);
        if (_remLifeTime >= this.configuration.minimumRemainingLifetime) {
            final scp.api.Subscriber<T> _handler = this.configuration.subscriber;
            // consume the data
            final Callable<Void> _callable = () -> {
                _handler.consume(msg.data, _remLifeTime);
                return null;
            };
            Future _future = executorService.submit(_callable);

            try {
                _future.get(this.configuration.maximumLatency, TimeUnit.MILLISECONDS);
                this.numUnconsumedMessages = 0;
            } catch (TimeoutException _e) {
                // handle slow consumption
                LOGGER.warn("Timed out handling {}-th message", this.messageCount);
                this.numUnconsumedMessages++;
                if (this.numUnconsumedMessages > configuration.consumptionTolerance) {
                    final Callable <Void> _tmp1 = () -> {
                        _handler.handleSlowConsumption(SubscriberInvoker.this.numUnconsumedMessages);
                        return null;
                    };
                    executorService.submit(_tmp1);
                }
            } catch (ExecutionException | InterruptedException _e) {
                LOGGER.error(MessageFormat.format("Failed to handle {0}-th message", this.messageCount), _e);
            }
        } else {
            LOGGER.info("Handling stale {}-th message", this.messageCount);
            // handle stale data
            final scp.api.Subscriber<T> _handler = this.configuration.subscriber;
            final Callable<Void> _callable = () -> {
                _handler.handleStaleMessage(msg.data, _remLifeTime);
                return null;
            };
            executorService.submit(_callable);
        }
    }

    private void resetMaximumSeparationTracker() {
        if (this.timerTask != null) {
            this.timerTask.cancel();
        }
        this.timerTask = new TimerTask() {
            @Override
            public void run() {
                SubscriberInvoker.this.executorService.submit(() -> {
                    SubscriberInvoker.this.configuration.subscriber.handleSlowPublication();
                    return null;
                });
            }
        };
        this.timer.schedule(this.timerTask, this.configuration.maximumSeparation);
    }
}
