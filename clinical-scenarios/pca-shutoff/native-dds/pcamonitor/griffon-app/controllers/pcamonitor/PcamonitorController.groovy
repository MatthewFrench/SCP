/**  
 * Authors:
 *   Venkatesh-Prasad Ranganath
 * 
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0 
 * http://www.eclipse.org/legal/epl-v10.html                             
 */

package pcamonitor

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

class PcamonitorController {
  // these will be injected by Griffon
  def model
  def view

  def dds = new DDS()

  private allhookedup = 0

  void mvcGroupInit(Map args) {
    dds.initializeCommandSendCapability(Constants.INFUSION_PUMP)
    def tmp1 = new SimpleValueHandlerWrapper(this.&onSPO2)
    dds.subscribeTo(
        Topics.SPO2,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
    tmp1 = new SimpleValueHandlerWrapper(this.&onETCO2)
    dds.subscribeTo(
        Topics.ETCO2,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
    tmp1 = new SimpleValueHandlerWrapper(this.&onRespiratoryRate)
    dds.subscribeTo(
        Topics.RESPIRATORY_RATE,
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

  def onSPO2(spo2) { 
    edt { model.spo2 = spo2 }
    allhookedup |= 0x1
    if (!model.needToStop && allhookedup == 0xF) {
      app.event "Update"
    }
  }

  def onETCO2(etco2) {
    edt { model.etco2 = etco2 }
    allhookedup |= 0x2
    if (!model.needToStop && allhookedup == 0xF) {
      app.event "Update"
    }
  }

  def onRespiratoryRate(rr) {
    edt { model.respiratoryRate = rr }
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
    def needToStop = model.spo2 < 96 || 
      model.etco2 < 30 || model.etco2 > 43 ||
      model.respiratoryRate < 12 || model.respiratoryRate > 20 ||
      model.pulseRate < 60 || model.pulseRate > 100

    if (needToStop) {
      println("stop pump ${model.spo2} ${model.etco2} ${model.respiratoryRate} ${model.pulseRate}")
      CompletionStatus reply = dds.issueCommandTo(Command.STOP, 
          Constants.INFUSION_PUMP)

      if (reply == CompletionStatus.SUCCESS) {
        edt {
          JOptionPane.showMessageDialog(null,
              """ALERT: Infusion pump stopped due to bad vitals.
              spo2: ${model.spo2}
              etco2: ${model.etco2}
              pulse rate: ${model.pulseRate}
              respiratory rate: ${model.respiratoryRate}
              """, 
              'ALERT', 
              JOptionPane.WARNING_MESSAGE)
        }
        edt { model.needToStop = true }
      } else {
        edt {
          JOptionPane.showMessageDialog(null,
              """PANIC: Infusion pump non-responsive to stop command.
              spo2: ${model.spo2}
              etco2: ${model.etco2}
              pulse rate: ${model.pulseRate}
              respiratory rate: ${model.respiratoryRate}
              """,
              'PANIC', 
              JOptionPane.ERROR_MESSAGE)
        }
      }
    }
  }
}
