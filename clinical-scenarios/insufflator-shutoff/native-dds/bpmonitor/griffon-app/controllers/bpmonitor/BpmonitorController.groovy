/**  
 * Authors:
 *   Venkatesh-Prasad Ranganath
 * 
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0 
 * http://www.eclipse.org/legal/epl-v10.html                             
 */

package bpmonitor

import lib.Command
import lib.CompletionStatus
import lib.Constants
import lib.DDS
import lib.SimpleValue
import lib.SimpleValueTypeSupport
import lib.SimpleValueHandlerWrapper
import lib.Topics

class BpmonitorController {
  // these will be injected by Griffon
  def model
  def view

  def dds = new DDS()
  def systolic = new Random()
  def diastolic = new Random()
  def pr = new Random()
  def fastRate = false

  void mvcGroupInit(Map args) {
    dds.publishOn(
        Topics.SYSTOLIC,
        SimpleValueTypeSupport.get_type_name())
    dds.publishOn(
        Topics.DIASTOLIC,
        SimpleValueTypeSupport.get_type_name())
    dds.publishOn(
        Topics.PULSE_RATE,
        SimpleValueTypeSupport.get_type_name())
	def tmp1 = new SimpleValueHandlerWrapper(this.&onBPFREQUENCY)
    dds.subscribeTo(
        Topics.BPFREQUENCY,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
    new javax.swing.Timer(1000, update).start()
  }

  void mvcGroupDestroy() {
    dds.destroy()
  }
  
  def onBPFREQUENCY(frequency) { 
	if (frequency == 1) {
		fastRate = true
	} else {
		fastRate = false
	}
    //edt { model.systolic = systolic }
    //allhookedup |= 0x1
    //if (!model.needToStop && allhookedup == 0xF) {
    //  app.event "Update"
    //}
  }

  def update = { evt ->
    def _pr = model.pulseRate + pr.nextInt(11) - 5 // 60-100 per minute 
    def _systolic = model.systolic + systolic.nextInt(3) - 1  // 96-99 %
    def _diastolic = model.diastolic + diastolic.nextInt(3) - 1  // 96-99 %
    edt {
      if (_pr >= 0 && _pr < 151)
        model.pulseRate = _pr
      if (_systolic > 0 && _systolic < 180)
        model.systolic = _systolic
      if (_diastolic > 0 && _diastolic < 101)
        model.diastolic = _diastolic
    }

    def tmp1 = new SimpleValue()
    tmp1.value = _systolic
    dds.publish(Topics.SYSTOLIC, tmp1)

    def tmp3 = new SimpleValue()
    tmp3.value = _diastolic
    dds.publish(Topics.DIASTOLIC, tmp3)

    def tmp2 = new SimpleValue()
    tmp2.value = _pr
    dds.publish(Topics.PULSE_RATE, tmp2)
  }
}
