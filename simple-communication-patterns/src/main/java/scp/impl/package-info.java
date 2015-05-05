/*
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

/**
 * Contains remoting patterns based service layer implementation that enforces the constraints specified while using
 * communication patterns.  The implementation uses <i>Requester</i> and <i>Invoker</i> patterns to isolate the service
 * layer features from the lower-level communication substrate specific features, which is accessed via <i>Client/Server
 * Request Handler</i> pattern.
 *
 * <i>Requester, Invoker, Client Request Handler,</i> and <i>Server Request Handler</i> patterns are described in the
 * book <a href="http://www.amazon.com/Remoting-Patterns-Foundations-Enterprise-Distributed/dp/0470856629">Remoting
 * Patterns</a> written by Markus Volter, Michael Kircher, and Uwe Zdun.
 */
package scp.impl;