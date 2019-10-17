package org.six11.util.spud;

import org.six11.util.Debug;
import org.six11.util.pen.CircleArc;
import org.six11.util.pen.Line;
import org.six11.util.pen.Pt;
import org.six11.util.pen.Vec;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class Slot {

  protected String type;
  protected Object value;
  protected boolean valid;

  public Slot(String type) {
    this.type = type;
    this.valid = false;
  }

  public void setValue(Object value) {
    this.value = value;
    this.valid = true;
  }

  public boolean isValid() {
    return valid;
  }

  public Pt getPt() {
    return (Pt) value;
  }

  public Vec getDirection() {
    return (Vec) value;
  }
  
  public Line getLine() {
    return (Line) value;
  }

  public double getDouble() {
    return (Double) value;
  }

  public CircleArc getCircle() {
    return (CircleArc) value;
  }

  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append(type + " = ");
    if (valid) {
      if (value instanceof Pt) {
        buf.append(Debug.num((Pt) value));
      } else if (value instanceof Vec) {
        buf.append(Debug.num((Vec) value));
      } else {
        buf.append(value);
      }
    } else {
      buf.append("<invalid>");
    }
    return buf.toString();
  }


}
