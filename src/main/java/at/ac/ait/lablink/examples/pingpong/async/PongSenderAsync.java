//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.examples.pingpong.async;

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

import at.ac.ait.lablink.examples.pingpong.PingPongDataService;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class PongSenderAsync.
 */
public class PongSenderAsync {

  /** Client name. */
  private static final String CLIENT_NAME = "PongSenderAsync";

  /** Service name for sending pong data. */
  private static final String SERVICE_NAME_SEND = "PongSenderService";

  /** Service name for receiving ping data. */
  private static final String SERVICE_NAME_RECV = "PingReceiverService";

  /** Group name. */
  private static final String GROUP_NAME = "PingPongDemo";

  /** Scenario name. */
  private static final String SCENARIO_NAME = "PingPongAsync";

  /** General Lablink properties configuration. */
  private static final String llprop = "$LLCONFIG$ait.example.all.llproperties";

  /** Sync properties configuration. */
  private static final String llsync = "$LLCONFIG$ait.example.pingpong.sync.sync-host.properties";

  /** Logger. */
  private static final Logger logger = LogManager.getLogger( CLIENT_NAME );

  /** Schedule executor for sending delayed pong messages. */
  private static final ScheduledExecutorService executor =
      Executors.newSingleThreadScheduledExecutor();

  /** Pong counter. */
  private static int intPong = 0;

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

    PongSenderAsync app = new PongSenderAsync();
    app.setupClient();
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

    MqttCommInterfaceUtility.addClientProperties( client,
        "A pong sender.", SCENARIO_NAME, GROUP_NAME, CLIENT_NAME,
        this.llprop, this.llsync, null );

    PingPongDataService pongSender = new PingPongDataService( SERVICE_NAME_SEND, false );

    pongSender.addStateChangeNotifier( new IServiceStateChangeNotifier<LlService, String>() {
      @Override
      public void stateChanged( LlService service, String oldVal, String newVal ) {
        logger.info( "{}: value changed from '{}' to '{}'", service.getName(), oldVal, newVal );
      }
    } );

    MqttCommInterfaceUtility.addDataPointProperties( pongSender, SERVICE_NAME_SEND,
        "pong sender", SERVICE_NAME_SEND, "none" );

    // Add service to the client.
    client.addService( pongSender );

    PingPongDataService pingReceiver = new PingPongDataService( SERVICE_NAME_RECV, false );

    // Define and add a state change notifier that sends a delayed pong message whenever
    // a ping message is received.
    pingReceiver.addStateChangeNotifier( new IServiceStateChangeNotifier<LlService, String>() {
      @Override
      public void stateChanged( LlService service, String oldVal, String newVal ) {
        logger.info( "{}: value changed from '{}' to '{}'", service.getName(), oldVal, newVal );

        Runnable pong = new Runnable() {
          public void run() {
            sendPong();
            logger.info( "pong message sent" );
          }
        };

        // Schedule the pong message to be sent with a delay.
        int delay = 2000;
        TimeUnit unit = TimeUnit.MILLISECONDS;
        executor.schedule( pong, delay, unit );
        logger.info( "schedule new pong message to be sent in {} ms", delay );
      }
    } );

    MqttCommInterfaceUtility.addDataPointProperties( pingReceiver, SERVICE_NAME_RECV,
        "pong receiver", SERVICE_NAME_RECV, "none" );

    // Add service to the client.
    client.addService( pingReceiver );

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
  protected void sendPong() {
    @SuppressWarnings( "unchecked" )
    IImplementedService<String> service =
        ( IImplementedService<String> ) client.getImplementedServices().get( SERVICE_NAME_SEND );

    service.setValue( "PONG#" + Integer.toString( intPong ) );
    
    // Increment pong counter.
    ++intPong;
  }
}
