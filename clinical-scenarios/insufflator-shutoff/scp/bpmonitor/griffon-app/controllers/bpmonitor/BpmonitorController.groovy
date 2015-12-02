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
import scp.api.CommunicationManager.Status;
import scp.impl.*;
import scp.impl.ExecutorInvoker.ExecutorInvokerStatus;
import scp.impl.ReceiverInvoker.ReceiverInvokerStatus;
import scp.impl.ResponderInvoker.ResponderInvokerStatus;
import scp.util.NonNull;
import scp.util.Pair;
import scp.util.TimestampedBox;

import java.io.*;
import java.util.concurrent.Semaphore;
import scp.targets.vertx.CommunicationManagerImpl as CommunicationManagerVertX;
import scp.targets.dds.CommunicationManagerImpl as CommunicationManagerDDS;


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

  def timer

  def SCP_DDS = 0, SCP_VERTX = 1
  def scpPattern = SCP_VERTX

//Minimum duration of time (in milliseconds) between two consecutive consumptions. 
//In other words, after a consumption of data, new data will be inhibited for this duration of time.

//Maximum latency to consume the data (in milliseconds).

//Minimum remaining lifetime required of the consumed data (in milliseconds).

  void mvcGroupInit(Map args) {
    def startupArgs = app.getStartupArgs()
    if (startupArgs.length > 0) {
      if (startupArgs[0] == "vertx") {
        scpPattern = SCP_VERTX
      }
      if (startupArgs[0] == "dds") {
        scpPattern = SCP_DDS
      }
    }
    if (scpPattern == SCP_DDS) {
      println("Initializing DDS")
      communicationManager = new CommunicationManagerDDS(0)
    } else if (scpPattern == SCP_VERTX) {
      println("Initializing VertX")
      communicationManager = new CommunicationManagerVertX(0, "127.0.0.1")
    }
  	
  	communicationManager.setUp()
    //Create the publishers

    //PublisherConfiguration(String topic,@Offers @NonZero @NonNegative minimumSeparation, @Expects @NonZero @NonNegative final long maximumLatency, @Offers @NonZero @NonNegative final long minimumRemainingLifetime, Class<S> dataType) {
  	//PublisherConfiguration( topic, minimumSeparation, maximumLatency, minimumRemainingLifetime, dataType) {
//createPublisher("testSuccessfulPublication", 1000, this.minimumInteractionLatency + 50, 600, Integer.class, this.communicationManager);
    systolicPublisher = communicationManager.createPublisher(new PublisherConfiguration("systolic", 1, 50000, 50000, Integer.class)).second
  	diastolicPublisher = communicationManager.createPublisher(new PublisherConfiguration("diastolic", 1, 50000, 50000, Integer.class)).second
  	pulseRatePublisher = communicationManager.createPublisher(new PublisherConfiguration("pulserate", 1, 50000, 50000, Integer.class)).second

    //Responds the current seconds of the BPMonitor
    //println("Creating seconds responder")

    //ResponderConfiguration( @NonNull @NonEmpty final String identifier, @Supports @NonZero @NonNegative final long minimumSeparation, @Offers @NonZero @NonNegative final long maximumLatency, @Offers @NonZero @NonNegative final long minimumRemainingLifetime, @NonNull final Responder<T> responder)
  	//ResponderConfiguration( identifier, minimumSeparation, maximumLatency, minimumRemainingLifetime,  responder)
//registerResponder(_responderId, 1000, 100, 600, new BasicResponder());
//Is this necessary? Does responder even block?
Thread.start {
    Status secondsResponderStatus = communicationManager.registerResponder(new ResponderConfiguration("seconds", 1, 50000, 50000, new Responder() {
  			Pair<Responder.ResponseStatus, Integer> respond() {
          //println("Sending seconds from responder");
  				return new Pair<>(Responder.ResponseStatus.RESPONSE_PROVIDED, currentSeconds);
  			}
    }))
  }
    //println("Registered seconds responder: " + secondsResponderStatus);
    //Sets the interval until the next reading

    //registerReceiver(String identifier, long minimumSeparation, long maximumLatency, Receiver<Integer> receiver)
//registerReceiver(_receiverId, 1000, 100, _tmp1);
//Is this necessary? Does receiever block?
Thread.start {
    communicationManager.registerReceiver(new ReceiverConfiguration("bpfrequency", 1, 50000, new Receiver() {
  			Receiver.ReceptionAcknowledgement receive(java.io.Serializable data) {
          edt {
          currentInterval = data
          if (currentSeconds > currentInterval && currentState == STATE_IDLE) {
            currentSeconds = currentInterval
          }
        }
  				return Receiver.ReceptionAcknowledgement.DATA_ACCEPTED
  			}
  	}))
  }
    timer = new javax.swing.Timer(1000, update)
    timer.start()
  }

  void mvcGroupDestroy() {
    timer.stop()
    //communicationManager.tearDown();
    //Thread.sleep(10000)
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
     model.pulseRate = pulseRate
    }
  }
}
