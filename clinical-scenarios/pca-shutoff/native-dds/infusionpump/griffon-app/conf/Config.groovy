application {
    title = 'infusionpump'
    startupGroups = ['infusionpump']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "infusionpump"
    'infusionpump' {
        model      = 'clinicalscenario.InfusionpumpModel'
        view       = 'clinicalscenario.InfusionpumpView'
        controller = 'clinicalscenario.InfusionpumpController'
    }
}