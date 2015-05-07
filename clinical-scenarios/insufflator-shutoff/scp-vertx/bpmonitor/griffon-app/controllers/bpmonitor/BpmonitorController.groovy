/**  
 * Authors:
 *   Venkatesh-Prasad Ranganath and Matthew French
 * 
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0 
 * http://www.eclipse.org/legal/epl-v10.html                             
 */

 package bpmonitor

//import org.vertx.groovy.core.Vertx;
//import org.vertx.groovy.core.*
//import scp.api.*;
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
import scp.targets.vertx.CommunicationManagerImpl;


class BpmonitorController {
  // these will be injected by Griffon
  def model
  def view

  //def dds = new DDS()
  def systolic = new Random()
  def diastolic = new Random()
  def pr = new Random()
  def fastRate = false
  
  def currentSeconds = 0
  def STATE_IDLE = 0
  def STATE_INFLATING = 1
  def LONG_TIME = 180 
  def SHORT_TIME = 60
  def INFLATING_TIME = 10 
  def currentState = STATE_IDLE
  
  def communicationManager
  PublishRequester<Integer> systolicPublisher, diastolicPublisher, pulseRatePublisher;

  def publisher

  def counter = 0

  void mvcGroupInit(Map args) {
  	communicationManager = new CommunicationManagerImpl(0, "localhost");
  	communicationManager.setUp()
  	def p = communicationManager.createPublisher(new PublisherConfiguration("Systolic", 1, 10000, 20000, Integer.class))
  	println("Status: " + p.first)
  	systolicPublisher = p.second
  	diastolicPublisher = communicationManager.createPublisher(
  		new PublisherConfiguration("Diastolic", 1, 10000, 20000, Integer.class)).second

  	def conf1 = new PublisherConfiguration<Integer>("PulseRate", 1, 10000, 20000, Integer.class)
  	def tmp2 = communicationManager.createPublisher(conf1)
  	println tmp2
  	pulseRatePublisher = tmp2.second


  	def conf = new PublisherConfiguration("test",
  		1, 10000, 20000, Integer.class)
  	def tmp1 = communicationManager.createPublisher(conf)
  	println tmp1
  	publisher = tmp1.second


  	communicationManager.registerResponder(new ResponderConfiguration(
  		"Seconds",
  		1, 10000, 20000,
  		new Responder() {
  			Pair<Responder.ResponseStatus, Integer> respond() {
  				println("Sending seconds: " + currentSeconds);
  				return new Pair<>(Responder.ResponseStatus.RESPONSE_PROVIDED, currentSeconds);
  				}}));


  	println("Status: " + communicationManager.registerReceiver(new ReceiverConfiguration(
  		"BPFrequency",
  		1,
  		10000,
  		new Receiver() {
  			Receiver.ReceptionAcknowledgement receive(java.io.Serializable data) {
  				println("Test")
  				println("Test")
  				println("Test")
  				println("Test")
  				edt { 
  					if (data == 1 && fastRate == false) {
  						fastRate = true
  						currentSeconds = INFLATING_TIME 
  						currentState = STATE_INFLATING
  						} else if (fastRate == true && data == 0) {
  							fastRate = false
  							currentSeconds = INFLATING_TIME 
  							currentState = STATE_INFLATING
  						}
  					}
  					return Receiver.ReceptionAcknowledgement.DATA_ACCEPTED
  				}

  			}
  			)))
  /*
    dds.publishOn(
        Topics.SYSTOLIC,
        SimpleValueTypeSupport.get_type_name())
    dds.publishOn(
        Topics.DIASTOLIC,
        SimpleValueTypeSupport.get_type_name())
    dds.publishOn(
        Topics.PULSE_RATE,
        SimpleValueTypeSupport.get_type_name())
	dds.publishOn(
        Topics.SECONDS,
        SimpleValueTypeSupport.get_type_name())
	def tmp1 = new SimpleValueHandlerWrapper(this.&onBPFREQUENCY)
    dds.subscribeTo(
        Topics.BPFREQUENCY,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
*/
new javax.swing.Timer(1000, update).start()
}

void mvcGroupDestroy() {
    //dds.destroy()
}
  /*
  def onBPFREQUENCY(frequency) { 
  edt { 
	if (frequency == 1 && fastRate == false) {
		fastRate = true
		currentSeconds = INFLATING_TIME 
		currentState = STATE_INFLATING
	} else if (fastRate == true && frequency == 0) {
		fastRate = false
		currentSeconds = INFLATING_TIME 
		currentState = STATE_INFLATING
	}
	}
    //edt { model.systolic = systolic }
    //allhookedup |= 0x1
    //if (!model.needToStop && allhookedup == 0xF) {
    //  app.event "Update"
    //}
  }
  */
  def update = { evt ->
  	def _pr2 = pr.nextInt(40) + 80
  	println("Status: "+pulseRatePublisher.publish(_pr2))
  	println("Publishing pulse rate: " + _pr2)

  	println ( "Sent " + (++counter))
  	publisher.publish(counter)



  	if (currentState == STATE_IDLE) {
  		if (currentSeconds > 0) {
  			currentSeconds = currentSeconds - 1
  			} else {
  				currentSeconds = INFLATING_TIME 
  				currentState = STATE_INFLATING
  			}
  			} else if (currentState == STATE_INFLATING) {
  				if (currentSeconds > 0) {
  					currentSeconds -= 1
  					} else {
  						currentState = STATE_IDLE
  						if (fastRate) {
  							currentSeconds = SHORT_TIME
  							} else {
  								currentSeconds = LONG_TIME
  							}

		def _pr = pr.nextInt(40) + 80 // 60-100 per minute 
		 def _systolic = systolic.nextInt(50) + 100  // 96-99 %
		 def _diastolic = diastolic.nextInt(20) + 80  // 96-99 %
		 if (fastRate) {
		 	_pr = _pr/2.0
		 	_systolic = _systolic/2.0
		 	_diastolic = _diastolic/2.0
		 }
		 edt {
		 	if (_pr >= 0 && _pr < 151)
		 	model.pulseRate = _pr
		 	if (_systolic > 0 && _systolic < 180)
		 	model.systolic = _systolic
		 	if (_diastolic > 0 && _diastolic < 101)
		 	model.diastolic = _diastolic
		 }
		 systolicPublisher.publish(_systolic)
		 diastolicPublisher.publish(_diastolic)
		 println("Status: "+pulseRatePublisher.publish(_pr))
		 println("Publishing pulse rate: " + _pr)
		 println("Published systolic and diastolic");
			/*
			def tmp1 = new SimpleValue()
			tmp1.value = _systolic
			dds.publish(Topics.SYSTOLIC, tmp1)

			def tmp3 = new SimpleValue()
			tmp3.value = _diastolic
			dds.publish(Topics.DIASTOLIC, tmp3)

			def tmp2 = new SimpleValue()
			tmp2.value = _pr
			dds.publish(Topics.PULSE_RATE, tmp2)
			*/
		}
	}

	model.seconds = currentSeconds
	if (currentState == STATE_IDLE) {
		//secondsPublisher.publish(model.seconds)
		//def tmp4 = new SimpleValue()
		//tmp4.value = model.seconds
		//dds.publish(Topics.SECONDS, tmp4)
	}
}
}
