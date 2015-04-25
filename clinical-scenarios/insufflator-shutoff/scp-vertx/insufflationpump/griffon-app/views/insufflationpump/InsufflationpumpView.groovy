/**  
 * Authors:
 *   Venkatesh-Prasad Ranganath
 * 
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0 
 * http://www.eclipse.org/legal/epl-v10.html                             
 */

package insufflationpump

application(title: 'insufflationpump',
  preferredSize: [160, 100],
  pack: true,
  locationByPlatform: true,
  iconImage:   imageIcon('/griffon-icon-48x48.png').image,
  iconImages: [imageIcon('/griffon-icon-48x48.png').image,
               imageIcon('/griffon-icon-32x32.png').image,
               imageIcon('/griffon-icon-16x16.png').image]) {
  panel() {
    migLayout()
    model.griffonClass.propertyNames.each { name ->
      color = name == 'state' ? Color.BLUE : Color.BLACK
        label text: name, constraints: 'left'
        label text: bind(source:model, name), constraints: 'growx, wrap',
          foreground: color
    }
  }
}
