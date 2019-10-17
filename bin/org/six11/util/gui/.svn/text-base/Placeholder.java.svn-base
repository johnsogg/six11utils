// $Id$

package org.six11.util.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Random;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

public class Placeholder extends JComponent {

  private static int randomCounter = 0;
  Dimension pref;
  String name;
  String text;
  Color myColor;
  Stroke myStroke;
  Font normal;
  Font bold;

  public Placeholder(Dimension pref, String name) {
    this.pref = pref;
    this.name = name;
    this.text = name;
    this.myColor = randColor();
    this.myStroke = new BasicStroke
      (3f,                        // pen thickness
       BasicStroke.CAP_BUTT,      // cap
       BasicStroke.JOIN_MITER,    // join
       1f,                        // miter limit
       new float[] { 7, 7 },      // dash
       7);                        // dash phase
    this.bold = new Font("Dialog", Font.BOLD, 12);
    this.normal = new Font("Dialog", Font.PLAIN, 12);

    addComponentListener(new ComponentAdapter() {
	public void componentResized(ComponentEvent e) {
	  boolean happy = (getSize().equals(Placeholder.this.pref));
	  setText(getName() + "" + (happy ? ": (prefsize)" : ""));
	  repaint();
	}
      });
  }

  public void setText(String t) {
    text = t;
  }

  public String getName() {
    return name;
  }

  public void paintComponent(Graphics g1) {
    Graphics2D g = (Graphics2D) g1;
    RoundRectangle2D rec = new RoundRectangle2D.Double(3, 3, getWidth() - 6, getHeight() - 6, 40, 40);
    g.setColor(myColor.brighter());
    g.fill(rec);
    g.setColor(myColor.darker());
    g.setStroke(myStroke);
    g.draw(rec);
    FontMetrics fm = g.getFontMetrics(bold);
    int tw = fm.stringWidth(text);
    int th = fm.getHeight();
    g.setFont(bold);
    g.drawString(text, (getWidth() / 2) - (tw / 2), (getHeight() / 2) - (th / 2));
    
    fm = g.getFontMetrics(normal);
    String sz = getSize().width + "x" + getSize().height + " (pref: "+ 
        getPreferredSize().width + "x" + getPreferredSize().height + ")";
    tw = fm.stringWidth(sz);
    th = fm.getHeight();
    g.setFont(normal);
    g.drawString(sz, (getWidth() / 2) - (tw / 2), (getHeight() / 2) + th);
    
  }

  public Dimension getPreferredSize() {
    return pref;
  }

  Color randColor() {
    Random rand = new Random(System.currentTimeMillis() + randomCounter++);
    int r = 225 - rand.nextInt(96);
    int g = 225 - rand.nextInt(96);
    int b = 225 - rand.nextInt(96);
    return new Color(r, g, b);
  }
}
