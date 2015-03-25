application {
    title = 'Pcamonitor'
    startupGroups = ['pcamonitor']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "pcamonitor"
    'pcamonitor' {
        model      = 'pcamonitor.PcamonitorModel'
        view       = 'pcamonitor.PcamonitorView'
        controller = 'pcamonitor.PcamonitorController'
    }

}
