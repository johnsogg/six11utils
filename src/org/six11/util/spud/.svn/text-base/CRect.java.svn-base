package org.six11.util.spud;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.six11.util.pen.Functions;
import org.six11.util.pen.IntersectionData;
import org.six11.util.pen.Line;
import org.six11.util.pen.Pt;

/**
 * This is a rectangle, useful for intersecting with things, especially to find out what is onscreen
 * or not. E.g. you can intersect a CRect representing the visible area with a CLine to find out
 * which points (if any) specify the points you should draw.
 * 
 * @author johnsogg
 * 
 */
public class CRect extends Geom {

  public CRect(Rectangle2D rect) {
    super();
    addSlot("Rect");
    offer(rect);
  }

  public void offer(Rectangle2D rect) {
    slots.get("Rect").setValue(rect);
    solved = true;
  }

  @Override
  public String getDebugString() {
    return getName() + " rectangle";
  }

  @Override
  public boolean isDiscrete() {
    // TODO Auto-generated method stub
    warn(getClass().getSimpleName() + ".isDiscrete not yet implemented.");
    return false;
  }

  @Override
  public Geom intersectCircle(CCircle circ) {
    // TODO Auto-generated method stub
    warn(getClass().getSimpleName() + ".intersectCircle not yet implemented.");
    return null;
  }

  @Override
  public Geom intersectPoint(CPoint pt) {
    // TODO Auto-generated method stub
    warn(getClass().getSimpleName() + ".intersectPoint not yet implemented.");
    return null;
  }

  @Override
  public Geom intersectPointSet(CPointSet ptset) {
    // TODO Auto-generated method stub
    warn(getClass().getSimpleName() + ".intersectPointSet not yet implemented.");
    return null;
  }

  @Override
  public Geom intersectLine(CLine line) {
    // formulate the rectangle as four individual line segments. then intersect 'line' with each. 
    // if the intersection parameter for the segments is in [0..1] then line crosses the rectangle
    // at the associated point.
    List<Pt> intersections = new ArrayList<Pt>();
    Line in = line.getLine();
    for (Line seg : getLineSegments()) {
      IntersectionData ix = Functions.getIntersectionData(in, seg);
      if (ix.intersectsOnLineTwo()) {
        intersections.add(ix.getIntersection());
      }
    }
    Geom ret = new Nothing();
    if (intersections.size() == 1) {
      ret = new CPoint(intersections.get(0));
    } else if (intersections.size() > 1) {
      // it is possible that the same point could have been found more than one time if the
      // intersection happened to be at the corner. This code doesn't check for it yet.
      ret = new CPointSet();
      ret.offer(intersections.toArray(new Pt[0]));
    }
    return ret;
  }

  public Collection<Line> getLineSegments() {
    Set<Line> segs = new HashSet<Line>();
    if (solved) {
      Rectangle2D rect = getRect();
      Pt cornerA = new Pt(rect.getMinX(), rect.getMinY());
      Pt cornerB = new Pt(rect.getMinX(), rect.getMaxY());
      Pt cornerC = new Pt(rect.getMaxX(), rect.getMaxY());
      Pt cornerD = new Pt(rect.getMaxX(), rect.getMinY());
      segs.add(new Line(cornerA, cornerB));
      segs.add(new Line(cornerB, cornerC));
      segs.add(new Line(cornerC, cornerD));
      segs.add(new Line(cornerD, cornerA));
    }
    return segs;
  }

  public Rectangle2D getRect() {
    return (Rectangle2D) slots.get("Rect").value;
  }

  @Override
  public String getHumanReadableName() {
    return "rectangle " + getName();
  }

  public Type getType() {
    return Type.Rectangle;
  }

}
