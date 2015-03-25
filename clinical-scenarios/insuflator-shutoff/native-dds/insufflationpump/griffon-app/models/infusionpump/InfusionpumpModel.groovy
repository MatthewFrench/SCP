/**  
 * Authors:
 *   Venkatesh-Prasad Ranganath
 * 
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0 
 * http://www.eclipse.org/legal/epl-v10.html                             
 */

package infusionpump

import groovy.beans.Bindable

class InfusionpumpModel {
   @Bindable String state = "Active"
   @Bindable String drugName = "Morphine"
   @Bindable String concentration = 1 // mg/ml
   @Bindable float basal = 0.5 // 0.5-2 mcg/hr
   @Bindable float dose = 1.0 // 0.5-3 mcg/hr
}
