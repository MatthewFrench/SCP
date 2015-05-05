/**
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.api;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import scp.api.Subscriber.AbstractSubscriber;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import static scp.api.PublisherTest.createPublisher;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class SubscriberTest extends BaseTest {
    protected CommunicationManager communicationManager;

    @Before
    public void setUp() throws Exception {
        final TestCustomizer _customizer = this.getTestCustomizer();

        this.communicationManager = _customizer.getCommunicationManager();
        assertNotNull(this.communicationManager);
        this.communicationManager.setUp();
    }

    @After
    public void tearDown() throws Exception {
        this.communicationManager.tearDown();
    }

    @Test(timeout = 5000)
    public void testSuccessfulConsumption() throws Exception {
        assumeTrue("CommunicationManager was null", this.communicationManager != null);

        final String _topic = "testSuccessfulConsumption";
        final long _minimumRemainingLifetime = 300;
        final BasicSubscriber _subscriber= new BasicSubscriber(_minimumRemainingLifetime);
        registerSubscriber(_topic, 1000, 2000, 400, _minimumRemainingLifetime, 1, _subscriber);

        final int _value = 10;
        final Publisher<Integer> _publisher =
                createPublisher(_topic, 1000, 500, 800, Integer.class, this.communicationManager);
        final Publisher.PublicationStatus _status = _publisher.publish(_value);
        assertEquals(Publisher.PublicationStatus.PUBLISHED, _status);
        _subscriber.sem.acquire();
        assertEquals(1, _subscriber.values.size());
        assertEquals(_value, (int) _subscriber.values.get(0));
    }
/*
*/
    @Test(timeout = 5000)
    public void testSlowConsumption() throws Exception {
        assumeTrue("CommunicationManager was null", this.communicationManager != null);

        final String _topic = "testSlowConsumption";
        final long _minimumRemainingLifetime = 100;
        final BasicSubscriber _subscriber = new BasicSubscriber(_minimumRemainingLifetime) {
            @Override
            public void consume(Integer message, long remainingLifetime) {
                assertTrue(remainingLifetime > this.minimumRemainingLifetime);
                try {
                    Thread.sleep(800);
                } catch (InterruptedException _e) {
                    System.err.println(_e);
                }
            }
        };
        registerSubscriber(_topic, 1000, 2000, 100, _minimumRemainingLifetime, 2, _subscriber);

        final Publisher<Integer> _publisher =
                createPublisher(_topic, 1000, 100, 5000, Integer.class, this.communicationManager);
        for (int i = 0; i < 4; i++) {
            final Publisher.PublicationStatus _status = _publisher.publish(10 + i);
            assert(_status == Publisher.PublicationStatus.PUBLISHED);
            Thread.sleep(1000);
        }
        _subscriber.sem.acquire();
        assertTrue(_subscriber.numOfUnconsumedConsecutiveMessages >= 3);
    }

    @Test(timeout = 10000)
    public void testStaleMessage() throws Exception {
        assumeTrue("CommunicationManager was null", this.communicationManager != null);

        final String _topic = "testHandleStaleMessage";
        final long _minimumRemainingLifetime = 100;
        final BasicSubscriber _subscriber = new BasicSubscriber(_minimumRemainingLifetime);
        registerSubscriber(_topic, 1000, 3000, 100, _minimumRemainingLifetime, 1, _subscriber);

        final int _value = 10;
        final Publisher<Integer> _publisher =
                createPublisher(_topic, 1000, 55, 56, Integer.class, this.communicationManager);
        final Publisher.PublicationStatus _status = _publisher.publish(_value);
        assertEquals(Publisher.PublicationStatus.PUBLISHED, _status);
        _subscriber.sem.acquire();
        assertTrue(_subscriber.staleMessage);
        assertEquals(_value, (int) _subscriber.values.get(0));
    }

    @Test(timeout = 10000)
    public void testSlowPublication() throws Exception {
        assumeTrue("CommunicationManager was null", this.communicationManager != null);

        final String _topic = "testHandleSlowPublication";
        final long _minimumRemainingLifetime = 100;
        final BasicSubscriber _subscriber = new BasicSubscriber(_minimumRemainingLifetime);
        registerSubscriber(_topic, 250, 1000, 100, _minimumRemainingLifetime, 1, _subscriber);

        final int _value = 10;
        final Publisher<Integer> _publisher =
                createPublisher(_topic, 500, 100, 500, Integer.class, this.communicationManager);
        final Publisher.PublicationStatus _status1 = _publisher.publish(_value);
        assertEquals(Publisher.PublicationStatus.PUBLISHED, _status1);
        _subscriber.sem.acquire();  // ack for the processing the message
        _subscriber.sem.acquire();  // ack for slow publication notification
        assertTrue(_subscriber.slowPublication);
        assertEquals(_value, (int) _subscriber.values.get(0));
    }

    private <T extends Serializable> void registerSubscriber(
            String topic,
            long minimumSeparation,
            long maximumSeparation,
            long maximumLatency,
            long minimumRemainingLifetime,
            int consumptionTolerance,
            Subscriber<T> subscriber) {
        final SubscriberConfiguration _config1 = new SubscriberConfiguration<>(
                topic, minimumSeparation, maximumSeparation, maximumLatency, minimumRemainingLifetime,
                consumptionTolerance, subscriber);
        final CommunicationManager.Status _ret1 = this.communicationManager.registerSubscriber(_config1);
        assumeTrue("Could not register subscriber", _ret1 == CommunicationManager.Status.SUCCESS);
    }

    private class BasicSubscriber extends AbstractSubscriber<Integer> {
        final Semaphore sem = new Semaphore(0);
        final long minimumRemainingLifetime;
        final List<Integer> values = new ArrayList<>();
        int numOfUnconsumedConsecutiveMessages = -1;
        boolean slowPublication = false;
        boolean staleMessage = false;

        BasicSubscriber(long minimumRemainingLifetime) {
            this.minimumRemainingLifetime = minimumRemainingLifetime;
        }

        @Override
        public void consume(Integer message, long remainingLifetime) {
            assertTrue(remainingLifetime > this.minimumRemainingLifetime);
            this.values.add(message);
            this.sem.release();
        }

        @Override
        public void handleSlowPublication() {
            this.slowPublication = true;
            this.sem.release();
        }

        @Override
        public void handleStaleMessage(Integer data, long remainingLifetime) {
            this.values.add(data);
            this.staleMessage = true;
            this.sem.release();
        }

        @Override
        public void handleSlowConsumption(int numOfUnconsumedConsecutiveMessages) {
            this.numOfUnconsumedConsecutiveMessages = numOfUnconsumedConsecutiveMessages;
            this.sem.release();
        }
    }
}
