package org.six11.util.spud;

import org.six11.util.pen.Vec;

/**
 * 
 *
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class CVecDirection extends Constraint {

  protected Vec dir;
  
  public CVecDirection(Vec dir, CVec vA) {
    geometry.put("dir", vA);
    this.dir = dir;
  }

  public void solve() {
    geometry.get("dir").offer(dir);
    solved = true; 
    geometry.get("dir").solveRelatedConstraints();
  }

  public String getHumanReadableName() {
    return "vec-direction";
  }

}
