// $Id: InfoTextBox.java 23 2009-11-24 21:09:08Z gabe.johnson $

package org.six11.util.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class InfoTextBox extends ColoredTextPane {

  public InfoTextBox() {
    super();
    setEditable(false);
  }

  public void paint(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    super.paint(g);
  }

}
