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
import com.rti.connext.requestreply.SimpleReplierListener;
import com.rti.dds.type.builtin.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scp.util.NonNull;

import java.io.*;

/**
 * Generic server request handler based on request-reply API provided by RTI Connext DDS.  This generic handler is used
 * to enable requester-response, send-receive, and initiate-execute communication patterns.
 * @param <I>  the type of request
 * @param <O>  the type of reply
 */
class RequestReplyBasedGeneralServerRequestHandler<I extends Serializable, O extends Serializable>
        implements SimpleReplierListener<Bytes, Bytes> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestReplyBasedGeneralServerRequestHandler.class);
    private final Invoker<I, O> invoker;

    /**
     * Instantiates a new RequestReplyBasedGeneralServerRequestHandler.
     *
     * @param invoker the invoker.
     */
    public RequestReplyBasedGeneralServerRequestHandler(@NonNull Invoker<I, O> invoker) {
        assert invoker != null;

        this.invoker = invoker;
    }

    @Override
    public Bytes onRequestAvailable(Sample<Bytes> request) {
        final I _data;
        final Bytes _bytes = request.getData();
        if (_bytes.length == 0) {
            _data = null;
        } else {
            final ByteArrayInputStream _tmp3 = new ByteArrayInputStream(_bytes.value);
            try {
                final ObjectInputStream _tmp4 = new ObjectInputStream(_tmp3);
                _data = (I) _tmp4.readObject();
                _tmp4.close();
            } catch (final ClassNotFoundException | IOException _e) {
                LOGGER.error("Unhandled exception", _e);
                throw new RuntimeException(_e);
            }
        }

        final O _ret = this.invoker.invoke(_data);

        try {
            final ByteArrayOutputStream _tmp1 = new ByteArrayOutputStream();
            final ObjectOutputStream _tmp2 = new ObjectOutputStream(_tmp1);
            _tmp2.writeObject(_ret);
            _tmp2.flush();
            return new Bytes(_tmp1.toByteArray());
        } catch (IOException _e) {
            LOGGER.error("Unhandled exception", _e);
            throw new RuntimeException(_e);
        }
    }

    @Override
    public void returnLoan(Bytes bytes) {

    }

    /**
     * Adapter interface to connect the invoker to <code>RequestReplyBasedGeneralServerRequestHandler</code>.
     * @param <I>  the type of input.
     * @param <O>  the type of output.
     */
    static interface Invoker<I extends Serializable, O extends Serializable> {
        /**
         * Invoke the invoker.
         *
         * @param input the input.
         * @return the output.
         */
        O invoke(I input);
    }
}
