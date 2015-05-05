/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.api;

public abstract class TestCustomizer {
    protected abstract CommunicationManager getCommunicationManager();

    protected abstract long getMinimumInteractionLatency();

    protected long getMinimumInteractionAndTransportLatency() {
        return getMinimumInteractionLatency() + getMinimumTransportLatency();
    }

    protected abstract long getMinimumTransportLatency();
}
