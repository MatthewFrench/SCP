/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.impl.spi;

import scp.impl.ReceiverInvoker;
import scp.util.NonNull;

import java.io.Serializable;

/**
 * Interface used by service layer to interact with the lower-level communication substrate to communicate the
 * send to the receiver.
 * @param <T>  the type of sent data.
 */
public interface SendClientRequestHandler<T extends Serializable> {
    /**
     * Send data
     *
     * @param data the data
     * @return the status of sending and receiving the data.
     */
    ReceiverInvoker.ReceiverInvokerStatus send(@NonNull T data);
}
