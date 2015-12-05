@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  monitor startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

@rem Add default JVM options here. You can also use JAVA_OPTS and MONITOR_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Dgriffon.env=prod"

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windowz variants

if not "%OS%" == "Windows_NT" goto win9xME_args
if "%@eval[2+2]" == "4" goto 4NT_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:4NT_args
@rem Get arguments from the 4NT Shell from JP Software
set CMD_LINE_ARGS=%$

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\monitor-0.2.jar;%APP_HOME%\lib\ddslibrary-0.2.jar;%APP_HOME%\lib\nddsjava.jar;%APP_HOME%\lib\rticonnextmsg.jar;%APP_HOME%\lib\griffon-guice-2.4.0.jar;%APP_HOME%\lib\slf4j-simple-1.7.13.jar;%APP_HOME%\lib\griffon-core-2.4.0.jar;%APP_HOME%\lib\griffon-core-java8-2.4.0.jar;%APP_HOME%\lib\griffon-swing-2.4.0.jar;%APP_HOME%\lib\griffon-swing-groovy-2.4.0.jar;%APP_HOME%\lib\griffon-groovy-2.4.0.jar;%APP_HOME%\lib\griffon-miglayout-swing-groovy-1.1.1.jar;%APP_HOME%\lib\miglayout-swing-5.0.jar;%APP_HOME%\lib\guice-4.0.jar;%APP_HOME%\lib\slf4j-api-1.7.13.jar;%APP_HOME%\lib\javax.inject-1.jar;%APP_HOME%\lib\jsr305-3.0.0.jar;%APP_HOME%\lib\groovy-all-2.4.4.jar;%APP_HOME%\lib\miglayout-core-5.0.jar;%APP_HOME%\lib\aopalliance-1.0.jar;%APP_HOME%\lib\guava-16.0.1.jar

@rem Execute monitor
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %MONITOR_OPTS%  -classpath "%CLASSPATH%" clinicalscenario.simplemonitor.Launcher %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable MONITOR_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%MONITOR_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
