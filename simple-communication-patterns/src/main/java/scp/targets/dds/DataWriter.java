/**
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.targets.dds;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.topic.Topic;
import com.rti.dds.type.builtin.Bytes;
import com.rti.dds.type.builtin.BytesDataWriter;
import com.rti.dds.type.builtin.BytesTypeSupport;
import scp.api.PublisherConfiguration;
import scp.util.NonNull;
import scp.util.TimestampedBox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Data writer that serves as the <i>Client Request Handler</i> for the publisher.
 * @param <T>  the type parameter
 */
final class DataWriter<T extends Serializable> implements scp.impl.spi.PublishClientRequestHandler<T> {
    private final BytesDataWriter writer;

    /**
     * Instantiates a new DataWriter.
     *
     * @param communicationManager the communication manager associated with this writer.
     * @param configuration the configuration associated with this writer.
     */
    DataWriter(
            @NonNull final CommunicationManagerImpl communicationManager,
            @NonNull final PublisherConfiguration<T> configuration) {
        assert communicationManager != null;
        assert configuration != null;

        final Topic _topic = communicationManager.createTopic(
                configuration.topic,
                BytesTypeSupport.get_type_name(), // TODO: use configuration.dataType instead
                DomainParticipant.TOPIC_QOS_DEFAULT,  // TODO: specify qos via configuration
                StatusKind.STATUS_MASK_NONE);

        this.writer = (BytesDataWriter) communicationManager.getParticipant().create_datawriter(
                _topic,
                com.rti.dds.publication.Publisher.DATAWRITER_QOS_DEFAULT,
                null,
                StatusKind.STATUS_MASK_NONE);
    }

    @Override
    public void publish(@NonNull final TimestampedBox<T> data) {
        assert data != null;

        try {
            final ByteArrayOutputStream _tmp1 = new ByteArrayOutputStream();
            final ObjectOutputStream _tmp2 = new ObjectOutputStream(_tmp1);
            _tmp2.writeObject(data);
            _tmp2.flush();
            writer.write(new Bytes(_tmp1.toByteArray()), InstanceHandle_t.HANDLE_NIL);
            _tmp2.close();
        } catch (IOException _e) {
            throw new RuntimeException(_e);
        }
    }

}
