@ECHO OFF

REM Load the setup for the examples.
CALL %~DP0\..\..\setup.cmd

REM Data point bridge configuration.
SET CONFIG_URI=%LLCONFIG%ait.example.ping.sync.sync-host.properties

REM Logger configuration.
SET LOGGER_CONFIG=-Dlog4j.configurationFile=%LLCONFIG%ait.example.all.log4j2

REM IPv4 configuration.
SET IPV4_CONFIG=-Djava.net.prefIPv4Stack=true

REM Sync host scenario file must be copied to the current working directory.
COPY /Y %~DP0\sync_config_ping.json .

java.exe %IPV4_CONFIG% %LOGGER_CONFIG% -jar %SYNC_JAR_FILE% %CONFIG_URI%