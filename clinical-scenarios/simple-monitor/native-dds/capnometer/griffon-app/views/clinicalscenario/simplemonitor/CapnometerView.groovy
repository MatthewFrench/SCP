/**
 *  Authors:
 *    Venkatesh-Prasad Ranganath
 *  
 *  Copyright (c) 2014, Kansas State University
 *  Licensed under Eclipse Public License v1.0 
 *  http://www.eclipse.org/legal/epl-v10.html                             
 */

package clinicalscenario.simplemonitor

import griffon.core.artifact.GriffonView
import griffon.metadata.ArtifactProviderFor
import java.awt.Color

@ArtifactProviderFor(GriffonView)
class CapnometerView {
  FactoryBuilderSupport builder
  def model

  void initUI() {
    builder.with {
      application(title: 'capnometer',
        preferredSize: [160, 100],
        pack: true,
        locationByPlatform: true,
        iconImage:   imageIcon('/griffon-icon-48x48.png').image,
        iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                     imageIcon('/griffon-icon-32x32.png').image,
                     imageIcon('/griffon-icon-16x16.png').image]) {
        panel(border: emptyBorder(6)) {
          migLayout(layoutConstraints: 'fill')
          label text: "End Tidal CO2"
          label text: bind { model.etco2 }, foreground: Color.BLUE, 
            constraints: 'wrap'
          label text: "Respiratory Rate"
          label text: bind { model.respiratoryRate }, foreground: Color.BLUE, 
            constraints: 'wrap'
        }
      }
    }
  }
}
