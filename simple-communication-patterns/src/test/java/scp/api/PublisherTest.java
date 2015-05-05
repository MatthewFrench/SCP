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
import org.junit.Test;
import scp.util.Pair;

import java.io.Serializable;
import java.text.MessageFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

public abstract class PublisherTest extends BaseTest {
    protected CommunicationManager communicationManager;
    protected long minimumInteractionLatency;

    @Before
    public void setUp() throws Exception {
        final TestCustomizer _customizer = this.getTestCustomizer();

        this.communicationManager = _customizer.getCommunicationManager();
        assertNotNull(this.communicationManager);
        this.communicationManager.setUp();

        this.minimumInteractionLatency = _customizer.getMinimumInteractionLatency();
    }

    @After
    public void tearDown() throws Exception {
        this.communicationManager.tearDown();
    }

    @Test(timeout = 2000)
    public void testFastPublication() throws Exception {
        assumeTrue("CommunicationManager was null.", this.communicationManager != null);

        final Publisher<Integer> _publisher =
                createPublisher("testFastPublication", 10000, this.minimumInteractionLatency + 50, 600,
                        Integer.class, this.communicationManager);
        _publisher.publish(10);
        final Publisher.PublicationStatus _status = _publisher.publish(20);
        assertEquals(Publisher.PublicationStatus.FAST_PUBLICATION_DROPPED, _status);
    }

    @Test(timeout = 2000)
    public void testSuccessfulPublication() throws Exception {
        assumeTrue("CommunicationManager was null.", this.communicationManager != null);

        final Publisher<Integer> _publisher =
                createPublisher("testSuccessfulPublication", 1000, this.minimumInteractionLatency + 50, 600,
                        Integer.class, this.communicationManager);
        final Publisher.PublicationStatus _status = _publisher.publish(10);
        assertEquals(Publisher.PublicationStatus.PUBLISHED, _status);
    }

    @Test(timeout = 2000)
    public void testPublicationTimeOut() throws Exception  {
        final long _tmp1 = 1;
        final long _tmp2 = this.minimumInteractionLatency - _tmp1;
        assumeTrue("CommunicationManager was null.", this.communicationManager != null);
        assumeTrue(MessageFormat.format("Platform is too fast ({0}ms) to test timeout.", _tmp2), _tmp2 > 0);

        final Publisher<Integer> _publisher =
                createPublisher("testPublicationTimedOut", 1000, _tmp1, 600,
                        Integer.class, this.communicationManager);
        final Publisher.PublicationStatus _status = _publisher.publish(10);
        assertEquals(Publisher.PublicationStatus.TIME_OUT, _status);
    }

    static <T extends Serializable> Publisher<T> createPublisher(
            String topic,
            long minimumSeparation,
            long maximumLatency,
            long minimumRemainingLifetime,
            Class<T> dataType,
            CommunicationManager communicationManager) {
        final PublisherConfiguration<T> _config = new PublisherConfiguration<>(
                topic, minimumSeparation, maximumLatency, minimumRemainingLifetime, dataType);
        final Pair<CommunicationManager.Status, Publisher<T>> _ret1 =
                communicationManager.createPublisher(_config);
        assumeTrue("Could not register publisher", _ret1.first == CommunicationManager.Status.SUCCESS);
        return _ret1.second;
    }
}
