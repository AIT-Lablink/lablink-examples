REM =============================================================
REM Edit the following variables to comply with your local setup.
REM =============================================================

REM Connection string for configuration server.
SET LLCONFIG=http://localhost:10101/get?id=

REM Version of example package.
SET VERSION=0.0.1

REM Root directory of example package (only change this if you really know what you are doing).
SET EXAMPLE_ROOT_DIR=%~DP0..

REM Path to Java JAR file of examples package.
SET EXAMPLES_JAR_FILE=%EXAMPLE_ROOT_DIR%\target\assembly\examples-%VERSION%-jar-with-dependencies.jar 

REM Path to Java JAR file of datapoint bridge.
SET DPB_JAR_FILE=%EXAMPLE_ROOT_DIR%\target\dependency\dpbridge-0.0.1-jar-with-dependencies.jar

REM Path to Java JAR file of simple sync host.
SET SYNC_JAR_FILE=%EXAMPLE_ROOT_DIR%\target\dependency\sync-0.0.1-jar-with-dependencies.jar

REM Path to Java JAR file of config server.
SET CONFIG_JAR_FILE=%EXAMPLE_ROOT_DIR%\target\dependency\config-0.0.1-jar-with-dependencies.jar

REM Check if environment variable JAVA_HOME has been defined.
IF NOT DEFINED JAVA_HOME (
    ECHO WARNING: environment variable JAVA_HOME not has been defined!
    PAUSE
)
