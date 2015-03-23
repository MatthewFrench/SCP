/**
 *  Authors:
 *    Venkatesh-Prasad Ranganath
 *  
 *  Copyright (c) 2014, Kansas State University
 *  Licensed under Eclipse Public License v1.0 
 *  http://www.eclipse.org/legal/epl-v10.html                             
 */

package capnometer

import groovy.beans.Bindable

class CapnometerModel {
   @Bindable int etco2 = 36
   @Bindable int respiratoryRate = 16
}
