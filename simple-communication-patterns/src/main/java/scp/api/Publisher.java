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
 * Publisher of data.
 * @param <T>  the type of published data
 */
public interface Publisher<T extends Serializable> {

    /**
     * Enumeration of publication status.
     */
    enum PublicationStatus {
        /**
         * Publication was dropped as it exceeded the specified rate of publication (minimum separation).
         */
        FAST_PUBLICATION_DROPPED,
        /**
         * Publication succeeded.
         */
        PUBLISHED,
        /**
         * Lower-level communication substrate exceeded the expected maximum latency to accept the request for
         * publication.
         */
        TIME_OUT,
        /**
         * Unknown failure.
         */
        UNKNOWN_FAILURE
    }

    /**
     * Publish data.
     *
     * @param data the data
     * @return the publication status
     */
    @NonNull
    PublicationStatus publish(@NonNull T data);

}

