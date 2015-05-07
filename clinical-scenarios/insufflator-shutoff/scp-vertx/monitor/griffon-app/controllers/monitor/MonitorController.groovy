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
  def communicationManager

//class SecondsSubscriber<T> extends Subscriber.AbstractSubscriber {
//   void consume(T data, long remainingLifetime) {
//   writeln("Recieved seconds");
//   edt { model.seconds = data }
//   }
//}
Sender<Integer> bpFrequencySender
Initiator<Integer> insufflatorShutOffInitiator
Requester<Integer> secondsRequester;
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
communicationManager = new CommunicationManagerImpl(0, "localhost");
communicationManager.setUp()
bpFrequencySender = communicationManager.createSender(new SenderConfiguration("BPFrequency", 1, 10000, Integer.class)).second

insufflatorShutOffInitiator = communicationManager.createInitiator(
 new InitiatorConfiguration("InsufflatorShutOff", 1, 10000, Integer.class)).second

secondsRequester = communicationManager.createRequester(new RequesterConfiguration(
  "Seconds",
  1, 10000, 20000,
  Integer.class)).second;

communicationManager.registerSubscriber(new SubscriberConfiguration("Diastolic",
  1, 10000, 10000, 12, 1,new Subscriber.AbstractSubscriber() {
   void consume(java.io.Serializable data, long remainingLifetime) {
    println("Received diastolic")
    edt { model.diastolic = data }
    allhookedup |= 0x2
    if (!model.needToStop && allhookedup == 0xF) {
      onUpdate()
    }
  }
  } ));
communicationManager.registerSubscriber(new SubscriberConfiguration("Systolic",
  1, 10000, 10000, 12, 1,  new Subscriber.AbstractSubscriber() {
   void consume(java.io.Serializable data, long remainingLifetime) {
    println("Received systolid")
    edt { model.systolic = data }
    allhookedup |= 0x1
    if (!model.needToStop && allhookedup == 0xF) {
      onUpdate()
    }
  }
  } ));
communicationManager.registerSubscriber(new SubscriberConfiguration("Insufflator Pressure",
  1, 10000, 10000, 12, 1, new Subscriber.AbstractSubscriber() {
   void consume(java.io.Serializable data, long remainingLifetime) {
    println("Received pressure")
    edt { model.pressure = data }
    allhookedup |= 0x4
    if (!model.needToStop && allhookedup == 0xF) {
      onUpdate()
    }
  }
  }));
communicationManager.registerSubscriber(new SubscriberConfiguration("Device State",
  1, 10000, 10000, 12, 1, new Subscriber.AbstractSubscriber() {
   void consume(java.io.Serializable data, long remainingLifetime) {
    println("Received device state")
    edt { model.state = data?"Active":"Inactive" }
    if (data == 1) {
     bpFrequencySender.send(1);
               //def tmp1 = new SimpleValue()
               //   tmp1.value = 1
               //   dds.publish(Topics.BPFREQUENCY, tmp1)
               } else {
                 bpFrequencySender.send(0);
               //def tmp1 = new SimpleValue()
               //   tmp1.value = 0
               //   dds.publish(Topics.BPFREQUENCY, tmp1)
             }
           }
           } ));
def conf1 = new SubscriberConfiguration("PulseRate",
  1, 10000, 10000, 12, 1,  new Subscriber() {
   void consume(java.io.Serializable data, long remainingLifetime) {
    println("Received pulse rate")
    edt { model.pulseRate = data }
    allhookedup |= 0x8
    if (!model.needToStop && allhookedup == 0xF) {
      onUpdate()
    }
  }
  void handleStaleMessage(java.io.Serializable msg, long rlt) {

              println "Stale message $msg"
            }
            void handleSlowPublication() {
              println "slow pub"
            }
            void handleSlowConsumption(int i) {
              println "slow con $i"
            }
  } )
def tmp2 = communicationManager.registerSubscriber(conf1);
println(tmp2)


def conf = new SubscriberConfiguration("test",
          1, 10000, 10000, 12, 1, 
          new Subscriber() {
            void consume(java.io.Serializable msg, long rlt) {
              println "got test message $msg"
            }
            void handleStaleMessage(java.io.Serializable msg, long rlt) {

              println "Stale message $msg"
            }
            void handleSlowPublication() {
              println "slow pub"
            }
            void handleSlowConsumption(int i) {
              println "slow con $i"
            }
          });
      def tmp1 = communicationManager.registerSubscriber(conf)
      println tmp1


    new javax.swing.Timer(1000, update).start()
}

void mvcGroupDestroy() {
  println('destroying')
    //dds.destroy()
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
      onUpdate()
    }
    }*/
/*
  def onDIASTOLIC(diastolic) {
    edt { model.diastolic = diastolic }
    allhookedup |= 0x2
    if (!model.needToStop && allhookedup == 0xF) {
      onUpdate()
    }
    }*/
/*
  def onPRESSURE(pressure) {
    edt { model.pressure = pressure }
    allhookedup |= 0x4
    if (!model.needToStop && allhookedup == 0xF) {
      onUpdate()
    }
    }*/
/*
  def onPulseRate(pr) {
    edt { model.pulseRate = pr }
    allhookedup |= 0x8
    if (!model.needToStop && allhookedup == 0xF) {
      onUpdate()
    }
    }*/

    def update = { evt ->
      println("Requesting seconds")
      //edt {
        println("Attempting seconds requester request");
        def tmp = secondsRequester.request()
        println("Got seconds requestor request: " + tmp)
        println("Second: " + tmp.second);
        if (tmp.second != null) {
         model.seconds = tmp.second;
      //}
      }
    }

    def onUpdate = { evt ->
      println("updating $allhookedup")
      def needToStop = model.systolic < 96 || 
      model.diastolic < 50 || model.pulseRate < 50


      if (needToStop) {
        println("stop pump ${model.systolic} ${model.diastolic} ${model.pressure} ${model.pulseRate}")
      //CompletionStatus reply = dds.issueCommandTo(Command.STOP, 
      //    Constants.INSUFFLATION_PUMP)
insufflatorShutOffInitiator.initiate(1);
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
