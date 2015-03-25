/**  
 * Authors:
 *   Venkatesh-Prasad Ranganath
 * 
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0 
 * http://www.eclipse.org/legal/epl-v10.html                             
 */

package insufflationpump

import lib.Command
import lib.CompletionStatus
import lib.Constants
import lib.DDS
import lib.Topics
import com.rti.dds.type.builtin.StringTypeSupport

class InsufflationpumpController {
  // these will be injected by Griffon
  def model
  def view

  def dds = new DDS()

  void mvcGroupInit(Map args) {
    dds.publishOn(
        Topics.DEVICE_STATE, 
        StringTypeSupport.get_type_name())
    dds.publishOn(
        Topics.PRESSURE, 
        StringTypeSupport.get_type_name())
    dds.initializeCommandHandlerFor(this.&onCommand, Constants.INSUFFLATION_PUMP)
  }

  void mvcGroupDestroy() {
    dds.destroy()
  }

  def onCommand(Command cmd) {
    println(cmd)
    println(cmd == Command.STOP)
    if (cmd == Command.STOP) { 
        edt{
          model.state = 'Inactive'
        }
    }
    dds.publish(Topics.DEVICE_STATE, model.state)
    return CompletionStatus.SUCCESS
  }
}
