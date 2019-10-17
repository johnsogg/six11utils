// $Id: AppView.java 23 2009-11-24 21:09:08Z gabe.johnson $

package org.six11.util.lev;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.SwingUtilities;


/**
 * The GUI for the application. The app view is responsible for laying out
 * modules, controlling the use of dialogs, handling top-level window events
 * (such as closing and minimizing). This also sets up the default colors for
 * the entire application.
 */
public abstract class AppView {


  private Application app;
  private Container root;

  /**
   * Create a new AppView for the given application.
   */
  public AppView(Application app_) {
    app = app_;
  }

  /**
   * Returns a reference to the application.
   */
  public Application getApp() {
    return app;
  }

  /**
   * Make the application's GUI either visible or hidden.
   */
  public abstract void setVisible(boolean visible_);

  /**
   * Release all resources associated with the view. This essentially means
   * storing whatever needs to be stored about the state of the GUI and then
   * calling dispose() on all the constituent modules views.
   */
  public abstract void dispose();

  /**
   * Returns the top-most container, which is useful for centering. This is an
   * optional method, so it may return null.
   */
  public Container getRoot() {
    return root;
  }

  /**
   * Sets the top-most container, which is accessed with getRoot().
   */
  public final void setRoot(Container root_) {
    root = root_;
  }

  /**
   * For some reason, SwingUtilities.invokeAndWait() bitches if you
   * call it from within the event dispatch thread. This is dumb.
   *
   * The doInSwingThread method will perform the given Runnable and
   * not complain that you're doing it from the wrong thread--it will
   * figure things out for you.
   *
   * If an exception is thrown during the invokeAndWait stage, it will
   * be caught and silently ignored.
   */
  public static void doInSwingThread(Runnable runner) {
    if (SwingUtilities.isEventDispatchThread()) {
      runner.run();
    } else {
      try {
	SwingUtilities.invokeAndWait(runner);
      } catch (Exception ex) { ; }
    }
  }

  /**
   * Centers the component over the target component. If the target component
   * is null, the 'centerMe' component is put in the middle of the screen.
   *
   * @param centerMe the component to be centered.
   * @param target the target component, or null for no target
   */
  public static void centerComponent(Component centerMe, Component target) {
    Dimension targetSize;

    if (target == null) {
      targetSize = Toolkit.getDefaultToolkit().getScreenSize();
    } else {
      targetSize = target.getSize();
    }

    int dx = 0;
    int dy = 0;

    if (target instanceof Window) {
      dx   = target.getLocationOnScreen().x;
      dy   = target.getLocationOnScreen().y;
    }

    Point targetCenter = new Point(targetSize.width / 2, targetSize.height / 2);

    Point topLeft = new Point((dx) +
        (targetCenter.x - (centerMe.getSize().width / 2)),
        (dy) + (targetCenter.y - (centerMe.getSize().height / 2)));
    centerMe.setLocation(topLeft);
  }

//  /**
//   * Retrieve an icon with the given path name from the classpath. If
//   * your application has jar files, these are checked. This will
//   * return an ImageIcon if the icon can be loaded; null otherwise.
//   */
//  public static ImageIcon getIcon(String name) {
//    throw new RuntimeException("This method shouldn't be used at Ecovate because it doesn't use an allowed network connection.");
//  }
}
