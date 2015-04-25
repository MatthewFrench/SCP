/**  
 * Authors:
 *   Venkatesh-Prasad Ranganath
 * 
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0 
 * http://www.eclipse.org/legal/epl-v10.html                             
 */

package insufflationpump

/*
import lib.Command
import lib.CompletionStatus
import lib.Constants
import lib.DDS
import lib.Topics
import lib.SimpleValue
import lib.SimpleValueTypeSupport
import com.rti.dds.type.builtin.StringTypeSupport
*/
//import org.vertx.java.core.Handler;
//import org.vertx.java.core.eventbus.Message;
//import org.vertx.java.platform.Verticle;
import org.vertx.groovy.core.Vertx;
import org.vertx.groovy.core.*

class InsufflationpumpController {
  // these will be injected by Griffon
  def model
  def view

  //def dds = new DDS()
  def deviceOn = true
  def pressure = 0.0
  def pr = new Random()
  
  def eb = Vertx.newVertx().getEventBus()
  
  void mvcGroupInit(Map args) {
  eb.registerHandler("Insufflator Shut Off", { 
  edt{
          model.state = 'Inactive'
		  deviceOn = false
        }
})
  
  /*
    dds.publishOn(
        Topics.DEVICE_STATE, 
        SimpleValueTypeSupport.get_type_name())
    dds.publishOn(
        Topics.PRESSURE, 
        SimpleValueTypeSupport.get_type_name())
    dds.initializeCommandHandlerFor(this.&onCommand, Constants.INSUFFLATION_PUMP)
	*/
	
	new javax.swing.Timer(1000, update).start()
	
	model.state = 'Active'
	/*
	def tmp1 = new SimpleValue()
    tmp1.value = deviceOn?1:0
	dds.publish(Topics.DEVICE_STATE, tmp1)
	*/
  }

  void mvcGroupDestroy() {
    //dds.destroy()
  }
  
  def update = { evt ->
	if (deviceOn == true ) {
	  pressure += pr.nextInt(10) / 10.0
	  if (pressure > 15) {
		pressure = 15
	  }
	} else {
	  pressure -= pr.nextInt(10) / 10.0
	  if (pressure < 0) {
		pressure = 0
	  }
	}
	model.pressure = pressure
	
	eb.publish("Insufflator Pressure", String.valueOf(pressure))
	/*
	def tmp1 = new SimpleValue()
    tmp1.value = pressure
    dds.publish(Topics.PRESSURE, tmp1)
	
	 tmp1 = new SimpleValue()
    tmp1.value = deviceOn?1:0
    dds.publish(Topics.DEVICE_STATE, tmp1)
	*/
  }
/*
  def onCommand(Command cmd) {
    println(cmd)
    println(cmd == Command.STOP)
    if (cmd == Command.STOP) { 
        edt{
          model.state = 'Inactive'
		  deviceOn = false
        }
    }
	def tmp1 = new SimpleValue()
    tmp1.value = deviceOn?1:0
    dds.publish(Topics.DEVICE_STATE, tmp1)
    return CompletionStatus.SUCCESS
  }*/
}
