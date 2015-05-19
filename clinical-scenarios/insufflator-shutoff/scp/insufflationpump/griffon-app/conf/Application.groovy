application {
    title = 'Insufflationpump'
    startupGroups = ['insufflationpump']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "insufflationpump"
    'insufflationpump' {
        model      = 'insufflationpump.InsufflationpumpModel'
        view       = 'insufflationpump.InsufflationpumpView'
        controller = 'insufflationpump.InsufflationpumpController'
    }

}
