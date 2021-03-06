/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.targets.vertx.unclustered;

import scp.api.TestCustomizer;
import scp.targets.vertx.TestCustomizerForVertx;

public class InitiatorExecutorTest extends scp.api.InitiatorExecutorTest {
    private static final TestCustomizerForVertx TEST_CUSTOMIZER_FOR_VERTX = new TestCustomizerForVertx(false);

    @Override
    protected TestCustomizer getTestCustomizer() {
        return TEST_CUSTOMIZER_FOR_VERTX;
    }
}
