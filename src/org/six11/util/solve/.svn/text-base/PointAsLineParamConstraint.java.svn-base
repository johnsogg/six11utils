package org.six11.util.solve;

import java.awt.Color;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.six11.util.Debug;
import org.six11.util.data.Lists;
import org.six11.util.pen.DrawingBuffer;
import org.six11.util.pen.DrawingBufferRoutines;
import org.six11.util.pen.Pt;
import org.six11.util.pen.Vec;
import static java.lang.Math.abs;
import static org.six11.util.Debug.bug;
import static org.six11.util.Debug.num;

/**
 * Constrains a point to be on a line segment and some percentage of the distance between the end
 * points of that line segment. Useful for forcing a point to be exactly between two others, for
 * example.
 */
public class PointAsLineParamConstraint extends Constraint {

  public static double TOLERANCE = 0.0001;
  public static final String NAME = "Pont As Line Param";

  Pt lineA, lineB, target;
  NumericValue dist;

  public PointAsLineParamConstraint(Pt lineA, Pt lineB, NumericValue proportionFromAToB, Pt target) {
    System.out.println("Warning: you should really not use the PointAsLineParamConstraint class.");
    this.lineA = lineA;
    this.lineB = lineB;
    this.target = target;
    this.dist = proportionFromAToB;
    setPinned(target, true);
  }

  public PointAsLineParamConstraint(JSONObject obj, VariableBank vars) throws JSONException {
    super(obj);
    fromJson(obj, vars);
  }

  public boolean isValid(VariableBank vars) {
    return vars.getPoints().containsAll(Lists.makeSet(lineA, lineB, target));
  }

  public String getType() {
    return NAME;
  }

  public void accumulateCorrection(double heat) {
    double e = measureError();
    if (e > TOLERANCE) {
      Pt auth = getAuthority();
      Vec v = new Vec(target, auth);
      target.move(v);
    }
  }

  public double measureError() {
    Pt auth = getAuthority();
    return auth.distance(target);
  }

  private Pt getAuthority() {
    double len = lineA.distance(lineB);
    double authDist = len * dist.getValue();
    Vec v = new Vec(lineA, lineB).getUnitVector().getScaled(authDist);
    return new Pt(lineA.x + v.getX(), lineA.y + v.getY());
  }

  public void draw(DrawingBuffer buf) {
    // only need to draw a line. the points should be taken care of elsewhere.
    Color col = (abs(measureError()) > TOLERANCE) ? Color.RED : Color.GREEN;
    DrawingBufferRoutines.line(buf, lineA, lineB, col, 1.0);
  }

  public Pt getTarget() {
    return target;
  }

  public static Manipulator getManipulator() {
    Manipulator man = new Manipulator(PointAsLineParamConstraint.class,
        "Point As Line Param", //
        new Manipulator.Param("p1", "Point 1", true), new Manipulator.Param("p2", "Point 2", true),
        new Manipulator.Param("target", "Target Point", true), new Manipulator.Param("dist",
            "Distance", true));
    return man;
  }

  @Override
  public void assume(Manipulator m, VariableBank vars) {
    if (m.ptOrConstraint != getClass()) {
      bug("Can't build " + getClass().getName() + " based on manipulator for " + m.label
          + "(its ptOrConstraint is " + m.ptOrConstraint.getName() + ")");
    } else {
      bug("Yay I can build a point-as-line-param thing from this manipulator");
    }
    Map<String, String> paramVals = m.getParamsAsMap();
    bug(num(paramVals.values(), " "));
    lineA = vars.getPointWithName(paramVals.get("p1"));
    lineB = vars.getPointWithName(paramVals.get("p2"));
    target = vars.getPointWithName(paramVals.get("target"));
    dist = new NumericValue(Double.parseDouble(paramVals.get("dist")));
  }

  @Override
  public Manipulator getManipulator(VariableBank vars) {
    Manipulator man = PointAsLineParamConstraint.getManipulator();
    man.setParamValue("p1", lineA.getString("name"));
    man.setParamValue("p2", lineB.getString("name"));
    man.setParamValue("target", target.getString("name"));
    man.setParamValue("dist", "" + dist.getValue());
    man.newThing = false;
    man.constraint = this;
    return man;
  }

  public String getHumanDescriptionString() {
    return "PointAsLineParam " + name(lineA) + ", " + name(lineB) + ", " + name(lineB) + ", "
        + name(target) + " =  " + num(dist.getValue());
  }

  public JSONObject toJson() throws JSONException {
    JSONObject ret = new JSONObject();
    ret.put("p1", lineA.getString("name"));
    ret.put("p2", lineB.getString("name"));
    ret.put("target", target.getString("name"));
    ret.put("dist", dist.getValue());
    return ret;
  }

  public void fromJson(JSONObject obj, VariableBank vars) throws JSONException {
    lineA = vars.getPointWithName(obj.getString("p1"));
    lineB = vars.getPointWithName(obj.getString("p2"));
    target = vars.getPointWithName(obj.getString("target"));
    dist = new NumericValue(obj.getDouble("dist"));
    Debug.errorOnNull(lineA, "lineA");
    Debug.errorOnNull(lineB, "lineB");
    Debug.errorOnNull(target, "target");
  }

  @Override
  public boolean involves(Pt who) {
    return (who == lineA || who == lineB || who == target);
  }

  @Override
  public void replace(Pt oldPt, Pt newPt) {
    if (oldPt == lineA) {
      lineA = newPt;
    }
    if (oldPt == lineB) {
      lineB = newPt;
    }
    if (oldPt == target) {
      target = newPt;
    }
  }

  @Override
  public Pt[] getRelatedPoints() {
    return new Pt[] {
        lineA, lineB, target
    };
  }
}
