/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.targets.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scp.api.*;
import scp.impl.*;
import scp.impl.ExecutorInvoker.ExecutorInvokerStatus;
import scp.impl.ReceiverInvoker.ReceiverInvokerStatus;
import scp.impl.ResponderInvoker.ResponderInvokerStatus;
import scp.util.Pair;
import scp.util.TimestampedBox;

import scp.util.NonNull;

import java.io.*;
import java.util.concurrent.Semaphore;

/**
 * A <a href="http://vertx.io">Vert.x</a> event bus based implementation of communication manager.
 */
public class CommunicationManagerImpl implements CommunicationManager {
    private final static Logger LOGGER = LoggerFactory.getLogger(CommunicationManagerImpl.class);
    private Vertx vertx;
    private EventBus eventBus;

    /**
     * Instantiates a new Communication manager impl based on non-clustered Vertx instance.
     */
    public CommunicationManagerImpl() {
        this.vertx = Vertx.vertx();
    }

    /**
     * Instantiates a new Communication manager impl based on clustered Vertx instance.
     *
     * @param port the port.  If this argument is 0, then the system will pick the port.
     * @param hostname the hostname
     */
    public CommunicationManagerImpl(int port, String hostname) {
        final Semaphore _sem = new Semaphore(0);
        final Handler<AsyncResult<Vertx>> _handler = event -> {
            try {
                if (event.succeeded()) {
                    LOGGER.info("Vertx creation succeeded");
                    CommunicationManagerImpl.this.vertx = event.result();
                } else {
                    LOGGER.error("Vertx creation failed");
                    throw new RuntimeException("Could not initialize Communication manager", event.cause());
                }
            } finally {
                _sem.release();
            }
        };
        final VertxOptions _opt = new VertxOptions();
        _opt.setClustered(true);
        _opt.setClusterHost(hostname);
        _opt.setClusterPort(port);
        Vertx.clusteredVertx(_opt, _handler);

        try {
            _sem.acquire();
        } catch (final InterruptedException _e) {
            final String _msg = "Why did the semaphore encountered InterruptedException?";
            LOGGER.error(_msg, _e);
            throw new RuntimeException(_msg, _e);
        }

        assert this.vertx != null;
    }

    @Override
    public void setUp() {
        this.eventBus = this.vertx.eventBus();

        assert this.eventBus != null;
        LOGGER.info("Done setting up vertx eventbus");
    }

    @Override
    public void tearDown() {
        assert this.vertx != null;

        /*
        TODO: Need to figured out how to call vertx.close without tripping Vertx

        final Semaphore _tmp2 = new Semaphore(0);
        this.vertx.close(evt -> {
            LOGGER.info("Vertx closed");
            _tmp2.release();
        });
        try {
            _tmp2.acquire();
            LOGGER.info("Done tearing down vertx");
        } catch (final InterruptedException _e) {
            throw new RuntimeException((_e));
        }
        */
    }

    @Override
    public <T extends Serializable> Pair<Status, Publisher<T>> createPublisher(
            @NonNull PublisherConfiguration<T> configuration) {
        final PublishRequester<T> _publisher = new PublishRequester<>(
                configuration,
                data ->  CommunicationManagerImpl.this.eventBus.publish(configuration.topic, getBytes(data)));
        return new Pair<>(Status.SUCCESS, _publisher);
    }

    @Override
    public <T extends Serializable> Status registerSubscriber(
            @NonNull SubscriberConfiguration<T> configuration) {
        final SubscriberInvoker<T> _subscriberInvoker = new SubscriberInvoker<>(configuration);
        this.eventBus.consumer(
                configuration.topic,
                (Message<byte[]> msg) -> _subscriberInvoker.processData(getData(msg.body())));
        return Status.SUCCESS;
    }

    @Override
    public <T extends Serializable> Pair<Status, Requester<T>> createRequester(
            @NonNull RequesterConfiguration<T> configuration) {
        final RequestRequester<T> _requester = new RequestRequester<>(
                configuration,
                () -> {
                    final Semaphore _sem = new Semaphore(0);
                    final Box<Pair<ResponderInvokerStatus, TimestampedBox<T>>> _ret = new Box<>();
                    final DeliveryOptions _opt = new DeliveryOptions();
                    _opt.setSendTimeout(configuration.maximumLatency);
                    CommunicationManagerImpl.this.eventBus.send(
                            configuration.responderIdentifier,
                            new byte[]{},
                            _opt,
                            new SendHandler<T, Pair<ResponderInvokerStatus, TimestampedBox<T>>>(
                                    configuration.maximumLatency, _sem, _ret));
                    return waitAndBailOnFailure(_sem, _ret);
                });
        return new Pair<>(Status.SUCCESS, _requester);
    }

    @Override
    public <T extends Serializable> Status registerResponder(
            @NonNull ResponderConfiguration<T> configuration) {
        final ResponderInvoker<T> _responderInvoker = new ResponderInvoker<>(configuration);
        this.eventBus.consumer(
                configuration.identifier,
                msg -> msg.reply(CommunicationManagerImpl.getBytes(_responderInvoker.serviceRequest())));
        return Status.SUCCESS;
    }

