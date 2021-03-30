#!/bin/bash

#Load the setup for the examples.
. ../../setup.sh

#Path to class implementing the main routine.
export PING_SENDER=at.ac.ait.lablink.examples.pingpong.sync.PingSenderSync

#Logger configuration.
export LOGGER_CONFIG=-Dlog4j.configurationFile=$(echo $LLCONFIG)ait.example.all.log4j2

#Start the sender.
java $LOGGER_CONFIG -cp $EXAMPLES_JAR_FILE $PING_SENDER