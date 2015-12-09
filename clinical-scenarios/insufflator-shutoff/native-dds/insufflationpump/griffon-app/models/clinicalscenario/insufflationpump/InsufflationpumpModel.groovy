package clinicalscenario.insufflationpump

import griffon.core.artifact.GriffonModel
import griffon.transform.Observable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class InsufflationpumpModel {
	   @Observable String state = "Active"
   @Observable float pressure = 0.0
}