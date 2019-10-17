// $Id$

package org.six11.util.lev;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * A data model suitable for use in an application where more than one module may be interested in
 * modifying its data, and being notified when changes occur.
 */
public abstract class Model {
  private Application app;
  private List<AppDataListener> listeners;
  private String name;
  private List<NamedAction> actions; // list of NamedActions

  /**
   * Creates a new model with the provided name. On exit the model's private data members should at
   * least hold sane defaults, if it is not possible for the data to be legitimate.
   */
  public Model(Application app_, String name_) {
    app = app_;
    name = name_;
  }

  /**
   * Talk to the authoritative data source and completely re-synch all private data members with
   * that source. This method may be called any number of times.
   */
  public abstract void reset();

  /**
   * Route the given event to the given listener. For example, if your listener's salient method is
   * 'fooPerformed(FooEvent)', then your implementation would simply be
   * <tt>((FooListener)lis).fooPerformed((FooEvent)ev)</tt>.
   */
  protected abstract void routeEvent(AppDataListener lis, AppDataEvent ev);

  /**
   * Sends the given event to the list of registered listeners. The provided event MUST be
   * assignable from this model's intended event class (see <tt>getEventType</tt>). The
   * <tt>routeEvent</tt> method will be called for each listener on the list. After the event has
   * been routed to all listeners, the model's tweakAll() method is invoked.
   */
  public final void fireAppDataEvent(AppDataEvent ev) {
    switch (getApp().getState()) {
      case Application.STATE_STOPPED:
      case Application.STATE_DISPOSED:
        return;
    }

    if (ev.getSource() == null) {
      ev.setSource(this);
    }
    for (Iterator<AppDataListener> it = (new ArrayList<AppDataListener>(getListeners())).iterator(); it
        .hasNext();) {
      AppDataListener obj = null;
      try {
        obj = it.next();
        routeEvent((AppDataListener) obj, ev);
      } catch (ClassCastException ex) {
        if (obj == null) {
          System.out.println(getClass().getName() + ": obj null in lev Model");
        } else {
          System.out.println(getClass().getName() + ": Error casting alleged listener of type: "
              + obj.getClass().getName() + " in model " + getClass().getName());
        }
      } catch (Exception ex) {
        System.out.println(getClass().getName()
            + " caught unexpected exception in fireAppDataEvent: " + ex);
        ex.printStackTrace();
      }
    }
    tweakAll();
  }

  /**
   * Adds the listener to this data model's list of registered listeners.
   */
  public void addListener(AppDataListener listener) {
    if (getListeners().contains(listener) == false) {
      getListeners().add(listener);
    }
  }

  /**
   * Adds the action to the model's list. When the model calls tweakAll(), each of the named actions
   * in this list will be tweak()ed.
   */
  public void addAction(NamedAction action) {
    if (getActions().contains(action) == false) {
      getActions().add(action);
    }
  }

  /**
   * Returns the list of registered NamedAction objects. These are tweaked automatically when an
   * event is routed to listeners.
   */
  public List<NamedAction> getActions() {
    if (actions == null) {
      actions = new Vector<NamedAction>();
    }
    return actions;
  }

  /**
   * Tweaks all actions that have been associated with this model using addAction().
   */
  public void tweakAll() {
    NamedAction action;
    for (Iterator<NamedAction> it = getActions().iterator(); it.hasNext();) {
      action = it.next();
      action.tweak();
    }
  }

  /**
   * Removes the given listener from the model's list of listeners. Nothing happens if the listener
   * is not there.
   */
  public void removeListener(AppDataListener listener) {
    getListeners().remove(listener);
  }

  /**
   * Gives you a list of listeners that are registered with this data model.
   */
  public List<AppDataListener> getListeners() {
    if (listeners == null) {
      listeners = new ArrayList<AppDataListener>();
    }

    return listeners;
  }

  /**
   * Gives you the name for this model.
   */
  public String getName() {
    return name;
  }

  /**
   * Gives you the application that is using this model.
   **/
  public Application getApp() {
    return app;
  }

}
