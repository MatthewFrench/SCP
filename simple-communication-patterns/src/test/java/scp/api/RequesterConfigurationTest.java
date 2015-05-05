/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.api;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class RequesterConfigurationTest {

    @Test
    public void testValidConfiguration() {
        final RequesterConfiguration<Integer> _tmp1 = new RequesterConfiguration<>(
                "test", 1000, 100, 500, Integer.class);
        assertNotNull(_tmp1);
    }

    @Test(expected = AssertionError.class)
    public void testNullResponderIdentifier() {
        new RequesterConfiguration<>(null, 1000, 100, 500, Integer.class);
    }

    @Test(expected = AssertionError.class)
    public void testEmptyResponderIdentifier() {
        new RequesterConfiguration<>("", 1000, 100, 500, Integer.class);
    }

    @Test(expected = AssertionError.class)
    public void testZeroMinimumSeparation() {
        new RequesterConfiguration<>("test", 0, 100, 500, Integer.class);
    }

    @Test(expected = AssertionError.class)
    public void testZeroMaximumLatency() {
        new RequesterConfiguration<>("test", 1000, 0, 500, Integer.class);
    }

    @Test(expected = AssertionError.class)
    public void testZeroMinimumRemainingLifetime() {
        new RequesterConfiguration<>("test", 1000, 100, 0, Integer.class);
    }

    @Test(expected = AssertionError.class)
    public void testMaximumLatencyGreaterThanOrEqualMinimumSeparation() {
        new RequesterConfiguration<>("test", 100, 100, 500, Integer.class);
    }

    @Test(expected = AssertionError.class)
    public void testNullDataType() {
        new RequesterConfiguration<>("test", 1000, 100, 500, null);
    }
}
