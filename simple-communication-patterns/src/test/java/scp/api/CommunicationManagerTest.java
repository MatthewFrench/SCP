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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public abstract class CommunicationManagerTest extends BaseTest {

    protected CommunicationManager communicationManager;

    @Before
    public final void setUp() throws Exception {
        final TestCustomizer _customizer = this.getTestCustomizer();
        this.communicationManager = _customizer.getCommunicationManager();
        assertNotNull(this.communicationManager);
        this.communicationManager.setUp();
    }

    @After
    public final void tearDown() throws Exception {
        communicationManager.tearDown();
    }

    @Test
    public void testCreatePublisherSuccess() throws Exception {
        final PublisherConfiguration<Integer> _config = new PublisherConfiguration<>(
                "testCreatePublisherSuccess", 1000, 100, 600, Integer.class);
        final Pair<CommunicationManager.Status, Publisher<Integer>> _ret =
                this.communicationManager.createPublisher(_config);
        assertEquals(CommunicationManager.Status.SUCCESS, _ret.first);
        assertNotNull(_ret.second);
    }

    @Test
    public void testRegisterSubscriberSuccess() throws Exception {
        final SubscriberConfiguration _config = new SubscriberConfiguration<>(
                "testRegisterSubscriberSuccess", 333, 1000, 100, 300, 2, SubscriberConfigurationTest.DUMMY_SUBSCRIBER);
        final CommunicationManager.Status _ret = this.communicationManager.registerSubscriber(_config);
        assertEquals(CommunicationManager.Status.SUCCESS, _ret);
    }

    @Test
    public void testCreateRequesterSuccess() throws Exception {
        final RequesterConfiguration<Integer> _config = new RequesterConfiguration<>(
                "testCreateRequesterSuccess", 1000, 100, 200, Integer.class);
        final Pair<CommunicationManager.Status, Requester<Integer>> _ret =
                this.communicationManager.createRequester(_config);
        assertEquals(CommunicationManager.Status.SUCCESS, _ret.first);
        assertNotNull(_ret.second);
    }

    @Test
    public void testRegisterResponderSuccess() throws Exception {
        final ResponderConfiguration _config = new ResponderConfiguration<>(
                "testRegisterResponderSuccess", 1000, 100, 200, ResponderConfigurationTest.DUMMY_RESPONDER);
        final CommunicationManager.Status _ret = this.communicationManager.registerResponder(_config);
        assertEquals(CommunicationManager.Status.SUCCESS, _ret);
    }

    @Test
    public void testCreateSenderSuccess() throws Exception {
        final SenderConfiguration<Integer> _config = new SenderConfiguration<>(
                "testCreateSenderSuccess", 1000, 100, Integer.class);
        final Pair<CommunicationManager.Status, Sender<Integer>> _ret =
                this.communicationManager.createSender(_config);
        assertEquals(CommunicationManager.Status.SUCCESS, _ret.first);
        assertNotNull(_ret.second);
    }

    @Test
    public void testRegisterReceiverSuccess() throws Exception {
        final ReceiverConfiguration<Integer> _config = new ReceiverConfiguration<>(
                "testRegisterReceiverSuccess", 1000, 100, ReceiverConfigurationTest.DUMMY_RECEIVER);
        final CommunicationManager.Status _ret = this.communicationManager.registerReceiver(_config);
        assertEquals(CommunicationManager.Status.SUCCESS, _ret);
    }

    @Test
    public void testCreateInitiatorSuccess() throws Exception {
        final InitiatorConfiguration<Integer> _config = new InitiatorConfiguration<>(
                "testCreateInitiatorSuccess", 1000, 100, Integer.class);
        final Pair<CommunicationManager.Status, Initiator<Integer>> _ret =
                this.communicationManager.createInitiator(_config);
        assertEquals(CommunicationManager.Status.SUCCESS, _ret.first);
        assertNotNull(_ret.second);
    }

    @Test
    public void testRegisterExecutorSuccess() throws Exception {
        final ExecutorConfiguration<Integer> _config = new ExecutorConfiguration<>(
                "testRegisterExecutorSuccess", 1000, 100, ExecutorConfigurationTest.DUMMY_EXECUTOR);
        final CommunicationManager.Status _ret = this.communicationManager.registerExecutor(_config);
        assertEquals(CommunicationManager.Status.SUCCESS, _ret);
    }
}