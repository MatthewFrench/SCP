application {
    title = 'pulseoximeter'
    startupGroups = ['pulseoximeter']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "pulseoximeter"
    'pulseoximeter' {
        model      = 'clinicalscenario.simplemonitor.PulseoximeterModel'
        view       = 'clinicalscenario.simplemonitor.PulseoximeterView'
        controller = 'clinicalscenario.simplemonitor.PulseoximeterController'
    }
}