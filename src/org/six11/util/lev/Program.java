// $Id: Program.java 23 2009-11-24 21:09:08Z gabe.johnson $

package org.six11.util.lev;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * A Program is a holder for a bunch of modules and views. This is useful when a set of views are
 * generally only used together and you'd like to manipulate them as a group. For instance, Mozilla
 * Classic has a web browser, mailer, HTML editor, address book, and who knows how many other
 * things. Each of these pieces are the logical equivalent of an org.six11.util2.lev.Program. In
 * some respects they seem like full-fledged applications but they are still dependent on an
 * application's resources and model/module/view management.
 * 
 * <p>
 * A Program only works if the modules and models it needs have already been registered with the
 * application.
 **/
public abstract class Program {

  private String name;
  private Application app;
  private List<ProgramListener> programListeners;

  /**
   * Make a new named program that is part of the given application.
   */
  public Program(Application app_, String name_) {
    app = app_;
    name = name_;
  }

  /**
   * Returns the name this program goes by.
   */
  public String getName() {
    return name;
  }

  /**
   * Show or hide the program. You must override this so it does the right thing for your particular
   * context. This should turn it on or off.
   */
  public abstract void setVisible(boolean state);

  /**
   * Gets the module with the given name.
   */
  public Module getModule(String modName) {
    return app.getModule(modName);
  }

  /**
   * Gets the model with the given name.
   */
  public Model getModel(String modName) {
    return app.getModel(modName);
  }

  /**
   * Get the value of app.
   **/
  public Application getApp() {
    return app;
  }

  /**
   * Called by the application (if implemented) when either the user or some other source has
   * indicated that the program should be shut down. This gives the program a chance to clean up and
   * possibly query the user if unsaved work should be saved. If the program decides that the
   * program should NOT be stopped, return false.
   */
  public abstract boolean stopRequested();

  /**
   * Registers the given listener to be notified when the program is started or stopped.
   */
  public void addProgramListener(ProgramListener lis) {
    if (programListeners == null) {
      programListeners = new ArrayList<ProgramListener>();
    }
    programListeners.add(lis);
  }

  /**
   * Informs all registered listeners that this program has started (true) or stopped (false).
   */
  protected void fireProgramState(boolean started) {
    if (programListeners != null) {
      for (Iterator<ProgramListener> it = programListeners.iterator(); it.hasNext();) {
        it.next().programStateChanged(this, started);
      }
    }
  }

  public abstract boolean isStarted();

}
