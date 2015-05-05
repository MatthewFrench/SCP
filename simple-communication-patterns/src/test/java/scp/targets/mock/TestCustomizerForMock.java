/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.targets.mock;

import scp.api.CommunicationManager;
import scp.api.TestCustomizer;

class TestCustomizerForMock extends TestCustomizer {
    static final long INTERACTION_LATENCY = 20;
    static final long TRANSPORT_LATENCY = 50;

    protected InProcessCommunicationManagerImpl communicationManager =
            new InProcessCommunicationManagerImpl(INTERACTION_LATENCY, TRANSPORT_LATENCY);

    @Override
    protected long getMinimumTransportLatency() {
        return INTERACTION_LATENCY;
    }

    @Override
    protected CommunicationManager getCommunicationManager() {
        return this.communicationManager;
    }

    @Override
    protected long getMinimumInteractionLatency() {
        return INTERACTION_LATENCY;
    }
}
