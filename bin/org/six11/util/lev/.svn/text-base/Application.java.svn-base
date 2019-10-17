// $Id$

package org.six11.util.lev;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

/**
 * The starting point for the entire application. Subclasses must override the four methods
 * <tt>initApplication</tt>, <tt>startApplication</tt>, <tt>stopApplication</tt>, and
 * <tt>disposeApplication</tt>. These methods constitute the Application's life cycle.
 */
public abstract class Application {

  public static boolean headless = false;

  /**
   * Indicates the application hasn't been touched, or it doesn't have a clue what state it is in.
   */
  public final static int STATE_NONE = 1;

  /**
   * Indicates the application has been initialized but not started.
   */
  public final static int STATE_INITIALIZED = 2;

  /**
   * Indicates the application has been started and is currently running.
   */
  public final static int STATE_STARTED = 3;

  /**
   * Indicates the application has been stopped. From here the application can be started again or
   * disposed.
   */
  public final static int STATE_STOPPED = 4;

  /**
   * Indicates the application has been disposed. From here the application (depending on
   * implementation details) may go back into an initialized state, or into a custom state defined
   * by the implementation (such as a dead, "you can call System.exit() now" sort of state).
   */
  public final static int STATE_DISPOSED = 5;

  private Map<String, Module> modules;
  private Map<String, Model> models;
  private Map<String, Program> programs;
  private List<LifeCycleListener> lifeCycleListeners;

  private AppView view;

  /**
   * Initialize the application. This may mean reading resources from the local file system or off
   * the network in order to determine some environment settings. This method should <b>not</b> lead
   * to large amounts of resources (memory, processor, network) being consumed and staying consumed.
   * The most likely tasks to perform in this method are: reading preferences from the network or
   * file system, adding modules and models, and registering modules as listeners.
   */
  public abstract void initApplication();

  /**
   * Start the application. This method may be invoked from either an initialized state or from a
   * stopped state, so keep that in mind. This is where resource-intensive things are brought to
   * life. In this method, the most likely sequence of events: lay out the module views, set up
   * network, database, and file connetions, and any other main()-sorts of things.
   */
  public abstract void startApplication();

  /**
   * Stop the application. This method must only be invoked from a started state. Bring down all
   * resource-intensive connections or processes--essentially put the application in a state of
   * hibernation.
   */
  public abstract void stopApplication();

  /**
   * Dispose the application and completely free up resources. This is most likely followed by a
   * System.exit() call, but this is not a requirement. In fact, from a disposed state, the
   * application could just as correctly be asked to enter an initialized state.
   */
  public abstract void disposeApplication();

  /**
   * Returns the current lifecycle state of the application. This should return one of the five
   * constant state values (those that begin with STATE_) or an implementation-specific custom
   * state.
   */
  public abstract int getState();

  /**
   * Adds a life cycle listener to the application.
   */
  public void addLifeCycleListener(LifeCycleListener lis) {
    if (lifeCycleListeners == null) {
      lifeCycleListeners = new ArrayList<LifeCycleListener>();
    }
    lifeCycleListeners.add(lis);
  }

  /**
   * Cycles through all life cycle listeners and calls their 'lifeCycleNotice' method using the
   * CURRENT value of getState().
   */
  protected void fireLifeCycleEvent() {
    if (lifeCycleListeners != null) {
      int s = getState();
      for (Iterator<LifeCycleListener> it = lifeCycleListeners.iterator(); it.hasNext();) {
        it.next().lifeCycleNotice(s);
      }
    }
  }

  /**
   * A shortcut for iterating through all this Application's Models and calling their <tt>reset</tt>
   * methods.
   */
  public void resetModels() {
    for (Iterator<Model> it = getModels().values().iterator(); it.hasNext();) {
      try {
        it.next().reset();
      } catch (ClassCastException ex) {
        ex.printStackTrace();
      }
    }
  }

  /**
   * Retrieve the named module.
   * 
   * @throws RuntimeException
   *           if no such module can be found.
   */
  public Module getModule(String name) {
    if (getModules().containsKey(name)) {
      return (Module) getModules().get(name);
    }

    throw new RuntimeException("module " + name + " is unknown");
  }

  /**
   * Returns the map of modules, keyed off their string names.
   */
  public Map<String, Module> getModules() {
    if (modules == null) {
      modules = new HashMap<String, Module>();
    }

    return modules;
  }

  /**
   * Adds a module to the list of named modules. If this is a duplicate entry nothing will happen.
   */
  public void addModule(Module module) {
    getModules().put(module.getName(), module);
  }

  /**
   * Tells you if the given module is in the application's list.
   */
  public boolean hasModule(String name) {
    return getModules().containsKey(name);
  }

  /**
   * Adds the given model to the list of named models. If this is a duplication, nothing happens.
   */
  public void addModel(Model model) {
    getModels().put(model.getName(), model);
  }

  /**
   * Returns the named model.
   * 
   * @throws RuntimeException
   *           if no such model can be found.
   */
  public Model getModel(String name) {
    if (getModels().containsKey(name)) {
      return (Model) getModels().get(name);
    }

    throw new RuntimeException("model " + name + " is unknown");
  }

  /**
   * Returns the map of models, keyed off their string names.
   */
  public Map<String, Model> getModels() {
    if (models == null) {
      models = new HashMap<String, Model>();
    }
    return models;
  }

  /**
   * Tells you if the named model is in the application's list.
   */
  public boolean hasModel(String name) {
    return getModels().containsKey(name);
  }

  /**
   * Adds the given program to the list of named programs. If this is a duplication, nothing
   * happens.
   */
  public void addProgram(Program program) {
    getPrograms().put(program.getName(), program);
  }

  /**
   * Returns the named program.
   * 
   * @throws RuntimeException
   *           if no such program can be found.
   */
  public Program getProgram(String name) {
    if (getPrograms().containsKey(name)) {
      return (Program) getPrograms().get(name);
    }

    throw new RuntimeException("program " + name + " is unknown");
  }

  /**
   * Returns the map of programs, keyed off their string names.
   */
  public Map<String, Program> getPrograms() {
    if (programs == null) {
      programs = new HashMap<String, Program>();
    }

    return programs;
  }

  /**
   * Registered the given module (as identified by its name) as being interested in notifications
   * when the given model (also identified by its name) changes.
   */
  public void registerListener(String moduleName, String modelName) {
    Model mod = getModel(modelName);
    mod.addListener((AppDataListener) getModule(moduleName));
  }

  /**
   * Returns the current application view delegate. <b>Note that this must be set in either the
   * initApplication or startApplication methods</b>.
   * 
   * @return the current view
   */
  public AppView getView() {
    return view;
  }

  /**
   * Sets the application view to the provided subclass of AppView.
   * 
   * @param view_
   *          The view to install for the application.
   */
  public void setView(AppView view_) {
    view = view_;
  }

  /**
   * This method is called AFTER a module's enabled state has changed, so its semantics are of
   * notification, rather than imparative.
   */
  public void moduleEnableNotification(boolean enabledState, Module mod) {

  }
}
