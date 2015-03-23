/**
 *  Authors:
 *    Venkatesh-Prasad Ranganath
 *  
 *  Copyright (c) 2014, Kansas State University
 *  Licensed under Eclipse Public License v1.0 
 *  http://www.eclipse.org/legal/epl-v10.html                             
 */

package pulseoximeter

application(title: 'pulseoximeter',
  preferredSize: [160, 100],
  pack: true,
  locationByPlatform: true,
  iconImage:   imageIcon('/griffon-icon-48x48.png').image,
  iconImages: [imageIcon('/griffon-icon-48x48.png').image,
               imageIcon('/griffon-icon-32x32.png').image,
               imageIcon('/griffon-icon-16x16.png').image]) {
  panel() {
    migLayout()
    label text: "SpO2"
    label text: bind { model.spo2 }, 
      constraints: 'wrap', foreground: Color.BLUE
    label text: "Pulse Rate"
    label text: bind { model.pulseRate }, 
      constraints: 'wrap', foreground: Color.BLUE
  }
}
