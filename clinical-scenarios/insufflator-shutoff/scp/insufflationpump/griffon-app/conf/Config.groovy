application {
    title = 'insufflationpump'
    startupGroups = ['insufflationPump']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "insufflationPump"
    'insufflationPump' {
        model      = 'clinicalscenario.insufflationpump.InsufflationPumpModel'
        view       = 'clinicalscenario.insufflationpump.InsufflationPumpView'
        controller = 'clinicalscenario.insufflationpump.InsufflationPumpController'
    }
}