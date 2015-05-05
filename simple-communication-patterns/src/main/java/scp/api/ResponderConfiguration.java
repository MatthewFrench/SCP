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
 * Responder configuration.
 * @param <T>  the type of response.
 */
public class ResponderConfiguration<T> {
    /**
     * Responder's remote Identifier.
     */
    @NonNull
    @NonEmpty
    public final String identifier;
    /**
     * Minimum duration of time (in milliseconds) between two consecutive requests for response.  In other words,
     * after a request has accepted for servicing, new requests will be inhibited for this duration of time.
     */
    @Supports
    @NonZero
    @NonNegative
    public final long minimumSeparation; // in millis
    /**
     * Maximum latency to service the request with response (in milliseconds).
     */
    @Offers @NonZero @NonNegative public final long maximumLatency; // in millis
    /**
     * Minimum remaining lifetime of the provided response (in milliseconds).
     */
    @Offers @NonZero @NonNegative public final long minimumRemainingLifetime; // in millis
    /**
     * Responder.
     */
    @NonNull public final Responder<T> responder;

    /**
     * Instantiates a new responder configuration.
     *
     * @param identifier the responder's remote identifier
     * @param minimumSeparation Minimum duration of time (in milliseconds) between two consecutive requests for
     *                          response.  In other words,
     * @param maximumLatency the maximum latency to service the request with response (in milliseconds).
     * @param minimumRemainingLifetime the minimum remaining lifetime of the provided response (in milliseconds).
     * @param responder the responder
     */
    public ResponderConfiguration(
            @NonNull @NonEmpty final String identifier,
            @Supports @NonZero @NonNegative final long minimumSeparation,
            @Offers @NonZero @NonNegative final long maximumLatency,
            @Offers @NonZero @NonNegative final long minimumRemainingLifetime,
            @NonNull final Responder<T> responder) {
        assert identifier != null;
        assert !identifier.isEmpty();
        assert maximumLatency > 0;
        assert minimumSeparation > 0;
        assert maximumLatency < minimumSeparation;
        assert minimumRemainingLifetime > 0;
        assert responder != null;

        this.identifier = identifier;
        this.maximumLatency = maximumLatency;
        this.minimumSeparation = minimumSeparation;
        this.minimumRemainingLifetime = minimumRemainingLifetime;
        this.responder = responder;
    }
}
