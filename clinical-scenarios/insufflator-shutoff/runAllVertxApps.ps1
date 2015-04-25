#
#  Authors:
#    Venkatesh-Prasad Ranganath
#
#  Copyright (c) 2014, Kansas State University
#  Licensed under Eclipse Public License v1.0
#  http://www.eclipse.org/legal/epl-v10.html
#

$scriptpath = $MyInvocation.MyCommand.Path
$dir = Split-Path $scriptpath

Write-Host "Current directory: " $dir

cd $dir

$monitor = "./scp-vertx\monitor\runMonitor.ps1"
Start-Process powershell -ArgumentList $monitor

Write-Host "Running:" $monitor

$bpmonitor = "./scp-vertx\bpmonitor\runBPMonitor.ps1"
Start-Process powershell -ArgumentList $bpmonitor

Write-Host "Running:" $bpmonitor

$pump = "./scp-vertx\insufflationpump\runInsufflationPump.ps1"
Start-Process powershell -ArgumentList $pump

Write-Host "Running:" $pump