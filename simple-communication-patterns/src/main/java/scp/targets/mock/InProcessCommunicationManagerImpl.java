/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.targets.mock;

import scp.api.*;
import scp.impl.*;
import scp.impl.ExecutorInvoker.ExecutorInvokerStatus;
import scp.impl.ReceiverInvoker.ReceiverInvokerStatus;
import scp.impl.ResponderInvoker.ResponderInvokerStatus;
import scp.impl.spi.InitiateClientRequestHandler;
import scp.impl.spi.PublishClientRequestHandler;
import scp.impl.spi.RequestClientRequestHandler;
import scp.impl.spi.SendClientRequestHandler;
import scp.util.NonNull;
import scp.util.Pair;
import scp.util.TimestampedBox;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.*;

/**
 * An in-process and in-memory communication substrate based implementation of communication manager.
 */
public class InProcessCommunicationManagerImpl implements CommunicationManager {
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final long interactionLatency;
    private final long transportLatency;
    private final Map<String, CustomObservable> topic2observable = new HashMap<>();
    private final Map<String, ResponderInvoker> identifier2responder = new HashMap<>();
    private final Map<String, ReceiverInvoker> identifier2receiver = new HashMap<>();
    private final Map<String, ExecutorInvoker> identifier2executor = new HashMap<>();

    /**
     * Instantiates a new InProcessCommunicationManagerImpl.
     *
     * @param interactionLatency the latency while interacting with the communication manager.
     * @param transportLatency the latency of delivering message between two end points.
     */
    public InProcessCommunicationManagerImpl(final long interactionLatency, final long transportLatency) {
        this.interactionLatency = interactionLatency;
        this.transportLatency = transportLatency;
    }

    @Override
    public void setUp() { }

    @Override
    public void tearDown() { }

    @Override
    public <T extends Serializable> Pair<Status, Publisher<T>> createPublisher(
            @NonNull final PublisherConfiguration<T> configuration) {
        assert configuration != null;

        if (!this.topic2observable.containsKey(configuration.topic)) {
            this.topic2observable.put(configuration.topic, new CustomObservable());
        }
        final CustomObservable _observable = this.topic2observable.get(configuration.topic);
        final PublishClientRequestHandler<T> _tmp1 = new PublishClientRequestHandler<T>() {
            @Override
            public void publish(final TimestampedBox<T> data) {
                executorService.submit(() -> {
                    _observable.setChanged();
                    _observable.notifyObservers(data);
                });
                try {
                    Thread.sleep(InProcessCommunicationManagerImpl.this.interactionLatency);
                } catch (InterruptedException _e) {
                    throw new RuntimeException(_e);
                }
            }
        };
        final scp.api.Publisher<T> _publisher = new PublishRequester<>(configuration, _tmp1);
        return new Pair<>(Status.SUCCESS, _publisher);
    }

    @Override
    public <T extends Serializable> Status registerSubscriber(
            @NonNull final SubscriberConfiguration<T> configuration) {
        assert configuration != null;

        if (!this.topic2observable.containsKey(configuration.topic)) {
            this.topic2observable.put(configuration.topic, new CustomObservable());
        }
        final CustomObservable _observable = this.topic2observable.get(configuration.topic);
        final Observer _observer = new Observer() {
            private final SubscriberInvoker<T> subscriber = new SubscriberInvoker<>(configuration);
            @Override
            public void update(final Observable o, final Object arg) {
                final TimestampedBox<T> _data = (TimestampedBox<T>) arg;
                subscriber.processData(_data);
            }
        };
        _observable.addObserver(_observer);
        return Status.SUCCESS;
    }

