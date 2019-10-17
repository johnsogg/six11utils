package org.six11.util.spud;

import org.six11.util.pen.Pt;

public class CPointLocation extends Constraint {

  private Pt data;
  
  public CPointLocation(Geom c, Pt data) {
    super();
    geometry.put("pt", c);
    this.data = data;
  }

  public String getHumanReadableName() {
    return "point-location";
  }

  public void solve() {
    geometry.get("pt").offer(data);
    // must set this to solved before solving pt's related constraints, because this
    // is among those related constraints.
    solved = true; 
    geometry.get("pt").solveRelatedConstraints();
  }

}
