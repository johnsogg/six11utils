package org.six11.util.spud;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class CDistance extends Constraint {

  public CDistance(CPoint a, CPoint b, CDouble dist) {
    geometry.put("PtA", a);
    geometry.put("PtB", b);
    geometry.put("Distance", dist);
    geometry.put("Circle", null);
  }

  public CDistance(CPoint a, CPoint b, double d) {
    this(a, b, new CDouble(d));
  }

  public void solve() {
    CPoint ptA = (CPoint) geometry.get("PtA");
    CPoint ptB = (CPoint) geometry.get("PtB");
    CDouble dist = (CDouble) geometry.get("Distance");
    if (known(ptA, ptB)) {
      warn("CDistance.solve: Both points are known, so I can solve for their distance. "
          + "NOT IMPLEMENTED YET.");
      // it will go something like this:
      // double d = Functions.getDistance(ptA.getPt(), ptB.getPt());
      // dist.offer(d);
      // solved = true;
      // dist.solveRelatedConstraints();
    } else if (known(ptA, dist)) {
      // if one point is known, it constrains the solution space the other in a circle.
      CCircle sln = new CCircle();
      sln.offer(ptA.getPt());
      sln.offer(dist.getDouble());
      geometry.put("Circle", sln);
      solved = true;
      ptB.offer(sln);
      ptB.solveRelatedConstraints();
    }
  }

  public String getHumanReadableName() {
    return "distance-constraint ";
  }

}
