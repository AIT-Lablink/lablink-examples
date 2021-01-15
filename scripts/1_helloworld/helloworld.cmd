@ECHO OFF

SETLOCAL

REM Load the setup for the examples.
CALL %~DP0\..\setup.cmd

REM Path to class implementing the main routine.
SET HELLO_WORLD=at.ac.ait.lablink.examples.helloworld.HelloWorld

REM Logger configuration.
SET LOGGER_CONFIG=-Dlog4j.configurationFile=%LLCONFIG%ait.example.all.log4j2

REM Run the example.
java.exe %LOGGER_CONFIG% -cp %EXAMPLES_JAR_FILE% %HELLO_WORLD%