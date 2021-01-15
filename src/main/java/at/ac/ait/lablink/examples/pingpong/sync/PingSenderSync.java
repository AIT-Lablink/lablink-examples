//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.examples.pingpong.sync;

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

/**
 * Class PingSenderSync.
 */
public class PingSenderSync {

  /** Client name. */
  private static final String CLIENT_NAME = "PingSenderSync";

  /** Service name for sending ping data. */
  private static final String SERVICE_NAME_SEND = "PingSenderService";

  /** Service name for receiving pong data. */
  private static final String SERVICE_NAME_RECV = "PongReceiverService";

  /** Group name. */
  private static final String GROUP_NAME = "PingPongDemo";

  /** Scenario name. */
  private static final String SCENARIO_NAME = "PingPongSync";

  /** General Lablink properties configuration. */
  private static final String llprop = "$LLCONFIG$ait.example.all.llproperties";

  /** Sync properties configuration. */
  private static final String llsync = "$LLCONFIG$ait.example.pingpong.sync.sync-host.properties";

  /** Logger. */
  private static final Logger logger = LogManager.getLogger( CLIENT_NAME );

  /** Ping counter. */
  private int intPing = 0;

  /** Only send ping messages at synchronization points when this flag is set to true. */
  private boolean sendNextPing = false;

  /** This flag controls the initialization of the ping-pong message exchange. */
  private boolean sendFirstPing = true;

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

    PingSenderSync app = new PingSenderSync();
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

    PingPongSyncConsumer sync = new PingPongSyncConsumer( this );

    MqttCommInterfaceUtility.addClientProperties( client,
        "A ping sender.", SCENARIO_NAME, GROUP_NAME, CLIENT_NAME,
        this.llprop, this.llsync, sync );

    PingPongDataService pingSender = new PingPongDataService( SERVICE_NAME_SEND, false );

    pingSender.addStateChangeNotifier( new IServiceStateChangeNotifier<LlService, String>() {
      @Override
      public void stateChanged( LlService service, String oldVal, String newVal ) {
        logger.info( "{}: SEND ping message = '{}'", service.getName(), newVal );
      }
    } );

    MqttCommInterfaceUtility.addDataPointProperties( pingSender, SERVICE_NAME_SEND,
        "ping sender", SERVICE_NAME_SEND, "none" );

    // Add service to the client.
    client.addService( pingSender );

    PingPongDataService pongReceiver = new PingPongDataService( SERVICE_NAME_RECV, false );

    // Whenever a pong message is received, prepare to send a ping message at the next
    // synchronization point (i.e., set the flag 'sendNextPong' to true).
    pongReceiver.addStateChangeNotifier( new IServiceStateChangeNotifier<LlService, String>() {
      @Override
      public void stateChanged( LlService service, String oldVal, String newVal ) {
        logger.info( "{}: RECEIVE pong message = '{}'", service.getName(), newVal );

        sendNextPing = true;
      }
    } );

    MqttCommInterfaceUtility.addDataPointProperties( pongReceiver, SERVICE_NAME_RECV,
        "pong receiver", SERVICE_NAME_RECV, "none" );

    // Add service to the client.
    client.addService( pongReceiver );

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
    // Only send a ping message when either the flag "sendNextPing" or "sendFirstPing" is set
    // to true (see state change notifier for pong message receiver above).
    if ( true == sendFirstPing || true == sendNextPing ) {
      @SuppressWarnings( "unchecked" )
      IImplementedService<String> service =
          ( IImplementedService<String> ) client.getImplementedServices().get( SERVICE_NAME_SEND );

      service.setValue( "PING#" + Integer.toString( intPing ) );

      // Increment ping counter.
      ++intPing;

      // Reset the flags.
      sendFirstPing = false;
      sendNextPing = false;
    }
  }

}
