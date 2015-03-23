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
import scp.api.PublisherConfiguration;
import scp.impl.spi.PublishClientRequestHandler;
import scp.util.NonNegative;
import scp.util.NonNull;
import scp.util.TimestampedBox;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.concurrent.*;

/**
 * Publish requester.
 * @param <T>  the type of published data.
 */
public final class PublishRequester<T extends Serializable> implements scp.api.Publisher<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublishRequester.class);
    private final PublisherConfiguration<T> configuration;
    private final ExecutorService executorService;
    private final PublishClientRequestHandler<T> publishClientRequestHandler;
    private long currPublicationTime;
    private int messageCount;

    /**
     * Instantiates a new PublishRequester.
     *
     * @param configuration the configuration
     * @param clientRequestHandler the client request handler used to dispatch the publish request.
     */
    public PublishRequester(@NonNull PublisherConfiguration<T> configuration,
                            @NonNull PublishClientRequestHandler<T> clientRequestHandler) {
        assert configuration != null;
        assert clientRequestHandler != null;

        this.messageCount = 0;
        this.configuration = configuration;
        this.currPublicationTime = 0;
        this.publishClientRequestHandler = clientRequestHandler;
        executorService = Executors.newCachedThreadPool();
    }

    @Override
    public PublicationStatus publish(@NonNull final T data) {
        assert data != null;

        this.messageCount++;
        final long _lastPubTime = this.currPublicationTime;
        this.currPublicationTime = System.currentTimeMillis();
        if (_lastPubTime == 0 ||
                this.currPublicationTime - _lastPubTime >= this.configuration.minimumSeparation) {
            return handlePublication(data, this.configuration.minimumRemainingLifetime);
        } else {
            LOGGER.warn("Dropping {}-th message", this.messageCount);
            return PublicationStatus.FAST_PUBLICATION_DROPPED;
        }
    }

    private PublicationStatus handlePublication(@NonNull final T data, @NonNegative final long remainingLifetime) {
        assert data != null;
        assert remainingLifetime > -1;

        final Future _future = executorService.submit(() -> {
            PublishRequester.this.publishClientRequestHandler.publish(new TimestampedBox<>(data, remainingLifetime));
            return null;
        });
        try {
            _future.get(this.configuration.maximumLatency, TimeUnit.MILLISECONDS);
            return PublicationStatus.PUBLISHED;
        } catch (TimeoutException _e) {
            LOGGER.warn("Timed out handling {}-th message", this.messageCount);
            return PublicationStatus.TIME_OUT;
        } catch (ExecutionException | InterruptedException _e) {
            LOGGER.error(MessageFormat.format("Failed to handle {0}-th message", this.messageCount), _e);
            return PublicationStatus.UNKNOWN_FAILURE;
        }
    }
}
