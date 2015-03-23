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
 * Subscriber of data.
 * @param <T>  the type of subscribed data.
 */
public interface Subscriber<T extends Serializable> {

    /**
     * Consume data
     *
     * @param data the data
     * @param remainingLifetime the remaining lifetime of the data (in milliseconds)
     */
    public abstract void consume(@NonNull T data, long remainingLifetime);

    /**
     * Handle stale data.
     *
     * @param data the data
     * @param remainingLifetime of the data (in milliseconds).  This will be less than the minimum remaining lifetime
     *                          expected by the subscriber.
     */
    void handleStaleMessage(@NonNull T data, long remainingLifetime);

    /**
     * Handle slow publication.  This is triggered when no new data arrives for maximum separation duration since the
     * last arrival of data.
     */
    void handleSlowPublication();

    /**
     * Handle slow consumption.  This is triggered when the subscriber consecutively times out consuming more than a
     * fixed number of data (specified via {@link SubscriberConfiguration#consumptionTolerance}).
     *
     * @param numOfUnconsumedConsecutiveMessages the num of unconsumed consecutive messages
     */
    void handleSlowConsumption(@NonZero @NonNegative int numOfUnconsumedConsecutiveMessages);

    /**
     * An empty subscriber.
     * @param <T>  the type of subscribed data.
     */
    abstract class AbstractSubscriber<T extends Serializable> implements Subscriber<T> {
        @Override
        public void handleStaleMessage(@NonNull T data, long remainingLifetime) { }

        @Override
        public void handleSlowPublication() { }

        @Override
        public void handleSlowConsumption(@NonZero @NonNegative int numOfUnconsumedConsecutiveMessages) { }
    }

}
