package org.six11.util.spud;

import java.util.ArrayList;
import java.util.List;

import org.six11.util.pen.Pt;

/**
 * 
 *
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class CPointSet extends Geom {

  public CPointSet() {
    super();
    addSlot("Points");
  }

  public String getHumanReadableName() {
    return "pointset " + getName();
  }
  
  public void offer(Pt[] points) {
    slots.get("Points").value = points;
    solved = true;
  }

  public String getDebugString() {
    return getName() + " pointset with " + size() + " points";
  }

  public Geom intersectCircle(CCircle circ) {
    return circ.intersectPointSet(this);
  }

  public Geom intersectLine(CLine line) {
    return line.intersectPointSet(this);
  }

  public Geom intersectPoint(CPoint pt) {
    return pt.intersectPointSet(this);
  }

  public Pt[] getPoints() {
    return (Pt[]) slots.get("Points").value;
  }

  public int size() {
    int ret = 0;
    if (slots.get("Points").valid) {
      ret =  getPoints().length;
    }
    return ret;
  }
  
  public Geom intersectPointSet(CPointSet ptset) {
    List<Pt> overlap = new ArrayList<Pt>();
    for (Pt myPoint : getPoints()) {
      for (Pt otherPoint : ptset.getPoints()) {
        if (myPoint.isSameLocation(otherPoint)) {
          overlap.add(myPoint);
        }
      }
    }
    Geom ret = new Nothing();
    if (overlap.size() > 0) {
      ret = new CPointSet();
      ret.offer(overlap.toArray(new Pt[overlap.size()]));
    }
    return ret;
  }

  @Override
  public boolean isDiscrete() {
    return true;
  }

  public Type getType() {
    return Type.PointSet;
  }

}
