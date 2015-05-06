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
import scp.api.CommunicationManager;


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
  
  def communicationManager = new CommunicationManagerImpl(0, "localhost");
  PublishRequester<Integer> systolicPublisher, diastolicPublisher, pulseRatePublisher, secondsPublisher;

  
  class bpFrequencySubscriber<T> extends Subscriber.AbstractSubscriber {
	void consume(T data, long remainingLifetime) {
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
	}
	public void handleStaleMessage( T data, long remainingLifetime) {
		println("Test1")
		println("Test1")
		println("Test1")
		println("Test1") }

        public void handleSlowPublication() {
		println("Test2")
		println("Test2")
		println("Test2")
		println("Test2") }

        public void handleSlowConsumption( int numOfUnconsumedConsecutiveMessages) {
		println("Test3")
		println("Test3")
		println("Test3")
		println("Test3") }
  }
  void mvcGroupInit(Map args) {
	communicationManager.setUp()
	systolicPublisher = communicationManager.createPublisher(new PublisherConfiguration<Integer>("Systolic", 0, 1000, 0, Integer)).second
	diastolicPublisher = communicationManager.createPublisher(new PublisherConfiguration<Integer>("Diastolic", 0, 1000, 0, Integer)).second
	pulseRatePublisher = communicationManager.createPublisher(new PublisherConfiguration<Integer>("PulseRate", 0, 1000, 0, Integer)).second
	secondsPublisher = communicationManager.createPublisher(new PublisherConfiguration<Integer>("Seconds", 0, 1000, 0, Integer)).second
	
	communicationManager.registerSubscriber(new SubscriberConfiguration(
            "BPFrequency",
            0,
            1000,
            1000,
            0,
            100,
            new bpFrequencySubscriber<Integer>()
			));
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
			pulseRatePublisher.publish(_pr)
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
		secondsPublisher.publish(model.seconds)
		//def tmp4 = new SimpleValue()
		//tmp4.value = model.seconds
		//dds.publish(Topics.SECONDS, tmp4)
	}
  }
}
