package clinicalscenario.bpmonitor

import griffon.core.artifact.GriffonModel
import griffon.transform.Observable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class BPMonitorModel {
   @Observable int systolic= 120
   @Observable int diastolic= 80
   @Observable int pulseRate = 80
   @Observable int seconds = 0
}