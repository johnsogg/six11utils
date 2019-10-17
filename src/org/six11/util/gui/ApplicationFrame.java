// $Id: ApplicationFrame.java 111 2011-03-02 18:34:25Z gabe.johnson@gmail.com $

package org.six11.util.gui;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.MenuBar;

/**
 * A JFrame that handles some of things you commonly do to a frame when you first make it -- it
 * centers itself and makes it so the application quits when you close it (unless you're in an
 * applet).
 * 
 * <pre>
 * // create a centered app frame
 * ApplicationFrame af = new ApplicationFrame(&quot;test&quot;, w, h);
 * af.setVisible(true);
 * </pre>
 **/

public class ApplicationFrame extends JFrame {

  protected MenuBar menu;

  public ApplicationFrame() {
    this("ApplicationFrame v1.0");
  }

  public ApplicationFrame(String title) {
    this(title, 500, 400);
  }

  public ApplicationFrame(String title, Dimension desiredSize) {
    this(title, desiredSize.width, desiredSize.height);
  }

  public ApplicationFrame(String title, int w, int h) {
    super(title);
    try {
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    } catch (java.security.AccessControlException ex) {
      System.out
          .println("It looks like you aren't allowed to exit the VM, so closing the window won't "
              + "cause that to happen.");
    }
    createUI(w, h);
  }

  public void setNoQuitOnClose() {
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
  }

  public void maximize() {
    setExtendedState(Frame.MAXIMIZED_BOTH);
  }

  protected void createUI(int w, int h) {
    setSize(w, h);
    center();
  }

  public void center() {
    Components.centerComponent(this);
  }
}
