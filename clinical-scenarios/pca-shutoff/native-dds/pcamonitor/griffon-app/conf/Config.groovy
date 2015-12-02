application {
    title = 'pcamonitor'
    startupGroups = ['pcamonitor']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "pcamonitor"
    'pcamonitor' {
        model      = 'clinicalscenario.PcamonitorModel'
        view       = 'clinicalscenario.PcamonitorView'
        controller = 'clinicalscenario.PcamonitorController'
    }
}