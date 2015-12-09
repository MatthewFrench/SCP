#
#  Authors:
#    Venkatesh-Prasad Ranganath
#
#  Copyright (c) 2014, Kansas State University
#  Licensed under Eclipse Public License v1.0
#  http://www.eclipse.org/legal/epl-v10.html
#

# $env:NDDSHOME="C:\Program Files (x86)\RTI\ndds.5.1.0"
# $env:RTI_LICENSE_FILE="C:\Program Files (x86)\RTI\rti_license.dat"
# $env:PATH=$env:PATH + ";C:\Program Files (x86)\RTI\ndds.5.1.0\lib\x64Win64jdk"

$env:NDDSHOME="C:\Program Files\rti_connext_dds-5.2.0\lib\x64Win64VS2013"
$env:RTI_LICENSE_FILE="C:\Program Files\rti_connext_dds-5.2.0\rti_license.dat"

$scriptpath = $MyInvocation.MyCommand.Path
$dir = Split-Path $scriptpath

cd $dir

cd ..

cd ..

cd "insufflationpump"

$griffonScript = "./griffonw.bat"

gvm v

gradle run
# Read-Host -Prompt "Press Enter to exit."