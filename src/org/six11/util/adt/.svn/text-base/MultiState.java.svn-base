// $Id$

package org.six11.util.adt;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This object holds several state values for a program. It is intended to be shared among many
 * objects. There are a series of slots, keyed off Strings. Clients of this multi-state object may
 * get or set values by key. All values are of type Object. Clients may also register (or
 * unregister) as change listeners for particular slots by name.
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class MultiState {
  private Map<String, Object> stateValues;
  private Map<String, List<PropertyChangeListener>> changeListeners;

  public MultiState() {
    stateValues = new HashMap<String, Object>();
    changeListeners = new HashMap<String, List<PropertyChangeListener>>();
  }

  public synchronized Object getValue(String key) {
    return stateValues.get(key);
  }

  public synchronized String getString(String key) {
    String ret = null;
    Object val = getValue(key);
    if (val instanceof String) {
      ret = (String) val;
    } else if (val instanceof Integer) {
      ret = "" + ((Integer) val).intValue();
    } else if (val instanceof Boolean) {
      ret = "" + ((Boolean) val).booleanValue();
    }
    return ret;
  }

  public synchronized int getInt(String key) {
    return ((Integer) getValue(key)).intValue();
  }

  /**
   * Sets the value for the named slot and fires a property change event, even if the old value is
   * the same as the new.
   */
  public synchronized void setValue(String key, Object value) {
    Object oldValue = getValue(key);
    stateValues.put(key, value);
    fire(new PropertyChangeEvent(this, key, oldValue, value));
  }

  /**
   * Triggers a change event for the slot with the given key, even though there is no actual change.
   * This can be useful for notifying listeners that "something" happened when it is not necessary
   * to be too specific.
   */
  public synchronized void whackValue(String key) {
    Object v = getValue(key);
    fire(new PropertyChangeEvent(this, key, v, v));
  }

  public void addChangeListener(String key, PropertyChangeListener lis) {
    if (!changeListeners.containsKey(key)) {
      changeListeners.put(key, new ArrayList<PropertyChangeListener>());
    }
    if (!changeListeners.get(key).contains(lis)) {
      changeListeners.get(key).add(lis);
    }
  }

  public void removeChangeListener(String key, PropertyChangeListener lis) {
    if (changeListeners.containsKey(key)) {
      changeListeners.get(key).remove(lis);
    }
  }

  private void fire(PropertyChangeEvent ev) {
    List<PropertyChangeListener> listeners = changeListeners.get(ev.getPropertyName());
    if (listeners != null) {
      for (PropertyChangeListener lis : listeners) {
        lis.propertyChange(ev);
      }
    }
  }

  public boolean hasValue(String key) {
    return (stateValues.containsKey(key) && stateValues.get(key) != null);
  }

}
