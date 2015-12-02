/**  
 * Authors:
 *   Venkatesh-Prasad Ranganath
 * 
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0 
 * http://www.eclipse.org/legal/epl-v10.html                             
 */

package clinicalscenario

import griffon.core.artifact.GriffonView
import griffon.metadata.ArtifactProviderFor
import javax.swing.SwingConstants

@ArtifactProviderFor(GriffonView)
class PcamonitorView {
  FactoryBuilderSupport builder
  def model

  void initUI() {
    builder.with {
      application(size: [320, 160], id: 'mainWindow',
        pack: true,
        locationByPlatform: true,
        title: application.configuration['application.title'],
        iconImage: imageIcon('/griffon-icon-48x48.png').image,
        iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                     imageIcon('/griffon-icon-32x32.png').image,
                     imageIcon('/griffon-icon-16x16.png').image]) {
        panel() {
          migLayout()
          model.griffonClass.propertyNames.each { name ->
            label text: name
            label text: bind(source:model, name), constraints: 'wrap'
          }
        }
      }
    }
  }
}
