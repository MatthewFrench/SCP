/**  
 * Authors:
 *   Venkatesh-Prasad Ranganath
 * 
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0 
 * http://www.eclipse.org/legal/epl-v10.html                             
 */

package clinicalscenario

import griffon.core.artifact.GriffonController
import griffon.metadata.ArtifactProviderFor

import lib.Command
import lib.CompletionStatus
import lib.Constants
import lib.DDS
import lib.Topics
import com.rti.dds.type.builtin.StringTypeSupport

@ArtifactProviderFor(GriffonController)
class InfusionpumpController {
  def model
  def view

  def dds = new DDS()

  void mvcGroupInit(Map args) {
    dds.publishOn(
        Topics.DEVICE_STATE, 
        StringTypeSupport.get_type_name())
    dds.initializeCommandHandlerFor(this.&onCommand, Constants.INFUSION_PUMP)
  }

  void mvcGroupDestroy() {
    dds.destroy()
  }

  def onCommand(Command cmd) {
    println("$cmd ${cmd == Command.STOP}")
    if (cmd == Command.STOP) { 
        runInsideUISync {
          model.state = 'Inactive'
        }
    }
    dds.publish(Topics.DEVICE_STATE, model.state)
    return CompletionStatus.SUCCESS
  }
}
