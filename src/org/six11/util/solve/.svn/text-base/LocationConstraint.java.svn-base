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

import static org.six11.util.Debug.bug;
import static org.six11.util.Debug.num;

public class LocationConstraint extends Constraint {

  public static double TOLERANCE = 0.0001;
  public static final String NAME = "Pin";
  Pt p, target;

  public LocationConstraint(Pt p, Pt target) {
    this.p = p;
    this.target = target;
  }

  public LocationConstraint(JSONObject obj, VariableBank vars) throws JSONException {
    super(obj);
    fromJson(obj, vars);
  }

  public boolean isValid(VariableBank vars) {
    return vars.getPoints().containsAll(Lists.makeSet(p));
  }

  public String getType() {
    return NAME;
  }

  public void accumulateCorrection(double heat) {
    double error = measureError();
    addMessage(p.getString("name") + ": " + num(error) + ". correction vector: "
        + num(target.x - p.x) + ", " + num(target.y - p.y));
    if (!isPinned(p) && error > TOLERANCE) {
      Vec toTarget = new Vec(target.x - p.x, target.y - p.y);
      accumulate(p, toTarget, heat);
    }
  }

  public double measureError() {
    return p.distance(target);
  }

  public void draw(DrawingBuffer buf) {
    DrawingBufferRoutines.dot(buf, target, 3, 0.1, Color.BLACK, Color.red.brighter());
    if (!p.isSameLocation(target)) {
      DrawingBufferRoutines.arrow(buf, p, target, 2, Color.LIGHT_GRAY);
    }
  }

  public static Manipulator getManipulator() {
    Manipulator man = new Manipulator(LocationConstraint.class, "Location", //
        new Manipulator.Param("p", "Point", true), new Manipulator.Param("target",
            "Desired Location", true));
    return man;
  }

  public void assume(Manipulator m, VariableBank vars) {
    if (m.ptOrConstraint != getClass()) {
      bug("Can't build " + getClass().getName() + " based on manipulator for " + m.label
          + "(its ptOrConstraint is " + m.ptOrConstraint.getName() + ")");
    } else {
      bug("Yay I can build a location thing from this manipulator");
    }
    Map<String, String> paramVals = m.getParamsAsMap();
    bug(num(paramVals.values(), " "));
    p = vars.getPointWithName(paramVals.get("p"));
    target = vars.getPointWithName(paramVals.get("target"));
  }

  public Manipulator getManipulator(VariableBank vars) {
    Manipulator man = LocationConstraint.getManipulator();
    man.setParamValue("p", p.getString("name"));
    man.setParamValue("target", target.getString("name"));
    man.newThing = false;
    man.constraint = this;
    return man;
  }

  public String getHumanDescriptionString() {
    return "Location " + name(p) + " => " + name(target);
  }

  public JSONObject toJson() throws JSONException {
    JSONObject ret = new JSONObject();
    ret.put("p", p.getString("name"));
    ret.put("target", target.getString("name"));
    return ret;
  }

  public void fromJson(JSONObject obj, VariableBank vars) throws JSONException {
    p = vars.getPointWithName(obj.getString("p"));
    target = vars.getPointWithName(obj.getString("target"));
    Debug.errorOnNull(p, "p");
    Debug.errorOnNull(target, "target");
  }

  @Override
  public boolean involves(Pt who) {
    return (who == p || who == target);
  }

  @Override
  public void replace(Pt oldPt, Pt newPt) {
    if (oldPt == p) {
      p = newPt;
    }
    if (oldPt == target) {
      target = newPt;
    }
  }

  @Override
  public Pt[] getRelatedPoints() {
    return new Pt[] {
        p, target
    };
  }

}
