/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.targets.mock;

import scp.api.TestCustomizer;

public class SenderReceiverTest extends scp.api.SenderReceiverTest {
    private static final TestCustomizerForMock TEST_CUSTOMIZER_FOR_MOCK = new TestCustomizerForMock();

    @Override
    protected TestCustomizer getTestCustomizer() {
        return TEST_CUSTOMIZER_FOR_MOCK;
    }
}
