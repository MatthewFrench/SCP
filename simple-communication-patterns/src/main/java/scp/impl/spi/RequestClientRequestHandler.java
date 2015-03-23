/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.impl.spi;

import scp.impl.ResponderInvoker;
import scp.util.NonNull;
import scp.util.Pair;
import scp.util.TimestampedBox;

import java.io.Serializable;

/**
 * Interface used by service layer to interact with the lower-level communication substrate to communicate the
 * request to the responder.
 *
 * @param <T>  the type of requested data.
 */
public interface RequestClientRequestHandler<T extends Serializable> {
    /**
     * Request data.
     *
     * @return the status of requesting and responding along with the response.
     */
    @NonNull
    Pair<ResponderInvoker.ResponderInvokerStatus, TimestampedBox<T>> request();
}
