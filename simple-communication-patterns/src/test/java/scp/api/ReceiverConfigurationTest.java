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
import scp.util.NonNull;

public class ReceiverConfigurationTest {
    static final Receiver<Integer> DUMMY_RECEIVER = new Receiver<Integer>() {
        @Override
        public ReceptionAcknowledgement receive(@NonNull Integer message) {
            return ReceptionAcknowledgement.DATA_ACCEPTED;
        }
    };

    @Test
    public void testValidConfiguration() {
        new ReceiverConfiguration<>("test", 1000, 100, DUMMY_RECEIVER);
    }

    @Test(expected = AssertionError.class)
    public void testNullIdentifier() {
        new ReceiverConfiguration<>(null, 0, 100, DUMMY_RECEIVER);
    }

    @Test(expected = AssertionError.class)
    public void testEmptyIdentifier() {
        new ReceiverConfiguration<>("", 0, 100, DUMMY_RECEIVER);
    }

    @Test(expected = AssertionError.class)
    public void testZeroMinimumSeparation() {
        new ReceiverConfiguration<>("test", 0, 100, DUMMY_RECEIVER);
    }

    @Test(expected = AssertionError.class)
    public void testMaximumLatencyGreaterThanOrEqualMinimumSeparation() {
        new ReceiverConfiguration<>("test", 100, 100, DUMMY_RECEIVER);
    }

    @Test(expected = AssertionError.class)
    public void testNullHandler() {
        new ReceiverConfiguration<>("test", 100, 90, null);
    }
}
