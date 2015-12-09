application {
    title = 'bpmonitor'
    startupGroups = ['bpmonitor']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "bpmonitor"
    'bpmonitor' {
        model      = 'clinicalscenario.bpmonitor.BpmonitorModel'
        view       = 'clinicalscenario.bpmonitor.BpmonitorView'
        controller = 'clinicalscenario.bpmonitor.BpmonitorController'
    }
}