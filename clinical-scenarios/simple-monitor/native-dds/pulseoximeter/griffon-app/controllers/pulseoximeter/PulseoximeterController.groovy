/**
 *  Authors:
 *    Venkatesh-Prasad Ranganath
 *  
 *  Copyright (c) 2014, Kansas State University
 *  Licensed under Eclipse Public License v1.0 
 *  http://www.eclipse.org/legal/epl-v10.html                             
 */

package pulseoximeter

import lib.DDS
import lib.SimpleValue
import lib.SimpleValueTypeSupport
import lib.Topics

class PulseoximeterController {
  // these will be injected by Griffon
  def model
  def view

  def dds = new DDS()
  def spo2 = new Random()
  def pr = new Random()

  void mvcGroupInit(Map args) {
    dds.publishOn(
        Topics.SPO2,
        SimpleValueTypeSupport.get_type_name())
    dds.publishOn(
        Topics.PULSE_RATE,
        SimpleValueTypeSupport.get_type_name())
    new javax.swing.Timer(1000, update).start()
  }

  void mvcGroupDestroy() {
    dds.destroy()
  }

  def update = { evt ->
    def _pr = model.pulseRate + pr.nextInt(11) - 5 // 60-100 per minute 
    def _spo2 = model.spo2 + spo2.nextInt(3) - 1  // 96 and above 
    edt {
      if (_pr >= 0 && _pr < 150)
        model.pulseRate = _pr
      if (_spo2 > 0 && _spo2 < 101)
        model.spo2 = _spo2
    }

    def tmp1 = new SimpleValue()
    tmp1.value = _spo2
    dds.publish(Topics.SPO2, tmp1)

    def tmp2 = new SimpleValue()
    tmp2.value = _pr
    dds.publish(Topics.PULSE_RATE, tmp2)
  }
}
