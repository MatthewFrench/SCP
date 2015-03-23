/**
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.targets.dds;

import scp.api.TestCustomizer;

public class PublisherTest extends scp.api.PublisherTest {
    private static final TestCustomizerForDDS TEST_CUSTOMIZER_FOR_DDS = new TestCustomizerForDDS();

    @Override
    protected TestCustomizer getTestCustomizer() {
        return TEST_CUSTOMIZER_FOR_DDS;
    }
}
