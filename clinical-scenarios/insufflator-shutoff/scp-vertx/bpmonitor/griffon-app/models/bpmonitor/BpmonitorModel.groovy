/**  
 * Authors:
 *   Venkatesh-Prasad Ranganath
 * 
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0 
 * http://www.eclipse.org/legal/epl-v10.html                             
 */

package bpmonitor

import groovy.beans.Bindable

class BpmonitorModel {
   @Bindable int systolic= 120
   @Bindable int diastolic= 80
   @Bindable int pulseRate = 80
   @Bindable int seconds = 0
}
