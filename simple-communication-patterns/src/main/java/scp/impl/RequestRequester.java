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
import scp.api.RequesterConfiguration;
import scp.impl.ResponderInvoker.ResponderInvokerStatus;
import scp.impl.spi.RequestClientRequestHandler;
import scp.util.NonNull;
import scp.util.Pair;
import scp.util.TimestampedBox;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.concurrent.*;

/**
 * Request requester.
 * @param <T>  the type of request.
 */
public final class RequestRequester<T extends Serializable> implements scp.api.Requester<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestRequester.class);
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final RequesterConfiguration<T> configuration;
    private final RequestClientRequestHandler<T> requestClientRequestHandler;
    private long currRequestTime;
    private int requestCount;

    /**
     * Instantiates a new RequestRequester.
     *
     * @param configuration the configuration
     * @param clientRequestHandler the client request handler used to dispatch the request.
     */
    public RequestRequester(@NonNull RequesterConfiguration<T> configuration,
                            @NonNull RequestClientRequestHandler<T> clientRequestHandler) {
        assert configuration != null;
        assert clientRequestHandler != null;

        this.requestCount = 0;
        this.currRequestTime = 0;
        this.configuration = configuration;
        this.requestClientRequestHandler = clientRequestHandler;
    }

    @Override
    public Pair<RequestStatus, T> request() {
        this.requestCount++;
        final long _lastReqTime = this.currRequestTime;
        this.currRequestTime = System.currentTimeMillis();
        if (_lastReqTime == 0 ||
                this.currRequestTime - _lastReqTime >= this.configuration.minimumSeparation) {
            return handleRequest();
        } else {
            LOGGER.warn("Dropping {}-th request", this.requestCount);
            return new Pair<>(RequestStatus.FAST_REQUEST_DROPPED, null);
        }
    }

    private Pair<RequestStatus, T> handleRequest() {
        final Future<Pair<ResponderInvokerStatus, TimestampedBox<T>>> _future =
                executorService.submit(RequestRequester.this.requestClientRequestHandler::request);
        try {
            final Pair<ResponderInvokerStatus, TimestampedBox<T>> _ret =
                    _future.get(this.configuration.maximumLatency, TimeUnit.MILLISECONDS);
            switch (_ret.first) {
                case DATA_UNAVAILABLE:
                    return new Pair<>(RequestStatus.DATA_UNAVAILABLE, null);
                case EXCESS_LOAD:
                    return new Pair<>(RequestStatus.EXCESS_LOAD, null);
                case RESPONSE_PROVIDED:
                    final TimestampedBox<T> _tmp1 = _ret.second;
                    long _arrTime = System.currentTimeMillis();
                    final long _remLifeTime = _tmp1.remainingLifetime - (_arrTime - _tmp1.timestamp);
                    final RequestStatus _status;
                    if (_remLifeTime >= this.configuration.minimumRemainingLifetime) {
                       _status = RequestStatus.SUCCEEDED;
                    } else {
                        LOGGER.info("Handling stale {}-th response", this.requestCount);
                        _status = RequestStatus.STALE_DATA;
                    }
                    return new Pair<>(_status, _tmp1.data);
                case TIMED_OUT:
                    return new Pair<>(RequestStatus.REMOTE_TIME_OUT, null);
                case UNKNOWN_FAILURE:
                    return new Pair<>(RequestStatus.REMOTE_UNKNOWN_FAILURE, null);
                default:
                    final String _msg = MessageFormat.format("Unhandled {0} status for {1}-th request",
                            _ret.first, this.requestCount);
                    LOGGER.error(_msg);
                    throw new RuntimeException(_msg);
            }
        } catch (TimeoutException _e) {
            LOGGER.warn("Timed out handling {}-th request", this.requestCount);
            return new Pair<>(RequestStatus.LOCAL_TIME_OUT, null);
        } catch (ExecutionException | InterruptedException _e) {
            LOGGER.error(MessageFormat.format("Failed to handle {0}-th request", this.requestCount), _e);
            return new Pair<>(RequestStatus.LOCAL_UNKNOWN_FAILURE, null);
        }
    }
}
