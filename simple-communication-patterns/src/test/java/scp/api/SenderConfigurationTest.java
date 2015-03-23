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

public class SenderConfigurationTest {

    @Test
    public void testValidConfiguration() {
        new SenderConfiguration<>("test", 1000, 100, Integer.class);
    }

    @Test(expected = AssertionError.class)
    public void testNullIdentifier() {
        new SenderConfiguration<>(null, 1000, 100, Integer.class);
    }

    @Test(expected = AssertionError.class)
    public void testEmptyIdentifier() {
        new SenderConfiguration<>("", 1000, 100, Integer.class);
    }

    @Test(expected = AssertionError.class)
    public void testZeroMinimumSeparation () {
        new SenderConfiguration<>("test", 0, 100, Integer.class);
    }

    @Test(expected = AssertionError.class)
    public void testZeroMaximumLatency () {
        new SenderConfiguration<>("test", 1000, 0, Integer.class);
    }

    @Test(expected = AssertionError.class)
    public void testMaximumLatencyGreaterThanOrEqualMinimumSeparation () {
        new SenderConfiguration<>("test", 1000, 0, Integer.class);
    }

    @Test(expected = AssertionError.class)
    public void testNullDataType() {
        new SenderConfiguration<>("Test", 1000, 100, null);
    }
}
