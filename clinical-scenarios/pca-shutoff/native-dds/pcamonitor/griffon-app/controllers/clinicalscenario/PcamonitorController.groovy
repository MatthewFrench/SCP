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
import griffon.core.RunnableWithArgs
import griffon.metadata.ArtifactProviderFor

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

@ArtifactProviderFor(GriffonController)
class PcamonitorController {
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
    application.eventRouter.addEventListener('Update', 
        onUpdate as RunnableWithArgs)
  }

  void mvcGroupDestroy() {
    println('destroying')
    dds.destroy()
  }

  def onSPO2(spo2) { 
    runInsideUISync { model.spo2 = spo2 }
    allhookedup |= 0x1
    if (!model.needToStop && allhookedup == 0xF) {
      application.eventRouter.publishEvent('Update')
    }
  }

  def onETCO2(etco2) {
    runInsideUISync { model.etco2 = etco2 }
    allhookedup |= 0x2
    if (!model.needToStop && allhookedup == 0xF) {
      application.eventRouter.publishEvent('Update')
    }
  }

  def onRespiratoryRate(rr) {
    runInsideUISync { model.respiratoryRate = rr }
    allhookedup |= 0x4
    if (!model.needToStop && allhookedup == 0xF) {
      application.eventRouter.publishEvent('Update')
    }
  }

  def onPulseRate(pr) {
    runInsideUISync { model.pulseRate = pr }
    allhookedup |= 0x8
    if (!model.needToStop && allhookedup == 0xF) {
      application.eventRouter.publishEvent('Update')
    }
  }

  def onUpdate = { evt -> 
    // println("updating $allhookedup")
    def needToStop = model.spo2 < 96 || 
      model.etco2 < 30 || model.etco2 > 43 ||
      model.respiratoryRate < 12 || model.respiratoryRate > 20 ||
      model.pulseRate < 60 || model.pulseRate > 100

    if (needToStop) {
      println("stop pump ${model.spo2} ${model.etco2} ${model.respiratoryRate} ${model.pulseRate}")

      CompletionStatus reply = dds.issueCommandTo(Command.STOP, 
          Constants.INFUSION_PUMP)

      if (reply == CompletionStatus.SUCCESS) {
        runInsideUISync {
          JOptionPane.showMessageDialog(null,
              """ALERT: Infusion pump stopped due to bad vitals.
              spo2: ${model.spo2}
              etco2: ${model.etco2}
              pulse rate: ${model.pulseRate}
              respiratory rate: ${model.respiratoryRate}
              """, 
              'ALERT', 
              JOptionPane.WARNING_MESSAGE)
           model.needToStop = true 
        }
      } else {
        runInsideUISync {
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
