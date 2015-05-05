/**
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.util;

import java.io.Serializable;

/**
 * A container of data and its lifetime information.
 *
 * @param <T> the type of contained data.
 */
public class TimestampedBox<T extends Serializable> implements Serializable {
    /**
     * Contained data.
     */
    public final T data;
    /**
     * Remaining lifetime of contained data.
     */
    public final long remainingLifetime;
    /**
     * Timestamp of this container (when it was created).
     */
    public final long timestamp;

    @Override
    public String toString() {
        final StringBuilder _sb = new StringBuilder("TimestampedBox{");
        _sb.append("data=").append(data);
        _sb.append(", remainingLifetime=").append(remainingLifetime);
        _sb.append(", timestamp=").append(timestamp);
        _sb.append('}');
        return _sb.toString();
    }

    /**
     * Instantiates a new Timestamped box.
     *
     * @param data the data
     * @param remainingLifetime the remaining lifetime of data argument.
     */
    public TimestampedBox(final T data,
                          @NonZero @NonNegative final long remainingLifetime) {
        assert remainingLifetime > 0;

        this.data = data;
        this.remainingLifetime = remainingLifetime;
        this.timestamp = System.currentTimeMillis();
    }
}
