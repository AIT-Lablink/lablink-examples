@ECHO OFF

SETLOCAL

REM Load the setup for the examples.
CALL "%~DP0\..\..\setup.cmd"

REM Path to class implementing the main routine.
SET PING_RECEIVER_ASYNC=at.ac.ait.lablink.examples.ping.async.PingReceiverAsync

REM Logger configuration.
SET LOGGER_CONFIG=-Dlog4j.configurationFile=%LLCONFIG%ait.example.all.log4j2

REM Start the receiver.
"%JAVA_HOME%\bin\java.exe" %LOGGER_CONFIG% -cp "%EXAMPLES_JAR_FILE%" %PING_RECEIVER_ASYNC%

PAUSE