    @Override
    public <T extends Serializable> Pair<Status, Requester<T>> createRequester(
            @NonNull final RequesterConfiguration<T> configuration) {
        assert configuration != null;

        final InProcessCommunicationManagerImpl _this = this;
        final RequestClientRequestHandler<T> _tmp2 = new RequestClientRequestHandler<T>() {
            @Override
            public Pair<ResponderInvokerStatus, TimestampedBox<T>> request() {
                final Callable<Pair<ResponderInvokerStatus, TimestampedBox<T>>> _tmp1 =
                        () -> {
                            final String _tmp2 = configuration.responderIdentifier;
                            return _this.identifier2responder.get(_tmp2).serviceRequest();
                        };
                try {
                    Thread.sleep(_this.transportLatency);
                    final Future<Pair<ResponderInvokerStatus, TimestampedBox<T>>> _future =
                            executorService.submit(_tmp1);
                    Thread.sleep(_this.transportLatency);
                    return _future.get();
                } catch (ExecutionException | InterruptedException _e) {
                    throw new RuntimeException(_e);
                }
            }
        };
        final RequestRequester<T> _requestRequester = new RequestRequester<>(configuration, _tmp2);
        return new Pair<>(Status.SUCCESS, _requestRequester);
    }

    @Override
    public <T extends Serializable> Status registerResponder(
            @NonNull final ResponderConfiguration<T> configuration) {
        assert configuration != null;

        final ResponderInvoker<T> _responder = new ResponderInvoker<>(configuration);
        this.identifier2responder.put(configuration.identifier, _responder);
        return Status.SUCCESS;
    }

    @Override
    public <T extends Serializable> Pair<Status, Sender<T>> createSender(
            @NonNull final SenderConfiguration<T> configuration) {
        assert configuration != null;

        final InProcessCommunicationManagerImpl _this = this;
        final SendClientRequestHandler<T> _tmp2 = new SendClientRequestHandler<T>() {
            @Override
            public ReceiverInvokerStatus send(@NonNull T data) {
                try {
                    Thread.sleep(InProcessCommunicationManagerImpl.this.transportLatency);
                    final String _id = configuration.receiverIdentifier;
                    final Future<ReceiverInvokerStatus> _future =
                            executorService.submit(() -> _this.identifier2receiver.get(_id).receive(data));
                    Thread.sleep(InProcessCommunicationManagerImpl.this.transportLatency);
                    return _future.get();
                } catch (ExecutionException | InterruptedException _e) {
                    throw new RuntimeException(_e);
                }
            }
        };
        final SendRequester<T> _sender = new SendRequester<>(configuration, _tmp2);
        return new Pair<>(Status.SUCCESS, _sender);
    }

    @Override
    public <T extends Serializable> Status registerReceiver(
            @NonNull final ReceiverConfiguration<T> configuration) {
        assert configuration != null;

        final ReceiverInvoker<T> _receiver = new ReceiverInvoker<>(configuration);
        this.identifier2receiver.put(configuration.identifier, _receiver);
        return Status.SUCCESS;
    }

    @Override
    public <T extends Serializable> Pair<Status, Initiator<T>> createInitiator(
            @NonNull final InitiatorConfiguration<T> configuration) {
        assert configuration != null;

        final InProcessCommunicationManagerImpl _this = this;
        final InitiateClientRequestHandler<T> _tmp2 = new InitiateClientRequestHandler<T>() {
            @Override
            public ExecutorInvokerStatus initiate(@NonNull T cmd) {
                try {
                    Thread.sleep(InProcessCommunicationManagerImpl.this.transportLatency);
                    final String _id = configuration.executorIdentifier;
                    final Future<ExecutorInvokerStatus> _future =
                            executorService.submit(() -> _this.identifier2executor.get(_id).execute(cmd));
                    Thread.sleep(InProcessCommunicationManagerImpl.this.transportLatency);
                    return _future.get();
                } catch (ExecutionException | InterruptedException _e) {
                    throw new RuntimeException(_e);
                }
            }
        };
        final InitiateRequester<T> _sender = new InitiateRequester<>(configuration, _tmp2);
        return new Pair<>(Status.SUCCESS, _sender);
    }

    @Override
    public <T extends Serializable> Status registerExecutor(
            @NonNull final ExecutorConfiguration<T> configuration) {
        assert configuration != null;

        final ExecutorInvoker<T> _executor = new ExecutorInvoker<>(configuration);
        this.identifier2executor.put(configuration.identifier, _executor);
        return Status.SUCCESS;
    }

    private static class CustomObservable extends Observable {
        @Override
        public synchronized void setChanged() {
            super.setChanged();
        }
    }
}