    @Override
    public <T extends Serializable> Pair<Status, Sender<T>> createSender(
            @NonNull SenderConfiguration<T> configuration) {
        final SendRequester<T> _sender = new SendRequester<>(
                configuration,
                (data) -> {
                    final Semaphore _sem = new Semaphore(0);
                    final Box<ReceiverInvokerStatus> _ret = new Box<>();
                    final DeliveryOptions _opt = new DeliveryOptions();
                    _opt.setSendTimeout(configuration.maximumLatency);
                    CommunicationManagerImpl.this.eventBus.send(
                            configuration.receiverIdentifier,
                            getBytes(data),
                            _opt,
                            new SendHandler<T, ReceiverInvokerStatus>(configuration.maximumLatency, _sem, _ret));
                    return waitAndBailOnFailure(_sem, _ret);
                });
        return new Pair<>(Status.SUCCESS, _sender);
    }

    @Override
    public <T extends Serializable> Status registerReceiver(@NonNull ReceiverConfiguration<T> configuration) {
        final ReceiverInvoker<T> _receiverInvoker = new ReceiverInvoker<>(configuration);
        this.eventBus.consumer(
                configuration.identifier,
                (Message<byte[]> msg) -> {
                    final ReceiverInvokerStatus _status = _receiverInvoker.receive(getData(msg.body()));
                    msg.reply(CommunicationManagerImpl.getBytes(_status));
                });
        return Status.SUCCESS;
    }

    @Override
    public <T extends Serializable> Pair<Status, Initiator<T>> createInitiator(
            @NonNull InitiatorConfiguration<T> configuration) {
        final InitiateRequester<T> _sender = new InitiateRequester<>(
                configuration,
                (data) -> {
                    final Semaphore _sem = new Semaphore(0);
                    final Box<ExecutorInvokerStatus> _ret = new Box<>();
                    final DeliveryOptions _opt = new DeliveryOptions();
                    _opt.setSendTimeout(configuration.maximumLatency);
                    CommunicationManagerImpl.this.eventBus.send(
                            configuration.executorIdentifier,
                            getBytes(data),
                            _opt,
                            new SendHandler<T, ExecutorInvokerStatus>(configuration.maximumLatency, _sem, _ret));
                    return waitAndBailOnFailure(_sem, _ret);
                });
        return new Pair<>(Status.SUCCESS, _sender);
    }

    @Override
    public <T extends Serializable> Status registerExecutor(
            @NonNull ExecutorConfiguration<T> configuration) {
        final ExecutorInvoker<T> _receiverInvoker = new ExecutorInvoker<>(configuration);
        this.eventBus.consumer(
                configuration.identifier,
                (Message<byte[]> msg) -> {
                    final ExecutorInvokerStatus _status = _receiverInvoker.execute(getData(msg.body()));
                    msg.reply(CommunicationManagerImpl.getBytes(_status));
                });
        return Status.SUCCESS;
    }

    private static <T extends Serializable> T waitAndBailOnFailure(Semaphore sem, Box<T> ret) {
        try {
            sem.acquire();
            return ret.data;
        } catch (final InterruptedException _e) {
            LOGGER.error("Semaphore interrupted", _e);
            throw new RuntimeException(_e);
        }
    }

    private static <T extends Serializable> byte[] getBytes(T data) {
        try {
            final ByteArrayOutputStream _tmp1 = new ByteArrayOutputStream();
            final ObjectOutputStream _tmp2 = new ObjectOutputStream(_tmp1);
            _tmp2.writeObject(data);
            _tmp2.flush();
            final byte[] _msg = _tmp1.toByteArray();
            _tmp2.close();
            return _msg;
        } catch (final IOException _e) {
            CommunicationManagerImpl.LOGGER.error("Serialization failed", _e);
            throw new RuntimeException(_e);
        }
    }

    private static <T extends Serializable> T getData(byte[] bytes) {
        try {
            final ByteArrayInputStream _tmp2 = new ByteArrayInputStream(bytes);
            final ObjectInputStream _tmp3 = new ObjectInputStream(_tmp2);
            final T _data = (T) _tmp3.readObject();
            _tmp3.close();
            return _data;
        } catch (final ClassNotFoundException | IOException _e) {
            LOGGER.error("Deserialization failed", _e);
            throw new RuntimeException(_e);
        }
    }

    private static class Box<T extends Serializable> {
        /**
         * The Data.
         */
        T data;
    }

    private static class SendHandler<T extends Serializable, S extends Serializable>
            implements Handler<AsyncResult<Message<byte[]>>> {
        private long maximumLatency;
        private Semaphore sem;
        private Box<S> ret;

        public SendHandler(long maximumLatency, Semaphore sem, Box<S> ret) {
            this.maximumLatency = maximumLatency;
            this.sem = sem;
            this.ret = ret;
        }

        @Override
        public void handle(AsyncResult<Message<byte[]>> event) {

            if (event.succeeded()) {
                ret.data = getData(event.result().body());
            } else {
                LOGGER.error("Vertx Send failed: {}",
                        ((ReplyException) event.cause()).failureType());
                //try {
                    //Thread.sleep(maximumLatency * 2);
                //} catch (final InterruptedException _e) {
                //    LOGGER.error("This is bad!", _e);
                //    throw new RuntimeException(_e);
                //}
            }
            sem.release();
        }
    }
}
