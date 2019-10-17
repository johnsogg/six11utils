package org.six11.util.spud;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class CDoubleDefinition extends Constraint {

  private double amt;

  public CDoubleDefinition(CDouble dest, double amt) {
    super();
    geometry.put("double", dest);
    this.amt = amt;
  }

  public void solve() {
    geometry.get("double").offer(amt);
    solved = true;
  }

  public String getHumanReadableName() {
    return "double-definition";
  }

}
