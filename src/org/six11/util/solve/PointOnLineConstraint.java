package org.six11.util.solve;

import java.awt.Color;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.six11.util.Debug;
import org.six11.util.data.Lists;
import org.six11.util.data.Statistics;
import org.six11.util.pen.DrawingBuffer;
import org.six11.util.pen.DrawingBufferRoutines;
import org.six11.util.pen.Functions;
import org.six11.util.pen.Line;
import org.six11.util.pen.Pt;
import org.six11.util.pen.Vec;
import static java.lang.Math.abs;
import static org.six11.util.Debug.bug;
import static org.six11.util.Debug.num;

public class PointOnLineConstraint extends Constraint {

  public static double TOLERANCE = 0.0001;
  public static final String NAME = "Point On Line";

  Pt antipodeA, antipodeB;
  Set<Pt> manyPoints;

  public PointOnLineConstraint(Set<Pt> points) {
    super();
    manyPoints = new HashSet<Pt>(points);
    makeAntipodes();
  }

  public PointOnLineConstraint(JSONObject obj, VariableBank vars) throws JSONException {
    super(obj);
    fromJson(obj, vars);
  }

  public boolean isValid(VariableBank vars) {
    return manyPoints.size() > 2 && vars.getPoints().containsAll(manyPoints);
  }

  public String getType() {
    return NAME;
  }

  public void accumulateCorrection(double heat) {
    double e = measureError();
    if (e > TOLERANCE) {
      int pins = countPinned(manyPoints);
      Line target = getTargetLine();
      for (Pt pt : manyPoints) {
        maybeMove(pins, pt, target, heat);
      }
    }
  }

  public Line getTargetLine() {
    return getTargetLineRegressionStyle();
  }

  private Line getTargetLineRegressionStyle() {
    Pt mean = Functions.getMean(manyPoints);
    Pt[] asArray = manyPoints.toArray(new Pt[0]);
    double varianceX = 0;
    double varianceY = 0;
    double varianceXY = 0;
    for (int i = 0; i < asArray.length; i++) {
      double diffX = asArray[i].x - mean.x;
      double diffY = asArray[i].y - mean.y;
      varianceX = varianceX + (diffX * diffX); // diffX squared
      varianceY = varianceY + (diffY * diffY); // diffY squared
      varianceXY = varianceXY + (diffX * diffY); // diff X times diff Y
    }
    Vec slopeVec;
    if (varianceX > varianceY) {
      double slope = varianceXY / varianceX;
      slopeVec = new Vec(1, slope).getUnitVector();
    } else {
      double slope = varianceXY / varianceY;
      slopeVec = new Vec(slope, 1).getUnitVector();
    }
    
    Line ret = new Line(mean, slopeVec);
    return ret;
  }

  public Line getTargetLineAntipodeStyle() {
    Vec dir = new Vec(antipodeA, antipodeB);
    Pt c = Functions.getMean(manyPoints);
    return new Line(c, dir);
  }

  private void maybeMove(int pins, Pt move, Line target, double heat) {
    if (!isPinned(move)) {
      Pt near = Functions.getNearestPointOnLine(move, target);
      double shift = near.distance(move) / (manyPoints.size() - pins);
      Vec delta = new Vec(move, near).getVectorOfMagnitude(shift);
      accumulate(move, delta, heat);
    }
  }

  @Override
  public double measureError() {
    double sum = 0;
    Line line = getTargetLine();
    for (Pt pt : manyPoints) {
      sum = sum + Functions.getDistanceBetweenPointAndLine(pt, line);
    }
    return sum;
  }

  @Override
  public void draw(DrawingBuffer buf) {
    Color col = (abs(measureError()) > TOLERANCE) ? Color.RED : Color.GREEN;
    DrawingBufferRoutines.line(buf, new Line(antipodeA, antipodeB), col, 1.0);
  }

  public static Manipulator getManipulator() {
    Manipulator man = new Manipulator(PointOnLineConstraint.class, "Point on Line", //
        new Manipulator.Param("p1", "Point 1 (Line)", true), new Manipulator.Param("p2",
            "Point 2 (Line)", true), new Manipulator.Param("p3", "Point 3 (Target)", true));
    return man;
  }

  @Override
  public void assume(Manipulator man, VariableBank vars) {
    //    if (man.ptOrConstraint != getClass()) {
    //      bug("Can't build " + getClass().getName() + " based on manipulator for " + man.label
    //          + "(its ptOrConstraint is " + man.ptOrConstraint.getName() + ")");
    //    } else {
    //      bug("Yay I can build a point-on-line thing from this manipulator");
    //    }
    //    Map<String, String> paramVals = man.getParamsAsMap();
    //    bug(num(paramVals.values(), " "));
    //    a = vars.getPointWithName(paramVals.get("p1"));
    //    b = vars.getPointWithName(paramVals.get("p2"));
    //    m = vars.getPointWithName(paramVals.get("p3"));
  }

  /**
   * Create a manipulator that holds the values of this constraint.
   */
  public Manipulator getManipulator(VariableBank vars) {
    Manipulator man = PointOnLineConstraint.getManipulator();
    //    man.setParamValue("p1", a.getString("name"));
    //    man.setParamValue("p2", b.getString("name"));
    //    man.setParamValue("p3", m.getString("name"));
    //    man.newThing = false;
    //    man.constraint = this;
    return man;
  }

  public String getHumanDescriptionString() {
    return "PointAsLineParam " + name(antipodeA) + ", " + name(antipodeB) + ", "
        + manyPoints.size() + " others";
  }

  public JSONObject toJson() throws JSONException {
    JSONObject ret = new JSONObject();
    JSONArray arr = new JSONArray();
    for (Pt pt : manyPoints) {
      arr.put(pt.getString("name"));
    }
    ret.put("points", arr);
    return ret;
  }

  public void fromJson(JSONObject obj, VariableBank vars) throws JSONException {
    JSONArray arr = obj.getJSONArray("points");
    manyPoints = new HashSet<Pt>();
    for (int i = 0; i < arr.length(); i++) {
      String name = arr.getString(i);
      Pt pt = vars.getPointWithName(name);
      manyPoints.add(pt);
    }
    makeAntipodes();
  }

  @Override
  public boolean involves(Pt who) {
    return manyPoints.contains(who);
  }

  @Override
  public void replace(Pt oldPt, Pt newPt) {
    manyPoints.remove(oldPt);
    manyPoints.add(newPt);
    makeAntipodes();
  }

  public Pt[] getRelatedPoints() {
    return manyPoints.toArray(new Pt[0]);
  }

  private void makeAntipodes() {
    Pt[] points = getRelatedPoints();
    Pt bestA = null;
    Pt bestB = null;
    double bestDist = 0;
    for (int i = 0; i < points.length; i++) {
      Pt a = points[i];
      for (int j = i + 1; j < points.length; j++) {
        Pt b = points[j];
        double thisDist = a.distance(b);
        if (thisDist > bestDist) {
          bestDist = thisDist;
          bestA = a;
          bestB = b;
        }
      }
    }
    antipodeA = bestA;
    antipodeB = bestB;
  }

  public void addPoints(Pt... pts) {
    for (Pt pt : pts) {
      manyPoints.add(pt);
    }
    makeAntipodes();
  }

  public void remove(Pt pt) {
    manyPoints.remove(pt);
    makeAntipodes();
  }
}
