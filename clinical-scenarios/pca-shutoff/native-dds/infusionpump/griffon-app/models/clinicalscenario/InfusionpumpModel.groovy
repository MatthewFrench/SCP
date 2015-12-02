/**  
 * Authors:
 *   Venkatesh-Prasad Ranganath
 * 
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0 
 * http://www.eclipse.org/legal/epl-v10.html                             
 */

package clinicalscenario

import griffon.core.artifact.GriffonModel
import griffon.transform.Observable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class InfusionpumpModel {
   @Observable String state = "Active"
   @Observable String drugName = "Morphine"
   @Observable String concentration = 1 // mg/ml
   @Observable float basal = 0.5 // 0.5-2 mcg/hr
   @Observable float dose = 1.0 // 0.5-3 mcg/hr
}
