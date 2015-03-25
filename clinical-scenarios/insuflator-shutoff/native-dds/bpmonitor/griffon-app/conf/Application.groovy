application {
    title = 'Pulseoximeter'
    startupGroups = ['pulseoximeter']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "pulseoximeter"
    'pulseoximeter' {
        model      = 'pulseoximeter.PulseoximeterModel'
        view       = 'pulseoximeter.PulseoximeterView'
        controller = 'pulseoximeter.PulseoximeterController'
    }

}
