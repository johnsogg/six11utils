// $Id$

package org.six11.util.lev;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

public abstract class ModuleView extends JPanel implements AppDataListener {
  private Module mod;
  private boolean firstFocus;
  private Component defaultFocus;
  private FocusListener focusListener;

  /**
   * Constructs a view for the given module.
   **/
  public ModuleView(Module mod_) {
    super();
    mod = mod_;
    focusListener = new FocusListener() {
      public void focusGained(FocusEvent ev) {
        if (firstFocus) {
          focusDefault();
          firstFocus = false;
        }

      }

      public void focusLost(FocusEvent ev) {
      }
    };
    addFocusListener(focusListener);
  }

  /**
   * Returns the module associated with this view.
   **/
  public Module getModule() {
    return mod;
  }

  /**
   * Called when this module is registered with the given model.
   */
  public void modelRegistered(Model model) {
  }

  public void setDefaultFocusedItem(Component comp) {
    defaultFocus = comp;
    firstFocus = true;
  }

  private void focusDefault() {
    Runnable runner = new Runnable() {
      public void run() {
        if (defaultFocus != null) {
          defaultFocus.requestFocus();
        }
      }
    };
    SwingUtilities.invokeLater(runner);
  }
}
