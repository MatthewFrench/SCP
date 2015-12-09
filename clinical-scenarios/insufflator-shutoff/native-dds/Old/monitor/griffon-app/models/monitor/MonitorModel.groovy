/**  
 * Authors:
 *   Venkatesh-Prasad Ranganath
 * 
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0 
 * http://www.eclipse.org/legal/epl-v10.html                             
 */

package monitor

import groovy.beans.Bindable

class MonitorModel {
   @Bindable boolean needToStop
   @Bindable int pressure
   @Bindable int diastolic
   @Bindable int pulseRate
   @Bindable int systolic
   @Bindable int seconds
   @Bindable String state
}
