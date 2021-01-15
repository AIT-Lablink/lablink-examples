//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.examples.ping.async;

import at.ac.ait.lablink.core.client.ci.mqtt.impl.MqttCommInterfaceUtility;
import at.ac.ait.lablink.core.client.ex.ClientNotReadyException;
import at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException;
import at.ac.ait.lablink.core.client.ex.DataTypeNotSupportedException;
import at.ac.ait.lablink.core.client.ex.NoServicesInClientLogicException;
import at.ac.ait.lablink.core.client.ex.NoSuchCommInterfaceException;
import at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType;
import at.ac.ait.lablink.core.client.impl.LlClient;
import at.ac.ait.lablink.core.service.IImplementedService;
import at.ac.ait.lablink.core.service.IServiceStateChangeNotifier;
import at.ac.ait.lablink.core.service.LlService;

import at.ac.ait.lablink.examples.ping.PingDataService;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class PingSenderAsync.
 */
public class PingSenderAsync {

  /** Client name. */
  private static final String CLIENT_NAME = "PingSenderAsync";

  /** Service name. */
  private static final String SERVICE_NAME = "PingSenderService";

  /** Group name. */
  private static final String GROUP_NAME = "PingDemo";

  /** Scenario name. */
  private static final String SCENARIO_NAME = "PingAsync";

  /** General Lablink properties configuration. */
  private static final String llprop = "$LLCONFIG$ait.examples.all.llproperties";

  /** Sync properties configuration. */
  private static final String llsync = "$LLCONFIG$ait.examples.all.sync-host.properties";

  /** Logger. */
  private static final Logger logger = LogManager.getLogger( CLIENT_NAME );

  /** Schedule executor for repeated ping messages. */
  private static final ScheduledExecutorService executor = 
      Executors.newSingleThreadScheduledExecutor();

  /** Ping counter. */
  private int intPing = 0;

  /** Client. */
  private LlClient client;

  /**
   * The main method.
   *
   * @param args arguments to main method
   * @throws ClientNotReadyException client not ready
   * @throws CommInterfaceNotSupportedException comm interface not supported
   * @throws DataTypeNotSupportedException data type not supported
   * @throws NoServicesInClientLogicException no services in client logic
   * @throws NoSuchCommInterfaceException no such comm interface
   * @throws ServiceTypeDoesNotMatchClientType service type does not match client type
   * @throws ConfigurationException bad configuration
   */
  public static void main( String[] args )
      throws ClientNotReadyException, CommInterfaceNotSupportedException,
      DataTypeNotSupportedException, NoServicesInClientLogicException,
      NoSuchCommInterfaceException, ServiceTypeDoesNotMatchClientType,
      ConfigurationException {

    PingSenderAsync app = new PingSenderAsync();
    app.setupClient();

    Runnable ping = new Runnable() {
      public void run() {
        app.sendPing();
        logger.info( "ping message sent" );
      }
    };

    long initialDelay = 0;
    long period = 4000;
    TimeUnit unit = TimeUnit.MILLISECONDS;

    // Schedule the ping messages to be repeatedly sent with fixed time steps.
    executor.scheduleAtFixedRate( ping, initialDelay, period, unit );
  }

  /**
   * Execute the client setup.
   *
   * @throws ClientNotReadyException client not ready
   * @throws CommInterfaceNotSupportedException comm interface not supported
   * @throws DataTypeNotSupportedException data type not supported
   * @throws NoServicesInClientLogicException no services in client logic
   * @throws NoSuchCommInterfaceException no such comm interface
   * @throws ServiceTypeDoesNotMatchClientType service type does not match client type
   * @throws ConfigurationException bad configuration
   */
  private void setupClient()
      throws ClientNotReadyException, CommInterfaceNotSupportedException,
      DataTypeNotSupportedException, NoServicesInClientLogicException,
      NoSuchCommInterfaceException, ServiceTypeDoesNotMatchClientType,
      ConfigurationException {

    client = new LlClient( CLIENT_NAME, MqttCommInterfaceUtility.SP_ACCESS_NAME, true, false );

    MqttCommInterfaceUtility.addClientProperties( client, "A simple sender.",
        SCENARIO_NAME, GROUP_NAME, CLIENT_NAME, this.llprop, this.llsync, null );

    PingDataService pingSignal = new PingDataService(SERVICE_NAME, false );

    pingSignal.addStateChangeNotifier( new IServiceStateChangeNotifier<LlService, String>() {
      @Override
      public void stateChanged( LlService service, String oldVal, String newVal ) {
        logger.info( "{}: value changed from '{}' to '{}'", service.getName(), oldVal, newVal );
      }
    } );

    MqttCommInterfaceUtility.addDataPointProperties( pingSignal, SERVICE_NAME,
        "ping signal", SERVICE_NAME, "none" );

    // Add service to the client.
    client.addService( pingSignal );

    // Create the client.
    client.create();

    // Initialize the client.
    client.init();

    // Start the client.
    client.start();
  }

  /**
   * Send ping message.
   */
  protected void sendPing() {
    @SuppressWarnings( "unchecked" )
    IImplementedService<String> service =
        ( IImplementedService<String> ) client.getImplementedServices().get( SERVICE_NAME );

    service.setValue( "PING#" + Integer.toString( intPing ) );

    // Increment ping counter.
    ++intPing;
  }
}
