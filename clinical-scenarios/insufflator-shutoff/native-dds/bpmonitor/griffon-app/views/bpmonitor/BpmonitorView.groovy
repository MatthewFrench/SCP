/**  
 * Authors:
 *   Venkatesh-Prasad Ranganath
 * 
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0 
 * http://www.eclipse.org/legal/epl-v10.html                             
 */

package bpmonitor

application(title: 'bpmonitor',
  preferredSize: [160, 130],
  pack: true,
  locationByPlatform: true,
  iconImage:   imageIcon('/griffon-icon-48x48.png').image,
  iconImages: [imageIcon('/griffon-icon-48x48.png').image,
               imageIcon('/griffon-icon-32x32.png').image,
               imageIcon('/griffon-icon-16x16.png').image]) {
  panel() {
    migLayout()
    label text: "Systolic"
    label text: bind { model.systolic }, 
      constraints: 'wrap', foreground: Color.BLUE
    label text: "Diastolic"
    label text: bind { model.diastolic }, 
      constraints: 'wrap', foreground: Color.BLUE
    label text: "Pulse Rate"
    label text: bind { model.pulseRate }, 
      constraints: 'wrap', foreground: Color.BLUE
	label text: "Seconds"
    label text: bind { model.seconds }, 
      constraints: 'wrap', foreground: Color.BLUE
  }
}
