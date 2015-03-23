/**
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.targets.dds;

import com.rti.connext.requestreply.SimpleReplier;
import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantListener;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.topic.Topic;
import com.rti.dds.topic.TopicQos;
import com.rti.dds.type.builtin.BytesTypeSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scp.api.*;
import scp.impl.*;
import scp.impl.ExecutorInvoker.ExecutorInvokerStatus;
import scp.impl.ReceiverInvoker.ReceiverInvokerStatus;
import scp.impl.ResponderInvoker.ResponderInvokerStatus;
import scp.targets.dds.RequestReplyBasedGeneralServerRequestHandler.Invoker;
import scp.util.NonNegative;
import scp.util.NonNull;
import scp.util.Pair;
import scp.util.TimestampedBox;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A <a href="http://www.rti.com">RTI Connext DDS</a> based implementation of communication manager.
 */
public final class CommunicationManagerImpl implements CommunicationManager {
    private final static Logger LOGGER = LoggerFactory.getLogger(CommunicationManagerImpl.class);
    private final int domainId;
    private final DomainParticipantQos qos;
    private final DomainParticipantListener listener;
    private final int statusKind;

    private DomainParticipantFactory factory;
    private DomainParticipant participant;
    private final Map<String, Topic> name2topic = new HashMap<>();

    /**
     * Creates an instance of CommunicationManagerImpl.
     *
     * @param domainId the domain id as required by DomainParticipantFactory.create_participant in DDS.
     * @param qos the qos as required by DomainParticipantFactory.create_participant in DDS.
     * @param listener the listener as required by DomainParticipantFactory.create_participant in DDS.
     * @param statusKind the status kind as required by DomainParticipantFactory.create_participant in DDS.
     */
    public CommunicationManagerImpl(
            @NonNegative int domainId,
            @NonNull DomainParticipantQos qos,
            DomainParticipantListener listener,
            int statusKind) {
        assert domainId >= 0;
        assert qos != null;

        this.domainId = domainId;
        this.qos = qos;
        this.listener = listener;
        this.statusKind = statusKind;
    }

