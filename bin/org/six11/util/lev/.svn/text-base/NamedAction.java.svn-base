// $Id$

package org.six11.util.lev;

import java.awt.event.ActionEvent;
import javax.swing.Icon;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * A special subclass of AbstractAction that guarantees that the associated module be enabled
 * (preventing you from invoking actions on a disabled module). This also provides a zero-argument
 * <tt>activate</tt> method so the action may be taken from any place in the code without needing to
 * know details.
 */
public abstract class NamedAction extends AbstractAction {

  /**
   * Create an app action with a default description string, and no icon. The action is only
   * invokable when both the module and the action are enabled.
   */
  public NamedAction(String name_) {
    super();
    setName(name_);
  }

  public NamedAction(String name_, String toolTip_, Icon icon_) {
    this(name_);
    setToolTip(toolTip_);
    setIcon(icon_);
  }

  protected NamedAction() {
    super();
  }

  /**
   * @param string
   * @param keyStroke
   */
  public NamedAction(String name_, KeyStroke keyStroke) {
    this(name_);
    setKeystroke(keyStroke);
  }

  /**
   * Set the icon.
   **/
  public final void setIcon(Icon icon_) {
    putValue(SMALL_ICON, icon_);
  }

  /**
   * Get the current icon (may be null if one hasn't been set).
   **/
  public Icon getIcon() {
    return (Icon) getValue(SMALL_ICON);
  }

  /**
   * Set the name (the localized String the user sees).
   **/
  public final void setName(String name_) {
    putValue(NAME, name_);
  }

  /**
   * Get the name (the localized String the user sees).
   **/
  public String getName() {
    return (String) getValue(NAME);
  }

  /**
   * Set the value of toolTip.
   **/
  public final void setToolTip(String toolTip_) {
    putValue(SHORT_DESCRIPTION, toolTip_);
  }

  /**
   * Get the value of toolTip.
   **/
  public String getToolTip() {
    return (String) getValue(SHORT_DESCRIPTION);
  }

  /**
   * Gives the KeyStroke accelerator key for this action.
   */
  public KeyStroke getKeystroke() {
    return (KeyStroke) getValue(ACCELERATOR_KEY);
  }
  
  /**
   * Sets the KeyStroke accelerator associated with this action.
   */
  public void setKeystroke(KeyStroke key) {
    putValue(ACCELERATOR_KEY, key);
  }
  
  /**
   * Invokes activate() if the action is enabled.
   */
  public void actionPerformed(ActionEvent ev) {
    if (isEnabled()) {
      activateLater();
    }
  }

  /**
   * This is the workhorse of the AppAction class. When the action must be invoked, this method is
   * used. Implement all functionality here.
   */
  public abstract void activate();

  /**
   * Creates a Runnable that executes 'activate' in the Swing event dispatch thread.
   */
  public void activateLater() {
    Runnable r = new Runnable() {
      public void run() {
        activate();
      }
    };
    if (!Application.headless)
      SwingUtilities.invokeLater(r);
    else
      r.run();
  }

  /**
   * Asks the action to look at the relevent models and determine if it should be enabled or not.
   * This is a null implementation, so if there is no need to implement it, you don't need to.
   * 
   * <p>
   * <b>NOTE</b>: This method doesn't handle threading issues. It is generally bad to run code that
   * modifies the GUI in anything other than the event dispatch thread, so in the case that this
   * code DOES modify the GUI (like setting an action enabled or disabled), you must ensure it runs
   * in the swing event thread.
   * </p>
   * 
   * <p>
   * <b>Override this to do what you need to do to set the enabled state of this action. But when
   * you want to run this method, don't call it directly, call tweak(), which will run this method
   * in the swing thread.</b>
   * </p>
   */
  public synchronized void tweakState() {
  }

  protected boolean canTweak() {
    return true;
  }

  /**
   * Creates a Runnable that executes tweakState(), and sticks that runnable in the swing event
   * queue using SwingUtilities.invokeLater(). <b>This is declared final to ensure that the
   * tweakState() method is run in the swing thread. You may therefore not override this. Call this
   * method when you want to have tweakState invoked.</b>
   */
  public final void tweak() {
    if (!canTweak()) {
      return;
    }

    Runnable r = new Runnable() {
      public void run() {
        tweakState();
      }
    };

    if (!Application.headless)
      SwingUtilities.invokeLater(r);
    else
      r.run();
  }

}
