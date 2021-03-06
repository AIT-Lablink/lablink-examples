#!/bin/bash

#Load the setup for the examples.
. ../../setup.sh

#Path to class implementing the main routine.
export PING_RECEIVER_ASYNC=at.ac.ait.lablink.examples.ping.async.PingReceiverAsync

#Logger configuration.
export LOGGER_CONFIG=-Dlog4j.configurationFile=$(echo $LLCONFIG)ait.example.all.log4j2

#Start the receiver.
java $LOGGER_CONFIG -cp $EXAMPLES_JAR_FILE $PING_RECEIVER_ASYNC