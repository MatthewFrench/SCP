# Clinical Scenarios

This sub-project explores the following clinical scenarios (in order of increasing
complexity) on top of various middleware using [a basic set of communication
patterns](https://bitbucket.org/rvprasad/clinical-scenarios/downloads/mdd4ms.pdf).

* Simple Monitor (*simple-monitor*) simulates a scenario where an application
monitors the measures from pulse oximeter and capnometer to alert the nurses'
station when the patient's condition deteriorates.
* PCA Shutoff (*pca-shutoff*) simulates a scenario where an application
monitors the measures from pulse oximeter and capnometer to shutoff an infusion
pump when the patient's condition deteriorates.


In each scenario,

* *native-dds* realizes the scenario by directly programming against RTI DDS
  API.  DDS is accessed via *ddslibrary* and the mock devices and apps are
  realized as griffon apps.
* *scp* realizes the scenario via communication patterns.  Mock devices and
  apps are realized as griffon apps.

Following are the components/applications in each scenario.

* *pca-shutoff*: capnometer, infusionpump, pcamonitor, and pulseoximeter.
* *simple-monitor*: capnometer, monitor, and pulseoximeter.


## Requirements

* [Gradle](http://www.gradle.org/) 2.6+
* [Griffon](http://griffon-framework.org/) 2.4.0+
* [Groovy](http://groovy.codehaus.org/) 2.3.10+
* [Java](http://www.oracle.com/technetwork/java/javase/%20downloads/index.html) 1.8+
* [RTI Connext DDS](http://www.rti.com/products/dds/index.html) 5.1.0+
* [Vert.x](http://vertx.io) 3.1.0+

*Note:* On Unix based system, use [sdkman](http://sdkman.io) to get groovy,
griffon, and gradle.  On Windows based system, use Powershell as the
command-line shell and get groovy, griffon, and gradle via
[posh-gvm](https://github.com/flofreud/posh-gvm).


## Bootstrapping

### Native-DDS (*native-dds* folder)

* Modify the variables in *scripts/setDDSEnvVars.sh (or scripts/setDDSEnvVars.ps1 on
  Windows)* according to your RTI DDS installation and then source it into your
  shell.
  Note: *setEnv.ps1* has not been tested.
* Copy over *nddsjava.jar* and *rticonnextmsg.jar* from your RTI
  DDS installation into *ddslibrary/libs*.
* Execute `./gradlew clean build copyLibs` in *ddslibrary* folder.
* To launch the apps, do the following in each app's folder.
    + execute `./gradlew run` to launch the app.

## Relevant Code

### Native-DDS (*native-dds* folder)

* ddslibrary/src/main/groovy/lib/\*groovy
* ddslibrary/src/main/java/lib/\*java
* ddslibrary/src/main/java/\*idl
* ddslibrary/src/main/resources/\*xml
* <app folders>/build.gradle
* <app folders>/settings.gradle
* <app folders>/gradle.properties
* <app folders>/griffon-app/(controllers|views|models)/\*/\*groovy
* <app folders>/src/main/groovy/\*/\*groovy
