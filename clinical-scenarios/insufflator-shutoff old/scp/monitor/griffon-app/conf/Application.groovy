application {
    title = 'Monitor'
    startupGroups = ['monitor']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "monitor"
    'monitor' {
        model      = 'monitor.MonitorModel'
        view       = 'monitor.MonitorView'
        controller = 'monitor.MonitorController'
    }

}
