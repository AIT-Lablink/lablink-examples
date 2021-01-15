//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.examples.helloworld;

import at.ac.ait.lablink.core.client.ci.mqtt.impl.MqttCommInterfaceUtility;
import at.ac.ait.lablink.core.client.ex.ClientNotReadyException;
import at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException;
import at.ac.ait.lablink.core.client.ex.DataTypeNotSupportedException;
import at.ac.ait.lablink.core.client.ex.InvalidCastForServiceValueException;
import at.ac.ait.lablink.core.client.ex.NoServicesInClientLogicException;
import at.ac.ait.lablink.core.client.ex.NoSuchCommInterfaceException;
import at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException;
import at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType;
import at.ac.ait.lablink.core.client.impl.LlClient;
import at.ac.ait.lablink.core.service.IImplementedService;
import at.ac.ait.lablink.core.service.IServiceStateChangeNotifier;
import at.ac.ait.lablink.core.service.LlService;
import at.ac.ait.lablink.core.service.LlServiceString;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Class HelloWorld.
 */
public class HelloWorld {

  /** Logger. */
  private static final Logger logger = LogManager.getLogger( "HelloWorld" );

  /**
   * The main method.
   *
   * @param args arguments to main method
   * @throws ClientNotReadyException client not ready
   * @throws CommInterfaceNotSupportedException comm interface not supported
   * @throws DataTypeNotSupportedException data type not supported
   * @throws NoServicesInClientLogicException no services in client logic
   * @throws NoSuchCommInterfaceException no such comm interface
   * @throws ServiceIsNotRegisteredWithClientException service is not registered with client
   * @throws ServiceTypeDoesNotMatchClientType service type does not match client type
   * @throws ConfigurationException bad configuration
   */
  public static void main( String[] args )
      throws ClientNotReadyException, CommInterfaceNotSupportedException,
      DataTypeNotSupportedException, NoServicesInClientLogicException,
      NoSuchCommInterfaceException, ServiceIsNotRegisteredWithClientException,
      ServiceTypeDoesNotMatchClientType, ConfigurationException {

    // Scenario name.
    String scenarioName = "HelloWorldScenario";

    // Group name.
    String groupName = "HelloWorldDemo";

    // Client name.
    String clientName = "HelloWorld";

    // Client description.
    String clientDesc = "A simple client to demonstrate the basic usage.";

    // General Lablink properties configuration.
    String llprop = "$LLCONFIG$ait.example.all.llproperties";

    // Sync properties configuration (dummy).
    String llsync = "$LLCONFIG$ait.example.all.sync-host.properties";

    boolean giveShell = true;
    boolean isPseudo = false;

    // Declare the client with required interface.
    LlClient client = new LlClient( clientName,
        MqttCommInterfaceUtility.SP_ACCESS_NAME, giveShell, isPseudo );

    // Specify client configuration (no sync host).
    MqttCommInterfaceUtility.addClientProperties( client, clientDesc,
        scenarioName, groupName, clientName, llprop, llsync, null );

    // ============================================================

    // Data service name.
    String serviceName = "HelloWorldDataService";

    // Data service description.
    String serviceDesc = "Say HELLO to the world!";

    // Unit associated to values handled by the data service.
    String serviceUnit = "none";

    // Create new data service.
    HelloWorldData dataService = new HelloWorldData();
    dataService.setName( serviceName );

    // Specify data service properties.
    MqttCommInterfaceUtility.addDataPointProperties( dataService,
        serviceName, serviceDesc, serviceName, serviceUnit );

    // ============================================================

    // Add notifier.
    dataService.addStateChangeNotifier( new HelloWorldNotifier() );

    // Add another notifier.
    dataService.addStateChangeNotifier( new IServiceStateChangeNotifier<LlService, String>() {
      @Override
      public void stateChanged(LlService service, String oldVal, String newVal) {
        logger.info( "{}: another notifier -> state changed from '{}' to '{}'",
            service.getName(), oldVal, newVal );
      }
    } );

    // Add yet another notifier.
    dataService.addStateChangeNotifier( ( service, oldVal, newVal ) -> {
      logger.info( "{}: yet another notifier -> state changed from '{}' to '{}'",
          service.getName(), oldVal, newVal );
    } );

    // ============================================================

    // Add service to the client.
    client.addService( dataService );

    // Create the client.
    client.create();

    // Initialize the client.
    client.init();

    // Start the client.
    client.start();

    // Set and get the value programmatically.
    logger.info( "service name: {}", client.getImplementedServices().get( serviceName ).getName() );

    @SuppressWarnings( "unchecked" )
    IImplementedService<String> service =
        ( IImplementedService<String> ) client.getImplementedServices().get( serviceName );
    service.setValue( new String( "Hello world!" ) );

    Object currentValue = client.getImplementedServices().get( serviceName ).getValue();
    logger.info( "service value: {}", currentValue );

    if ( client.setServiceValue( serviceName, new String( "Hello again!" ) ) ) {
      logger.info( "service value set successfully." );
    }

    if ( client.setServiceValue( dataService, new String( "Hello once more!" ) ) ) {
      logger.info( "service value set again successfully." );
    }

    try {
      logger.info( client.getServiceValueString( dataService ) );
    } catch ( InvalidCastForServiceValueException ex ) {
      ex.printStackTrace();
    }

  }

  /**
   * Class HelloWorldData.
   * Subclass from a Service variation and provide the implementation
   */
  static class HelloWorldData extends LlServiceString {
    /**
     * @see at.ac.ait.lablink.core.service.LlService#get()
     */
    @Override
    public String get() {
      return this.getCurState();
    }

    /**
     * @see at.ac.ait.lablink.core.service.LlService#set( java.lang.Object )
     */
    @Override
    public boolean set( String newVal ) {
      logger.info( "{}: set new value to '{}'", this.getName(), newVal );
      this.setCurState( newVal );
      return true;
    }
  }

  /**
   * Class HelloWorldNotifier.
   */
  static class HelloWorldNotifier implements IServiceStateChangeNotifier<LlService, String> {
    /**
     * @see at.ac.ait.lablink.core.service.IServiceStateChangeNotifier#stateChanged(
     * java.lang.Object, java.lang.Object, java.lang.Object 
     * )
     */
    @Override
    public void stateChanged( LlService service, String oldVal, String newVal ) {
      logger.info( "{}: notifier -> state Changed from '{}' to '{}'",
          service.getName(), oldVal, newVal);
    }
  }
}
