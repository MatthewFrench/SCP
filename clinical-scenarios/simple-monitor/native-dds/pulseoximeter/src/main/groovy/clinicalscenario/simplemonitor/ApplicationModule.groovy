/**
 *  Authors:
 *    Venkatesh-Prasad Ranganath
 *  
 *  Copyright (c) 2014, Kansas State University
 *  Licensed under Eclipse Public License v1.0 
 *  http://www.eclipse.org/legal/epl-v10.html                             
 */

package clinicalscenario.simplemonitor

import griffon.core.event.EventHandler
import griffon.inject.DependsOn
import griffon.core.injection.Module
import griffon.util.BuilderCustomizer
import griffon.builder.swing.MiglayoutSwingBuilderCustomizer
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.kordamp.jipsy.ServiceProviderFor

@DependsOn('swing-groovy')
@ServiceProviderFor(Module)
class ApplicationModule extends AbstractModule {
  @Override
  protected void doConfigure() {
    bind(EventHandler)
      .to(ApplicationEventHandler)
      .asSingleton()
    bind(BuilderCustomizer.class)
      .to(MiglayoutSwingBuilderCustomizer.class)
      .asSingleton()
  }
}
