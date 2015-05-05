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

public abstract class InitiatorExecutorTest extends BaseTest {
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
    public void testActionSucceeded() {
        assumeTrue("Communication manager is null", this.communicationManager != null);

        final String _executorId = "testActionSucceeded";
        final BasicExecutor _tmp1 = new BasicExecutor();
        registerExecutor(_executorId, 1000, 100, _tmp1);
        final Initiator<Integer> _initiator = registerInitiator(_executorId, 1000, 600);
        final Initiator.InitiationStatus _status = _initiator.initiate(10);
        assertEquals(Initiator.InitiationStatus.ACTION_SUCCEEDED, _status);
        assertEquals(10, _tmp1.executedValue);
    }

    @Test
    public void testActionFailed() {
        assumeTrue("Communication manager is null", this.communicationManager != null);

        final String _executorId = "testActionFailed";
        registerExecutor(_executorId, 1000, 100, new Executor<Integer>() {
            @Override
            public ExecutionAcknowledgement execute(@NonNull Integer message) {
                return ExecutionAcknowledgement.ACTION_FAILED;
            }
        });
        final Initiator<Integer> _initiator = registerInitiator(_executorId, 1000, 600);
        final Initiator.InitiationStatus _status = _initiator.initiate(10);
        assertEquals(Initiator.InitiationStatus.ACTION_FAILED, _status);
    }

    @Test
    public void testActionUnavailable() {
        assumeTrue("Communication manager is null", this.communicationManager != null);

        final String _executorId = "testActionUnavailable";
        registerExecutor(_executorId, 1000, 100, new Executor<Integer>() {
            @Override
            public ExecutionAcknowledgement execute(@NonNull Integer message) {
                return ExecutionAcknowledgement.ACTION_UNAVAILABLE;
            }
        });
        final Initiator<Integer> _initiator = registerInitiator(_executorId, 1000, 600);
        final Initiator.InitiationStatus _status = _initiator.initiate(10);
        assertEquals(Initiator.InitiationStatus.ACTION_UNAVAILABLE, _status);
    }

    @Test
    public void testExcessLoad() {
        assumeTrue("Communication manager is null", this.communicationManager != null);

        final String _executorId = "testExcessLoad";
        registerExecutor(_executorId, 1000, 100, new BasicExecutor());
        final Initiator<Integer> _initiator1 = registerInitiator(_executorId, 1000, 600);
        final Initiator<Integer> _initiator2 = registerInitiator(_executorId, 1000, 600);
        final Initiator.InitiationStatus _status1 = _initiator1.initiate(10);
        assertEquals(Initiator.InitiationStatus.ACTION_SUCCEEDED, _status1);
        final Initiator.InitiationStatus _status2 = _initiator2.initiate(10);
        assertEquals(Initiator.InitiationStatus.EXCESS_LOAD, _status2);
    }

    @Test
    public void testFastInitiation() {
        assumeTrue("Communication manager is null", this.communicationManager != null);

        final String _executorId = "testFastInitiation";
        registerExecutor(_executorId, 1000, 100, new BasicExecutor());
        final Initiator<Integer> _initiator = registerInitiator(_executorId, 1000, 600);
        _initiator.initiate(10);
        final Initiator.InitiationStatus _status = _initiator.initiate(10);
        assertEquals(Initiator.InitiationStatus.FAST_INITIATION_DROPPED, _status);
    }

    @Test
    public void testLocalTimeOut() {
        assumeTrue("Communication manager is null", this.communicationManager != null);
        final long _tmp1 = 1;
        final long _tmp2 = this.minimumInteractionAndTransportLatency - _tmp1;
        assumeTrue(MessageFormat.format("Platform is too fast ({0}ms) to test timeout.", _tmp2), _tmp2 > 0);

        final String _executorId = "testLocalTimeOut";
        registerExecutor(_executorId, 1000, 100, new BasicExecutor());
        final Initiator<Integer> _initiator = registerInitiator(_executorId, 1000, _tmp1);
        final Initiator.InitiationStatus _status = _initiator.initiate(10);
        assertEquals(Initiator.InitiationStatus.LOCAL_TIME_OUT, _status);
    }

    @Test
    public void testRemoteTimeOut() {
        assumeTrue("Communication manager is null", this.communicationManager != null);

        final String _executorId = "testRemoteTimeOut";
        registerExecutor(_executorId, 1000, 100, new Executor<Integer>() {
            @Override
            public ExecutionAcknowledgement execute(@NonNull Integer message) {
                try {
                    Thread.sleep(800);
                    return ExecutionAcknowledgement.ACTION_SUCCEEDED;
                } catch (InterruptedException _e) {
                    throw new RuntimeException(_e);
                }
            }
        });
        final Initiator<Integer> _initiator = registerInitiator(_executorId, 1000, 400);
        final Initiator.InitiationStatus _status = _initiator.initiate(10);
        assertEquals(Initiator.InitiationStatus.REMOTE_TIME_OUT, _status);
    }

    @Test
    public void testRemoteUnknownFailure() {
        assumeTrue("Communication manager is null", this.communicationManager != null);

        registerExecutor("testRemoteUnknownFailure", 1000, 100, new Executor<Integer>() {
            @Override
            public ExecutionAcknowledgement execute(@NonNull Integer message) {
                throw new RuntimeException("Don't Panic! This exception is good :)");
            }
        });
        final Initiator<Integer> _initiator = registerInitiator("testRemoteUnknownFailure", 1000, 600);
        final Initiator.InitiationStatus _status = _initiator.initiate(10);
        assertEquals(Initiator.InitiationStatus.REMOTE_UNKNOWN_FAILURE, _status);
    }

    private void registerExecutor(
            String identifier,
            long minimumSeparation,
            long maximumLatency,
            Executor<Integer> executor) {
        final ExecutorConfiguration<Integer> _config = new ExecutorConfiguration<>(
                identifier, minimumSeparation, maximumLatency, executor);
        final CommunicationManager.Status _status = this.communicationManager.registerExecutor(_config);
        assertEquals(CommunicationManager.Status.SUCCESS, _status);
    }

    private Initiator<Integer> registerInitiator(
            String identifier,
            long minimumSeparation,
            long maximumLatency) {
        final InitiatorConfiguration<Integer> _config = new InitiatorConfiguration<>(
                identifier, minimumSeparation, maximumLatency, Integer.class);
        final Pair<CommunicationManager.Status, Initiator<Integer>> _ret =
                this.communicationManager.createInitiator(_config);
        assertEquals(CommunicationManager.Status.SUCCESS, _ret.first);
        assertNotNull(_ret.second);
        return _ret.second;
    }

    private static class BasicExecutor implements Executor<Integer> {
        int executedValue;
        @Override
        public ExecutionAcknowledgement execute(@NonNull Integer message) {
            this.executedValue = message;
            return ExecutionAcknowledgement.ACTION_SUCCEEDED;
        }
    }
}
