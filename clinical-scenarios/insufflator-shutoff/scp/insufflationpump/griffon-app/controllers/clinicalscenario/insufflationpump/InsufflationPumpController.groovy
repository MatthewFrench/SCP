package clinicalscenario.insufflationpump

import griffon.core.artifact.GriffonController
import griffon.metadata.ArtifactProviderFor

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

@ArtifactProviderFor(GriffonController)
class InsufflationPumpController {
    // these will be injected by Griffon
  def model
  def view

  def deviceOn = true
  def pressure = 0.0
  def randomGenerator = new Random()
  def communicationManager
  
  def deviceStatePublisher, pressurePublisher

    def SCP_DDS = 0, SCP_VERTX = 1
  def scpPattern = SCP_VERTX

//Minimum duration of time (in milliseconds) between two consecutive consumptions. 
//In other words, after a consumption of data, new data will be inhibited for this duration of time.

//Maximum latency to consume the data (in milliseconds).

//Minimum remaining lifetime required of the consumed data (in milliseconds).


  void mvcGroupInit(Map args) {
    def startupArgs = getApplication().getStartupArgs()
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

    //createPublisher( String topic, long minimumSeparation, long maximumLatency, long minimumRemainingLifetime, Class<T> dataType, CommunicationManager communicationManager)
//createPublisher("testSuccessfulPublication", 1000, this.minimumInteractionLatency + 50, 600, Integer.class, this.communicationManager); 
    deviceStatePublisher = communicationManager.createPublisher(new PublisherConfiguration("devicestate", 1, 50000, 50000,  Integer.class)).second
    pressurePublisher = communicationManager.createPublisher(new PublisherConfiguration("insufflatorpressure", 1, 50000, 50000,  Integer.class)).second
    //Create the turn off executor

    //registerExecutor( String identifier, long minimumSeparation, long maximumLatency, Executor<Integer> executor)
//registerExecutor(_executorId, 1000, 100, _tmp1);
Thread.start {
    communicationManager.registerExecutor(new ExecutorConfiguration("insufflatorshutoff", 1, 50000, new Executor() {
        Executor.ExecutionAcknowledgement execute(java.io.Serializable action) {
          deviceOn = false
          //edt{
            model.state = 'Inactive'
          //}
          return Executor.ExecutionAcknowledgement.ACTION_SUCCEEDED
        }
    }));
  }

    //edt{
      model.state = 'Active'
    //}

    new javax.swing.Timer(1000, update).start()
  }

  void mvcGroupDestroy() {
    //communicationManager.tearDown()
  }
  
  def update = { evt ->
    if (deviceOn == true ) {
      pressure += randomGenerator.nextInt(10) / 10.0
      if (pressure > 15) {
        pressure = 15
      }
    } else {
      pressure -= randomGenerator.nextInt(10) / 10.0
      if (pressure < 0) {
        pressure = 0
      }
    }
    //edt {
      model.pressure = pressure
    //}
    //Send the current device state and pressure
    pressurePublisher.publish(pressure);
    deviceStatePublisher.publish(deviceOn?1:0);
  }
}