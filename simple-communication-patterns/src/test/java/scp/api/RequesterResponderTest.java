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
import scp.util.Pair;

import java.io.Serializable;
import java.text.MessageFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

public abstract class RequesterResponderTest extends BaseTest {
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
    public void testExcessLoad() throws Exception {
        assumeTrue("Communication manager is null", this.communicationManager != null);

        final String _responderId = "testExcessLoad";
        registerResponder(_responderId, 1000, 100, 600, new BasicResponder());

        final Requester<Integer> _requester1 = getRequester(_responderId, 1000, 600, 400);
        final Requester<Integer> _requester2 = getRequester(_responderId, 1000, 600, 400);
        final Pair<Requester.RequestStatus, Integer> _ret1 = _requester1.request();
        final Pair<Requester.RequestStatus, Integer> _ret2 = _requester2.request();
        assertEquals(Requester.RequestStatus.SUCCEEDED, _ret1.first);
        assertEquals(Integer.valueOf(10), _ret1.second);
        assertEquals(Requester.RequestStatus.EXCESS_LOAD, _ret2.first);
        assertEquals(null, _ret2.second);
    }

    @Test
    public void testFastRequest() throws Exception {
        assumeTrue("Communication manager is null", this.communicationManager != null);

        final String _responderId = "testFastRequest";
        registerResponder(_responderId, 1000, 100, 400, new BasicResponder());
        final Requester<Integer> _requester = getRequester(_responderId, 1000, 600, 400);
        _requester.request();
        final Pair<Requester.RequestStatus, Integer> _ret = _requester.request();
        assertEquals(Requester.RequestStatus.FAST_REQUEST_DROPPED, _ret.first);
        assertEquals(null, _ret.second);
    }

    @Test
    public void testStaleData() throws Exception {
        assumeTrue("Communication manager is null", this.communicationManager != null);

        registerResponder("testStaleData", 1000, 100, 55, new BasicResponder());
        final Requester<Integer> _requester = getRequester("testStaleData", 1000, 600, 400);
        final Pair<Requester.RequestStatus, Integer> _ret = _requester.request();
        assertEquals(Requester.RequestStatus.STALE_DATA, _ret.first);
        assertEquals(Integer.valueOf(10), _ret.second);
    }

    @Test
    public void testSuccessfulRequest() throws Exception {
        assumeTrue("Communication manager is null", this.communicationManager != null);

        final String _responderId = "testSuccessfulRequest";
        registerResponder(_responderId, 1000, 100, 600, new BasicResponder());
        final Requester<Integer> _requester = getRequester(_responderId, 1000, 600, 300);
        final Pair<Requester.RequestStatus, Integer> _ret = _requester.request();
        assertEquals(Requester.RequestStatus.SUCCEEDED, _ret.first);
        assertEquals(Integer.valueOf(10), _ret.second);
    }

    @Test
    public void testDataUnavailable() throws Exception {
        assumeTrue("Communication manager is null", this.communicationManager != null);

        final String _responderId = "testDataUnavailable";
        registerResponder(_responderId, 1000, 100, 400, new Responder<Integer>() {
            @Override
            public Pair<ResponseStatus, Integer> respond() {
                return new Pair<>(ResponseStatus.DATA_UNAVAILABLE, null);
            }
        });
        final Requester<Integer> _requester = getRequester(_responderId, 1000, 600, 400);
        final Pair<Requester.RequestStatus, Integer> _ret = _requester.request();
        assertEquals(Requester.RequestStatus.DATA_UNAVAILABLE, _ret.first);
        assertEquals(null, _ret.second);
    }

    @Test
    public void testLocalTimeOut() throws Exception {
        assumeTrue("Communication manager is null", this.communicationManager != null);
        final long _tmp1 = 1;
        final long _tmp2 = this.minimumInteractionAndTransportLatency - _tmp1;
        assumeTrue(MessageFormat.format("Platform is too fast ({0}ms) to test timeout.", _tmp2), _tmp2 > 0);

        final String _responderId = "testLocalTimeOut";
        registerResponder(_responderId, 1000, 100, 800, new BasicResponder());
        final Requester<Integer> _requester = getRequester(_responderId, 1000, _tmp1, 400);
        final Pair<Requester.RequestStatus, Integer> _ret2 = _requester.request();
        assertEquals(Requester.RequestStatus.LOCAL_TIME_OUT, _ret2.first);
        assertEquals(null, _ret2.second);
    }

    @Test
    public void testRemoteTimeOut() throws Exception {
        assumeTrue("Communication manager is null", this.communicationManager != null);

        final String _responderId = "testRemoteTimeOut";
        registerResponder(_responderId, 1000, 10, 400, new Responder<Integer>() {
                    @Override
                    public Pair<ResponseStatus, Integer> respond() {
                        try {
                            Thread.sleep(300);
                            return new Pair<>(ResponseStatus.RESPONSE_PROVIDED, 10);
                        } catch (InterruptedException _e) {
                            throw new RuntimeException(_e);
                        }
                    }
                });
        final Requester<Integer> _requester = getRequester(_responderId, 1000, 600, 400);
        final Pair<Requester.RequestStatus, Integer> _ret2 = _requester.request();
        assertEquals(Requester.RequestStatus.REMOTE_TIME_OUT, _ret2.first);
        assertEquals(null, _ret2.second);
    }

    @Test
    public void testRemoteUnknownFailure() throws Exception {
        // NOTE: This also tests ResponseStatus.UNKNOWN_FAILURE
        assumeTrue("Communication manager is null", this.communicationManager != null);

        final String _responderId = "testRemoteUnknownFailure";
        registerResponder(_responderId, 1000, 100, 400, new Responder<Integer>() {
                    @Override
                    public Pair<ResponseStatus, Integer> respond() {
                        throw new RuntimeException("Don't Panic! This exception is good :)");
                    }
                });
        final Requester<Integer> _requester = getRequester(_responderId, 1000, 600, 400);
        final Pair<Requester.RequestStatus, Integer> _ret2 = _requester.request();
        assertEquals(Requester.RequestStatus.REMOTE_UNKNOWN_FAILURE, _ret2.first);
        assertEquals(null, _ret2.second);
    }

    private Requester<Integer> getRequester(
            String identifier,
            long minimumSeparation,
            long maximumLatency,
            long minimumRemainingLifetime) {
        final RequesterConfiguration<Integer> _config1 = new RequesterConfiguration<>(
                identifier, minimumSeparation, maximumLatency, minimumRemainingLifetime, Integer.class);

        final Pair<CommunicationManager.Status, Requester<Integer>> _ret1 =
                this.communicationManager.createRequester(_config1);
        assumeTrue("Could not create requester", _ret1.first == CommunicationManager.Status.SUCCESS);
        return _ret1.second;
    }

    private <T extends Serializable> void registerResponder(
            String identifier,
            long minimumSeparation,
            long maximumLatency,
            long minimumRemainingLifetime,
            Responder<T> responder) {
        final ResponderConfiguration<T> _config1 = new ResponderConfiguration<>(
                identifier, minimumSeparation, maximumLatency, minimumRemainingLifetime, responder);

        final CommunicationManager.Status _ret = this.communicationManager.registerResponder(_config1);
        assumeTrue("Could not register responder", _ret == CommunicationManager.Status.SUCCESS);
    }

    private static class BasicResponder implements Responder<Integer> {
        @Override
        public Pair<ResponseStatus, Integer> respond() {
            return new Pair<>(ResponseStatus.RESPONSE_PROVIDED, 10);
        }
    }
}
