// $Id$

package org.six11.util.lev;

import java.awt.event.ActionEvent;
import javax.swing.Icon;

/**
 * A special subclass of AbstractAction that guarantees that the associated
 * module be enabled (preventing you from invoking actions on a disabled
 * module). This also provides a zero-argument <tt>activate</tt> method so the
 * action may be taken from any place in the code without needing to know
 * details.
 */
public abstract class AppAction extends NamedAction {

  private final Module mod;

  /**
   * Create an app action with a default description string, and no
   * icon. The action is only invokable when both the module and the
   * action are enabled.
   */
  public AppAction(Module mod_) {
    super();
    mod = mod_;
  }

  public AppAction(Module mod_, String name_, String toolTip_, Icon icon_) {
    super(name_, toolTip_, icon_);
    mod = mod_;
  }

  /**
   * Returns the module for this action.
   **/
  public Module getModule() {
    return mod;
  }

  /**
   * <tt>actionPerformed</tt> is finalized so that it guarantees that the
   * module is enabled, and that the provided action event is thrown away. If
   * the module is enabled, the activate method is invoked.
   */
  public final void actionPerformed(ActionEvent ev) {
    if (getModule().isEnabled() && isEnabled()) {
      activateLater();
    }
  }

  protected final boolean canTweak() {
    boolean ret = true;
    switch (getModule().getApp().getState()) {
      case Application.STATE_STOPPED:
      case Application.STATE_DISPOSED:
	ret = false;
    }
    return ret;
  }

}
