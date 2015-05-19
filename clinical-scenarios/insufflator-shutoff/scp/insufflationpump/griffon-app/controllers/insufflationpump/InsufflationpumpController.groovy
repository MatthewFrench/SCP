/**  
* Authors:
*   Venkatesh-Prasad Ranganath and Matthew French
* 
* Copyright (c) 2014, Kansas State University
* Licensed under Eclipse Public License v1.0 
* http://www.eclipse.org/legal/epl-v10.html                             
*/

package insufflationpump

import org.vertx.groovy.core.Vertx;
import org.vertx.groovy.core.*

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
import scp.targets.vertx.CommunicationManagerImpl as CommunicationManagerVertX;
import scp.targets.dds.CommunicationManagerImpl as CommunicationManagerDDS;

class InsufflationpumpController {
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
      communicationManager = new CommunicationManagerVertX(0, "localhost")
    }

    communicationManager.setUp()
    //Create the publishers
    deviceStatePublisher = communicationManager.createPublisher(new PublisherConfiguration("Device State", 1, 10000, 20000, Integer.class)).second
    pressurePublisher = communicationManager.createPublisher(new PublisherConfiguration("Insufflator Pressure", 1, 10000, 20000, Integer.class)).second
    //Create the turn off executor
    communicationManager.registerExecutor(new ExecutorConfiguration("InsufflatorShutOff", 1, 10000, new Executor() {
        Executor.ExecutionAcknowledgement execute(java.io.Serializable action) {
          deviceOn = false
          edt{
            model.state = 'Inactive'
          }
          return Executor.ExecutionAcknowledgement.ACTION_SUCCEEDED
        }
    }));

    edt{
      model.state = 'Active'
    }

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
    edt {
      model.pressure = pressure
    }
    //Send the current device state and pressure
    pressurePublisher.publish(pressure);
    deviceStatePublisher.publish(deviceOn?1:0);
  }
}