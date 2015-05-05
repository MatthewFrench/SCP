/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.impl.spi;

import scp.util.NonNull;
import scp.util.TimestampedBox;

import java.io.Serializable;

/**
 * Interface used by service layer to interact with the lower-level communication substrate to publish data.
 *
 * @param <T>  the type of published data.
 */
public interface PublishClientRequestHandler<T extends Serializable> {
    /**
     * Publish data.
     *
     * @param data the published data.
     */
    void publish(@NonNull TimestampedBox<T> data);
}
