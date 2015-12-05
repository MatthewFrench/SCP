application {
    title = 'monitor'
    startupGroups = ['monitor']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "monitor"
    'monitor' {
        model      = 'clinicalscenario.monitor.MonitorModel'
        view       = 'clinicalscenario.monitor.MonitorView'
        controller = 'clinicalscenario.monitor.MonitorController'
    }
}