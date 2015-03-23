/**
 *  Authors:
 *    Venkatesh-Prasad Ranganath
 *  
 *  Copyright (c) 2014, Kansas State University
 *  Licensed under Eclipse Public License v1.0 
 *  http://www.eclipse.org/legal/epl-v10.html                             
 */

package monitor

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
    dds.destroy()
  }

  def onSPO2(spo2) { 
    edt { model.spo2 = spo2 }
    allhookedup |= 0x1
    if (!model.needToAlert && allhookedup == 0xF) {
      app.event "Update"
    }
  }

  def onETCO2(etco2) {
    edt { model.etco2 = etco2 }
    allhookedup |= 0x2
    if (!model.needToAlert && allhookedup == 0xF) {
      app.event "Update"
    }
  }

  def onRespiratoryRate(rr) {
    edt { model.respiratoryRate = rr }
    allhookedup |= 0x4
    if (!model.needToAlert && allhookedup == 0xF) {
      app.event "Update"
    }
  }

  def onPulseRate(pr) {
    edt { model.pulseRate = pr }
    allhookedup |= 0x8
    if (!model.needToAlert && allhookedup == 0xF) {
      app.event "Update"
    }
  }

  def onUpdate = { evt ->
    println("updating $allhookedup")
    def needToAlert = model.spo2 < 96 || 
      model.etco2 < 30 || model.etco2 > 43 ||
      model.respiratoryRate < 12 || model.respiratoryRate > 20 ||
      model.pulseRate < 60 || model.pulseRate > 100
    model.needToAlert = needToAlert

    if (needToAlert) {
      println("alert nurse ${model.spo2} ${model.etco2} ${model.respiratoryRate} ${model.pulseRate}")
      JOptionPane.showMessageDialog(null,
          """ALERT: Bad vitals.
          spo2: ${model.spo2}
          etco2: ${model.etco2}
          pulse rate: ${model.pulseRate}
          respiratory rate: ${model.respiratoryRate}
          """, 
          'ALERT', 
          JOptionPane.WARNING_MESSAGE)
    }
  }
}
