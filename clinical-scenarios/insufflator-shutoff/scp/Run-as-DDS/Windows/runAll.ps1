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

cd $dir

start-process powershell.exe -argument '-command .\runBPMonitor.ps1'
start-process powershell.exe -argument '-command .\runInsufflatorPump.ps1'
start-process powershell.exe -argument '-command .\runMonitor.ps1'
# Read-Host -Prompt "Press Enter to exit."