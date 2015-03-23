/**
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
 * Executor of actions.
 * @param <T>  the type of action.
 */
public interface Executor<T extends Serializable> {
    /**
     * Enumeration of the acknowledgement provided for a requested action.
     */
    enum ExecutionAcknowledgement {
        /**
         * Action was unavailable.
         */
        ACTION_UNAVAILABLE,
        /**
         * Action failed.
         */
        ACTION_FAILED,
        /**
         * Action succeeded.
         */
        ACTION_SUCCEEDED
    }

    /**
     * Execute the requested action.
     *
     * @param action the action.
     * @return an acknowledgement for the execution of action.
     */
    @NonNull
    ExecutionAcknowledgement execute(@NonNull T action);
}
