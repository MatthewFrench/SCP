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

public class PublisherConfigurationTest {

    @Test
    public void testSuccessfulCreation() {
        new PublisherConfiguration<>("test", 1000, 100, 600, Object.class);
    }

    @Test(expected = AssertionError.class)
    public void testEmptyTopicName() throws Exception {
        new PublisherConfiguration<>("", 1000, 100, 600, Object.class);
    }

    @Test(expected = AssertionError.class)
    public void testNullTopicName() throws Exception {
        new PublisherConfiguration<>(null, 1000, 100, 600, Object.class);
    }

    @Test(expected = AssertionError.class)
    public void testZeroMinimumSeparation() throws Exception {
        new PublisherConfiguration<>("test", 0, 100, 600, Object.class);
    }

    @Test(expected = AssertionError.class)
    public void testZeroMaximumLatency() throws Exception {
        new PublisherConfiguration<>("test", 1000, 0, 600, Object.class);
    }

    @Test(expected = AssertionError.class)
    public void testMaximumLatencyGreaterThanOrEqualMinimumSeparation() throws Exception {
        new PublisherConfiguration<>("test", 1000, 1000, 600, Object.class);
    }

    @Test(expected = AssertionError.class)
    public void testZeroMinimumRemainingLifetime() throws Exception {
        new PublisherConfiguration<>("test", 1000, 100, 0, Object.class);
    }

    @Test(expected = AssertionError.class)
    public void testMinimumRemainingLifetimeLesserThanOrEqualMaximumLatency() throws Exception {
        new PublisherConfiguration<>("test", 1000, 100, 100, Object.class);
    }

    @Test(expected = AssertionError.class)
    public void testNullDataType() throws Exception {
        new PublisherConfiguration<>("test", 1000, 100, 600, null);
    }
}
