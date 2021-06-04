#!/bin/bash

#Load the setup for the examples.
. ../../setup.sh

#Data point bridge configuration.
export CONFIG_URI=$(echo $LLCONFIG)ait.example.ping.sync.sync-host.properties

#Logger configuration.
export LOGGER_CONFIG=-Dlog4j.configurationFile=$(echo $LLCONFIG)ait.example.all.dpb.log4j2

#IPv4 configuration.
export IPV4_CONFIG=-Djava.net.prefIPv4Stack=true

#Sync host scenario file must be copied to the current working directory.
# COPY /Y %~DP0\sync_config_ping.json .

java $IPV4_CONFIG $LOGGER_CONFIG -jar $SYNC_JAR_FILE $CONFIG_URI