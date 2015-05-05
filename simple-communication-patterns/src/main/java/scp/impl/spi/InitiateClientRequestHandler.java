/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.impl.spi;

import scp.impl.ExecutorInvoker;
import scp.util.NonNull;

import java.io.Serializable;

/**
 * Interface used by service layer to interact with the lower-level communication substrate to communicate the
 * initiation to the executor.
 *
 * @param <T>  the type of action
 */
public interface InitiateClientRequestHandler<T extends Serializable> {
    /**
     * Initiate the action.
     *
     * @param action the action
     * @return the status of initiating and executing the action.
     */
    ExecutorInvoker.ExecutorInvokerStatus initiate(@NonNull T action);
}
