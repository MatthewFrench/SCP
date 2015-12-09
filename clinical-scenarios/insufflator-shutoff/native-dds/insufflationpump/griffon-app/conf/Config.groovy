application {
    title = 'insufflationpump'
    startupGroups = ['insufflationpump']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "insufflationpump"
    'insufflationpump' {
        model      = 'clinicalscenario.insufflationpump.InsufflationpumpModel'
        view       = 'clinicalscenario.insufflationpump.InsufflationpumpView'
        controller = 'clinicalscenario.insufflationpump.InsufflationpumpController'
    }
}