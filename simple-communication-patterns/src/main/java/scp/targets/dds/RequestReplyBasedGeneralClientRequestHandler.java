/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.targets.dds;

import com.rti.connext.infrastructure.Sample;
import com.rti.connext.infrastructure.WriteSample;
import com.rti.connext.requestreply.Requester;
import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.type.builtin.Bytes;
import com.rti.dds.type.builtin.BytesTypeSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scp.impl.ExecutorInvoker;
import scp.impl.ReceiverInvoker.ReceiverInvokerStatus;
import scp.impl.ResponderInvoker.ResponderInvokerStatus;
import scp.impl.spi.InitiateClientRequestHandler;
import scp.impl.spi.RequestClientRequestHandler;
import scp.impl.spi.SendClientRequestHandler;
import scp.util.NonNegative;
import scp.util.NonNull;
import scp.util.Pair;
import scp.util.TimestampedBox;

import java.io.*;

/**
 * Generic client request handler based on request-reply API provided by RTI Connext DDS.  This generic handler is used
 * to enable requester-response, send-receive, and initiate-execute communication patterns.
 *
 * @param <I>  the type of request.
 * @param <O>  the type of reply.
 */
class RequestReplyBasedGeneralClientRequestHandler<I extends Serializable, O extends Serializable>
        implements SendClientRequestHandler<I>, RequestClientRequestHandler<O>, InitiateClientRequestHandler<I> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestReplyBasedGeneralClientRequestHandler.class);
    private final Requester<Bytes, Bytes> requester;
    private final long maximumLatency;

    /**
     * Instantiates a new RequestReplyBasedGeneralClientRequestHandler.
     *
     * @param responderIdentifier the responder identifier
     * @param participant the domain participant
     * @param maximumLatency the maximum latency to complete a request-reply instance.
     */
    public RequestReplyBasedGeneralClientRequestHandler(@NonNull String responderIdentifier,
                                                        @NonNull DomainParticipant participant,
                                                        @NonNegative long maximumLatency) {
        assert responderIdentifier != null;
        assert !responderIdentifier.isEmpty();
        assert maximumLatency > -1;

        this.maximumLatency = maximumLatency;
        this.requester = new Requester<>(
                participant, responderIdentifier, BytesTypeSupport.get_instance(), BytesTypeSupport.get_instance());
    }

    @Override
    public Pair<ResponderInvokerStatus, TimestampedBox<O>> request() {
        return handleRequest(null, "request-response");
    }

    @Override
    public ReceiverInvokerStatus send(@NonNull I data) {
        return handleRequest(data, "send-receive");
    }

    @Override
    public ExecutorInvoker.ExecutorInvokerStatus initiate(@NonNull I cmd) {
        return handleRequest(cmd, "initiate-execute");
    }

    /**
     * Handle request.
     *
     * @param <D>  the type of the request.
     * @param <R>  the type of the response.
     * @param data the data.
     * @param context a string to identify the communication pattern context of the invocation.
     * @return the response.
     */
    final <R, D extends Serializable> R handleRequest(D data, String context) {
        final WriteSample<Bytes> _tmp1 = this.requester.createRequestSample();
        if (data != null) {
            try {
                final ByteArrayOutputStream _tmp2 = new ByteArrayOutputStream();
                final ObjectOutputStream _tmp3 = new ObjectOutputStream(_tmp2);
                _tmp3.writeObject(data);
                _tmp3.flush();
                _tmp1.setData(new Bytes(_tmp2.toByteArray()));
                _tmp3.close();
            } catch (IOException _e) {
                throw new RuntimeException(_e);
            }
        }

        this.requester.sendRequest(_tmp1);

        final Sample<Bytes> _tmp2 = this.requester.createReplySample();
        final boolean _received = this.requester.receiveReply(_tmp2, Duration_t.from_millis(this.maximumLatency));

        if (_received && _tmp2.getInfo().valid_data) {
            final Bytes _bytes = _tmp2.getData();
            final ByteArrayInputStream _tmp3 = new ByteArrayInputStream(_bytes.value);
            try {
                final ObjectInputStream _tmp4 = new ObjectInputStream(_tmp3);
                final R _tmp5 = (R) _tmp4.readObject();
                _tmp4.close();
                return _tmp5;
            } catch (final ClassNotFoundException | IOException _e) {
                LOGGER.error("Unhandled exception", _e);
                throw new RuntimeException(_e);
            }
        } else {
            throw new RuntimeException(context + " failed");
        }
    }
}
