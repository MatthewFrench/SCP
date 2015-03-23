application {
    title = 'Capnometer'
    startupGroups = ['capnometer']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "capnometer"
    'capnometer' {
        model      = 'capnometer.CapnometerModel'
        view       = 'capnometer.CapnometerView'
        controller = 'capnometer.CapnometerController'
    }

}
