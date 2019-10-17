// $Id: MessageHandler.java 12 2009-11-09 22:58:47Z gabe.johnson $

package org.six11.util.io;

import org.six11.util.Debug;
import org.six11.util.adt.MultiState;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public abstract class MessageHandler {

  protected MultiState state;

  protected MessageHandler(MultiState state) {
    this.state = state;
  }

  public abstract void handle(Message msg);

  protected void debugMessage(Message msg) {
    bug("Debug output for message of type " + msg.getType() + "\n" + msg.toXml());
  }

  protected void bug(String what) {
    String n = getClass().getName();
    Debug.out(n.substring(n.lastIndexOf('.') + 1), what);
  }
}
