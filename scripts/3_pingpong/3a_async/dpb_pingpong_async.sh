#!/bin/bash

#Load the setup for the examples.
. ../../setup.sh

#Data point bridge configuration.
export CONFIG_FILE_URI=$(echo $LLCONFIG)ait.example.pingpong.async.dpb.config

#Logger configuration.
export LOGGER_CONFIG=-Dlog4j.configurationFile=$(echo $LLCONFIG)ait.example.all.dpb.log4j2

#IPv4 configuration.
export IPV4_CONFIG=-Djava.net.prefIPv4Stack=true

#Start the data point bridge.
java $IPV4_CONFIG $LOGGER_CONFIG -jar $DPB_JAR_FILE -c $CONFIG_FILE_URI