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

import java.io.Serializable;

/**
 * The Subscriber configuration.
 * @param <T>  the type of subscribed data.
 */
public class SubscriberConfiguration<T extends Serializable> {
    /**
     * The Topic.
     */
    @NonNull
    @NonEmpty
    public final String topic;
    /**
     * Minimum duration of time (in milliseconds) between two consecutive consumptions.  In other words, after a
     * consumption of data, new data will be inhibited for this duration of time.
     */
    @Supports
    @NonZero
    @NonNegative
    public final long minimumSeparation; // in millis
    /**
     * Maximum duration of time (in milliseconds) tolerated between two consecutive consumptions.  In other words,
     * after a consumption of data, the subscriber can wait for this duration of time for new data to arrive.  If no
     * data arrives, then the subscriber is notified of slow publication.
     */
    @Supports @NonZero @NonNegative public final long maximumSeparation; // in millis
    /**
     * Maximum latency to consume the data (in milliseconds).
     */
    @Offers @NonZero @NonNegative public final long maximumLatency; // in millis
    /**
     * Minimum remaining lifetime required of the consumed data (in milliseconds).
     */
    @Expects @NonZero @NonNegative public final long minimumRemainingLifetime; // in millis
    /**
     * Number of consecutive consumptions failing to complete within maximum latency duration that can be tolerated by
     * subscriber.  Upon breaching this number, the subscriber will be notified.
     */
    @Supports @NonZero @NonNegative public final int consumptionTolerance;
    /**
     * Subscriber.
     */
    @NonNull public final Subscriber<T> subscriber;

    /**
     * Instantiates a new Subscriber configuration.
     *
     * @param topic the topic
     * @param minimumSeparation the minimum duration of time (in milliseconds) between two consecutive consumptions.
     * @param maximumSeparation the maximum duration of time (in milliseconds) tolerated between two consecutive
     *                          consumptions.
     * @param maximumLatency the maximum latency to consume the data (in milliseconds).
     * @param minimumRemainingLifetime the minimum remaining lifetime required of the consumed data (in milliseconds).
     * @param consumptionTolerance the number of consecutive consumptions failing to complete within maximum latency
     *                             duration that can be tolerated by subscriber.  Upon breaching this number, the
     *                             subscriber will be notified.
     * @param subscriber the subscriber
     */
    public SubscriberConfiguration(
            @NonNull @NonEmpty String topic,
            @Supports @NonZero @NonNegative final long minimumSeparation,
            @Supports @NonZero @NonNegative final long maximumSeparation,
            @Offers @NonZero @NonNegative final long maximumLatency,
            @Expects @NonZero @NonNegative final long minimumRemainingLifetime,
            @Supports @NonZero @NonNegative final int consumptionTolerance,
            @NonNull final Subscriber<T> subscriber) {
        assert topic != null;
        assert !topic.isEmpty();
        assert minimumSeparation > 0;
        assert maximumSeparation > 0;
        assert maximumSeparation >= minimumSeparation;
        assert maximumLatency > 0;
        assert maximumLatency < minimumSeparation;
        assert minimumRemainingLifetime > 0;
        assert consumptionTolerance > 0;
        assert subscriber != null;

        this.topic = topic;
        this.minimumSeparation = minimumSeparation;
        this.maximumSeparation = maximumSeparation;
        this.maximumLatency = maximumLatency;
        this.minimumRemainingLifetime = minimumRemainingLifetime;
        this.consumptionTolerance = consumptionTolerance;
        this.subscriber = subscriber;
    }
}
