application {
    title = 'pulseoximeter'
    startupGroups = ['pulseoximeter']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "pulseoximeter"
    'pulseoximeter' {
        model      = 'clinicalscenario.PulseoximeterModel'
        view       = 'clinicalscenario.PulseoximeterView'
        controller = 'clinicalscenario.PulseoximeterController'
    }
}