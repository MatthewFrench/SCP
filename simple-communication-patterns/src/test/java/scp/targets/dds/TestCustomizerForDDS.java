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
import com.rti.connext.requestreply.SimpleReplier;
import com.rti.connext.requestreply.SimpleReplierListener;
import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.topic.Topic;
import com.rti.dds.type.builtin.StringDataWriter;
import com.rti.dds.type.builtin.StringTypeSupport;
import scp.api.CommunicationManager;
import scp.api.TestCustomizer;

class TestCustomizerForDDS extends TestCustomizer {
    private final CommunicationManagerImpl communicationManager = new CommunicationManagerImpl(0);
    private long minimumInteractionLatency = -1;
    private long minimumTransportLatency = -1;

    @Override
    protected CommunicationManager getCommunicationManager() {
        return this.communicationManager;
    }

    @Override
    protected long getMinimumInteractionLatency() {
        if (this.minimumInteractionLatency == -1) {
            final DomainParticipant _tmp1 = this.communicationManager.getParticipant();
            final Topic _topic = _tmp1.create_topic(
                    "getMinimumInteractionLatency",
                    StringTypeSupport.get_type_name(),
                    DomainParticipant.TOPIC_QOS_DEFAULT,
                    null,
                    StatusKind.STATUS_MASK_NONE);

            final StringDataWriter _writer = (StringDataWriter) _tmp1.create_datawriter(
                    _topic,
                    com.rti.dds.publication.Publisher.DATAWRITER_QOS_DEFAULT,
                    null,
                    StatusKind.STATUS_MASK_NONE);

            final long _start = System.currentTimeMillis();
            _writer.write("Test Request", InstanceHandle_t.HANDLE_NIL);
            final long _stop = System.currentTimeMillis();
            this.minimumInteractionLatency = _stop - _start;
        }
        return this.minimumInteractionLatency;
    }

    @Override
    protected long getMinimumTransportLatency() {
        if (this.minimumTransportLatency == -1) {
            final Requester<String, String> _requester = new Requester<>(
                    this.communicationManager.getParticipant(),
                    "getMinimumTransportLatency",
                    StringTypeSupport.get_instance(),
                    StringTypeSupport.get_instance());

            final SimpleReplier<String, String> _replier = new SimpleReplier<>(
                    this.communicationManager.getParticipant(),
                    "getMinimumTransportLatency",
                    new SimpleReplierListener<String, String>() {
                        @Override
                        public String onRequestAvailable(Sample<String> sample) {
                            return "Test Reply";
                        }

                        @Override
                        public void returnLoan(String o) { }
                    },
                    StringTypeSupport.get_instance(),
                    StringTypeSupport.get_instance());
            final String data = "Test Request";
            final WriteSample<String> _tmp1 = _requester.createRequestSample();
            final Sample<String> _tmp2 = _requester.createReplySample();
            _tmp1.setData(data);

            final long _start = System.currentTimeMillis();
            _requester.sendRequest(_tmp1);
            _requester.receiveReply(_tmp2, Duration_t.from_seconds(10));
            final long _stop = System.currentTimeMillis();
            _requester.close();
            _replier.close();
            this.minimumTransportLatency = _stop - _start;
        }
        return this.minimumTransportLatency;
    }
}
