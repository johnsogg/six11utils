package org.six11.util.spud;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.six11.util.Debug;

public class ConstraintModel {

  private List<Constraint> constraints;

  public ConstraintModel() {
    this.constraints = new ArrayList<Constraint>();
  }

  public void addConstraint(Constraint c) {
    constraints.add(c);
  }

  public String getMondoDebugString() {
    StringBuilder buf = new StringBuilder();
    String space1 = "  ";
    String space2 = "     ";
    buf.append("\n-------------------------------\n");
    for (Constraint c : constraints) {
      buf.append(space1 + c + ":\n");
      buf.append(c.getMondoDebugString(space2));
    }
    buf.append("-------------------------------");
    return buf.toString();
  }

  public void solve() {
    long startTime = System.currentTimeMillis();
    int lastUnsolved = countUnsolved();
    boolean progress;
    do {
      solveRound();
      int thisUnsolved = countUnsolved();
      progress = thisUnsolved < lastUnsolved;
      lastUnsolved = thisUnsolved;
    } while (lastUnsolved > 0 && progress);
    long endTime = System.currentTimeMillis();
    bug("solve() complete: " + (endTime - startTime) + " ms, " + lastUnsolved
        + " unsolved variables");
  }

  private int countUnsolved() {
    int numUnsolved = 0;
    Set<Geom> vars = new HashSet<Geom>();
    for (Constraint c : constraints) {
      if (!c.isSolved()) {
        numUnsolved++;
      }
      for (Geom value : c.geometry.values()) {
        vars.add(value);
      }
    }
    for (Geom var : vars) {
      if (var != null && !var.isSolved()) {
        numUnsolved++;
      }
    }
    return numUnsolved;
  }

  private void solveRound() {
    for (Constraint c : constraints) {
      c.solveSafely();
    }
  }

  public Set<Geom> getAllGeometry() {
    Set<Geom> ret = new HashSet<Geom>();
    for (Constraint c : constraints) {
      for (Geom var : c.geometry.values()) {
        ret.add(var);
      }
    }
    return ret;
  }

  private static void bug(String what) {
    Debug.out("ConstraintModel", what);
  }

}
