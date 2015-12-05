package clinicalscenario.insufflationpump

import griffon.core.artifact.GriffonView
import griffon.metadata.ArtifactProviderFor
import javax.swing.SwingConstants

@ArtifactProviderFor(GriffonView)
class InsufflationPumpView {
    FactoryBuilderSupport builder
    InsufflationPumpModel model

    void initUI() {
        builder.with {
            application(size: [320, 220], id: 'mainWindow',
                title: application.configuration['application.title'],
                iconImage:   imageIcon('/griffon-icon-48x48.png').image,
                iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                             imageIcon('/griffon-icon-32x32.png').image,
                             imageIcon('/griffon-icon-16x16.png').image]) {
        panel(border: emptyBorder(6)) {
          migLayout(layoutConstraints: 'fill')
          model.griffonClass.propertyNames.each { name ->
            label text: name
            label text: bind(source:model, name), constraints: 'wrap'
          }
        }
            }
        }
    }
}