/**
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.targets.dds;

import com.rti.dds.subscription.DataReaderAdapter;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.type.builtin.Bytes;
import com.rti.dds.type.builtin.BytesDataReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scp.impl.SubscriberInvoker;
import scp.util.NonNull;
import scp.util.TimestampedBox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Data reader that serves as the <i>Server Request Handler</i> for the subscriber.
 *
 * @param <T>  the type of subscribed data.
 */
final class DataReader<T extends Serializable> extends DataReaderAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataReader.class);
    private final SubscriberInvoker<T> subscriberInvoker;

    /**
     * Instantiates a new DataReader.
     *
     * @param subscriberInvoker the handler to handle new messages.
     */
    DataReader(@NonNull SubscriberInvoker<T> subscriberInvoker) {
        assert subscriberInvoker != null;

        this.subscriberInvoker = subscriberInvoker;
    }

    @Override
    public void on_data_available(final com.rti.dds.subscription.DataReader dataReader) {
        super.on_data_available(dataReader);

        final BytesDataReader _reader = (BytesDataReader) dataReader;
        final Bytes _bytes = new Bytes();
        SampleInfo _tmp1 = new SampleInfo();
        _reader.take_next_sample(_bytes, _tmp1);

        if (_tmp1.valid_data) {
            try {
                final ByteArrayInputStream _tmp2 = new ByteArrayInputStream(_bytes.value);
                final ObjectInputStream _tmp3 = new ObjectInputStream(_tmp2);
                final TimestampedBox<T> _tmp4 = (TimestampedBox<T>) _tmp3.readObject();
                _tmp3.close();
                subscriberInvoker.processData(_tmp4);
            } catch (final ClassNotFoundException | IOException _e) {
                LOGGER.error("Unhandled exception", _e);
                throw new RuntimeException(_e);
            }
        } else {
            LOGGER.error("Middleware missed a message");
        }
    }

}
