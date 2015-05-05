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

/**
 * Responder of requests.
 * @param <T>  the type parameter
 */
public interface Responder<T> {

    /**
     * Enumeration of the acknowledgement provided for requested data.
     */
    public enum ResponseStatus {
        /**
         * Data was unavailable.
         */
        DATA_UNAVAILABLE,
        /**
         * Response was provided.
         */
        RESPONSE_PROVIDED,
    }

    /**
     * Respond with data.
     *
     * @return <code>RESPONSE_PROVIDED</code> and data upon successful response; otherwise,
     * <code>DATA_UNAVAILABLE</code> and null.
     */
    @NonNull
    Pair<ResponseStatus, T> respond();

}
