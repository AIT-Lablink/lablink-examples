//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.examples.ping.sync;

import at.ac.ait.lablink.core.service.sync.ISyncParameter;
import at.ac.ait.lablink.core.service.sync.consumer.ISyncConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PingSyncConsumer implements ISyncConsumer {

  /** Logger. */
  private static final Logger logger = LogManager.getLogger( "PingSyncConsumer" );

  /** Synchronized ping sender. */
  private PingSenderSync sender;

  /**
   * Constructor.
   */
  public PingSyncConsumer() {
    this.sender = null;
  }

  /**
   * Constructor.
   *
   * @param sender ping sender
   */
  public PingSyncConsumer( PingSenderSync sender ) {
    this.sender = sender;
  }

  @Override
  public boolean init( ISyncParameter scs ) {
    logger.info( "Intialize sync client" );
    logger.info( "Sync client parameters: {}, {}, {}, {}, {}", scs.getSimMode(),
        scs.getScaleFactor(), scs.getSimBeginTime(), scs.getSimEndTime(), scs.getStepSize() );
    logger.info( "Sync client extra config: {}", scs.getClientConfig() );
    return true;
  }

  @Override
  public long go( long currentSimTime, long until, ISyncParameter scs ) {
    logger.info( "synchronization point at {}", currentSimTime );

    if ( sender != null ) {
      sender.sendPing(); 
    }

    return ( until + scs.getStepSize() );
  }

  @Override
  public boolean stop( ISyncParameter scs ) {
    logger.debug( "Sync Client stopped!" );
    return true;
  }
}
