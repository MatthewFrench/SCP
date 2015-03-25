/**  
 * Authors:
 *   Venkatesh-Prasad Ranganath
 * 
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0 
 * http://www.eclipse.org/legal/epl-v10.html                             
 */

package pulseoximeter

import groovy.beans.Bindable

class PulseoximeterModel {
   @Bindable int spo2= 99
   @Bindable int pulseRate = 80 
}
