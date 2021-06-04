#!/bin/bash

#Load the setup for the examples.
. ../setup.sh

#Path to class implementing the main routine.
export HELLO_WORLD=at.ac.ait.lablink.examples.helloworld.HelloWorld

#Logger configuration.
export LOGGER_CONFIG=-Dlog4j.configurationFile=$(echo $LLCONFIG)ait.example.all.log4j2

#Run the example.
java $LOGGER_CONFIG -cp $EXAMPLES_JAR_FILE $HELLO_WORLD