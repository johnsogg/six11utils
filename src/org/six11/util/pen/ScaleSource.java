// $Id: ScaleSource.java 12 2009-11-09 22:58:47Z gabe.johnson $

package org.six11.util.pen;

import java.awt.geom.Rectangle2D;

/**
 * An object (JComponent, probably) that straddles two different
 * coordinate spaces might need to scale points from one coordinate
 * space (ie the screen) to another (ie the rendered world) -- this
 * lets you indicate what the scale factor is at any given
 * point. FlowSelectionHandler can make use of this. The two
 * coordinate systems are referred to as 'screen' and 'world'
 * coordinates.
 */
public class ScaleSource {
  
  // screen * sf = world
  // screen = world / sf
  private double sf = 1.0;

  public double getScaleFactor() {
    return sf;
  }

  public void setScaleFactor(double sf) {
    this.sf = sf;
  }

  public Pt getWorldPt(Pt screenPt) {
    return new Pt(screenPt.getX() * sf, screenPt.getY() * sf);
  }

  public Pt getScreenPt(Pt worldPt) {
    return new Pt(worldPt.getX() / sf, worldPt.getY() / sf);
  }

  public Rectangle2D getWorldRect(Rectangle2D screenRect) {
    return new Rectangle2D.Double(screenRect.getX() * sf,
				  screenRect.getY() * sf,
				  screenRect.getWidth() * sf,
				  screenRect.getHeight() * sf);
  }

  public Rectangle2D getScreenRect(Rectangle2D worldRect) {
    return new Rectangle2D.Double(worldRect.getX() / sf,
				  worldRect.getY() / sf,
				  worldRect.getWidth() / sf,
				  worldRect.getHeight() / sf);
  }
}
