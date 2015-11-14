/**  
* Authors:
*   Venkatesh-Prasad Ranganath and Matthew French
* 
* Copyright (c) 2014, Kansas State University
* Licensed under Eclipse Public License v1.0 
* http://www.eclipse.org/legal/epl-v10.html                             
*/

package monitor

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
import javax.swing.JOptionPane

import java.io.*;
import java.util.concurrent.Semaphore;
import scp.targets.vertx.CommunicationManagerImpl as CommunicationManagerVertX;
import scp.targets.dds.CommunicationManagerImpl as CommunicationManagerDDS;

class MonitorController {
  // these will be injected by Griffon
  def model
  def view

  def communicationManager

  def bpFrequencySender
  def insufflatorShutOffInitiator
  def secondsRequester
  def secondsNeedUpdate = true

  def stoppedInsufflator = false

    def SCP_DDS = 0, SCP_VERTX = 1
  def scpPattern = SCP_VERTX

//Minimum duration of time (in milliseconds) between two consecutive consumptions. 
//In other words, after a consumption of data, new data will be inhibited for this duration of time.
  def minimumSeparation = 0
//Maximum latency to consume the data (in milliseconds).
  def maximumLatency = 500
//Minimum remaining lifetime required of the consumed data (in milliseconds).
  def minimumRemainingLifetime = 1
//Maximum duration of time (in milliseconds) tolerated between two consecutive consumptions.  In other words,
// after a consumption of data, the subscriber can wait for this duration of time for new data to arrive.  If no
// data arrives, then the subscriber is notified of slow publication.
  def maximumSeparation = 12
//Number of consecutive consumptions failing to complete within maximum latency duration that can be tolerated by
// subscriber.  Upon breaching this number, the subscriber will be notified.
  def consumptionTolerance = 5

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
    //Set up the BP Frequency sender. This is how we change read interval
    bpFrequencySender = communicationManager.createSender(new SenderConfiguration("bpfrequency", minimumSeparation, maximumLatency, Integer.class)).second
    //Set up the Insufflator Shutoff Initiator, it's how we telll the insufflator to turn off
    insufflatorShutOffInitiator = communicationManager.createInitiator(new InitiatorConfiguration("insufflatorshutoff", minimumSeparation, maximumLatency, Integer.class)).second
    //The seconds requester gets the amount of seconds before next reading
    Pair<Status, Requester<Integer>> secondsStatusAndRequester = communicationManager.createRequester(new RequesterConfiguration("seconds", minimumSeparation, maximumLatency, minimumRemainingLifetime, Integer.class))
    secondsRequester = secondsStatusAndRequester.second
    println("Seconds status: " + secondsStatusAndRequester.first)
    //Set up the subscribers
    /*
    communicationManager.registerSubscriber(new SubscriberConfiguration("Diastolic", minimumSeparation, maximumSeparation, maximumLatency, minimumRemainingLifetime, consumptionTolerance,new Subscriber.AbstractSubscriber() {
        void consume(java.io.Serializable data, long remainingLifetime) {
          edt { model.diastolic = data }
          onUpdate()
        }
    }))
    communicationManager.registerSubscriber(new SubscriberConfiguration("Systolic", minimumSeparation, maximumSeparation, maximumLatency, minimumRemainingLifetime, consumptionTolerance,  new Subscriber.AbstractSubscriber() {
        void consume(java.io.Serializable data, long remainingLifetime) {
          edt { model.systolic = data }
          onUpdate()
        }
    }))
    communicationManager.registerSubscriber(new SubscriberConfiguration("Insufflator Pressure", minimumSeparation, maximumSeparation, maximumLatency, minimumRemainingLifetime, consumptionTolerance, new Subscriber.AbstractSubscriber() {
        void consume(java.io.Serializable data, long remainingLifetime) {
          edt { model.pressure = data }
          onUpdate()
        }
    }))
    communicationManager.registerSubscriber(new SubscriberConfiguration("Device State", minimumSeparation, maximumSeparation, maximumLatency, minimumRemainingLifetime, consumptionTolerance, new Subscriber.AbstractSubscriber() {
        void consume(java.io.Serializable data, long remainingLifetime) {
          edt { model.state = data?"Active":"Inactive" }
          if (data == 1) {
            bpFrequencySender.send(60);
          } else {
            bpFrequencySender.send(180);
          }
        }
    }))
    communicationManager.registerSubscriber(new SubscriberConfiguration("PulseRate", minimumSeparation, maximumSeparation, maximumLatency, minimumRemainingLifetime, consumptionTolerance,  new Subscriber.AbstractSubscriber() {
        void consume(java.io.Serializable data, long remainingLifetime) {
          edt { model.pulseRate = data }
          onUpdate()
        }
    }))
*/
    new javax.swing.Timer(1000, updateSeconds).start()
  }

  void mvcGroupDestroy() {
    //communicationManager.tearDown()
  }
    
  def updateSeconds = { evt ->
    if (secondsNeedUpdate) {
      secondsNeedUpdate = false
      Thread.start {
      	println("Calling request on seconds requester")
        def tmp = secondsRequester.handleRequest()
        //secondsRequester.request()
        secondsNeedUpdate = true;
        println("Seconds Requester Request Status: " + tmp.first)
        edt {
          if (tmp.second != null) {
            model.seconds = tmp.second;
          }
        }
      }
    }
  }

  def onUpdate = { evt ->
    edt {
      model.needToStop = (model.systolic < 96 || model.diastolic < 50 || model.pulseRate < 50) && (model.systolic != 0 && model.diastolic != 0 && model.pulseRate != 0)

      if (model.needToStop && model.state == "Active" && stoppedInsufflator == false) {
        stoppedInsufflator = true
        println("stop pump ${model.systolic} ${model.diastolic} ${model.pressure} ${model.pulseRate}")
        insufflatorShutOffInitiator.initiate(1);
        Thread.start {
          JOptionPane.showMessageDialog(null,
            """ALERT: Insufflation pump stopped due to bad vitals.
            systolic: ${model.systolic}
            diastolic: ${model.diastolic}
            pulse rate: ${model.pulseRate}
            pressure: ${model.pressure}
            """, 
            'ALERT', 
            JOptionPane.WARNING_MESSAGE)
        }
        model.needToStop = true
      }
    }
  }
}