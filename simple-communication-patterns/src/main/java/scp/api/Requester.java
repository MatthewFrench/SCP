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
import scp.util.Pair;

/**
 * Requester of data.
 * @param <T>  the type of requested data
 */
public interface Requester<T> {

    /**
     * Enumeration of request status.
     */
    enum RequestStatus {
        /**
         * Requested data was unavailable.
         */
        DATA_UNAVAILABLE,
        /**
         * Responder rejected the request as there were too many requests.
         */
        EXCESS_LOAD,
        /**
         * Request was dropped as it violated the specified rate of request (minimum separation).
         */
        FAST_REQUEST_DROPPED,
        /**
         * Request exceeded the expected maximum latency.
         */
        LOCAL_TIME_OUT,
        /**
         * Unknown failure occurred at the requester.
         */
        LOCAL_UNKNOWN_FAILURE,
        /**
         * Responder failed to honor its maximum service latency.
         */
        REMOTE_TIME_OUT,
        /**
         * Unknown failure occurred at the responder.
         */
        REMOTE_UNKNOWN_FAILURE,
        /**
         * Provided data was stale.
         */
        STALE_DATA,
        /**
         * Request succeeded.
         */
        SUCCEEDED
    }

    /**
     * Request data.
     *
     * @return SUCCEEDED and the data if request succeeded; otherwise, the status and null.
     */
    @NonNull
    Pair<RequestStatus, T> request();

}
