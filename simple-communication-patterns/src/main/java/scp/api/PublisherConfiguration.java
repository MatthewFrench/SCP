/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.api;

import scp.util.*;

/**
 * Publisher configuration.
 * @param <S>  the type of published data.
 */
public final class PublisherConfiguration<S> {
    /**
     * Published topic.
     */
    @NonNull
    @NonEmpty
    public final String topic;
    /**
     * Minimum duration of time (in milliseconds) between two consecutive publications.  In other words, after a
     * publication, new publications will be inhibited for this duration of time.
     */
    @Offers
    @NonZero
    @NonNegative
    public final long minimumSeparation; // in millis
    /**
     * Maximum allowed latency for the publish request to be accepted by the lower-level communication substrate
     * (in milliseconds).
     */
    @Expects @NonZero @NonNegative public final long maximumLatency; // in millis
    /**
     * Minimum remaining lifetime of published data (in milliseconds).
     */
    @Offers @NonZero @NonNegative public final long minimumRemainingLifetime; // in millis
    /**
     * The type of published data.
     */
    @NonNull public final Class<S> dataType;

    /**
     * Instantiates a new Publisher configuration.
     *
     * @param topic the published topic.
     * @param minimumSeparation the minimum duration of time (in milliseconds) between two consecutive publications.
     * @param maximumLatency the maximum latency tolerated for the publish request to be accepted by the lower-level
     *                       communication substrate (in milliseconds).
     * @param minimumRemainingLifetime the minimum remaining lifetime of published data.
     * @param dataType the type of published data.
     */
    public PublisherConfiguration(
            @NonNull @NonEmpty final String topic,
            @Offers @NonZero @NonNegative final long minimumSeparation,
            @Expects @NonZero @NonNegative final long maximumLatency,
            @Offers @NonZero @NonNegative final long minimumRemainingLifetime,
            @NonNull Class<S> dataType) {
        assert topic != null;
        assert !topic.isEmpty();
        assert minimumSeparation > 0;
        assert maximumLatency > 0;
        assert maximumLatency < minimumSeparation;
        assert minimumRemainingLifetime > 0;
        assert minimumRemainingLifetime > maximumLatency;
        assert dataType != null;

        this.topic = topic;
        this.minimumSeparation = minimumSeparation;
        this.maximumLatency = maximumLatency;
        this.minimumRemainingLifetime = minimumRemainingLifetime;
        this.dataType = dataType;
    }
}
