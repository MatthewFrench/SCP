application {
    title = 'capnometer'
    startupGroups = ['capnometer']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "capnometer"
    'capnometer' {
        model      = 'clinicalscenario.CapnometerModel'
        view       = 'clinicalscenario.CapnometerView'
        controller = 'clinicalscenario.CapnometerController'
    }
}