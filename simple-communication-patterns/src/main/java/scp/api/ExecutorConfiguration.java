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
 * Executor configuration.
 * @param <S>  the type of action.
 */
public final class ExecutorConfiguration<S extends Serializable> {
    /**
     * Executor's remote identifier.
     */
    @NonNull
    @NonEmpty
    public final String identifier;
    /**
     * Minimum duration of time (in milliseconds) between servicing two consecutive executions  In other words,
     * after executing a action, new execution requests will not be serviced for this duration of time.
     */
    @Supports
    @NonZero
    @NonNegative
    public final long minimumSeparation;
    /**
     * Maximum latency to execute the action (in milliseconds).
     */
    @Offers @NonZero @NonNegative public final long maximumLatency;
    /**
     * Executor
     */
    @NonNull public final Executor<S> executor;

    /**
     * Instantiates a new executor configuration.
     *
     * @param identifier executor's remote identifier.
     * @param minimumSeparation Minimum duration of time (in milliseconds) between servicing two consecutive executions.
     * @param maximumLatency Maximum latency to execute the action (in milliseconds).
     * @param executor the executor
     */
    public ExecutorConfiguration(
            @NonNull @NonEmpty final String identifier,
            @Supports @NonZero @NonNegative final long minimumSeparation,
            @Offers @NonZero @NonNegative final long maximumLatency,
            @NonNull final Executor<S> executor) {
        assert identifier != null;
        assert !identifier.isEmpty();
        assert maximumLatency > 0;
        assert minimumSeparation > 0;
        assert maximumLatency < minimumSeparation;
        assert executor != null;

        this.identifier = identifier;
        this.maximumLatency = maximumLatency;
        this.minimumSeparation = minimumSeparation;
        this.executor = executor;
    }
}
