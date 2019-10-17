// $Id: Module.java 23 2009-11-24 21:09:08Z gabe.johnson $

package org.six11.util.lev;

import java.util.HashMap;
import java.util.Map;

/**
 * An abstract class that provides some basic functionality for creating modules in an application.
 * A module has a name, a set of actions (which represent things a user can directly manipulate in
 * the interface), an enabled state, and a visible state. Also, modules have view delegates that
 * serve as the piece the user interacts with. Code within the module (or rather, the subclass)
 * serves as the controller. Model data that could potentially be useful to other modules should
 * live in the application's model set.
 */
public abstract class Module {
  private Application app;
  private Map<String, AppAction> actions;
  private boolean enabled;
  private boolean viewVisible;
  protected String name;
  private ModuleView view;

  /**
   * Creates a new Module with a provided name. By default the module is NOT enabled but its view is
   * marked as visible. Remember that disabled Modules are never shown, but that when they are
   * enabled, their visibility state from before is used. So, to make this module be shown to the
   * user, simply enable the module.
   * 
   * @param name_
   *          Then name of the module.
   */
  public Module(Application app_, String name_) {
    app = app_;
    name = name_;
    actions = new HashMap<String, AppAction>();
    enabled = false;
    viewVisible = true;
  }

  /**
   * Gives you the application the module lives in.
   */
  public Application getApp() {
    return app;
  }

  /**
   * Gives you the name of the module.
   * 
   * @return The name of the module.
   */
  public String getName() {
    return name;
  }

  /**
   * If your module may go into a 'stopped' state (see the <tt>stop</tt> method), the <tt>start</tt>
   * method is used to bring it out of that state. An application should be able to start and stop
   * and module an arbitrary number of times without getting weird results. By default, this
   * method's definition is empty, so subclasses must override it in such a way that makes sense.
   */
  public void start() {
    /* does nothing */
  }

  /**
   * Puts your module into a 'stopped' state, where it is consuming only the bare minimum resources.
   * To start the module up again, define the <tt>start</tt> method. By default, this method's
   * definition is empty, so subclasses must override it in such a way that makes sense.
   */
  public void stop() {
    /* does nothing */
  }

  /**
   * Associates a name with an action and stores it in the module so that other modules may look it
   * up and programmatically execute that action or change its enabled state.
   * 
   * @param name
   *          the name of the action
   * @param action
   *          the action itself
   */
  protected void addAction(String name, AppAction action) {
    actions.put(name, action);
  }

  /**
   * This returns the action assicated with the provided name. If no such action is found, this will
   * return null.
   * 
   * @param name
   *          the name of the action to get
   * 
   * @return the action bound to the provided name
   */
  public AppAction getAction(String name) {
    return (AppAction) actions.get(name);
  }

  /**
   * Removes an action from the module's list.
   * 
   * @param name
   *          the name of the action to remove.
   */
  public void removeAction(String name) {
    actions.remove(name);
  }

  /**
   * Tells you if the module is enabled. When disabled, the module and it's actions are not supposed
   * to be used.
   * 
   * @return the enabled state.
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Sets the enabled state. If the module is currently visible and the module is asked to be
   * disabled, that visibility state is retained, so that when the module is enabled, its view
   * becomes visible again.
   * 
   * @param enabled_
   *          the new state.
   */
  public void setEnabled(boolean enabled_) {
    if (enabled_ != enabled) {
      enabled = enabled_;

      if (hasView() && (enabled == false) && (isVisible())) {
        try {
          getView().setVisible(false);
        } catch (HeadlessException ex) {
          /* do nothing */
        }
      } else if (hasView() && enabled) {
        try {
          getView().setVisible(viewVisible);
        } catch (HeadlessException ex) {
          /* do nothing */
        }
      }

      getApp().moduleEnableNotification(enabled, this);
    }
  }

  /**
   * Tells you if the module is visible. For a module to be visible, it MUST be enabled. It is
   * possible for a module to be enabled but not visible, in situations where that module is still
   * serving an under-the-hood purpose.
   * 
   * @return true if the module's view is visible to the user, false otherwise.
   */
  public boolean isVisible() {
    try {
      return (viewVisible && hasView() && getView().isVisible());

    } catch (HeadlessException ex) {
      return false;
    }
  }

  /**
   * Sets the visibility state. If this is set to true when the module is disabled, a runtime
   * exception will be thrown.
   * 
   * @param visible
   */
  public void setVisible(boolean visible) {
    if ((isEnabled() == false) && (visible == true)) {
      throw new RuntimeException("Disabled modules may not be made visible");
    }

    try {
      getView().setVisible(visible);
    } catch (HeadlessException ex) {
      /* do nothing */
    }

    viewVisible = visible;
  }

  /**
   * Returns the GUI component the user interacts with.
   * 
   * @throws HeadlessException
   *           if there is no view. Modules must be able to run in a non-graphical environment.
   * @return the GUI component.
   */
  public ModuleView getView() throws HeadlessException {
    if (hasView() == false) {
      throw new HeadlessException();
    }
    return view;
  }

  /**
   * Tells you whether this module has a view associated with it or not.
   * 
   * @return <code>true</code> if it has a view; <code>false</code> otherwise.
   */
  public boolean hasView() {
    return view != null;
  }

  /**
   * Sets the GUI delegate for this module.
   * 
   * @param view_
   *          the new GUI delegate.
   */
  public void setView(ModuleView view_) {
    view = view_;
  }

  /**
   * A convenience method that returns the internal storage of AppActions as an array.
   **/
  public AppAction[] getActions() {
    AppAction[] ret = new AppAction[0];
    ret = (AppAction[]) actions.values().toArray(ret);
    return ret;
  }
}
