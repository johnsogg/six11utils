package org.six11.util.spud;

/**
 * This is the opposite of Infinity: it represents no possible geometry. For example if you try to
 * intersect two parallel lines, you get Nothing. Intersecting Nothing with anything gives you
 * Nothing.
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class Nothing extends Geom {

  public Nothing() {
  }

  public String getDebugString() {
    return "nothing";
  }

  public Geom intersectLine(CLine line) {
    return this;
  }

  public Geom intersectPoint(CPoint pt) {
    return this;
  }

  public String getHumanReadableName() {
    return "nothing";
  }

  public Geom intersectCircle(CCircle circ) {
    return this;
  }

  public Geom intersectPointSet(CPointSet ptset) {
    return this;
  }

  @Override
  public boolean isDiscrete() {
    return true;
  }

  public Type getType() {
    return Type.Nothing;
  }

}
