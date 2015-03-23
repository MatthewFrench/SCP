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
 * Requester configuration.
 * @param <S>  the type of requested data.
 */
public final class RequesterConfiguration<S> {
    /**
     * Responder's remote identifier.
     */
    @NonNull
    @NonEmpty
    public final String responderIdentifier;
    /**
     * Minimum duration of time (in milliseconds) between two consecutive requests.  In other words, after a request,
     * new requests will be inhibited for this duration of time.
     */
    @Offers
    @NonZero
    @NonNegative
    public final long minimumSeparation; // in millis
    /**
     * Maximum allowed latency for the request to complete (in milliseconds).
     */
    @Expects @NonZero @NonNegative public final long maximumLatency; // in millis
    /**
     * Minimum remaining lifetime required on the response upon arrival (in milliseconds).
     */
    @Expects @NonZero @NonNegative public final long minimumRemainingLifetime; // in millis
    /**
     * The type of response.
     */
    @NonNull public final Class<S> responseType;

    /**
     * Instantiates a new Requester configuration.
     *
     * @param responderIdentifier the responder's remote identifier
     * @param minimumSeparation the minimum duration of time (in milliseconds) between issuing two consecutive requests.
     * @param maximumLatency the maximum allowed latency for the request to complete (in milliseconds).
     * @param minimumRemainingLifetime the minimum remaining lifetime required on the response upon arrival (in
     *                                 milliseconds).
     * @param responseType the type of response.
     */
    public RequesterConfiguration(
            @NonNull @NonEmpty String responderIdentifier,
            @Offers @NonZero @NonNegative final long minimumSeparation,
            @Expects @NonZero @NonNegative final long maximumLatency,
            @Expects @NonZero @NonNegative final long minimumRemainingLifetime,
            @NonNull final Class<S> responseType) {
        assert responderIdentifier != null;
        assert !responderIdentifier.isEmpty();
        assert maximumLatency > 0;
        assert minimumSeparation > 0;
        assert maximumLatency < minimumSeparation;
        assert minimumRemainingLifetime > 0;
        assert responseType != null;

        this.responderIdentifier = responderIdentifier;
        this.maximumLatency = maximumLatency;
        this.minimumSeparation = minimumSeparation;
        this.minimumRemainingLifetime = minimumRemainingLifetime;
        this.responseType = responseType;
    }
}
