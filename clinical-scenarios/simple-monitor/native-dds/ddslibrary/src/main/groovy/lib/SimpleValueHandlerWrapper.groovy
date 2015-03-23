/**
 *  Authors:
 *    Venkatesh-Prasad Ranganath
 *  
 *  Copyright (c) 2014, Kansas State University
 *  Licensed under Eclipse Public License v1.0 
 *  http://www.eclipse.org/legal/epl-v10.html                             
 */

package lib

import lib.SimpleValue
import com.rti.dds.subscription.DataReader
import com.rti.dds.subscription.DataReaderAdapter
import com.rti.dds.subscription.RequestedDeadlineMissedStatus 
import com.rti.dds.subscription.SampleInfo

class SimpleValueHandlerWrapper extends DataReaderAdapter {

  private handler

  def SimpleValueHandlerWrapper(handler) {
    this.handler = handler
  }

  void on_data_available(DataReader reader) {
    SimpleValue value = new SimpleValue()
    SampleInfo sInfo = new SampleInfo()
    reader.take_next_sample(value, sInfo)
    handler(value.value)
  }
}

