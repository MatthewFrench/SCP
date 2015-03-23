/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.targets.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import scp.api.*;
import scp.impl.*;
import scp.impl.ExecutorInvoker.ExecutorInvokerStatus;
import scp.impl.ReceiverInvoker.ReceiverInvokerStatus;
import scp.impl.ResponderInvoker.ResponderInvokerStatus;
import scp.util.NonNull;
import scp.util.Pair;
import scp.util.TimestampedBox;

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
     * Instantiates a new Communication manager impl.
     */
    public CommunicationManagerImpl() {
        this.vertx = VertxFactory.newVertx();
    }

    /**
     * Instantiates a new Communication manager impl.
     *
     * @param port the port
     * @param hostname the hostname
     */
    public CommunicationManagerImpl(int port, String hostname) {
        final Semaphore _sem = new Semaphore(0);
        final Handler<AsyncResult<Vertx>> _handler = event -> {
            if (event.succeeded()) {
                CommunicationManagerImpl.this.vertx = event.result();
                _sem.release();
            } else if (event.failed()) {
                _sem.release();
                throw new RuntimeException("Could not initialize Communication manager", event.cause());
            }
        };
        VertxFactory.newVertx(port, hostname, _handler);
        try {
            _sem.acquire();
        } catch (InterruptedException _e) {
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
    }

    @Override
    public void tearDown() {
        assert this.vertx != null;

        this.vertx.stop();
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
        this.eventBus.registerLocalHandler(
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
                    CommunicationManagerImpl.this.eventBus.send(
                            configuration.responderIdentifier,
                            new byte[]{},
                            (Message<byte[]> event) -> {
                                _ret.data = getData(event.body());
                                _sem.release();
                            });
                    try {
                        _sem.acquire();
                        return _ret.data;
                    } catch (InterruptedException _e) {
                        LOGGER.error("Semaphore interrupted", _e);
                        throw new RuntimeException(_e);
                    }
                });
        return new Pair<>(Status.SUCCESS, _requester);
    }

    @Override
    public <T extends Serializable> Status registerResponder(
            @NonNull ResponderConfiguration<T> configuration) {
        final ResponderInvoker<T> _responderInvoker = new ResponderInvoker<>(configuration);
        this.eventBus.registerLocalHandler(
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
                    CommunicationManagerImpl.this.eventBus.send(
                            configuration.receiverIdentifier,
                            getBytes(data),
                            (Message<byte[]> event) -> {
                                _ret.data = getData(event.body());
                                _sem.release();
                            });
                    try {
                        _sem.acquire();
                        return _ret.data;
                    } catch (InterruptedException _e) {
                        LOGGER.error("Semaphore interrupted", _e);
                        throw new RuntimeException(_e);
                    }
                });
        return new Pair<>(Status.SUCCESS, _sender);
    }

    @Override
    public <T extends Serializable> Status registerReceiver(@NonNull ReceiverConfiguration<T> configuration) {
        final ReceiverInvoker<T> _receiverInvoker = new ReceiverInvoker<>(configuration);
        this.eventBus.registerLocalHandler(
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
                    CommunicationManagerImpl.this.eventBus.send(
                            configuration.executorIdentifier,
                            getBytes(data),
                            (Message<byte[]> event) -> {
                                _ret.data = getData(event.body());
                                _sem.release();
                            });
                    try {
                        _sem.acquire();
                        return _ret.data;
                    } catch (InterruptedException _e) {
                        LOGGER.error("Semaphore interrupted", _e);
                        throw new RuntimeException(_e);
                    }
                });
        return new Pair<>(Status.SUCCESS, _sender);
    }

    @Override
    public <T extends Serializable> Status registerExecutor(
            @NonNull ExecutorConfiguration<T> configuration) {
        final ExecutorInvoker<T> _receiverInvoker = new ExecutorInvoker<>(configuration);
        this.eventBus.registerLocalHandler(
                configuration.identifier,
                (Message<byte[]> msg) -> {
                    final ExecutorInvokerStatus _status = _receiverInvoker.execute(getData(msg.body()));
                    msg.reply(CommunicationManagerImpl.getBytes(_status));
                });
        return Status.SUCCESS;
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
        } catch (IOException _e) {
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
}
