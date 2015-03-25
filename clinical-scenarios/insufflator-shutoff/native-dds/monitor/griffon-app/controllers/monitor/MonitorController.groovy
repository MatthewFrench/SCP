/**  
 * Authors:
 *   Venkatesh-Prasad Ranganath
 * 
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0 
 * http://www.eclipse.org/legal/epl-v10.html                             
 */

package monitor

import lib.Command
import lib.CompletionStatus
import lib.Constants
import lib.DDS
import lib.SimpleValue
import lib.SimpleValueHandlerWrapper
import lib.SimpleValueTypeSupport
import lib.Topics
import com.rti.dds.subscription.DataReaderAdapter
import com.rti.dds.subscription.SampleInfo
import javax.swing.JOptionPane

class MonitorController {
  // these will be injected by Griffon
  def model
  def view

  def dds = new DDS()

  private allhookedup = 0

  void mvcGroupInit(Map args) {
    dds.initializeCommandSendCapability(Constants.INFUSION_PUMP)
    def tmp1 = new SimpleValueHandlerWrapper(this.&onSYSTOLIC)
    dds.subscribeTo(
        Topics.SYSTOLIC,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
    tmp1 = new SimpleValueHandlerWrapper(this.&onDIASTOLIC)
    dds.subscribeTo(
        Topics.DIASTOLIC,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
    tmp1 = new SimpleValueHandlerWrapper(this.&onPRESSURE)
    dds.subscribeTo(
        Topics.PRESSURE,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
    tmp1 = new SimpleValueHandlerWrapper(this.&onPulseRate)
    dds.subscribeTo(
        Topics.PULSE_RATE,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
  }

  void mvcGroupDestroy() {
    println('destroying')
    dds.destroy()
  }

  def onSYSTOLIC(systolic) { 
    edt { model.systolic = systolic }
    allhookedup |= 0x1
    if (!model.needToStop && allhookedup == 0xF) {
      app.event "Update"
    }
  }

  def onDIASTOLIC(diastolic) {
    edt { model.diastolic = diastolic }
    allhookedup |= 0x2
    if (!model.needToStop && allhookedup == 0xF) {
      app.event "Update"
    }
  }

  def onPRESSURE(pressure) {
    edt { model.pressure = pressure }
    allhookedup |= 0x4
    if (!model.needToStop && allhookedup == 0xF) {
      app.event "Update"
    }
  }

  def onPulseRate(pr) {
    edt { model.pulseRate = pr }
    allhookedup |= 0x8
    if (!model.needToStop && allhookedup == 0xF) {
      app.event "Update"
    }
  }

  def onUpdate = { evt ->
    println("updating $allhookedup")
    def needToStop = model.systolic < 96 || 
      model.diastolic < 50 || model.pulseRate < 50

    if (needToStop) {
      println("stop pump ${model.systolic} ${model.diastolic} ${model.pressure} ${model.pulseRate}")
      CompletionStatus reply = dds.issueCommandTo(Command.STOP, 
          Constants.INSUFFLATION_PUMP)

      if (reply == CompletionStatus.SUCCESS) {
        edt {
          JOptionPane.showMessageDialog(null,
              """ALERT: Insufflation pump stopped due to bad vitals.
              systolic: ${model.systolic}
              diastolic: ${model.diastolic}
              pulse rate: ${model.pulseRate}
              pressure: ${model.[ressure}
              """, 
              'ALERT', 
              JOptionPane.WARNING_MESSAGE)
        }
        edt { model.needToStop = true }
      } else {
        edt {
          JOptionPane.showMessageDialog(null,
              """PANIC: Insufflation pump non-responsive to stop command.
              systolic: ${model.systolic}
              diastolic: ${model.diastolic}
              pulse rate: ${model.pulseRate}
              pressure: ${model.pressure}
              """,
              'PANIC', 
              JOptionPane.ERROR_MESSAGE)
        }
      }
    }
  }
}
