package org.six11.util.spud;

import org.six11.util.pen.CircleArc;
import org.six11.util.pen.Functions;
import org.six11.util.pen.Line;
import org.six11.util.pen.Pt;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class CCircle extends Geom {

  public CCircle() {
    addSlot("Center"); // Pt
    addSlot("Radius"); // double
    addSlot("Circle"); // CircleArc
  }

  public String getDebugString() {
    return getName() + " Circle: " + slots.get("Center") + ", " + slots.get("Distance");
  }

  public String getHumanReadableName() {
    return "circle " + getName();
  }

  public CircleArc getCircle() {
    return (CircleArc) slots.get("Circle").value;
  }

  public Pt getCenter() {
    return (Pt) slots.get("Center").value;
  }

  public double getRadius() {
    return (Double) slots.get("Radius").value;
  }

  public void offer(Pt pt) {
    slots.get("Center").setValue(pt);
    checkSolved();
  }

  public void offer(double radius) {
    slots.get("Radius").setValue(radius);
    checkSolved();
  }

  private void checkSolved() {
    if (slotsValid("Center", "Radius")) {
      CircleArc completeCircle = new CircleArc(getCenter(), getRadius());
      slots.get("Circle").setValue(completeCircle);
      solved = true;
    }
  }
  
  public Geom intersectLine(CLine line) {
    Geom ret = null;
    Line myLine = line.getLine();
    CircleArc circ = getCircle();
    Pt[] intersectionPoints = Functions.getIntersectionPoints(circ, myLine);
    if (intersectionPoints.length == 0) {
      ret = new Nothing();
    } else if (intersectionPoints.length == 1) {
      ret = new CPoint();
      ret.offer(intersectionPoints[0]);
    } else {
      ret = new CPointSet();
      ret.offer(intersectionPoints);
    }
    
    return ret;
  }
  
  public Geom intersectCircle(CCircle circ) {
    warn("Geom.intersectCircle not implemented yet!");
    return null;
  }

  public Geom intersectPoint(CPoint pt) {
    warn("Geom.intersectPoint not implemented yet!");
    return null;
  }

  public Geom intersectPointSet(CPointSet ptset) {
    warn("Geom.intersectPointSet not implemented yet!");
    return null;
  }

  public boolean isDiscrete() {
    return true;
  }

  public Type getType() {
    return Type.Circle;
  }
}
