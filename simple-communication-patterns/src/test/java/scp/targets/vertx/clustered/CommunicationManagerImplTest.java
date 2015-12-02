/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.targets.vertx.clustered;

import scp.api.CommunicationManagerTest;
import scp.api.TestCustomizer;
import scp.targets.vertx.TestCustomizerForVertx;

public class CommunicationManagerImplTest extends CommunicationManagerTest {
    private static final TestCustomizerForVertx TEST_CUSTOMIZER_FOR_VERTX = new TestCustomizerForVertx(true);

    @Override
    protected TestCustomizer getTestCustomizer() {
        return TEST_CUSTOMIZER_FOR_VERTX;
    }
}
