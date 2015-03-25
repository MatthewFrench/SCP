/**  
 * Authors:
 *   Venkatesh-Prasad Ranganath
 * 
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0 
 * http://www.eclipse.org/legal/epl-v10.html                             
 */

package insufflationpump

import groovy.beans.Bindable

class InsufflationpumpModel {
   @Bindable String state = "Active"
   @Bindable float pressure = 0.0
}
