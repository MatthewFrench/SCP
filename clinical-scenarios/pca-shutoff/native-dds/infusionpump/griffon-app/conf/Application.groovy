application {
    title = 'Infusionpump'
    startupGroups = ['infusionpump']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "infusionpump"
    'infusionpump' {
        model      = 'infusionpump.InfusionpumpModel'
        view       = 'infusionpump.InfusionpumpView'
        controller = 'infusionpump.InfusionpumpController'
    }

}
