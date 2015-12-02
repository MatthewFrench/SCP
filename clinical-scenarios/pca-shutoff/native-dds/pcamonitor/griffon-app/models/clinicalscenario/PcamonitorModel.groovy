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
class PcamonitorModel {
   @Observable boolean needToStop
   @Observable int etco2
   @Observable int spo2
   @Observable int pulseRate
   @Observable int respiratoryRate
}
