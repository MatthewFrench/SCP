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

import java.io.*;
import java.util.concurrent.Semaphore;
import scp.api.CommunicationManager;

class MonitorController {
  // these will be injected by Griffon
  def model
  def view

  //def dds = new DDS()

  private allhookedup = 0
  def communicationManager = new CommunicationManagerImpl(0, "localhost");

  class DiastolicSubscriber<T> extends Subscriber.AbstractSubscriber {
   void consume(T data, long remainingLifetime) {
      println("Recieved diastolic")
      edt { model.diastolic = data }
      allhookedup |= 0x2
      if (!model.needToStop && allhookedup == 0xF) {
        app.event "Update"
      }
   }
}
class SystolicSubscriber<T> extends Subscriber.AbstractSubscriber {
   void consume(T data, long remainingLifetime) {
   edt { model.systolic = data }
      allhookedup |= 0x1
      if (!model.needToStop && allhookedup == 0xF) {
        app.event "Update"
      }
   }
}
class PressureSubscriber<T> extends Subscriber.AbstractSubscriber {
   void consume(T data, long remainingLifetime) {
      edt { model.pressure = data }
      allhookedup |= 0x4
      if (!model.needToStop && allhookedup == 0xF) {
        app.event "Update"
      }
   }
}
class SecondsSubscriber<T> extends Subscriber.AbstractSubscriber {
   void consume(T data, long remainingLifetime) {
   writeln("Recieved seconds");
   edt { model.seconds = data }
   }
}
class PulseRateSubscriber<T> extends Subscriber.AbstractSubscriber {
   void consume(T data, long remainingLifetime) {
      edt { model.pulseRate = data }
      allhookedup |= 0x8
      if (!model.needToStop && allhookedup == 0xF) {
        app.event "Update"
      }
   }
}
class DeviceStateSubscriber<T> extends Subscriber.AbstractSubscriber {
   void consume(T data, long remainingLifetime) {
      edt { model.state = data?"Active":"Inactive" }
      if (data == 1) {
         bpFrequencyPublisher.publish(1);
         //def tmp1 = new SimpleValue()
         //   tmp1.value = 1
         //   dds.publish(Topics.BPFREQUENCY, tmp1)
      } else {
         bpFrequencyPublisher.publish(0);
         //def tmp1 = new SimpleValue()
         //   tmp1.value = 0
         //   dds.publish(Topics.BPFREQUENCY, tmp1)
      }
   }
}
  PublishRequester<Integer> bpFrequencyPublisher, insufflatorShutOffPublisher
  void mvcGroupInit(Map args) {
    //dds.initializeCommandSendCapability(Constants.INSUFFLATION_PUMP)
   /*
    def tmp1 = new SimpleValueHandlerWrapper(this.&onSYSTOLIC)
    dds.subscribeTo(
        Topics.SYSTOLIC,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
    tmp1 = new SimpleValueHandlerWrapper(this.&onDIASTOLIC)
    dds.subscribeTo(
        Topics.DIASTOLIC,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
    tmp1 = new SimpleValueHandlerWrapper(this.&onPRESSURE)
    dds.subscribeTo(
        Topics.PRESSURE,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
    tmp1 = new SimpleValueHandlerWrapper(this.&onPulseRate)
    dds.subscribeTo(
        Topics.PULSE_RATE,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
   tmp1 = new SimpleValueHandlerWrapper(this.&onDeviceState)
    dds.subscribeTo(
        Topics.DEVICE_STATE,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
   tmp1 = new SimpleValueHandlerWrapper(this.&onSeconds)
    dds.subscribeTo(
        Topics.SECONDS,
        SimpleValueTypeSupport.get_type_name(),
        tmp1)
   dds.publishOn(
        Topics.BPFREQUENCY, 
        SimpleValueTypeSupport.get_type_name())*/
      communicationManager.setUp()
      bpFrequencyPublisher = communicationManager.createPublisher(new PublisherConfiguration<Integer>("BPFrequency", 0, 1000, 0,  Integer)).second
      insufflatorShutOffPublisher = communicationManager.createPublisher(new PublisherConfiguration<Integer>("InsufflatorShutOff", 0, 1000, 0,  Integer)).second
   communicationManager.registerSubscriber(new SubscriberConfiguration("Diastolic",
            0,
            1000,
            1000,
            0,
            100,new DiastolicSubscriber<Integer>() ));
         communicationManager.registerSubscriber(new SubscriberConfiguration("Systolic",
            0,
            1000,
            1000,
            0,
            100, new SystolicSubscriber<Integer>() ));
         communicationManager.registerSubscriber(new SubscriberConfiguration("Seconds",
            0,
            1000,
            1000,
            0,
            100, new SecondsSubscriber<Integer>() ));
         communicationManager.registerSubscriber(new SubscriberConfiguration("Pressure",
            0,
            1000,
            1000,
            0,
            100,new PressureSubscriber<Integer>() ));
         communicationManager.registerSubscriber(new SubscriberConfiguration("DeviceState",
            0,
            1000,
            1000,
            0,
            100,new DeviceStateSubscriber<Integer>() ));
         communicationManager.registerSubscriber(new SubscriberConfiguration("PulseRate",
            0,
            1000,
            1000,
            0,
            100, new PulseRateSubscriber<Integer>() ));
  }

