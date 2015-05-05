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
 * Sender configuration.
 * @param <S>  the type of sent data.
 */
public final class SenderConfiguration<S extends Serializable> {
    /**
     * Receiver's remote identifier.
     */
    @NonNull
    @NonEmpty
    public final String receiverIdentifier;
    /**
     * Minimum duration of time (in milliseconds) between two consecutive sends.  In other words, after a send,
     * new sends will be inhibited for this duration of time.
     */
    @Offers
    @NonZero
    @NonNegative
    public final long minimumSeparation;
    /**
     * Maximum allowed latency for the send to complete (in milliseconds).
     */
    @Expects @NonZero @NonNegative public final long maximumLatency; // in millis
    /**
     * The type of sent data.
     */
    @NonNull public final Class<S> dataType;

    /**
     * Instantiates a new Sender configuration.
     *
     * @param receiverIdentifier the receiver's remote identifier.
     * @param minimumSeparation the minimum duration of time (in milliseconds) between two consecutive sends.
     * @param maximumLatency the maximum allowed latency for the send to complete (in milliseconds).
     * @param dataType the type of sent data
     */
    public SenderConfiguration(
            @NonNull @NonEmpty final String receiverIdentifier,
            @Offers @NonZero @NonNegative final long minimumSeparation,
            @Expects @NonZero @NonNegative final long maximumLatency,
            @NonNull final Class<S> dataType) {
        assert receiverIdentifier != null;
        assert !receiverIdentifier.isEmpty();
        assert maximumLatency > 0;
        assert minimumSeparation > 0;
        assert maximumLatency < minimumSeparation;
        assert dataType != null;

        this.receiverIdentifier = receiverIdentifier;
        this.maximumLatency = maximumLatency;
        this.minimumSeparation = minimumSeparation;
        this.dataType = dataType;
    }
}
