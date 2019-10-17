package org.six11.util.pen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.six11.util.mesh.HalfEdge;
import org.six11.util.mesh.Mesh;
import org.six11.util.mesh.Triangle;
import org.six11.util.Debug;
import org.six11.util.gui.shape.Circle;
import org.six11.util.pen.CircleArc;
import org.six11.util.pen.DrawingBuffer;
import org.six11.util.pen.Functions;
import org.six11.util.pen.Line;
import org.six11.util.pen.Pt;
import org.six11.util.pen.Sequence;
import org.six11.util.pen.Vec;
import static org.six11.util.Debug.num;
import static org.six11.util.Debug.bug;

/**
 * A collection of drawing routines for DrawingBuffer instances.
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public abstract class DrawingBufferRoutines {

  public static Font defaultFont = new Font("sansserif", Font.PLAIN, 11);

  public static void rect(DrawingBuffer db, Pt where, double sx, double sy, Color borderColor,
      Color fillColor, double borderThickness) {
    db.up();
    if (borderColor != null) {
      db.setColor(borderColor);
    }
    if (fillColor != null) {
      db.setFillColor(fillColor);
      db.setFilling(true);
    } else {
      db.setFilling(false);
    }
    if (borderThickness > 0.0) {
      db.setThickness(borderThickness);
    }
    db.moveTo(where.x, where.y);
    db.forward(sy / 2.0);
    db.turn(90);
    db.down();

    db.forward(sx / 2.0);
    db.turn(90);
    db.forward(sy);
    db.turn(90);
    db.forward(sx);
    db.turn(90);
    db.forward(sy);
    db.turn(90);
    db.forward(sx / 2.0);
    db.up();
    if (fillColor != null) {
      db.setFilling(false);
    }
  }

  public static void spline(DrawingBuffer db, List<Pt> ctrl, Color lineColor, double lineThickness,
      int numSteps) {
    Sequence spline = new Sequence();
    int last = ctrl.size() - 1;
    for (int i = 0; i < last; i++) {
      Functions.getSplinePatch(ctrl.get(Math.max(0, i - 1)), ctrl.get(i), ctrl.get(i + 1),
          ctrl.get(Math.min(last, i + 2)), spline, numSteps);
    }
    db.up();
    if (lineColor != null) {
      db.setColor(lineColor);
    }
    if (lineThickness > 0.0) {
      db.setThickness(lineThickness);
    }
    boolean first = true;
    for (Pt pt : spline) {
      db.moveTo(pt.getX(), pt.getY());
      if (first) {
        db.down();
        first = false;
      }
    }
    db.up();
  }

  public static void line(DrawingBuffer db, Pt start, Pt end, Color color, double thick) {
    db.up();
    db.setColor(color);
    db.setThickness(thick);
    db.moveTo(start.x, start.y);
    db.down();
    db.moveTo(end.x, end.y);
    db.up();
  }

  public static void line(DrawingBuffer db, Pt start, Pt end, Color color) {
    line(db, start, end, color, 1.0);
  }

  public static void lines(DrawingBuffer db, List<Pt> points, Color color, double thick) {
    if (points.size() > 0) {
      db.up();
      db.setColor(color);
      db.setThickness(thick);
      db.moveTo(points.get(0).x, points.get(0).y);
      db.down();
      for (Pt pt : points) {
        db.moveTo(pt.x, pt.y);
      }
      db.up();
    }
  }

  public static void arc(DrawingBuffer db, CircleArc arc, Color color) {
    db.up();
    Pt s = arc.start;
    Pt m = arc.mid;
    Pt e = arc.end;
    db.setColor(color);
    db.down();
    db.circleTo(s.x, s.y, m.x, m.y, e.x, e.y);
    db.up();
  }

  public static void seg(DrawingBuffer db, Segment seg, Color color) {
    db.up();
    db.setColor(color);
    db.setThickness(1.0);
    if (seg.getBestType() == Segment.Type.LINE) {
      db.moveTo(seg.start.x, seg.start.y);
      db.down();
      db.moveTo(seg.end.x, seg.end.y);
    } else if (seg.getBestType() == Segment.Type.ARC) {
      CircleArc arc = seg.bestCircle;
      Pt s = seg.start;
      Pt m = arc.mid;
      Pt e = seg.end;
      db.down();
      db.circleTo(s.x, s.y, m.x, m.y, e.x, e.y);
    } else if (seg.getBestType() == Segment.Type.SPLINE) {
      boolean is_up = true;
      for (Pt pt : seg.splinePoints) {
        db.moveTo(pt.x, pt.y);
        if (is_up) {
          db.down();
          is_up = false;
        }
      }
    }
    db.up();
    bug(seg.toString());
  }

  public static void drawShape(DrawingBuffer db, List<Pt> corners, Color color, double thickness) {
    if (db.isVisible() && corners.size() > 3) {
      GeneralPath gp = makePath(corners);
      drawShape(db, gp, color, thickness);
    }
  }

  public static void drawShape(DrawingBuffer db, Shape shape, Color color, double thickness) {
    db.up();
    db.setColor(color);
    db.setThickness(thickness);
    db.down();
    db.addShape(shape);
    db.up();
  }

  public static void fillShape(DrawingBuffer db, List<Pt> corners, Color fillColor,
      double borderThickness) {
    if (db.isVisible() && corners.size() > 3) {
      GeneralPath gp = makePath(corners);
      fillShape(db, gp, fillColor, borderThickness);
    }
  }

  public static void fillShape(DrawingBuffer db, Shape shape, Color fillColor,
      double borderThickness) {
    db.up();
    db.setFillColor(fillColor);
    db.setFilling(true);
    db.setColor(fillColor);
    db.setThickness(borderThickness);
    db.down();
    db.addShape(shape);
    db.up();
    db.setFilling(false);
  }

  public static GeneralPath makePath(List<Pt> corners) {
    GeneralPath gp = new GeneralPath(GeneralPath.WIND_NON_ZERO, corners.size());
    gp.moveTo(corners.get(0).getX(), corners.get(1).getY());
    for (int i = 1; i < corners.size(); i++) {
      gp.lineTo(corners.get(i).getX(), corners.get(i).getY());
    }
    return gp;
  }

  public static void dot(DrawingBuffer db, Pt center, double radius, double thickness,
      Color borderColor, Color fillColor) {
    db.up();
    if (fillColor != null) {
      db.setFillColor(fillColor);
      db.setFilling(true);
    }
    db.setColor(borderColor);
    db.setThickness(thickness);
    Circle circle = new Circle(center.x, center.y, radius);
    db.down();
    db.addShape(circle);
    db.up();
    if (fillColor != null) {
      db.setFilling(false);
    }
  }

  public static void dots(DrawingBuffer db, List<Pt> points, double radius, double thickness,
      Color borderColor, Color fillColor) {
    for (Pt pt : points) {
      dot(db, pt, radius, thickness, borderColor, fillColor);
    }
  }

  public static void fill(DrawingBuffer db, Collection<Pt> points, double thick, Color border,
      Color fill) {
    db.setFillColor(fill);
    db.setFilling(true);
    db.setColor(border);
    db.setThickness(thick);
    boolean first = true;
    Pt last = null;
    for (Pt pt : points) {
      last = pt;
      db.moveTo(pt.x, pt.y);
      if (first) {
        first = false;
        db.down();
      }
    }
    if (last != null) {
      db.moveTo(last.x, last.y);
    }
    db.up();
    db.setFilling(false);
  }

  public static void line(DrawingBuffer db, Line l, Color color, double thick) {
    line(db, l.getStart(), l.getEnd(), color, thick);
  }

  public static void text(DrawingBuffer db, Pt location, String msg, Color color) {
    text(db, location, msg, color, defaultFont);
  }

  public static void text(DrawingBuffer db, Pt location, String msg, Color color, Font font) {
    db.up();
    db.moveTo(location.x, location.y);
    db.down();
    db.addText(msg, color, font);
    db.up();
  }

  public static void patch(DrawingBuffer db, Sequence seq, int startIdx, int endIdx,
      double thickness, Color color) {
    db.setColor(color);
    db.setThickness(thickness);
    boolean first = true;
    for (int i = startIdx; i <= endIdx; i++) {
      Pt pt = seq.get(i);
      db.moveTo(pt.x, pt.y);
      if (first) {
        db.down();
        first = false;
      }
    }
    db.up();
  }

  public static void arrow(DrawingBuffer db, Pt start, Pt tip, double thick, Color color) {
    double length = start.distance(tip);
    double headLength = length / 10.0;
    Vec tipToStart = new Vec(tip, start).getVectorOfMagnitude(headLength);
    Pt cross = tip.getTranslated(tipToStart.getX(), tipToStart.getY());
    Vec outward = tipToStart.getNormal();
    Pt head1 = cross.getTranslated(outward.getX(), outward.getY());
    outward = outward.getFlip();
    Pt head2 = cross.getTranslated(outward.getX(), outward.getY());
    line(db, new Line(start, tip), color, thick);
    line(db, new Line(head1, tip), color, thick);
    line(db, new Line(head2, tip), color, thick);
  }

  public static void flowSelectEffect(DrawingBuffer db, Sequence seq, double thick) {
    if (seq.getPoints().size() > 0) {
      // each sequence might have several 'stretches' of selected areas. keep track of them.
      List<List<Pt>> stretches = new ArrayList<List<Pt>>();
      Pt prev = null;
      for (int i = 0; i < seq.getPoints().size() - 1; i++) {
        Pt pt = seq.get(i);
        double a = pt.getDouble("fs strength", 0);
        double b = seq.get(i + 1).getDouble("fs strength", 0);
        double c = (a + b) / 2;
        if (c > 0) {
          Color color = Functions.eq(c, 1, 0.05) ? Color.green // nearly full str = Green
              : new Color(1f, 1 - (float) c, 1 - (float) c, (float) c); // partial str = Red+Alpha
          line(db, pt, seq.get(i + 1), color, thick);
        }
        if (pt.getBoolean("hinge", false)) {
          dot(db, pt, 7.0, 0.7, Color.BLACK, Color.GREEN);
        } else if (pt.getBoolean("corner", false) && a == 1) {
          dot(db, pt, 7.0, 0.7, Color.BLACK, Color.RED);
        } else if (pt.getBoolean("corner", false) && a > 0) {
          dot(db, pt, 3.0, 0.3, Color.BLACK, Color.BLUE);
        }
        // now attend to the stretches of selected points
        if (prev != null && prev.getDouble("fs strength", 0) == 0
            && pt.getDouble("fs strength", 0) > 0) {
          // start a new selected area
          List<Pt> stretch = new ArrayList<Pt>();
          stretch.add(pt);
          stretches.add(stretch);
        } else if (pt.getDouble("fs strength", 0) > 0) {
          if (prev == null) {
            List<Pt> stretch = new ArrayList<Pt>();
            stretches.add(stretch);
          }
          stretches.get(stretches.size() - 1).add(pt);
        }
        prev = pt;
      }

      Color strColor = new Color(255, 0, 0, 64);
      for (List<Pt> stretch : stretches) {
        lines(db, stretch, strColor, 2 * 40.0 /* FlowSelection.OVERDRAW_NEARNESS_THRESHOLD */);
      }
    }
  }

  public static void cross(DrawingBuffer db, Pt spot, double lineLength, Color color, double thick) {
    Pt p1 = spot.getTranslated(-lineLength, -lineLength);
    Pt p2 = spot.getTranslated(-lineLength, lineLength);
    Pt p3 = spot.getTranslated(lineLength, lineLength);
    Pt p4 = spot.getTranslated(lineLength, -lineLength);
    line(db, p1, p3, color, thick);
    line(db, p2, p4, color, thick);
  }

  public static void cross(DrawingBuffer db, Pt spot, double lineLength, Color color) {
    cross(db, spot, lineLength, color, 2.0);
  }

  public static void screenLine(DrawingBuffer db, Rectangle bounds, Line geometryLine, Color color,
      double thick) {
    // draw a line that spans the entire rectangle
    if (bounds.intersectsLine(geometryLine)) {
      // find the two intersection points and connect them.
      Pt[] ix = Functions.getIntersectionPoints(bounds, geometryLine);
      if (ix[0] != null && ix[1] != null) {
        line(db, ix[0], ix[1], color, thick);
      }
    }
  }

  public static void mesh(DrawingBuffer db, Mesh mesh) {
    // List<Pt> points = mesh.getPoints();
    // dots(db, points, 2.0, 0.6, Color.LIGHT_GRAY, Color.LIGHT_GRAY);
    Color cCentInside = new Color(0, 0, 255, 50);
    // Color cCentLegit = new Color(255, 0, 0, 50);
    Set<Triangle> inside = mesh.getInsideTriangles();
    triangles(db, inside, cCentInside);
  }

  private static void triangle(DrawingBuffer db, Triangle t, Color fillColor) {
    HalfEdge start = t.getEdge();
    HalfEdge cursor = start;
    List<Pt> drawUs = new ArrayList<Pt>();
    do {
      Pt a = cursor.getPoint();
      drawUs.add(a);
      cursor = cursor.getNext();
    } while (cursor != start);
    drawUs.add(drawUs.get(0));
    fill(db, drawUs, 1.0, fillColor, fillColor);
  }

  public static void triangles(DrawingBuffer db, Set<Triangle> manyTriangles, Color color) {
    for (Triangle t : manyTriangles) {
      // if (t.getArea() > 10000) {
      // bug("This triangle is large: " + t + Debug.num(t.getArea()));
      // } else {
      triangle(db, t, color);
      // }
    }
  }

  /**
   * Given a sequence that represents a pen stroke, make a drawing buffer that has a colored stroke
   * of some thickness. This only makes the buffer---it does not add it to any data structures or
   * map it in any way.
   */
  public static DrawingBuffer makeSequenceBuffer(Sequence s) {
    DrawingBuffer buf = new DrawingBuffer();
    if (s.getAttribute("pen color") != null) {
      buf.setColor((Color) s.getAttribute("pen color"));
    } else {
      buf.setColor(DrawingBuffer.getBasicPen().color);
    }
    if (s.getAttribute("pen thickness") != null) {
      buf.setThickness((Double) s.getAttribute("pen thickness"));
    } else {
      buf.setThickness(DrawingBuffer.getBasicPen().thickness);
    }
    buf.up();
    buf.moveTo(s.get(0).x, s.get(0).y);
    buf.down();
    for (Pt pt : s) {
      buf.moveTo(pt.x, pt.y);
    }
    buf.up();
    return buf;
  }

  public static void meshBoundary(DrawingBuffer db, Mesh mesh, Color color, double thick) {
    Set<Triangle> triangles = mesh.getTriangles();
    List<Line> boundary = new ArrayList<Line>(); // NOT IN ORDER
    for (Triangle t : triangles) {
      bug("triangle location: " + t.getMeshLocation());
      HalfEdge he = t.getEdge();
      if (he.isBoundary() && he.getPoint().getID() < he.getPair().getPoint().getID()) {
        boundary.add(new Line(he.getPoint(), he.getPair().getPoint()));
      }
      he = he.getNext();
      if (he.isBoundary() && he.getPoint().getID() < he.getPair().getPoint().getID()) {
        boundary.add(new Line(he.getPoint(), he.getPair().getPoint()));
      }
      he = he.getNext();
      if (he.isBoundary() && he.getPoint().getID() < he.getPair().getPoint().getID()) {
        boundary.add(new Line(he.getPoint(), he.getPair().getPoint()));
      }
    }
    bug("There are " + boundary.size() + " boundary lines for this mesh.");
    for (Line line : boundary) {
      bug("mesh boundary from " + num(line));
      DrawingBufferRoutines.line(db, line, color, thick);
    }
  }

  public static void meshFiniteEdges(DrawingBuffer db, Mesh mesh, Color color, double thick) {
    Set<Triangle> triangles = mesh.getFiniteTriangles();
    List<Line> lines = new ArrayList<Line>();
    for (Triangle t : triangles) {
      HalfEdge he = t.getEdge();
      try {
        lines.add(new Line(he.getPoint(), he.getPair().getPoint()));
        he = he.getNext();
        lines.add(new Line(he.getPoint(), he.getPair().getPoint()));
        he = he.getNext();
        lines.add(new Line(he.getPoint(), he.getPair().getPoint()));
      } catch (NullPointerException ex) {
        bug("One of the edges has a null pair.");
      }
    }
    for (Line line : lines) {
      DrawingBufferRoutines.line(db, line, color, thick);
    }
  }

  public static void meshAllEdges(DrawingBuffer db, Mesh mesh, Color color, double thick) {
    Set<Triangle> triangles = mesh.getTriangles();
    List<Line> lines = new ArrayList<Line>();
    for (Triangle t : triangles) {
      HalfEdge he = t.getEdge();

      lines.add(new Line(he.getPoint(), he.getStartPoint()));
      he = he.getNext();
      lines.add(new Line(he.getPoint(), he.getStartPoint()));
      he = he.getNext();
      lines.add(new Line(he.getPoint(), he.getStartPoint()));
    }
    for (Line line : lines) {
      DrawingBufferRoutines.line(db, line, color, thick);
    }
  }

  public static void meshDebug(DrawingBuffer db, Mesh mesh) {
    DrawingBufferRoutines.meshBoundary(db, mesh, Color.RED, 2.0);
    DrawingBufferRoutines.meshFiniteEdges(db, mesh, Color.GRAY, 1.0);
    // DrawingBufferRoutines.meshAllEdges(db, mesh, Color.GRAY, 1.0);
    DrawingBufferRoutines.dots(db, mesh.getPoints(), 2.0, 0.2, Color.BLACK, Color.GREEN);
    DrawingBufferRoutines.mesh(db, mesh);
    DrawingBufferRoutines.meshVertexIDs(db, mesh);
    List<Pt> all = mesh.getPoints();
    if (all.size() > 0) {
      DrawingBufferRoutines.dot(db, all.get(all.size() - 1), 2.5, 0.3, Color.GREEN, Color.GREEN);
    }
    if (all.size() > 1) {
      DrawingBufferRoutines.dot(db, all.get(all.size() - 2), 2.5, 0.3, Color.BLUE, Color.BLUE);
    }
  }

  private static void meshVertexIDs(DrawingBuffer db, Mesh mesh) {
    Set<Triangle> triangles = mesh.getFiniteTriangles();
    Set<Pt> points = new HashSet<Pt>();
    StringBuilder tris = new StringBuilder();
    for (Triangle t : triangles) {
      tris.append("  Triangle " + t.id + ": [ ");
      for (Pt pt : t.getPoints()) {
        tris.append(pt.getID() + " ");
        points.add(pt);
      }
      tris.append("]\n");
    }
    Set<Triangle> allTriangles = mesh.getTriangles();
    for (Triangle t : allTriangles) {
      if (!triangles.contains(t)) {
        tris.append("  Infinite Triangle " + t.id + ": " + t.getVertIds() + "\n");
      }
    }
    System.out.println("-------------------------------------------------------------------------");
    System.out.println("Triangles:\n" + tris.toString());
    System.out.println("There are " + points.size() + " 'finite' points in this mesh:");
    for (Pt pt : points) {
      System.out.println("   Pt " + pt.getID() + " at " + Debug.num(pt));
      DrawingBufferRoutines.text(db, pt.getTranslated(4, 0), "" + pt.getID(), Color.CYAN);
    }
    System.out.println("'Infinite' root points for this mesh:");
    for (Pt rp : mesh.getRootPoints()) {
      System.out.println("   Pt " + rp.getID() + " at " + Debug.num(rp));
    }
    System.out.println("-------------------------------------------------------------------------");
  }

  public static void acuteHash(DrawingBuffer buf, Pt mid, Vec refDir, double length,
      double thickness, Color color) {
    Pt tip = mid.getTranslated(refDir, length / 2);
    AffineTransform xform = Functions.getRotationInstance(mid, Math.toRadians(45));
    Pt tip2 = new Pt();
    xform.transform(tip, tip2);
    Vec toOtherTip = new Vec(mid, tip2).getFlip();
    Pt otherTip = mid.getTranslated(toOtherTip.getX(), toOtherTip.getY());
    line(buf, tip2, otherTip, color, thickness);
    //    double length = start.distance(tip);
    //    double headLength = length / 10.0;
    //    Vec tipToStart = new Vec(tip, start).getVectorOfMagnitude(headLength);
    //    Pt cross = tip.getTranslated(tipToStart.getX(), tipToStart.getY());
    //    Vec outward = tipToStart.getNormal();
    //    Pt head1 = cross.getTranslated(outward.getX(), outward.getY());
    //    outward = outward.getFlip();
    //    Pt head2 = cross.getTranslated(outward.getX(), outward.getY());
    //    line(db, new Line(start, tip), color, thick);
    //    line(db, new Line(head1, tip), color, thick);
    //    line(db, new Line(head2, tip), color, thick);

  }

}
