package org.six11.util.solve;

import java.awt.Color;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.six11.util.Debug;
import org.six11.util.data.Lists;
import org.six11.util.pen.DrawingBuffer;
import org.six11.util.pen.DrawingBufferRoutines;
import org.six11.util.pen.Functions;
import org.six11.util.pen.Line;
import org.six11.util.pen.Pt;
import org.six11.util.pen.Vec;

import static org.six11.util.Debug.bug;
import static org.six11.util.Debug.num;
import static java.lang.Math.abs;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class AngleConstraint extends Constraint {

  public static double TOLERANCE = 0.0001;
  public static final String NAME = "Angle";
  Pt a, f, b;
  NumericValue angle;

  public AngleConstraint(Pt a, Pt fulcrum, Pt b, NumericValue radians) {
    this.a = a;
    this.f = fulcrum;
    this.b = b;
    this.angle = radians;
  }

  public AngleConstraint(JSONObject obj, VariableBank vars) throws JSONException {
    super(obj);
    fromJson(obj, vars);
  }

  public boolean isValid(VariableBank vars) {
    return vars.getPoints().containsAll(Lists.makeSet(a, f, b));
  }

  public NumericValue getValue() {
    return angle;
  }

  public void setValue(NumericValue nv) {
    this.angle = nv;
  }

  public String getType() {
    return NAME;
  }

  public void accumulateCorrection(double heat) {
    int free = 3 - countPinned(a, b, f);
    if (free > 0) {
      double e = measureError();
      if (abs(e) > TOLERANCE) {
        // Rotate a and b about f by e/2 and -e/2 radians. (assuming free = 2).
        // Also move fulcrum along bisector if it is free.
        double shift = e / free;
        double abSum = 0;
        Vec vecA = null;
        Vec vecB = null;
        Vec vecF = null;
        if (!isPinned(a)) {
          Pt rotatedA = Functions.rotatePointAboutPivot(a, f, shift);
          vecA = new Vec(rotatedA.x - a.x, rotatedA.y - a.y);
          abSum = abSum + vecA.mag();
          accumulate(a, vecA, heat);
        }
        if (!isPinned(b)) {
          Pt rotatedB = Functions.rotatePointAboutPivot(b, f, -shift);
          vecB = new Vec(rotatedB.x - b.x, rotatedB.y - b.y);
          abSum = abSum + vecB.mag();
          accumulate(b, vecB, heat);
        }
        if (!isPinned(f)) {
          Vec toA = new Vec(f, a);
          Vec toB = new Vec(f, b);
          // cross product's tells us the 'handedness' of the triangle using f->a as a reference
          double cross = toA.cross(toB);
          double signCross = Math.signum(cross);
          double signErr = Math.signum(e);
          double dirF = 1.0;
          if (signCross == signErr) {
            dirF = -1.0;
          }
          Vec bisector = Vec.sum(toA, toB); // this bisects the triangle starting from f
          // move fulcrum towards or away from centroid depending on handedness and error. 
          // if the system is too acute the fulcrum moves inward; too obtuse and it moves outward
          double magF = (abSum / 2.0);
          double moveF = dirF * magF;
          double damping = Math.sin(abs(e));
          if (damping < 0) {
            bug("damping is negative!");
          }
          moveF = moveF * damping; // dampen the fulcrum's movement because it tends to shorten lines a lot
          vecF = bisector.getVectorOfMagnitude(moveF);
          accumulate(f, vecF, heat);
        }
      }
    }
  }

  /**
   * This is the accumulateCorrection method assuming a stationary fulcrum.
   */
  public void accumulateCorrectionStationaryFulcrum(double heat) {
    int free = 2 - countPinned(a, b);
    if (free > 0) {
      double e = measureError();
      if (abs(e) > TOLERANCE) {
        // Rotate a and b about f by e/2 and -e/2 radians. (assuming free = 2)
        double shift = e / free;
        if (!isPinned(a)) {
          Pt rotatedA = Functions.rotatePointAboutPivot(a, f, shift);
          Vec vecA = new Vec(rotatedA.x - a.x, rotatedA.y - a.y);
          accumulate(a, vecA, heat);
        }
        if (!isPinned(b)) {
          Pt rotatedB = Functions.rotatePointAboutPivot(b, f, -shift);
          Vec vecB = new Vec(rotatedB.x - b.x, rotatedB.y - b.y);
          accumulate(b, vecB, heat);
        }
      }
    }
  }

  public static double measureAngle(Pt ptA, Pt ptFulcrum, Pt ptB) {
    Vec fa = new Vec(ptA, ptFulcrum);
    Vec fb = new Vec(ptB, ptFulcrum);
    double currentAngle = Functions.getSignedAngleBetween(fa, fb);
    return currentAngle;
  }

  public double measureError() {
    double currentAngle = measureAngle(a, f, b);
    double ret = Math.signum(currentAngle) * (Math.abs(currentAngle) - angle.getValue());
    return ret;
  }

  public Line getSegment1() {
    return new Line(f, a);
  }

  public Line getSegment2() {
    return new Line(f, b);
  }

  public Pt getPtA() {
    return a;
  }

  public Pt getPtFulcrum() {
    return f;
  }

  public Pt getPtB() {
    return b;
  }

  public void draw(DrawingBuffer buf) {
    double e = measureError();
    Color col = (abs(e) > TOLERANCE) ? Color.RED : Color.GREEN;
    DrawingBufferRoutines.line(buf, getSegment1(), col, 2);
    DrawingBufferRoutines.line(buf, getSegment2(), col, 2);
    DrawingBufferRoutines.text(buf, f.getTranslated(0, 10), num(toDegrees(angle.getValue()))
        + " deg", col.darker());
  }

  public static Manipulator getManipulator() {
    Manipulator man = new Manipulator(AngleConstraint.class,
        "Angle", //
        new Manipulator.Param("a", "Point 1", true), new Manipulator.Param("b", "Point 2", true),
        new Manipulator.Param("f", "Fulcrum", true), new Manipulator.Param("angle",
            "Angle (degrees)", true));
    return man;
  }

  @Override
  public void assume(Manipulator m, VariableBank vars) {
    if (m.ptOrConstraint != getClass()) {
      bug("Can't build " + getClass().getName() + " based on manipulator for " + m.label
          + "(its ptOrConstraint is " + m.ptOrConstraint.getName() + ")");
    } else {
      bug("Yay I can build a angle thing from this manipulator");
    }
    Map<String, String> paramVals = m.getParamsAsMap();
    a = vars.getPointWithName(paramVals.get("a"));
    b = vars.getPointWithName(paramVals.get("b"));
    f = vars.getPointWithName(paramVals.get("f"));
    angle = new NumericValue(toRadians(Double.parseDouble(paramVals.get("angle"))));
  }

  /**
   * Create a manipulator that holds the values of this constraint.
   */
  public Manipulator getManipulator(VariableBank vars) {
    Manipulator man = AngleConstraint.getManipulator();
    man.setParamValue("a", a.getString("name"));
    man.setParamValue("b", b.getString("name"));
    man.setParamValue("f", f.getString("name"));
    man.setParamValue("angle", "" + toDegrees(angle.getValue()));
    man.newThing = false;
    man.constraint = this;
    return man;
  }

  @Override
  public String getHumanDescriptionString() {
    return "Angle " + name(a) + ", " + name(b) + ", " + name(f) + num(toDegrees(angle.getValue()));
  }

  public JSONObject toJson() throws JSONException {
    JSONObject ret = new JSONObject();
    ret.put("a", a.getString("name"));
    ret.put("b", b.getString("name"));
    ret.put("f", f.getString("name"));
    ret.put("angle", angle.getValue());
    return ret;
  }

  public void fromJson(JSONObject obj, VariableBank vars) throws JSONException {
    a = vars.getPointWithName(obj.getString("a"));
    b = vars.getPointWithName(obj.getString("b"));
    f = vars.getPointWithName(obj.getString("f"));
    angle = new NumericValue(obj.getDouble("angle"));
    Debug.errorOnNull(a, "a");
    Debug.errorOnNull(b, "b");
    Debug.errorOnNull(f, "f");
  }

  @Override
  public boolean involves(Pt who) {
    return (who == a || who == b || who == f);
  }

  @Override
  public void replace(Pt oldPt, Pt newPt) {
    if (oldPt == a) {
      a = newPt;
    }
    if (oldPt == b) {
      b = newPt;
    }
    if (oldPt == f) {
      f = newPt;
    }

  }

  @Override
  public Pt[] getRelatedPoints() {
    return new Pt[] {
        a, b, f
    };
  }

}
