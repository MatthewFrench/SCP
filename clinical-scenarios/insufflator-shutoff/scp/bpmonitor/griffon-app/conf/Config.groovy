application {
    title = 'bpmonitor'
    startupGroups = ['BPMonitor']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "BPMonitor"
    'BPMonitor' {
        model      = 'clinicalscenario.bpmonitor.BPMonitorModel'
        view       = 'clinicalscenario.bpmonitor.BPMonitorView'
        controller = 'clinicalscenario.bpmonitor.BPMonitorController'
    }
}