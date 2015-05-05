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

public class ExecutorConfigurationTest {
    static final Executor<Integer> DUMMY_EXECUTOR = new Executor<Integer>() {
        @Override
        public ExecutionAcknowledgement execute(@NonNull Integer cmd) {
            return ExecutionAcknowledgement.ACTION_SUCCEEDED;
        }
    };

    @Test
    public void testValidConfiguration() {
        new ExecutorConfiguration<>("test", 1000, 100, DUMMY_EXECUTOR);
    }

    @Test(expected = AssertionError.class)
    public void testNullIdentifier() {
        new ExecutorConfiguration<>(null, 0, 100, DUMMY_EXECUTOR);
    }

    @Test(expected = AssertionError.class)
    public void testEmptyIdentifier() {
        new ExecutorConfiguration<>("", 0, 100, DUMMY_EXECUTOR);
    }

    @Test(expected = AssertionError.class)
    public void testZeroMinimumSeparation() {
        new ExecutorConfiguration<>("test", 0, 100, DUMMY_EXECUTOR);
    }

    @Test(expected = AssertionError.class)
    public void testMaximumLatencyGreaterThanOrEqualMinimumSeparation() {
        new ExecutorConfiguration<>("test", 100, 100, DUMMY_EXECUTOR);
    }

    @Test(expected = AssertionError.class)
    public void testNullHandler() {
        new ExecutorConfiguration<>("test", 100, 90, null);
    }
}
