#!/bin/bash

#=============================================================
#Edit the following variables to comply with your local setup.
#=============================================================

#Connection string for configuration server.
export LLCONFIG=http://localhost:10101/get?id=

#Version of example package.
export VERSION=0.0.3

#Root directory of example package (only change this if you really know what you are doing).
export EXAMPLE_ROOT_DIR=$(dirname $(cd $(dirname ${BASH_SOURCE[0]}) && pwd))

#Path to Java JAR file of examples package.
export EXAMPLES_JAR_FILE=$EXAMPLE_ROOT_DIR/target/assembly/examples-$VERSION-jar-with-dependencies.jar 

#Path to Java JAR file of datapoint bridge.
export DPB_JAR_FILE=$EXAMPLE_ROOT_DIR/target/dependency/dpbridge-0.0.2-jar-with-dependencies.jar

#Path to Java JAR file of simple sync host.
export SYNC_JAR_FILE=$EXAMPLE_ROOT_DIR/target/dependency/sync-0.0.2-jar-with-dependencies.jar

#Path to Java JAR file of config server.
export CONFIG_JAR_FILE=$EXAMPLE_ROOT_DIR/target/dependency/config-0.1.1-jar-with-dependencies.jar