/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.targets.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scp.api.CommunicationManager;
import scp.api.TestCustomizer;

import java.util.concurrent.Semaphore;

public class TestCustomizerForVertx extends TestCustomizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestCustomizerForVertx.class);
    private final CommunicationManagerImpl communicationManager;
    private long minimumInteractionLatency = -1;
    private long minimumTransportLatency = -1;

    public TestCustomizerForVertx(boolean clustered) {
        if (clustered) {
            communicationManager = new CommunicationManagerImpl(0, "localhost");
        } else {
            communicationManager = new CommunicationManagerImpl();
        }
    }

    @Override
    protected CommunicationManager getCommunicationManager() {
        return this.communicationManager;
    }

    @Override
    protected long getMinimumInteractionLatency() {
        if (this.minimumInteractionLatency == -1) {
            final Vertx _vertx = Vertx.vertx();
            final EventBus _eb = _vertx.eventBus();

            final long _start = System.currentTimeMillis();
            _eb.publish("getMinimumInteractionLatency", 10);
            final long _stop = System.currentTimeMillis();
            this.minimumInteractionLatency = _stop - _start;

            _vertx.close();
        }
        return this.minimumInteractionLatency;
    }

    @Override
    protected long getMinimumTransportLatency() {
        if (this.minimumTransportLatency == -1) {
            final Vertx _vertx = Vertx.vertx();
            final EventBus _eb = _vertx.eventBus();
            _eb.localConsumer("getMinimumTransportLatency", (m) -> m.reply(""));

            final Semaphore _sem = new Semaphore(0);
            final long _start = System.currentTimeMillis();
            final DeliveryOptions _opt = new DeliveryOptions();
            _opt.setSendTimeout(10);
            _eb.send("getMinimumTransportLatency", "", _opt,
                    (AsyncResult<Message<Object>> event) -> _sem.release());
            try {
                _sem.acquire();
            } catch (InterruptedException _e) {
                LOGGER.warn("Why did the semaphore get interrupted?", _e);
                throw new RuntimeException(_e);
            }
            final long _stop = System.currentTimeMillis();
            this.minimumTransportLatency = _stop - _start;

            _vertx.close();
        }
        return this.minimumTransportLatency;
    }
}
