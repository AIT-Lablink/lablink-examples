//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.examples.pingpong;

import at.ac.ait.lablink.core.service.LlServiceString;

/**
 * Class PingPongDataService.
 */
public class PingPongDataService extends LlServiceString {

  /**
   * Constructor.
   *
   * @param name service name
   * @param readonly readonly flag
   */
  public PingPongDataService( String name, boolean readonly ) {
    super( name, readonly );
  }

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
  public boolean set( String newval ) {
    this.setCurState( newval );
    logger.debug( "{}: set new value to '{}'", this.getName(), newval );
    return true;
  }
}