  void mvcGroupDestroy() {
    println('destroying')
    dds.destroy()
  }
  /*
  def onSeconds(seconds) { 
    edt { model.seconds = seconds }
  }*/
/*
  def onDeviceState(state) { 
    edt { model.state = state?"Active":"Inactive" }
   if (state == 1) {
      def tmp1 = new SimpleValue()
         tmp1.value = 1
         dds.publish(Topics.BPFREQUENCY, tmp1)
   } else {
      def tmp1 = new SimpleValue()
         tmp1.value = 0
         dds.publish(Topics.BPFREQUENCY, tmp1)
   }
  }*/
  /*
  def onSYSTOLIC(systolic) { 
    edt { model.systolic = systolic }
    allhookedup |= 0x1
    if (!model.needToStop && allhookedup == 0xF) {
      app.event "Update"
    }
  }*/
/*
  def onDIASTOLIC(diastolic) {
    edt { model.diastolic = diastolic }
    allhookedup |= 0x2
    if (!model.needToStop && allhookedup == 0xF) {
      app.event "Update"
    }
  }*/
/*
  def onPRESSURE(pressure) {
    edt { model.pressure = pressure }
    allhookedup |= 0x4
    if (!model.needToStop && allhookedup == 0xF) {
      app.event "Update"
    }
  }*/
/*
  def onPulseRate(pr) {
    edt { model.pulseRate = pr }
    allhookedup |= 0x8
    if (!model.needToStop && allhookedup == 0xF) {
      app.event "Update"
    }
  }*/

  def onUpdate = { evt ->
    println("updating $allhookedup")
    def needToStop = model.systolic < 96 || 
      model.diastolic < 50 || model.pulseRate < 50

    if (needToStop) {
      println("stop pump ${model.systolic} ${model.diastolic} ${model.pressure} ${model.pulseRate}")
      //CompletionStatus reply = dds.issueCommandTo(Command.STOP, 
      //    Constants.INSUFFLATION_PUMP)
   insufflatorShutOffPublisher.publish(1);
      //if (reply == CompletionStatus.SUCCESS) {
        edt {
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
        edt { model.needToStop = true }
      /*
      } else {
        edt {
          JOptionPane.showMessageDialog(null,
              """PANIC: Insufflation pump non-responsive to stop command.
              systolic: ${model.systolic}
              diastolic: ${model.diastolic}
              pulse rate: ${model.pulseRate}
              pressure: ${model.pressure}
              """,
              'PANIC', 
              JOptionPane.ERROR_MESSAGE)
        }
      }*/
    }
  }
}
