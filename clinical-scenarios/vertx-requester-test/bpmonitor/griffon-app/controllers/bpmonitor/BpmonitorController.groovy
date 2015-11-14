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

  def timer

  def SCP_DDS = 0, SCP_VERTX = 1
  def scpPattern = SCP_VERTX

//Minimum duration of time (in milliseconds) between two consecutive consumptions. 
//In other words, after a consumption of data, new data will be inhibited for this duration of time.
  def minimumSeparation = 0
//Maximum latency to consume the data (in milliseconds).
  def maximumLatency = 100000
//Minimum remaining lifetime required of the consumed data (in milliseconds).
  def minimumRemainingLifetime = 0

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

    //Responds the current seconds of the BPMonitor
    println("Creating seconds responder")
  	Status secondsResponderStatus = communicationManager.registerResponder(new ResponderConfiguration("seconds", minimumSeparation, maximumLatency, minimumRemainingLifetime, new Responder() {
  			Pair<Responder.ResponseStatus, Integer> respond() {
          println("Sending seconds from responder");
  				return new Pair<>(Responder.ResponseStatus.RESPONSE_PROVIDED, currentSeconds);
  			}
    }))
    println("Registered seconds responder: " + secondsResponderStatus);
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
  		}
  	}
    //Update GUI
    edt {
  	 model.seconds = currentSeconds
    }
  }
}
