package clinicalscenario.monitor

import griffon.core.artifact.GriffonModel
import griffon.transform.Observable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class MonitorModel {
  @Observable boolean needToStop
   @Observable int pressure
   @Observable int diastolic
   @Observable int pulseRate
   @Observable int systolic
   @Observable int seconds
   @Observable String state
}