#
#  Authors:
#    Venkatesh-Prasad Ranganath
#
#  Copyright (c) 2014, Kansas State University
#  Licensed under Eclipse Public License v1.0
#  http://www.eclipse.org/legal/epl-v10.html
#

# won't work unless you have added stuff to the path:
# C:\Program Files\rti_connext_dds-5.2.0\lib\x64Win64VS2013
# C:\Program Files\rti_connext_dds-5.2.0\lib\java

# $env:NDDSHOME="C:\Program Files\rti_connext_dds-5.2.0"
$env:NDDSHOME="C:\Program Files\rti_connext_dds-5.2.0\lib\x64Win64VS2013"
$env:RTI_LICENSE_FILE="C:\Program Files\rti_connext_dds-5.2.0\rti_license.dat"
# $env:PATH=$env:PATH + ";C:\Program Files\rti_connext_dds-5.2.0\lib\java"
# $env:PATH=$env:PATH + ";C:\Program Files\rti_connext_dds-5.2.0\lib\x64Win64VS2013"

$scriptpath = $MyInvocation.MyCommand.Path
$dir = Split-Path $scriptpath

echo $env:PATH

cd $dir

cd ..

cd ..

cd "monitor"

gvm v

gradlew run -PappArgs="['dds']"
# Read-Host -Prompt "Press Enter to exit."