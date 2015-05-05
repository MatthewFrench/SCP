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
import scp.util.Pair;

import static org.junit.Assert.assertNotNull;

public class ResponderConfigurationTest {

    static final Responder<Integer> DUMMY_RESPONDER =
            new Responder<Integer>() {
                public Pair<ResponseStatus, Integer> respond() {
                    return null;
                }
            };

    @Test
    public void testSuccessfulCreation() {
        final ResponderConfiguration<Integer> _tmp1 = new ResponderConfiguration<>(
                "test", 1000, 100, 500, DUMMY_RESPONDER);
        assertNotNull(_tmp1);
    }

    @Test(expected = AssertionError.class)
    public void testEmptyIdentifier() {
        new ResponderConfiguration<>("", 1000, 100, 500, DUMMY_RESPONDER);
    }

    @Test(expected = AssertionError.class)
    public void testNullIdentifier() {
        new ResponderConfiguration<>(null, 1000, 100, 500, DUMMY_RESPONDER);
    }

    @Test(expected = AssertionError.class)
    public void testZeroMinimumSeparation() {
        new ResponderConfiguration<>("test", 0, 100, 500, DUMMY_RESPONDER);
    }

    @Test(expected = AssertionError.class)
    public void testZeroMaximumLatency() {
        new ResponderConfiguration<>("test", 1000, 0, 500, DUMMY_RESPONDER);
    }

    @Test(expected = AssertionError.class)
    public void testMaximumLatencyEqualOrGreaterThanMinimumSeparation() {
        new ResponderConfiguration<>("test", 1000, 1000, 500, DUMMY_RESPONDER);
    }

    @Test(expected = AssertionError.class)
    public void testZeroMinimumRemainingLifetime() {
        new ResponderConfiguration<>("test", 1000, 100, 0, DUMMY_RESPONDER);
    }

    @Test(expected = AssertionError.class)
    public void testNullResponder() {
        new ResponderConfiguration<>("test", 1000, 100, 0, null);
    }
}
