package org.six11.util.solve;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.six11.util.Debug.bug;

import org.six11.util.pen.Pt;

public class VariableBank {

  private List<Pt> points;
  private List<Constraint> constraints;
  
  public VariableBank() {
    points = new ArrayList<Pt>();
    constraints = new ArrayList<Constraint>();
  }

  public List<Constraint> getConstraints() {
    return constraints;
  }
  
  public List<Pt> getPoints() {
    return points;
  }

  public void clear() {
    points.clear();
    getConstraints().clear();
  }
  
  public Pt getPointWithName(String n) {
    Pt ret = null;
    for (Pt pt : points) {
      if (pt.getString("name").equals(n)) {
        ret = pt;
        break;
      }
    }
    return ret;
  }
  
  public boolean hasPoint(Pt pt) {
    return points.contains(pt);
  }
  
  public Set<Constraint> searchConstraints(Set<ConstraintFilter> filters) {
    Set<Constraint> ret = new HashSet<Constraint>();
    ret.addAll(getConstraints());
    for (ConstraintFilter filter : filters) {
      ret = filter.filter(ret);
    }
    return ret;
  }
  
  public static ConstraintFilter getTypeFilter(final Class<? extends Constraint> constraintClass) {
    return new ConstraintFilter() {
      public Set<Constraint> filter(Set<Constraint> input) {
        Set<Constraint> ret = new HashSet<Constraint>();
        for (Constraint c : input) {
          if (constraintClass.isAssignableFrom(c.getClass())) {
            ret.add(c);
          }
        }
        return ret;
      }      
    };
  }
  
  public abstract static class ConstraintFilter {
    public abstract Set<Constraint> filter(Set<Constraint> input);
  }

  public Constraint getConstraintWithID(int cID) {
    Constraint ret = null;
    for (Constraint c : getConstraints()) {
      if (c.getID() == cID) {
        ret = c;
        break;
      }
    }
    if (ret == null) {
      bug("Warning: variable bank does not contain a constraint with the id " + cID + ". Here's what I have:");
      for (Constraint c : getConstraints()){
        bug("  " + c.getID() + " = " + c.getHumanDescriptionString());
      }
    }
    return ret;
  }
}
