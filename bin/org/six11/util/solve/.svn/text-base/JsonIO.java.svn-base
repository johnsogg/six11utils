package org.six11.util.solve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.six11.util.pen.Pt;
import static org.six11.util.Debug.bug;

import org.json.*;

public class JsonIO {

  public JSONObject write(Pt pt, String... persistedData) throws JSONException {
    JSONObject ret = new JSONObject();
    ret.put("x", pt.getX());
    ret.put("y", pt.getY());
    ret.put("t", pt.getTime());
    Map<String, Object> persist = new HashMap<String, Object>();
    for (String key : persistedData) {
      if (pt.hasAttribute(key)) {
        persist.put(key, pt.getAttribute(key));
      }
    }
    ret.put("data", persist);
    return ret;
  }

  public JSONArray write(List<Pt> points, String... dataNames) throws JSONException {
    JSONArray ret = new JSONArray();
    for (Pt pt : points) {
      ret.put(write(pt, dataNames));
    }
    return ret;
  }

  public Pt readPt(JSONObject obj, String... dataNames) throws JSONException {
    double xLoc = (Double) obj.getDouble("x");
    double yLoc = (Double) obj.getDouble("y");
    long time = (Long) obj.getLong("t");
    Pt pt = new Pt(xLoc, yLoc, time);
    JSONObject data = (JSONObject) obj.get("data");
    for (String key : dataNames) {
      if (data.has(key)) {
        pt.setAttribute(key, data.get(key));
      }
    }
    return pt;
  }

  public List<Pt> readPoints(JSONArray array, String... dataNames) throws JSONException {
    List<Pt> points = new ArrayList<Pt>();
    for (int i = 0; i < array.length(); i++) {
      points.add(readPt((JSONObject) array.get(i), dataNames));
    }
    return points;
  }

  public JSONArray write(List<Constraint> constraints) throws JSONException {
    JSONArray ret = new JSONArray();
    for (Constraint c : constraints) {
      JSONObject asJson = c.toJson();
      asJson.put("type", c.getType());
      asJson.put("id", c.getID());
      ret.put(asJson);
    }
    return ret;
  }
  
  public List<Constraint> readConstraints(JSONArray array, VariableBank vars) throws JSONException {
    List<Constraint> constraints = new ArrayList<Constraint>();
    for (int i=0; i < array.length(); i++) {
      JSONObject obj = array.getJSONObject(i);
      String type = obj.getString("type");
      Constraint c = null;
      if ("Angle".equals(type)) {
        c = new AngleConstraint(obj, vars);
      } else if ("Distance".equals(type)) {
        c = new DistanceConstraint(obj, vars);
      } else if ("Pin".equals(type)) {
        c = new LocationConstraint(obj, vars);
      } else if ("Orientation".equals(type)) {
        c = new OrientationConstraint(obj, vars);
      } else if ("Point As Line Param".equals(type)) {
        c = new PointAsLineParamConstraint(obj, vars);
      } else if ("Point On Line".equals(type)) {
        c = new PointOnLineConstraint(obj, vars);
      }
      if (c == null) {
        bug("** Unknown constraint type on load: " + type);
      } else {
        constraints.add(c);
      }
    }
    return constraints;
  }
}
