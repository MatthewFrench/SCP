/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.targets.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import scp.api.CommunicationManager;
import scp.api.TestCustomizer;

import java.util.concurrent.Semaphore;

class TestCustomizerForVertx extends TestCustomizer {
    /*
     * Note: We used non-clustered vertx instance in the test.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestCustomizerForVertx.class);
    private final CommunicationManagerImpl communicationManager = new CommunicationManagerImpl();
    private long minimumInteractionLatency = -1;
    private long minimumTransportLatency = -1;

    @Override
    protected CommunicationManager getCommunicationManager() {
        return this.communicationManager;
    }

    @Override
    protected long getMinimumInteractionLatency() {
        if (this.minimumInteractionLatency == -1) {
            final Vertx _vertex = VertxFactory.newVertx();
            final EventBus _eb = _vertex.eventBus();

            final long _start = System.currentTimeMillis();
            _eb.publish("getMinimumInteractionLatency", 10);
            final long _stop = System.currentTimeMillis();
            this.minimumInteractionLatency = _stop - _start;

            _vertex.stop();
        }
        return this.minimumInteractionLatency;
    }

    @Override
    protected long getMinimumTransportLatency() {
        if (this.minimumTransportLatency == -1) {
            final Vertx _vertx = VertxFactory.newVertx();
            final EventBus _eb = _vertx.eventBus();
            _eb.registerLocalHandler("getMinimumTransportLatency", (m) -> m.reply());

            final Semaphore _sem = new Semaphore(0);
            final long _start = System.currentTimeMillis();
            _eb.send("getMinimumTransportLatency", 10, (Message<Object> event) -> _sem.release());
            try {
                _sem.acquire();
            } catch (InterruptedException _e) {
                LOGGER.warn("Why did the semaphore get interrupted?", _e);
                throw new RuntimeException(_e);
            }
            final long _stop = System.currentTimeMillis();
            this.minimumTransportLatency = _stop - _start;

            _vertx.stop();
        }
        return this.minimumTransportLatency;
    }
}
