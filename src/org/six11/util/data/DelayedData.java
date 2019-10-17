// $Id: DelayedData.java 12 2009-11-09 22:58:47Z gabe.johnson $

package org.six11.util.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

/**
 * This is used as a bucket for putting data that will eventually be available. The initial use case
 * is for showing a dialog box that provides a list of options. Those options are downloaded from
 * the network, but we'd like to show a 'waiting' GUI component until the data is available.
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public abstract class DelayedData {

  protected Set<PropertyChangeListener> listeners;
  protected Object prev;
  protected Object data;

  public void addPropertyChangeListener(PropertyChangeListener lis) {
    if (listeners == null) {
      listeners = new HashSet<PropertyChangeListener>();
    }
    if (!listeners.contains(lis)) {
      listeners.add(lis);
    }
  }

  public void setData(Object d) {
    prev = data;
    data = d;
    fireDataPresent();
  }

  private void fireDataPresent() {
    PropertyChangeEvent ev = new PropertyChangeEvent(this, "data present", prev, data);
    if (listeners != null) {
      for (PropertyChangeListener lis : listeners) {
        lis.propertyChange(ev);
      }
    }
  }

  /**
   * The 'fetch' implementation should be responsible for gathering whatever data this object is
   * for, and eventually setting it with the 'setData' method. This does not automatically run in a
   * separate thread.
   */
  public abstract void fetch();
}
