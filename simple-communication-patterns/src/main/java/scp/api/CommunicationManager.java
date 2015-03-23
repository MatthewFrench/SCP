/**
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.api;

import scp.util.NonNull;
import scp.util.Pair;

import java.io.Serializable;

/**
 * The client interface to a communication manager that exposes communication patterns.
 */
public interface CommunicationManager {

    /**
     * Enumeration of Status.
     */
    enum Status {
        /**
         * FAILURE status.
         */
        FAILURE,
        /**
         * SUCCESS status.
         */
        SUCCESS
    }

    /**
     * Throws when initialization fails.
     */
    class FailedToInitializeException extends RuntimeException {
        /**
         * Instantiates a <code>FailedToInitializeException</code> with specified message.
         *
         * @param message the message.
         */
        public FailedToInitializeException(String message) {
            super(message);
        }
    }

    /**
     * Sets up communication manager.  The API protocol is to invoke this method before invoking
     * any other methods on the communication manager.
     */
    void setUp();

    /**
     * Tear down the communication manager.
     */
    void tearDown();

    /**
     * Create publisher.
     *
     * @param <T> the type of published data.
     * @param configuration the required configuration of the publisher.
     * @return <code>SUCCESS</code> and a publisher object if the creation was successful; otherwise, FAILURE and null.
     */
    @NonNull
    <T extends Serializable> Pair<Status, Publisher<T>> createPublisher(
            @NonNull PublisherConfiguration<T> configuration);

    /**
     * Register subscriber.
     *
     * @param <T> the type of subscribed data.
     * @param configuration the required configuration of the subscriber.
     * @return <code>SUCCESS</code> if the creation was successful; otherwise, FAILURE.
     */
    @NonNull
    <T extends Serializable> Status registerSubscriber(@NonNull SubscriberConfiguration<T> configuration);

    /**
     * Create requester.
     *
     * @param <T> the type of requested data.
     * @param configuration the required configuration of the requester.
     * @return <code>SUCCESS</code> and a requester object if the creation was successful; otherwise, FAILURE and null.
     */
    @NonNull
    <T extends Serializable> Pair<Status, Requester<T>> createRequester(
            @NonNull RequesterConfiguration<T> configuration);

    /**
     * Register responder.
     *
     * @param <T> the type of response.
     * @param configuration the required configuration of the subscriber.
     * @return <code>SUCCESS</code> if the creation was successful; otherwise, FAILURE.
     */
    @NonNull
    <T extends Serializable> Status registerResponder(@NonNull ResponderConfiguration<T> configuration);

    /**
     * Create sender.
     *
     * @param <T> the type of sent data.
     * @param configuration the required configuration of the sender.
     * @return <code>SUCCESS</code> and a sender object if the creation was successful; otherwise, FAILURE and null.
     */
    @NonNull
    <T extends Serializable> Pair<Status, Sender<T>> createSender(
            @NonNull SenderConfiguration<T> configuration);

    /**
     * Register receiver.
     *
     * @param <T> the type of received data.
     * @param configuration the required configuration of the receiver.
     * @return <code>SUCCESS</code> if the creation was successful; otherwise, FAILURE.
     */
    @NonNull
    <T extends Serializable> Status registerReceiver(@NonNull ReceiverConfiguration<T> configuration);

    /**
     * Create initiator.
     *
     * @param <T> the type of command.
     * @param configuration the required configuration of the initiator.
     * @return <code>SUCCESS</code> and an initiator object if the creation was successful; otherwise, FAILURE and null.
     */
    @NonNull
    <T extends Serializable> Pair<Status, Initiator<T>> createInitiator(
            @NonNull InitiatorConfiguration<T> configuration);

    /**
     * Register executor.
     *
     * @param <T> the type of command.
     * @param configuration the required configuration of the executor.
     * @return <code>SUCCESS</code> if the creation was successful; otherwise, FAILURE.
     */
    @NonNull
    <T extends Serializable> Status registerExecutor(@NonNull ExecutorConfiguration<T> configuration);
}
