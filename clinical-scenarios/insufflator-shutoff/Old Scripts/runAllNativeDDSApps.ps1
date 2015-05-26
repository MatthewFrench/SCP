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

Write-Host "Current directory: " $dir

cd $dir

$monitor = "./native-dds\monitor\runMonitor.ps1"
Start-Process powershell -ArgumentList $monitor

Write-Host "Running:" $monitor

$bpmonitor = "./native-dds\bpmonitor\runBPMonitor.ps1"
Start-Process powershell -ArgumentList $bpmonitor

Write-Host "Running:" $bpmonitor

$pump = "./native-dds\insufflationpump\runInsufflationPump.ps1"
Start-Process powershell -ArgumentList $pump

Write-Host "Running:" $pump