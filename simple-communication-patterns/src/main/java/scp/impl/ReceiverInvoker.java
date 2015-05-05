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
import scp.api.Receiver.ReceptionAcknowledgement;
import scp.api.ReceiverConfiguration;
import scp.util.NonNull;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.concurrent.*;

/**
 * Receiver invoker.
 * @param <T>  the type of received data.
 */
public final class ReceiverInvoker<T extends Serializable> {

    /**
     * Enumeration of the status of reception.
     */
    public enum ReceiverInvokerStatus {
        /**
         * Data was accepted.
         */
        DATA_ACCEPTED,
        /**
         * Data was rejected.
         */
        DATA_REJECTED,
        /**
         * Reception was rejected as there were too many sends.
         */
        EXCESS_LOAD,
        /**
         * Receiver failed to honor its maximum service latency.
         */
        TIMED_OUT,
        /**
         * Unknown failure occurred during reception.
         */
        UNKNOWN_FAILURE
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiverInvoker.class);
    private final ExecutorService executorService;
    private final ReceiverConfiguration<T> configuration;
    private int receptionCount;
    private long currReceptionTime;

    /**
     * Instantiates a new ReceiverInvoker.
     *
     * @param configuration the configuration
     */
    public ReceiverInvoker(@NonNull ReceiverConfiguration<T> configuration) {
        assert configuration != null;

        this.configuration = configuration;
        this.executorService = Executors.newCachedThreadPool();
        this.receptionCount = 0;
    }

    /**
     * Receive data.
     *
     * @param data the data.
     * @return the status of reception.
     */
    public ReceiverInvokerStatus receive(T data) {
        this.receptionCount++;
        final long _lastRecTime = this.currReceptionTime;
        final long _tmp1 = System.currentTimeMillis();
        if (_lastRecTime == 0 || _tmp1 - _lastRecTime >= this.configuration.minimumSeparation) {
            // handling request
            this.currReceptionTime = _tmp1;
            return invokeHandler(data);
        } else {
            // drop fast publication
            LOGGER.warn("Dropping {}-th reception", this.receptionCount);
            return ReceiverInvokerStatus.EXCESS_LOAD;
        }
    }

    private ReceiverInvokerStatus invokeHandler(T data) {
        final scp.api.Receiver<T> _handler = this.configuration.handler;
        final Future<ReceptionAcknowledgement> _future = executorService.submit(() ->  _handler.receive(data));

        try {
            final ReceptionAcknowledgement _ret = _future.get(this.configuration.maximumLatency, TimeUnit.MILLISECONDS);
            final ReceiverInvokerStatus _status;
            switch (_ret) {
                case DATA_ACCEPTED:
                    _status = ReceiverInvokerStatus.DATA_ACCEPTED;
                    break;
                case DATA_REJECTED:
                    _status = ReceiverInvokerStatus.DATA_REJECTED;
                    break;
                default:
                    final String _msg = MessageFormat.format("Unknown status {0}", _ret);
                    LOGGER.error(_msg);
                    throw new RuntimeException(_msg);
            }
            return _status;
        } catch (TimeoutException _e) {
            LOGGER.warn("Timed out handling {}-th reception", this.receptionCount);
            return ReceiverInvokerStatus.TIMED_OUT;
        } catch (ExecutionException | InterruptedException _e) {
            LOGGER.error(MessageFormat.format("Failed to handle {0}-th reception", this.receptionCount), _e);
            return ReceiverInvokerStatus.UNKNOWN_FAILURE;
        }
    }
}
