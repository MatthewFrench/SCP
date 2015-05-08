/**  
* Authors:
*   Venkatesh-Prasad Ranganath and Matthew French
* 
* Copyright (c) 2014, Kansas State University
* Licensed under Eclipse Public License v1.0 
* http://www.eclipse.org/legal/epl-v10.html                             
*/

package bpmonitor

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

  def randomGenerator = new Random()

  def currentInterval = 180
  def currentSeconds = 0
  def STATE_IDLE = 0
  def STATE_INFLATING = 1
  def INFLATING_TIME = 10 
  def currentState = STATE_IDLE
  
  def communicationManager
  def systolicPublisher, diastolicPublisher, pulseRatePublisher
  int systolic = 120, diastolic = 80, pulseRate = 80

  void mvcGroupInit(Map args) {
  	communicationManager = new CommunicationManagerImpl(0, "localhost")
  	communicationManager.setUp()
    //Create the publishers
  	systolicPublisher = communicationManager.createPublisher(new PublisherConfiguration("Systolic", 1, 10000, 20000, Integer.class)).second
  	diastolicPublisher = communicationManager.createPublisher(new PublisherConfiguration("Diastolic", 1, 10000, 20000, Integer.class)).second
  	pulseRatePublisher = communicationManager.createPublisher(new PublisherConfiguration("PulseRate", 1, 10000, 20000, Integer.class)).second

    //Responds the current seconds of the BPMonitor
  	communicationManager.registerResponder(new ResponderConfiguration("Seconds", 1, 10000, 20000, new Responder() {
  			Pair<Responder.ResponseStatus, Integer> respond() {
  				return new Pair<>(Responder.ResponseStatus.RESPONSE_PROVIDED, currentSeconds);
  			}
    }))
    //Sets the interval until the next reading
    communicationManager.registerReceiver(new ReceiverConfiguration("BPFrequency", 1, 10000, new Receiver() {
  			Receiver.ReceptionAcknowledgement receive(java.io.Serializable data) {
          currentInterval = data
          if (currentSeconds > currentInterval && currentState == STATE_IDLE) {
            currentSeconds = currentInterval
          }
  				return Receiver.ReceptionAcknowledgement.DATA_ACCEPTED
  			}
  	}))
    new javax.swing.Timer(1000, update).start()
  }

  void mvcGroupDestroy() {
    //communicationManager.tearDown();
  }

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
  			currentSeconds = currentInterval

		    int _pr = randomGenerator.nextInt(40) + 80 // 60-100 per minute 
		    int _systolic = randomGenerator.nextInt(50) + 100  // 96-99 %
		    int _diastolic = randomGenerator.nextInt(20) + 80  // 96-99 %
        //If the BP monitor is running faster than normal, lets simulate a failing patient
		    if (currentInterval < 180) {
		 	    _pr = _pr/2.0
		 	    _systolic = _systolic/2.0
		 	    _diastolic = _diastolic/2.0
		    }
		 	  if (_pr >= 0 && _pr < 151)
		 	    pulseRate = _pr
		    if (_systolic > 0 && _systolic < 180)
          systolic = _systolic
	 	    if (_diastolic > 0 && _diastolic < 101)
	 	      diastolic = _diastolic
  		}
  	}
    //Send all the data constantly
    pulseRatePublisher.publish(pulseRate)
    systolicPublisher.publish(systolic)
    diastolicPublisher.publish(diastolic)
    //Update GUI
    edt {
  	 model.seconds = currentSeconds
     model.systolic = systolic
     model.diastolic = diastolic
    }
  }
}
