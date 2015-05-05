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

import scp.targets.vertx.*;
import scp.util.Pair;
import scp.api.*;
import scp.impl.*;
import scp.impl.ExecutorInvoker.ExecutorInvokerStatus;
import scp.impl.ReceiverInvoker.ReceiverInvokerStatus;
import scp.impl.ResponderInvoker.ResponderInvokerStatus;
import scp.util.NonNull;
import scp.util.Pair;
import scp.util.TimestampedBox;

import java.io.*;
import java.util.concurrent.Semaphore;
import scp.api.CommunicationManager;

class InsufflationpumpController {
  // these will be injected by Griffon
  def model
  def view

  //def dds = new DDS()
  def deviceOn = true
  def pressure = 0.0
  def pr = new Random()
    def communicationManager = new CommunicationManagerImpl(0, "localhost");
  //def eb = Vertx.newVertx().getEventBus()
  
  PublishRequester<Integer> deviceStatePublisher, pressurePublisher
class InsufflatorShutOff<T> implements AbstractSubscriber {
	void consume(T data, long remainingLifetime) {
		edt{
          model.state = 'Inactive'
		  deviceOn = false
        }
	}
}
  
  void mvcGroupInit(Map args) {
  communicationManager.setUp()
	deviceStatePublisher = communicationManager.createPublisher(new PublisherConfiguration<Integer>("Device State", 0, 1000, 0, Integer)).second
	pressurePublisher = communicationManager.createPublisher(new PublisherConfiguration<Integer>("Insufflator Pressure", 0, 1000, 0, Integer)).second
	
	communicationManager.registerSubscriber(new SubscriberConfiguration(
            "InsufflatorShutOff",
            0,
            1000,
            1000,
            0,
            100,
            new InsufflatorShutOff<Integer>()
			));
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
	
	deviceStatePublisher.publish(deviceOn?1:0);
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
	
	//eb.publish("Insufflator Pressure", String.valueOf(pressure))
	pressurePublisher.publish(pressure);
	deviceStatePublisher.publish(deviceOn?1:0);
	
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
