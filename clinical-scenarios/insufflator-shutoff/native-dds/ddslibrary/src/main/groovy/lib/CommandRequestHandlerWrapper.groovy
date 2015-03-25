/**  
 * Authors:
 *   Venkatesh-Prasad Ranganath
 * 
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0 
 * http://www.eclipse.org/legal/epl-v10.html                             
 */

package lib

import com.rti.connext.infrastructure.Sample
import com.rti.connext.requestreply.SimpleReplierListener

class CommandRequestHandlerWrapper implements SimpleReplierListener {
	
  private final handler
	
  public CommandRequestHandlerWrapper(handler) {
    this.handler = handler
  }
	
  public onRequestAvailable(Sample request) {
    def reply = new CommandResponse()
    reply.status = this.handler(request.getData().cmd)
    println("replying with ${reply.status}")
    return reply
  }

  public void returnLoan(reply) {
  }
}
