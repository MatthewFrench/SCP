/**
 *  Authors:
 *    Venkatesh-Prasad Ranganath
 *  
 *  Copyright (c) 2014, Kansas State University
 *  Licensed under Eclipse Public License v1.0 
 *  http://www.eclipse.org/legal/epl-v10.html                             
 */

package clinicalscenario.simplemonitor

import griffon.core.artifact.GriffonController
import griffon.metadata.ArtifactProviderFor

import lib.DDS
import lib.SimpleValue
import lib.SimpleValueTypeSupport
import lib.Topics

@ArtifactProviderFor(GriffonController)
class CapnometerController {
  def model

  def dds = new DDS()
  def etco2 = new Random()
  def rr = new Random()

  void mvcGroupInit(Map args) {
    dds.publishOn(
        Topics.ETCO2, 
        SimpleValueTypeSupport.get_type_name())
    dds.publishOn(
        Topics.RESPIRATORY_RATE,
        SimpleValueTypeSupport.get_type_name())

    new javax.swing.Timer(1000, update).start()
  }

  void mvcGroupDestroy() {
    dds.destroy()
  }

  def update = { evt ->
    def _rr = model.respiratoryRate + rr.nextInt(3) - 1 // 12-20 breaths/min
    def _etco2 = model.etco2 + etco2.nextInt(5) - 2 // 30-43 mmHg

    runInsideUISync {
      if (_rr >= 0 && _rr < 25)
        model.respiratoryRate = _rr
      if (_etco2 >= 0 && _etco2 < 60)
        model.etco2 = _etco2
    }

    def tmp1 = new SimpleValue()
    tmp1.value = _rr
    dds.publish(Topics.RESPIRATORY_RATE, tmp1)

    def tmp2 = new SimpleValue()
    tmp2.value = _etco2
    dds.publish(Topics.ETCO2, tmp2)
  }
}
