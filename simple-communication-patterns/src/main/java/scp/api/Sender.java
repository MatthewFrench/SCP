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
 * Sender of data.
 * @param <T>  the type of sent data.
 */
public interface Sender<T extends Serializable> {

    /**
     * Enumeration of send status.
     */
    enum SendStatus {
        /**
         * Data was accepted.
         */
        DATA_ACCEPTED,
        /**
         * Data was rejected.
         */
        DATA_REJECTED,
        /**
         * Receiver rejected the send as there were too many sends.
         */
        EXCESS_LOAD,
        /**
         * Send was dropped as it violated the specified rate of send (minimum separation).
         */
        FAST_SEND_DROPPED,
        /**
         * Send exceeded the expected maximum latency.
         */
        LOCAL_TIME_OUT,
        /**
         * Unknown failure occurred at the sender.
         */
        LOCAL_UNKNOWN_FAILURE,
        /**
         * Receiver failed to honor its maximum service latency.
         */
        REMOTE_TIME_OUT,
        /**
         * Unknown failure occurred at the receiver.
         */
        REMOTE_UNKNOWN_FAILURE
    }

    /**
     * Send data.
     *
     * @param data the data
     * @return the status of sending and receiving the data.
     */
    @NonNull
    SendStatus send(@NonNull T data);

}
