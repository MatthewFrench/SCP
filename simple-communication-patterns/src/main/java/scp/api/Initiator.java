/**
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html                             
 */

package scp.api;

import scp.util.NonNull;

import java.io.Serializable;

/**
 * Initiator of actions.
 * @param <T>  the type of initiated action.
 */
public interface Initiator<T extends Serializable> {

    /**
     * Enumeration of initiation status.
     */
    enum InitiationStatus {
        /**
         * Action failed.
         */
        ACTION_FAILED,
        /**
         * Action succeeded.
         */
        ACTION_SUCCEEDED,
        /**
         * Action was unavailable.
         */
        ACTION_UNAVAILABLE,
        /**
         * Executor rejected the execution request as there were too many execution requests.
         */
        EXCESS_LOAD,
        /**
         * Initiation was dropped as it violated the specified rate of initiation (minimum separation).
         */
        FAST_INITIATION_DROPPED,
        /**
         * Initiation exceeded the expected maximum latency.
         */
        LOCAL_TIME_OUT,
        /**
         * Unknown failure occurred at the initiator.
         */
        LOCAL_UNKNOWN_FAILURE,
        /**
         * Executor failed to honor its maximum service latency.
         */
        REMOTE_TIME_OUT,
        /**
         * Unknown failure occurred at the executor.
         */
        REMOTE_UNKNOWN_FAILURE
    }

    /**
     * Initiate action.
     *
     * @param action the action
     * @return the initiation and execution status
     */
    @NonNull
    InitiationStatus initiate(@NonNull T action);

}
