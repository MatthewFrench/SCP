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
 * Initiator configuration.
 * @param <S>  the type of initiated action.
 */
public final class InitiatorConfiguration<S extends Serializable> {
    /**
     * Executor's remote identifier.
     */
    @NonNull public final String executorIdentifier;
    /**
     * Maximum latency tolerated to execute the command (in milliseconds).
     */
    @Offers @NonZero @NonNegative public final long maximumLatency; // in millis
    /**
     * Minimum duration of time (in milliseconds) between issuing two consecutive command executions.  In other words,
     * after issuing a request for execution, new requests will be inhibited for this duration of time.
     */
    @Expects @NonZero @NonNegative public final long minimumSeparation; // in millis
    /**
     * Action type.
     */
    @NonNull public final Class<S> actionType;

    /**
     * Instantiates a new Initiator configuration.
     *
     * @param executorIdentifier the executor's remote identifier
     * @param minimumSeparation the minimum duration of time (in milliseconds) between issuing two consecutive command
     *                          executions.
     * @param maximumLatency maximum latency to execute the command (in milliseconds).
     * @param actionType the type of initiated action.
     */
    public InitiatorConfiguration(
            @NonNull final String executorIdentifier,
            @Offers @NonZero @NonNegative final long minimumSeparation,
            @Expects @NonZero @NonNegative final long maximumLatency,
            @NonNull final Class<S> actionType) {
        assert executorIdentifier != null;
        assert !executorIdentifier.isEmpty();
        assert maximumLatency > 0;
        assert minimumSeparation > 0;
        assert maximumLatency < minimumSeparation;
        assert actionType != null;

        this.executorIdentifier = executorIdentifier;
        this.maximumLatency = maximumLatency;
        this.minimumSeparation = minimumSeparation;
        this.actionType = actionType;
    }
}
