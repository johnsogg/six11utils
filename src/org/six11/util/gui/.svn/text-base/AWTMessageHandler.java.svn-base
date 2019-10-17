// $Id$

package org.six11.util.gui;

import javax.swing.SwingUtilities;

import org.six11.util.io.Message;
import org.six11.util.io.MessageHandler;
import org.six11.util.adt.MultiState;

/**
 * 
 *
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public abstract class AWTMessageHandler extends MessageHandler {

  /**
   * @param state
   */
  protected AWTMessageHandler(MultiState state) {
    super(state);
  }

  /**
   * 
   */
  @Override
  public void handle(final Message msg) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        handleAWT(msg);
      }
    });
  }
  
  public abstract void handleAWT(Message msg);
}