    /**
     * Instantiates a new Communication manager impl with default QoS, no listener, and no status mask.
     *
     * @param domainId the domain id
     */
    public CommunicationManagerImpl(@NonNegative int domainId) {
        this(domainId, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
    }

    /**
     * Gets the domain participant.
     *
     * @return the domain participant
     */
    DomainParticipant getParticipant() {
        return this.participant;
    }

    /**
     * Create topic.
     *
     * @param topicName the topic name.
     * @param typeSupportName the type support name of the topic as required by DDS.
     * @param qos the qos argument as required by DDS.
     * @param statusKind the status kind as required by DDS.
     * @return the topic
     */
    Topic createTopic(
            @NonNull String topicName,
            @NonNull String typeSupportName,
            @NonNull TopicQos qos,
            int statusKind) {
        assert topicName != null;
        assert typeSupportName != null;
        assert qos != null;

        if (!name2topic.containsKey(topicName)) {
            final Topic _topic = participant.create_topic(topicName, typeSupportName, qos, null, statusKind);
            name2topic.put(topicName, _topic);
        }
        return name2topic.get(topicName);
    }

    /**
     * Set up RTI Connext DDS.
     *
     * @throws FailedToInitializeException if initialization of the participant fails.
     */
    @Override
    public void setUp() throws FailedToInitializeException {
        this.factory = DomainParticipantFactory.get_instance();
        this.participant = factory.create_participant(this.domainId, this.qos, this.listener, this.statusKind);
        if (this.participant == null) {
            throw new FailedToInitializeException("Could not create domain participant with domainId " + domainId);
        }
    }

    @Override
    public void tearDown() {
        if (this.participant != null) {
            this.participant.delete_contained_entities();
            if (this.factory != null) {
                this.factory.delete_participant(this.participant);
            }
        }
    }

    @Override
    public <T extends Serializable> Pair<Status, Publisher<T>> createPublisher(
            @NonNull final PublisherConfiguration<T> configuration) {
        assert configuration != null;

        try {
            final scp.api.Publisher<T> _tmp1 = new PublishRequester<>(configuration,
                    new DataWriter<>(this, configuration));
            return new Pair<>(Status.SUCCESS, _tmp1);
        } catch (RuntimeException _e) {
            LOGGER.warn("Publisher registration failed with exception", _e);
            return new Pair<>(Status.FAILURE, null);
        }
    }

    @Override
    public <T extends Serializable> Status registerSubscriber(
            @NonNull final SubscriberConfiguration<T> configuration) {
        assert configuration != null;

        try {
            final Topic _topic = createTopic(
                    configuration.topic,
                    BytesTypeSupport.get_type_name(),
                    DomainParticipant.TOPIC_QOS_DEFAULT,
                    StatusKind.STATUS_MASK_NONE);
            final com.rti.dds.subscription.DataReader _tmp = participant.create_datareader(
                    _topic,
                    Subscriber.DATAREADER_QOS_DEFAULT,
                    new DataReader<>(new SubscriberInvoker<>(configuration)),
                    StatusKind.DATA_AVAILABLE_STATUS);
            if (_tmp != null) {
                return Status.SUCCESS;
            } else {
                LOGGER.warn("DataReader could not be created");
                return Status.FAILURE;
            }
        } catch (RuntimeException _e) {
            LOGGER.warn("Subscription registration failed with exception", _e);
            return Status.FAILURE;
        }
    }

    @Override
    public <T extends Serializable> Pair<Status, Requester<T>> createRequester(
            @NonNull final RequesterConfiguration<T> configuration) {
        assert configuration != null;

        try {
            final RequestRequester<T> _tmp1 = new RequestRequester<>(
                    configuration,
                    new RequestReplyBasedGeneralClientRequestHandler<>(
                            configuration.responderIdentifier,
                            this.participant,
                            configuration.maximumLatency));
            return new Pair<>(Status.SUCCESS, _tmp1);
        } catch (RuntimeException _e) {
            LOGGER.warn("Requester registration failed with exception", _e);
            return new Pair<>(Status.FAILURE, null);
        }
    }

    @Override
    public <T extends Serializable> Status registerResponder(@NonNull final ResponderConfiguration<T> configuration) {
        assert configuration != null;

        try {
            final Invoker<Serializable, Pair<ResponderInvokerStatus, TimestampedBox<T>>> _tmp1 =
                    new Invoker<Serializable, Pair<ResponderInvokerStatus, TimestampedBox<T>>>() {
                        final ResponderInvoker<T> _responder = new ResponderInvoker<>(configuration);
                        @Override
                        public Pair<ResponderInvokerStatus, TimestampedBox<T>> invoke(Serializable data) {
                            return _responder.serviceRequest();
                        }
                    };
            new SimpleReplier<>(
                this.getParticipant(),
                configuration.identifier,
                new RequestReplyBasedGeneralServerRequestHandler<>(_tmp1),
                BytesTypeSupport.get_instance(),
                BytesTypeSupport.get_instance());
            return Status.SUCCESS;
        } catch (RuntimeException _e) {
            LOGGER.warn("Responder registration failed with exception", _e);
            return Status.FAILURE;
        }
    }

    @Override
    public <T extends Serializable> Pair<Status, Sender<T>> createSender(
            @NonNull final SenderConfiguration<T> configuration) {
        assert configuration != null;

        try {
            final SendRequester<T> _tmp1 = new SendRequester<>(
                    configuration,
                    new RequestReplyBasedGeneralClientRequestHandler<>(
                            configuration.receiverIdentifier,
                            this.participant,
                            configuration.maximumLatency));
            return new Pair<>(Status.SUCCESS, _tmp1);
        } catch (RuntimeException _e) {
            LOGGER.warn("Sender registration failed with exception", _e);
            return new Pair<>(Status.FAILURE, null);
        }
    }

    @Override
    public <T extends Serializable> Status registerReceiver(@NonNull final ReceiverConfiguration<T> configuration) {
        assert configuration != null;

        try {
            final Invoker<T, ReceiverInvokerStatus> _tmp1 =
                    new Invoker<T, ReceiverInvokerStatus>() {
                        final ReceiverInvoker<T> _receiver = new ReceiverInvoker<>(configuration);
                        @Override
                        public ReceiverInvokerStatus invoke(T data) {
                            return _receiver.receive(data);
                        }
                    };
            new SimpleReplier<>(
                    this.getParticipant(),
                    configuration.identifier,
                    new RequestReplyBasedGeneralServerRequestHandler<>(_tmp1),
                    BytesTypeSupport.get_instance(),
                    BytesTypeSupport.get_instance());
            return Status.SUCCESS;
        } catch (RuntimeException _e) {
            LOGGER.warn("Receiver registration failed with exception", _e);
            return Status.FAILURE;
        }
    }

    @Override
    public <T extends Serializable> Pair<Status, Initiator<T>> createInitiator(
            @NonNull final InitiatorConfiguration<T> configuration) {
        assert configuration != null;

        try {
            final InitiateRequester<T> _tmp1 = new InitiateRequester<>(
                    configuration,
                    new RequestReplyBasedGeneralClientRequestHandler<T, T>(
                            configuration.executorIdentifier,
                            this.participant,
                            configuration.maximumLatency));
            return new Pair<>(Status.SUCCESS, _tmp1);
        } catch (RuntimeException _e) {
            LOGGER.warn("Initiator registration failed with exception", _e);
            return new Pair<>(Status.FAILURE, null);
        }
    }

    @Override
    public <T extends Serializable> Status registerExecutor(@NonNull final ExecutorConfiguration<T> configuration) {
        assert configuration != null;

        try {
            final Invoker<T, ExecutorInvokerStatus> _tmp1 =
                    new Invoker<T, ExecutorInvokerStatus>() {
                        final ExecutorInvoker<T> _executor = new ExecutorInvoker<>(configuration);
                        @Override
                        public ExecutorInvokerStatus invoke(T cmd) {
                            return _executor.execute(cmd);
                        }
                    };
            new SimpleReplier<>(
                    this.getParticipant(),
                    configuration.identifier,
                    new RequestReplyBasedGeneralServerRequestHandler<>(_tmp1),
                    BytesTypeSupport.get_instance(),
                    BytesTypeSupport.get_instance());
            return Status.SUCCESS;
        } catch (RuntimeException _e) {
            LOGGER.warn("Executor registration failed with exception", _e);
            return Status.FAILURE;
        }
    }
}
