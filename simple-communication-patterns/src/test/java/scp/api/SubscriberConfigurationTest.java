/**
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.api;

import org.junit.Test;
import scp.api.Subscriber.AbstractSubscriber;

public class SubscriberConfigurationTest {
    static final Subscriber<Integer> DUMMY_SUBSCRIBER =
            new AbstractSubscriber<Integer>() {
                @Override public void consume(Integer message, long remainingLifetime) { }
            };

    @Test
    public void testSuccessfulCreation() {
        new SubscriberConfiguration<>("test", 333, 1000, 100, 600, 2, DUMMY_SUBSCRIBER);
    }

    @Test(expected = AssertionError.class)
    public void testEmptyTopicName() throws Exception {
        new SubscriberConfiguration<>("",  333, 1000, 100, 300, 2, DUMMY_SUBSCRIBER);
    }

    @Test(expected = AssertionError.class)
    public void testNullTopicName() throws Exception {
        new SubscriberConfiguration<>(null, 333, 1000, 100, 300, 2, DUMMY_SUBSCRIBER);
    }

    @Test(expected = AssertionError.class)
    public void testZeroMinimumSeparation() throws Exception {
        new SubscriberConfiguration<>("test", 0, 1000, 100, 300, 2, DUMMY_SUBSCRIBER);
    }

    @Test(expected = AssertionError.class)
    public void testZeroMaximumSeparation() throws Exception {
        new SubscriberConfiguration<>("test", 333, 0, 100, 300, 2, DUMMY_SUBSCRIBER);
    }

    @Test(expected = AssertionError.class)
    public void testMinimumSeparationGreaterThanOrEqualMaximumSeparation() throws Exception {
        new SubscriberConfiguration<>("test", 333, 332, 100, 300, 2, DUMMY_SUBSCRIBER);
    }

    @Test(expected = AssertionError.class)
    public void testZeroMaximumLatency() throws Exception {
        new SubscriberConfiguration<>("test", 333, 1000, 0, 300, 2, DUMMY_SUBSCRIBER);
    }

    @Test(expected = AssertionError.class)
    public void testMaximumLatencyGreaterThanOrEqualMinimumSeparation() throws Exception {
        new SubscriberConfiguration<>("test", 333, 1000, 333, 300, 2, DUMMY_SUBSCRIBER);
    }

    @Test(expected = AssertionError.class)
    public void testZeroMinimumRemainingLifetime() throws Exception {
        new SubscriberConfiguration<>("test", 333, 1000, 100, 0, 2, DUMMY_SUBSCRIBER);
    }

    @Test(expected = AssertionError.class)
    public void testZeroConsumptionTolerance() throws Exception {
        new SubscriberConfiguration<>("test", 333, 1000, 100, 300, 0, DUMMY_SUBSCRIBER);
    }

    @Test(expected = AssertionError.class)
    public void testNullHandler() throws Exception {
        new SubscriberConfiguration<>("test", 3, 100, 300, 2, 2, null);
    }
}
