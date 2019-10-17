package org.six11.util.spud;

import org.six11.util.pen.Pt;
import org.six11.util.pen.Vec;

/**
 * 
 *
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class CLineDefinition extends Constraint {


  public CLineDefinition(CLine line, Geom ptA, CVec dir) {
    super();
    geometry.put("line", line);
    geometry.put("ptA", ptA);
    geometry.put("ptB", null);
    geometry.put("dir", dir);
  }
  
  public void solve() {
    CPoint ptA = (CPoint) geometry.get("ptA");
    CPoint ptB = (CPoint) geometry.get("ptB");
    CVec dir = (CVec) geometry.get("dir");

    if (known(ptA, ptB)) {
      Vec derivedDirection = new Vec(ptA.getPt(), ptB.getPt());
      dir = new CVec();
      geometry.put("dir", dir);
      dir.offer(derivedDirection);
      fillLine();
    } else if (known(ptA, dir)) {
      Pt a = ptA.getPt();
      Vec v = dir.getDir();
      Pt b = new Pt(a.getX() + v.getX(), a.getY() + v.getY());
      ptB = new CPoint();
      geometry.put("ptB", ptB);
      ptB.offer(b);
      fillLine();
    } else {
      // can not solve yet.
    }
  }

  private void fillLine() {
    Geom line = (Geom) geometry.get("line");
    CPoint ptA = (CPoint) geometry.get("ptA");
    CVec dir = (CVec) geometry.get("dir");
    line.offer(ptA.getPt());
    line.offer(dir.getDir());
    solved = true;
    line.solveRelatedConstraints();
  }
  
  public String getHumanReadableName() {
    return "line-definition";
  }

}
