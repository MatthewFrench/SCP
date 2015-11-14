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

  def secondsRequester
  def secondsNeedUpdate = true


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
    Pair<Status, Requester<Integer>> secondsStatusAndRequester = communicationManager.createRequester(new RequesterConfiguration("seconds", minimumSeparation, maximumLatency, minimumRemainingLifetime, Integer.class))
    secondsRequester = secondsStatusAndRequester.second
    println("Seconds status: " + secondsStatusAndRequester.first)

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
  }
}