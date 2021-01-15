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
 * Class PongSenderSync.
 */
public class PongSenderSync {

  /** Client name. */
  private static final String CLIENT_NAME = "PongSenderSync";

  /** Service name for sending pong data. */
  private static final String SERVICE_NAME_SEND = "PongSenderService";

  /** Service name for receiving ping data. */
  private static final String SERVICE_NAME_RECV = "PingReceiverService";

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

  /** Pong counter. */
  private int intPong = 0;

  /** Only send pong messages at synchronization points when this flag is set to true. */
  private boolean sendNextPong = false;

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

    PongSenderSync app = new PongSenderSync();
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
        "A pong sender.", SCENARIO_NAME, GROUP_NAME, CLIENT_NAME,
        this.llprop, this.llsync, sync );

    PingPongDataService pongSender = new PingPongDataService( SERVICE_NAME_SEND, false );

    pongSender.addStateChangeNotifier( new IServiceStateChangeNotifier<LlService, String>() {
      @Override
      public void stateChanged( LlService service, String oldVal, String newVal ) {
        logger.info( "{}: SEND pong message = '{}'", service.getName(), newVal );
      }
    } );

    MqttCommInterfaceUtility.addDataPointProperties( pongSender, SERVICE_NAME_SEND,
        "pong sender", SERVICE_NAME_SEND, "none" );

    // Add service to the client
    client.addService( pongSender );

    PingPongDataService pingReceiver = new PingPongDataService( SERVICE_NAME_RECV, false );

    // Whenever a ping message is received, prepare to send a pong message at the next
    // synchronization point (i.e., set the flag 'sendNextPong' to true).
    pingReceiver.addStateChangeNotifier( new IServiceStateChangeNotifier<LlService, String>() {
      @Override
      public void stateChanged( LlService service, String oldVal, String newVal ) {
        logger.info( "{}: RECEIVE ping message = '{}'", service.getName(), newVal );

        sendNextPong = true;
      }
    } );

    MqttCommInterfaceUtility.addDataPointProperties( pingReceiver, SERVICE_NAME_RECV,
        "pong receiver", SERVICE_NAME_RECV, "none" );

    // Add service to the client
    client.addService( pingReceiver );

    // Create the client
    client.create();

    // Initialize the client
    client.init();

    // Start the client
    client.start();
  }

  /**
   * Send pong message.
   */
  protected void sendPong() {
    // Only send a pong message when the flag "sendNextPong" is set to true (see
    // state change notifier for ping message receiver above).
    if ( true == sendNextPong ) {
      @SuppressWarnings( "unchecked" )
      IImplementedService<String> service =
          ( IImplementedService<String> ) client.getImplementedServices().get( SERVICE_NAME_SEND );

      service.setValue( "PONG#" + Integer.toString( intPong ) );

      // Increment pong counter.
      ++intPong;

      // Reset the flag.
      sendNextPong = false;
    }
  }

}
