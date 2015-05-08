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
import scp.api.CommunicationManager;

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

  void mvcGroupInit(Map args) {
    communicationManager = new CommunicationManagerImpl(0, "localhost")
    communicationManager.setUp()
    //Set up the BP Frequency sender. This is how we change read interval
    bpFrequencySender = communicationManager.createSender(new SenderConfiguration("BPFrequency", 1, 10000, Integer.class)).second
    //Set up the Insufflator Shutoff Initiator, it's how we telll the insufflator to turn off
    insufflatorShutOffInitiator = communicationManager.createInitiator(new InitiatorConfiguration("InsufflatorShutOff", 1, 10000, Integer.class)).second
    //The seconds requester gets the amount of seconds before next reading
    secondsRequester = communicationManager.createRequester(new RequesterConfiguration("Seconds", 1, 10000, 20000, Integer.class)).second
    //Set up the subscribers
    communicationManager.registerSubscriber(new SubscriberConfiguration("Diastolic", 1, 10000, 10000, 12, 1,new Subscriber.AbstractSubscriber() {
        void consume(java.io.Serializable data, long remainingLifetime) {
          edt { model.diastolic = data }
          onUpdate()
        }
    }))
    communicationManager.registerSubscriber(new SubscriberConfiguration("Systolic", 1, 10000, 10000, 12, 1,  new Subscriber.AbstractSubscriber() {
        void consume(java.io.Serializable data, long remainingLifetime) {
          edt { model.systolic = data }
          onUpdate()
        }
    }))
    communicationManager.registerSubscriber(new SubscriberConfiguration("Insufflator Pressure", 1, 10000, 10000, 12, 1, new Subscriber.AbstractSubscriber() {
        void consume(java.io.Serializable data, long remainingLifetime) {
          edt { model.pressure = data }
          onUpdate()
        }
    }))
    communicationManager.registerSubscriber(new SubscriberConfiguration("Device State", 1, 10000, 10000, 12, 1, new Subscriber.AbstractSubscriber() {
        void consume(java.io.Serializable data, long remainingLifetime) {
          edt { model.state = data?"Active":"Inactive" }
          if (data == 1) {
            bpFrequencySender.send(60);
          } else {
            bpFrequencySender.send(180);
          }
        }
    }))
    communicationManager.registerSubscriber(new SubscriberConfiguration("PulseRate", 1, 10000, 10000, 12, 1,  new Subscriber.AbstractSubscriber() {
        void consume(java.io.Serializable data, long remainingLifetime) {
          edt { model.pulseRate = data }
          onUpdate()
        }
    }))

    new javax.swing.Timer(1000, updateSeconds).start()
  }

  void mvcGroupDestroy() {
    //communicationManager.tearDown()
  }
    
  def updateSeconds = { evt ->
    if (secondsNeedUpdate) {
      secondsNeedUpdate = false
      Thread.start {
        def tmp = secondsRequester.request()
        secondsNeedUpdate = true;
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