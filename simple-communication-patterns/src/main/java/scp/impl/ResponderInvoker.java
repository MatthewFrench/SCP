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
import scp.api.Responder.ResponseStatus;
import scp.api.ResponderConfiguration;
import scp.util.NonNull;
import scp.util.Pair;
import scp.util.TimestampedBox;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.concurrent.*;

/**
 * Responder invoker.
 * @param <T>  the type of response.
 */
public final class ResponderInvoker<T extends Serializable> {

    /**
     * The enum Responder invoker status.
     */
    public enum ResponderInvokerStatus {
        /**
         * Data was unavailable.
         */
        DATA_UNAVAILABLE,
        /**
         * Request was rejected as there were too many sends.
         */
        EXCESS_LOAD,
        /**
         * Response was provided.
         */
        RESPONSE_PROVIDED,
        /**
         * Responder failed to honor its maximum service latency.
         */
        TIMED_OUT,
        /**
         * Unknown failure occurred during reception.
         */
        UNKNOWN_FAILURE
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponderInvoker.class);
    private final ExecutorService executorService;
    private final ResponderConfiguration<T> configuration;
    private int requestCount;
    private long currRequestTime;

    /**
     * Instantiates a new ResponderInvoker.
     *
     * @param configuration the configuration
     */
    public ResponderInvoker(@NonNull ResponderConfiguration<T> configuration) {
        assert configuration != null;

        this.configuration = configuration;
        this.executorService = Executors.newCachedThreadPool();
        this.requestCount = 0;
    }

    /**
     * Service request.
     *
     * @return the status of servicing the request and the response.
     */
    public Pair<ResponderInvokerStatus, TimestampedBox<T>> serviceRequest() {
        this.requestCount++;
        final long _lastReqTime = this.currRequestTime;
        final long _tmp1 = System.currentTimeMillis();
        if (_lastReqTime == 0 || _tmp1 - _lastReqTime >= this.configuration.minimumSeparation) {
            // handling request
            this.currRequestTime = _tmp1;
            return invokeHandler();
        } else {
            // drop fast publication
            LOGGER.warn("Dropping {}-th reception", this.requestCount);
            return new Pair<>(ResponderInvokerStatus.EXCESS_LOAD, null);
        }
    }

    private Pair<ResponderInvokerStatus,TimestampedBox<T>> invokeHandler() {
        final scp.api.Responder<T> _handler = this.configuration.responder;
        final Future<Pair<ResponseStatus, T>> _future = executorService.submit(_handler::respond);

        try {
            final Pair<ResponseStatus, T> _ret = _future.get(this.configuration.maximumLatency, TimeUnit.MILLISECONDS);
            final ResponderInvokerStatus _status;
            switch (_ret.first) {
                case DATA_UNAVAILABLE:
                    _status = ResponderInvokerStatus.DATA_UNAVAILABLE;
                    break;
                case RESPONSE_PROVIDED:
                    _status = ResponderInvokerStatus.RESPONSE_PROVIDED;
                    break;
                default:
                    final String _msg = MessageFormat.format("Unknown status {0}", _ret.first);
                    LOGGER.error(_msg);
                    throw new RuntimeException(_msg);
            }
            return new Pair<>(_status,
                    new TimestampedBox<>(_ret.second, this.configuration.minimumRemainingLifetime));
        } catch (TimeoutException _e) {
            LOGGER.warn("Timed out handling {}-th reception", this.requestCount);
            return new Pair<>(ResponderInvokerStatus.TIMED_OUT, null);
        } catch (ExecutionException | InterruptedException _e) {
            LOGGER.error(MessageFormat.format("Failed to handle {0}-th reception", this.requestCount), _e);
            return new Pair<>(ResponderInvokerStatus.UNKNOWN_FAILURE, null);
        }
    }
}
