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
import scp.api.SenderConfiguration;
import scp.impl.ReceiverInvoker.ReceiverInvokerStatus;
import scp.impl.spi.SendClientRequestHandler;
import scp.util.NonNull;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.concurrent.*;

/**
 * Send requester.
 * @param <T>  the type of sent data.
 */
public final class SendRequester<T extends Serializable> implements scp.api.Sender<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendRequester.class);
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final SenderConfiguration<T> configuration;
    private final SendClientRequestHandler<T> sendClientRequestHandler;
    private long currRequestTime;
    private int sendCount;

    /**
     * Instantiates a new SendRequester.
     *
     * @param configuration the configuration
     * @param clientRequestHandler the client request handler used to dispatch the request.
     */
    public SendRequester(@NonNull SenderConfiguration<T> configuration,
                         @NonNull SendClientRequestHandler<T> clientRequestHandler) {
        assert configuration != null;
        assert clientRequestHandler != null;

        this.sendCount = 0;
        this.currRequestTime = 0;
        this.configuration = configuration;
        this.sendClientRequestHandler = clientRequestHandler;
    }

    @Override
    public SendStatus send(T data) {
        this.sendCount++;
        final long _lastSendTime = this.currRequestTime;
        this.currRequestTime = System.currentTimeMillis();
        if (_lastSendTime == 0 ||
                this.currRequestTime - _lastSendTime >= this.configuration.minimumSeparation) {
            return handleSend(data);
        } else {
            LOGGER.warn("Dropping {}-th send", this.sendCount);
            return SendStatus.FAST_SEND_DROPPED;
        }
    }

    private SendStatus handleSend(T data) {
        final Future<ReceiverInvokerStatus> _future =
                executorService.submit(() -> SendRequester.this.sendClientRequestHandler.send(data));
        try {
            final ReceiverInvokerStatus _ret =
                    _future.get(this.configuration.maximumLatency, TimeUnit.MILLISECONDS);
            switch (_ret) {
                case DATA_REJECTED:
                    return SendStatus.DATA_REJECTED;
                case EXCESS_LOAD:
                    return SendStatus.EXCESS_LOAD;
                case DATA_ACCEPTED:
                    return SendStatus.DATA_ACCEPTED;
                case TIMED_OUT:
                    return SendStatus.REMOTE_TIME_OUT;
                case UNKNOWN_FAILURE:
                    return SendStatus.REMOTE_UNKNOWN_FAILURE;
                default:
                    final String _msg = MessageFormat.format("Unhandled {0} status for {1}-th send",
                            _ret, this.sendCount);
                    LOGGER.error(_msg);
                    throw new RuntimeException(_msg);
            }
        } catch (TimeoutException _e) {
            LOGGER.warn("Timed out handling {}-th send", this.sendCount);
            return SendStatus.LOCAL_TIME_OUT;
        } catch (ExecutionException | InterruptedException _e) {
            LOGGER.error(MessageFormat.format("Failed to handle {0}-th send", this.sendCount), _e);
            return SendStatus.LOCAL_UNKNOWN_FAILURE;
        }
    }
}
