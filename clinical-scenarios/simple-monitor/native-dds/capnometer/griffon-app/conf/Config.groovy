application {
    title = 'capnometer'
    startupGroups = ['capnometer']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "capnometer"
    'capnometer' {
        model      = 'clinicalscenario.simplemonitor.CapnometerModel'
        view       = 'clinicalscenario.simplemonitor.CapnometerView'
        controller = 'clinicalscenario.simplemonitor.CapnometerController'
    }
}