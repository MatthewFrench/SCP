/*
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
import scp.util.NonNull;
import scp.util.Pair;

import java.text.MessageFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

public abstract class SenderReceiverTest extends BaseTest {
    /*
     * NOTE: We do not test for LOCAL_UNKNOWN_FAILURE.
     */

    protected CommunicationManager communicationManager;
    protected long minimumInteractionAndTransportLatency;

    @Before
    public void setUp() throws Exception {
        final TestCustomizer _customizer = this.getTestCustomizer();

        this.communicationManager = _customizer.getCommunicationManager();
        assertNotNull(this.communicationManager);
        this.communicationManager.setUp();

        this.minimumInteractionAndTransportLatency = _customizer.getMinimumInteractionAndTransportLatency();
    }

    @After
    public void tearDown() throws Exception {
        this.communicationManager.tearDown();
    }

    @Test
    public void testDataAccepted() {
        assumeTrue("Communication manager is null", this.communicationManager != null);

        final BasicReceiver _tmp1 = new BasicReceiver();
        final String _receiverId = "testDataAccepted";
        registerReceiver(_receiverId, 1000, 100, _tmp1);
        final Sender<Integer> _sender = registerSender(_receiverId, 1000, 400);
        final Sender.SendStatus _status = _sender.send(10);
        assertEquals(Sender.SendStatus.DATA_ACCEPTED, _status);
        assertEquals(10, _tmp1.receivedValue);
    }

    @Test
    public void testDataRejected() {
        assumeTrue("Communication manager is null", this.communicationManager != null);

        final String _receiverId = "testDataRejected";
        registerReceiver(_receiverId, 1000, 100, new Receiver<Integer>() {
                    @Override
                    public ReceptionAcknowledgement receive(@NonNull Integer message) {
                        return ReceptionAcknowledgement.DATA_REJECTED;
                    }
                });
        final Sender<Integer> _sender = registerSender(_receiverId, 1000, 400);
        final Sender.SendStatus _status = _sender.send(10);
        assertEquals(Sender.SendStatus.DATA_REJECTED, _status);
    }

    @Test
    public void testExcessLoad() {
        assumeTrue("Communication manager is null", this.communicationManager != null);

        final String _receiverId = "testExcessLoad";
        registerReceiver(_receiverId, 1000, 100, new BasicReceiver());
        final Sender<Integer> _sender1 = registerSender(_receiverId, 1000, 400);
        final Sender<Integer> _sender2 = registerSender(_receiverId, 1000, 400);
        final Sender.SendStatus _status1 = _sender1.send(10);
        assertEquals(Sender.SendStatus.DATA_ACCEPTED, _status1);
        final Sender.SendStatus _status2 = _sender2.send(10);
        assertEquals(Sender.SendStatus.EXCESS_LOAD, _status2);
    }

    @Test
    public void testFastSend() {
        assumeTrue("Communication manager is null", this.communicationManager != null);

        final String _receiverId = "testFastSend";
        registerReceiver(_receiverId, 1000, 100, new BasicReceiver());
        final Sender<Integer> _sender = registerSender("testFastRequest", 1000, 400);
        _sender.send(10);
        final Sender.SendStatus _status = _sender.send(10);
        assertEquals(Sender.SendStatus.FAST_SEND_DROPPED, _status);
    }

    @Test
    public void testLocalTimeOut() {
        assumeTrue("Communication manager is null", this.communicationManager != null);
        final long _tmp1 = 1;
        final long _tmp2 = this.minimumInteractionAndTransportLatency - _tmp1;
        assumeTrue(MessageFormat.format("Platform is too fast ({0}ms) to test timeout.", _tmp2), _tmp2 > 0);

        final String _receiverId = "testLocalTimeOut";
        registerReceiver(_receiverId, 1000, 100, new BasicReceiver());
        final Sender<Integer> _sender = registerSender(_receiverId, 1000, _tmp1);
        final Sender.SendStatus _status = _sender.send(10);
        assertEquals(Sender.SendStatus.LOCAL_TIME_OUT, _status);
    }

    @Test
    public void testRemoteTimeOut() {
        assumeTrue("Communication manager is null", this.communicationManager != null);

        final String _receiverId = "testRemoteTimeOut";
        registerReceiver(_receiverId, 1000, 100, new Receiver<Integer>() {
            @Override
            public ReceptionAcknowledgement receive(@NonNull Integer message) {
                try {
                    Thread.sleep(800);
                    return ReceptionAcknowledgement.DATA_ACCEPTED;
                } catch (InterruptedException _e) {
                    throw new RuntimeException(_e);
                }
            }
        });
        final Sender<Integer> _sender = registerSender(_receiverId, 1000, 400);
        final Sender.SendStatus _status = _sender.send(10);
        assertEquals(Sender.SendStatus.REMOTE_TIME_OUT, _status);
    }

    @Test
    public void testRemoteUnknownFailure() {
        assumeTrue("Communication manager is null", this.communicationManager != null);

        final String _receiverId = "testRemoteUnknownFailure";
        registerReceiver(_receiverId, 1000, 100, new Receiver<Integer>() {
            @Override
            public ReceptionAcknowledgement receive(@NonNull Integer message) {
                throw new RuntimeException("Don't Panic! This exception is good :)");
            }
        });
        final Sender<Integer> _sender = registerSender(_receiverId, 1000, 400);
        final Sender.SendStatus _status = _sender.send(10);
        assertEquals(Sender.SendStatus.REMOTE_UNKNOWN_FAILURE, _status);
    }

    private void registerReceiver(
            String identifier,
            long minimumSeparation,
            long maximumLatency,
            Receiver<Integer> receiver) {
        final ReceiverConfiguration<Integer> _config = new ReceiverConfiguration<>(
                identifier, minimumSeparation, maximumLatency, receiver);
        final CommunicationManager.Status _status = this.communicationManager.registerReceiver(_config);
        assertEquals(CommunicationManager.Status.SUCCESS, _status);
    }

    private Sender<Integer> registerSender(
            String identifier,
            long minimumSeparation,
            long maximumLatency) {
        final SenderConfiguration<Integer> _config = new SenderConfiguration<>(
                identifier, minimumSeparation, maximumLatency, Integer.class);
        final Pair<CommunicationManager.Status, Sender<Integer>> _ret =
                this.communicationManager.createSender(_config);
        assertEquals(CommunicationManager.Status.SUCCESS, _ret.first);
        assertNotNull(_ret.second);
        return _ret.second;
    }

    private static class BasicReceiver implements Receiver<Integer> {
        int receivedValue;
        @Override
        public ReceptionAcknowledgement receive(@NonNull Integer message) {
            this.receivedValue = message;
            return ReceptionAcknowledgement.DATA_ACCEPTED;
        }
    }
}
