/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scp.api.Executor.ExecutionAcknowledgement;
import scp.api.ExecutorConfiguration;
import scp.util.NonNull;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.concurrent.*;

/**
 * Executor invoker.
 * @param <T>  the type of action.
 */
public final class ExecutorInvoker<T extends Serializable> {

    /**
     * Enumeration of the status of requested action.
     */
    public enum ExecutorInvokerStatus {
        /**
         * Action was unavailable.
         */
        ACTION_UNAVAILABLE,
        /**
         * Action failed.
         */
        ACTION_FAILED,
        /**
         * Action succeeded.
         */
        ACTION_SUCCEEDED,
        /**
         * Execution request was rejected as there were too many execution requests.
         */
        EXCESS_LOAD,
        /**
         * Executor failed to honor its maximum service latency.
         */
        TIMED_OUT,
        /**
         * Unknown failure occurred during execution.
         */
        UNKNOWN_FAILURE
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorInvoker.class);
    private final ExecutorService executorService;
    private final ExecutorConfiguration<T> configuration;
    private int executionCount;
    private long currExecutionTime;

    /**
     * Instantiates a new ExecutorInvoker.
     *
     * @param configuration the configuration
     */
    public ExecutorInvoker(@NonNull ExecutorConfiguration<T> configuration) {
        assert configuration != null;

        this.configuration = configuration;
        this.executorService = Executors.newCachedThreadPool();
        this.executionCount = 0;
    }

    /**
     * Execute action.
     *
     * @param action the action
     * @return the status of executing the action.
     */
    public ExecutorInvokerStatus execute(T action) {
        this.executionCount++;
        final long _lastExeTime = this.currExecutionTime;
        final long _tmp1 = System.currentTimeMillis();
        if (_lastExeTime == 0 || _tmp1 - _lastExeTime >= this.configuration.minimumSeparation) {
            // handling request
            this.currExecutionTime = _tmp1;
            return invokeHandler(action);
        } else {
            // drop fast publication
            LOGGER.warn("Dropping {}-th request", this.executionCount);
            return ExecutorInvokerStatus.EXCESS_LOAD;
        }
    }

    private ExecutorInvokerStatus invokeHandler(T action) {
        final scp.api.Executor<T> _handler = this.configuration.executor;
        final Future<ExecutionAcknowledgement> _future = executorService.submit(() ->  _handler.execute(action));

        try {
            final ExecutionAcknowledgement _ret = _future.get(this.configuration.maximumLatency, TimeUnit.MILLISECONDS);
            final ExecutorInvokerStatus _status;
            switch (_ret) {
                case ACTION_SUCCEEDED:
                    _status = ExecutorInvokerStatus.ACTION_SUCCEEDED;
                    break;
                case ACTION_FAILED:
                    _status = ExecutorInvokerStatus.ACTION_FAILED;
                    break;
                case ACTION_UNAVAILABLE:
                    _status = ExecutorInvokerStatus.ACTION_UNAVAILABLE;
                    break;
                default:
                    final String _msg = MessageFormat.format("Unknown status {0}", _ret);
                    LOGGER.error(_msg);
                    throw new RuntimeException(_msg);
            }
            return _status;
        } catch (TimeoutException _e) {
            LOGGER.warn("Timed out handling {}-th request", this.executionCount);
            return ExecutorInvokerStatus.TIMED_OUT;
        } catch (ExecutionException | InterruptedException _e) {
            LOGGER.error(MessageFormat.format("Failed to handle {0}-th request", this.executionCount), _e);
            return ExecutorInvokerStatus.UNKNOWN_FAILURE;
        }
    }
}
