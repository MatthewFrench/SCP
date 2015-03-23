/*
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
import scp.api.Initiator;
import scp.api.InitiatorConfiguration;
import scp.impl.ExecutorInvoker.ExecutorInvokerStatus;
import scp.impl.spi.InitiateClientRequestHandler;
import scp.util.NonNull;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.concurrent.*;

/**
 * Initiate requester.
 * @param <T>  the type of action.
 */
public final class InitiateRequester<T extends Serializable> implements scp.api.Initiator<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitiateRequester.class);
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final InitiatorConfiguration<T> configuration;
    private final InitiateClientRequestHandler<T> initiateClientRequestHandler;
    private long currRequestTime;
    private int sendCount;

    /**
     * Instantiates a new InitiateRequester.
     *
     * @param configuration the configuration
     * @param clientRequestHandler the handler used to dispatch the request for execution to the executor.
     */
    public InitiateRequester(@NonNull InitiatorConfiguration<T> configuration,
                             @NonNull InitiateClientRequestHandler<T> clientRequestHandler) {
        assert configuration != null;
        assert clientRequestHandler != null;

        this.sendCount = 0;
        this.currRequestTime = 0;
        this.configuration = configuration;
        this.initiateClientRequestHandler = clientRequestHandler;
    }

    @Override
    public InitiationStatus initiate(T cmd) {
        this.sendCount++;
        final long _lastSendTime = this.currRequestTime;
        this.currRequestTime = System.currentTimeMillis();
        if (_lastSendTime == 0 ||
                this.currRequestTime - _lastSendTime >= this.configuration.minimumSeparation) {
            return handleInitiate(cmd);
        } else {
            LOGGER.warn("Dropping {}-th initiation", this.sendCount);
            return Initiator.InitiationStatus.FAST_INITIATION_DROPPED;
        }
    }

    private Initiator.InitiationStatus handleInitiate(T cmd) {
        final Future<ExecutorInvokerStatus> _future =
                executorService.submit(() -> InitiateRequester.this.initiateClientRequestHandler.initiate(cmd));
        try {
            final ExecutorInvokerStatus _ret =
                    _future.get(this.configuration.maximumLatency, TimeUnit.MILLISECONDS);
            switch (_ret) {
                case ACTION_FAILED:
                    return InitiationStatus.ACTION_FAILED;
                case ACTION_SUCCEEDED:
                    return InitiationStatus.ACTION_SUCCEEDED;
                case ACTION_UNAVAILABLE:
                    return InitiationStatus.ACTION_UNAVAILABLE;
                case EXCESS_LOAD:
                    return InitiationStatus.EXCESS_LOAD;
                case TIMED_OUT:
                    return InitiationStatus.REMOTE_TIME_OUT;
                case UNKNOWN_FAILURE:
                    return InitiationStatus.REMOTE_UNKNOWN_FAILURE;
                default:
                    final String _msg = MessageFormat.format("Unhandled {0} status for {1}-th initiation",
                            _ret, this.sendCount);
                    LOGGER.error(_msg);
                    throw new RuntimeException(_msg);
            }
        } catch (TimeoutException _e) {
            LOGGER.warn("Timed out handling {}-th initiation", this.sendCount);
            return InitiationStatus.LOCAL_TIME_OUT;
        } catch (ExecutionException | InterruptedException _e) {
            LOGGER.error(MessageFormat.format("Failed to handle {0}-th initiatrion", this.sendCount), _e);
            return InitiationStatus.LOCAL_UNKNOWN_FAILURE;
        }
    }
}
