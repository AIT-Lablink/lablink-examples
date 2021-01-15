//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.examples.pingpong.sync;

import at.ac.ait.lablink.core.service.sync.ELlSimulationMode;
import at.ac.ait.lablink.core.service.sync.ISyncParameter;
import at.ac.ait.lablink.core.service.sync.consumer.ISyncConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.InterruptedException;
import java.util.concurrent.TimeUnit;


public class PingPongSyncConsumer implements ISyncConsumer {

  /** Logger. */
  private static final Logger logger = LogManager.getLogger( "PingPongSyncConsumer" );

  /** Synchronized ping sender. */
  private PingSenderSync ping;

  /** Synchronized pong sender. */
  private PongSenderSync pong;

  /**
   * Constructor.
   *
   * @param ping synchronized ping sender
   */
  public PingPongSyncConsumer( PingSenderSync ping ) {
    this.ping = ping;
    this.pong = null;
  }

  /**
   * Constructor.
   *
   * @param pong synchronized pong sender
   */
  public PingPongSyncConsumer( PongSenderSync pong ) {
    this.pong = pong;
    this.ping = null;
  }

  @Override
  public boolean init( ISyncParameter scs ) {
    return true;
  }

  @Override
  public long go( long currentSimTime, long until, ISyncParameter scs ) {
    logger.info( "synchronization point at {}", currentSimTime );

    if ( ping != null ) {
      ping.sendPing();
    }

    if ( pong != null ) {
      pong.sendPong();
    }

    // When in simulation mode, sleep a few milliseconds to avoid racing conditions
    // bewteen synchronization points and message delivery between clients.
    if ( ELlSimulationMode.SIMULATION == scs.getSimMode() ) {
      try {
        TimeUnit.MILLISECONDS.sleep( 10 );
      } catch ( InterruptedException ex ) {
        // Nothing to do here ...
      }
    }

    return ( until + scs.getStepSize() );
  }

  @Override
  public boolean stop( ISyncParameter scs ) {
    return true;
  }

}
