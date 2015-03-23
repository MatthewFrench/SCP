/**  
 * Authors:
 *   Venkatesh-Prasad Ranganath
 * 
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0 
 * http://www.eclipse.org/legal/epl-v10.html                             
 */

package pcamonitor

import groovy.beans.Bindable

class PcamonitorModel {
   @Bindable boolean needToStop
   @Bindable int etco2
   @Bindable int spo2
   @Bindable int pulseRate
   @Bindable int respiratoryRate

}
