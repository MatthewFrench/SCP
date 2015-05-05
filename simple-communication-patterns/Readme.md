# Simple Communication Patterns (SCP)

This sub-project proposes a small set of simple communication patterns to enable
communication between medical devices and apps.  It documents the [communication
patterns](https://bitbucket.org/rvprasad/clinical-scenarios/downloads/mdd4ms.pdf) and the rationale for their design.  In addition, it proposes an API
along with a prototype implementation of the API based on [RTI Connext
DDS](http://www.rti.com) and [Vert.x](http://vertx.io).

## Requirements

* [Gradle](http://www.gradle.org/) 2.2+
* [Java](http://www.oracle.com/technetwork/java/javase/%20downloads/index.html) 1.8+
* [RTI Connext DDS](http://www.rti.com/products/dds/index.html) 5.1.0+
* [Vert.x](http://vertx.io) 2.1.5+

*Note:* On Unix based system, use [gvm](http://gvmtool.net) to get groovy,
griffon, and gradle.  On Windows based system, use Powershell as the 
command-line shell and get groovy, griffon, and gradle via 
[posh-gvm](https://github.com/flofreud/posh-gvm).
  

## Build

* Modify the variables in *scripts/setEnv.sh (or scripts/setEnv.ps1 on
  Windows)* according to your RTI DDS installation and then source it into your
  shell. 
  Note: *setEnv.ps1* has not been tested.
* Copy over *nddsjava.jar* and *rticonnextmsg.jar* from your RTI 
  DDS installation into *libs* folder.
* Execute `gradle clean build`.
