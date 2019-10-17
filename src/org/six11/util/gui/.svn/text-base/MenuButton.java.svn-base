package org.six11.util.gui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

/**
 * A quite simple JButton implementation that triggers a JPopupMenu whenever you press the mouse on
 * it---left or right or middle or whatever-click.
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class MenuButton extends JButton {

  protected JPopupMenu pip;

  public MenuButton() {
    initialize();
  }

  public MenuButton(Icon icon) {
    super(icon);
    initialize();
  }

  public MenuButton(String text) {
    super(text);
    initialize();
  }

  public MenuButton(Action a) {
    super(a);
    initialize();
  }

  public MenuButton(String text, Icon icon) {
    super(text, icon);
    initialize();
  }

  private final void initialize() {
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        if (pip != null) {
          Component c = e.getComponent();
          pip.show(c, 0, c.getHeight());
        }
      }
    });
  }

  /**
   * Sets the popup menu that will be shown when this button is pressed. This is distinct from
   * setComponentPopupMenu.
   */
  public void setMenu(JPopupMenu pip) {
    this.pip = pip;
  }

  /**
   * If you want to try it out, or see how this is used...
   */
  public static void main(String[] args) {
    ApplicationFrame aFrame = new ApplicationFrame("Testing");
    aFrame.setSize(400, 600);
    aFrame.center();
    JPopupMenu menu = new JPopupMenu("A Nice Menu");
    menu.add("One thing");
    menu.add("Another thing");
    JMenu anotherMenu = new JMenu("More stuff...");
    anotherMenu.add("Foo");
    anotherMenu.add("Bar");
    menu.add(anotherMenu);
    menu.add("One last thing");
    MenuButton button = new MenuButton("Touch Me");
    button.setMenu(menu);
    JPanel panel = new JPanel();
    panel.add(button);
    aFrame.add(panel);
    aFrame.setVisible(true);
  }
}
