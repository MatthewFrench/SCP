/**
 *  Authors:
 *    Venkatesh-Prasad Ranganath
 *  
 *  Copyright (c) 2014, Kansas State University
 *  Licensed under Eclipse Public License v1.0 
 *  http://www.eclipse.org/legal/epl-v10.html                             
 */

package lib

import com.rti.dds.domain.DomainParticipant
import com.rti.dds.domain.DomainParticipantFactory
import com.rti.dds.infrastructure.InstanceHandle_t
import com.rti.dds.infrastructure.StatusKind
import com.rti.dds.publication.Publisher
import com.rti.dds.subscription.Subscriber

class DDS {
  private final int DOMAIN_ID = 0
  private final participant
  private final topicName2reader = [:], 
                topicName2writer = [:]

  public def DDS() {
    participant = DomainParticipantFactory.get_instance().create_participant(
        DOMAIN_ID, 
        DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT,
        null, // Listener
        StatusKind.STATUS_MASK_NONE) 
    assert participant != null 
    SimpleValueTypeSupport.register_type(
        participant, 
        SimpleValueTypeSupport.get_type_name())
  }

  public subscribeTo(topicName, topicTypeName, listener) {
    if (!topicName2reader.containsKey(topicName)) {
      println("subscriber $topicName")
      def topic = participant.create_topic(
          topicName,
          topicTypeName, 
          DomainParticipant.TOPIC_QOS_DEFAULT,
          null,
          StatusKind.STATUS_MASK_NONE)
      def reader = participant.create_datareader(
          topic,
          Subscriber.DATAREADER_QOS_DEFAULT,
          listener,
          StatusKind.DATA_AVAILABLE_STATUS)
      topicName2reader[topicName] = reader
    }
  }

  public publishOn(topicName, topicTypeName) {
    if (!topicName2writer.containsKey(topicName)) {
      println("publisher $topicName")
      def topic = participant.create_topic(
          topicName,
          topicTypeName, 
          DomainParticipant.TOPIC_QOS_DEFAULT,
          null,
          StatusKind.STATUS_MASK_NONE)
      def writer = participant.create_datawriter(
          topic,
          Publisher.DATAWRITER_QOS_DEFAULT,
          null,
          StatusKind.STATUS_MASK_NONE)
      topicName2writer[topicName] = writer
    }
  }

  public def publish(topicName, value) {
    if (topicName2writer.containsKey(topicName)) {
      // println("publish $topicName $value")
      def writer = topicName2writer[topicName]
      writer.write(value, InstanceHandle_t.HANDLE_NIL)
    } else {
      throw new IllegalStateException("topic $topicName not registered.")
    }
  }

  public def destroy() {
    println('destroying participant')
    if (this.participant != null) {
      this.participant.delete_contained_entities();
      DomainParticipantFactory.get_instance().delete_participant(this.participant);
    }
  }
}
