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
 * Receiver of data.
 * @param <T>  the type of received data.
 */
public interface Receiver<T extends Serializable> {

    /**
     * Enumeration of the acknowledgement provided for sent data.
     */
    enum ReceptionAcknowledgement {
        /**
         * Data was accepted.
         */
        DATA_ACCEPTED,
        /**
         * Data was rejected.
         */
        DATA_REJECTED
    }

    /**
     * Receive data.
     *
     * @param data the data.
     * @return the reception acknowledgement.
     */
    @NonNull
    ReceptionAcknowledgement receive(@NonNull T data);

}
