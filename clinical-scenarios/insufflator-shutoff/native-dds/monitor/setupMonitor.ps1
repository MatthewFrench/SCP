#
#  Authors:
#    Venkatesh-Prasad Ranganath
#
#  Copyright (c) 2014, Kansas State University
#  Licensed under Eclipse Public License v1.0
#  http://www.eclipse.org/legal/epl-v10.html
#

$env:NDDSHOME="C:\Program Files (x86)\RTI\ndds.5.1.0"
$env:RTI_LICENSE_FILE="C:\Program Files (x86)\RTI\rti_license.dat"
$env:PATH=$env:PATH + ";C:\Program Files (x86)\RTI\ndds.5.1.0\lib\x64Win64jdk"

$scriptpath = $MyInvocation.MyCommand.Path
$dir = Split-Path $scriptpath

$griffonScript = $dir + "\griffonw.bat"

$gradleScript = $dir + "\..\ddslibrary\gradlew.bat";

cd $dir+"\..\ddslibrary"

Start-Process powershell -ArgumentList "$gradleScript clean build copyLibs"

# Write-Host "DDS Library re-built, setting up layout"

cd $dir

.$griffonScript install-plugin miglayout