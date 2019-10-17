package org.six11.util.spud;

import org.six11.util.pen.Pt;
import org.six11.util.pen.Vec;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class CPointAlongSegment extends Constraint {

  public CPointAlongSegment(CPoint ptA, CPoint ptB, CPoint ptSomewhere, double val) {
    this(ptA, ptB, ptSomewhere, new CDouble(val));
  }

  public CPointAlongSegment(CPoint ptA, CPoint ptB, CPoint ptSomewhere, CDouble val) {
    geometry.put("start", ptA);
    geometry.put("end", ptB);
    geometry.put("mid", ptSomewhere);
    geometry.put("scalar", val);
  }

  public void solve() {
    // the scalar value must be known.
    CDouble val = (CDouble) geometry.get("scalar");
    if (known(val)) {
      CPoint ptA = (CPoint) geometry.get("start");
      CPoint ptB = (CPoint) geometry.get("end");
      CPoint ptM = (CPoint) geometry.get("mid");
      // Given any two of these we can derive the third point.
      if (known(ptA, ptB)) {
        // derive ptM
        double v = val.getDouble();
        Vec aToB = new Vec(ptA.getPt(), ptB.getPt());
        Vec aToBDir = aToB.getUnitVector();
        double d = aToB.mag() * v;
        double mx = ptA.getPt().x + (d * aToBDir.getX());
        double my = ptA.getPt().y + (d * aToBDir.getY());
        Pt mPt = new Pt(mx, my);
        ptM.offer(mPt);
      } else if (known(ptA, ptM)) {
        // derive ptB
        double v = val.getDouble();
        Vec aToM = new Vec(ptA.getPt(), ptM.getPt());
        double aToMDist = aToM.mag();
        Vec aToMDir = aToM.getUnitVector();
        double d = aToMDist / v;
        double bx = ptA.getPt().x + (d * aToMDir.getX());
        double by = ptA.getPt().y + (d * aToMDir.getY());
        Pt bxPt = new Pt(bx, by);
        ptB.offer(bxPt);
      } else if (known(ptM, ptB)) {
        // derive ptA
        double vInv = 1.0 - val.getDouble();
        Vec bToM = new Vec(ptB.getPt(), ptM.getPt());
        double bToMDist = bToM.mag();
        Vec bToMDir = bToM.getUnitVector();
        double d = bToMDist / vInv;
        double ax = ptB.getPt().x + (d * bToMDir.getX());
        double ay = ptB.getPt().y + (d * bToMDir.getY());
        Pt axPt = new Pt(ax, ay);
        ptA.offer(axPt);
      }
      checkSolved();
    }
  }

  private void checkSolved() {
    CPoint ptA = (CPoint) geometry.get("start");
    CPoint ptB = (CPoint) geometry.get("end");
    CPoint ptM = (CPoint) geometry.get("mid");
    if (known(ptA, ptB, ptM)) {
      solved = true;
      ptA.solveRelatedConstraints();
      ptB.solveRelatedConstraints();
      ptM.solveRelatedConstraints();
    }
  }
  
  public String getHumanReadableName() {
    return "point-along-segment";
  }

}
