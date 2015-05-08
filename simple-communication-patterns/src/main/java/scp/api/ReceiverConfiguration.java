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
 * Receiver configuration.
 * @param <S>  the type of received data.
 */
public class ReceiverConfiguration<S extends Serializable> {
    /**
     * Minimum duration of time (in milliseconds) between two consecutive receptions.  In other words, after a
     * reception, new requests will not be serviced for this duration of time.
     */
    @NonNull @NonEmpty public final long minimumSeparation; // in millis
    /**
     * Maximum latency to receive the data (in milliseconds).
     */
    @Supports @NonZero @NonNegative public final long maximumLatency; // in millis
    /**
     * Receiver
     */
    @Offers @NonZero @NonNegative public final Receiver<S> handler;
    /**
     * Receiver's remote identifier.
     */
    @NonNull public final String identifier;

    /**
     * Instantiates a new receiver configuration.
     *
     * @param identifier the receiver's remote identifier.
     * @param minimumSeparation the minimum duration of time (in milliseconds) between two consecutive receptions.
     * @param maximumLatency the maximum latency to receive the data(in milliseconds).
     * @param responder the responder
     */
    public ReceiverConfiguration(
            @NonNull @NonEmpty final String identifier,
            @Supports @NonZero @NonNegative final long minimumSeparation,
            @Offers @NonZero @NonNegative final long maximumLatency,
            @NonNull final Receiver<S> responder) {
        assert identifier != null;
        assert !identifier.isEmpty();
        assert minimumSeparation > 0;
        assert maximumLatency > 0;
        assert maximumLatency < minimumSeparation;
        assert responder != null;

        this.identifier = identifier;
        this.maximumLatency = maximumLatency;
        this.minimumSeparation = minimumSeparation;
        this.handler = responder;
    }
}
