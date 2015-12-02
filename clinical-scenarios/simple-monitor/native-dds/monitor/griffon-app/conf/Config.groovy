application {
    title = 'monitor'
    startupGroups = ['monitor']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "monitor"
    'monitor' {
        model      = 'clinicalscenario.simplemonitor.MonitorModel'
        view       = 'clinicalscenario.simplemonitor.MonitorView'
        controller = 'clinicalscenario.simplemonitor.MonitorController'
    }
}