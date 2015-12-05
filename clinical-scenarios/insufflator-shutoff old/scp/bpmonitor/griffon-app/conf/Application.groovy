application {
    title = 'Bpmonitor'
    startupGroups = ['bpmonitor']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "bpmonitor"
    'bpmonitor' {
        model      = 'bpmonitor.BpmonitorModel'
        view       = 'bpmonitor.BpmonitorView'
        controller = 'bpmonitor.BpmonitorController'
    }

}
