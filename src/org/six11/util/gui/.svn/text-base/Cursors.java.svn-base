package org.six11.util.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import org.six11.util.pen.Functions;
import org.six11.util.pen.Pt;
import org.six11.util.pen.Vec;
import static org.six11.util.Debug.bug;

public abstract class Cursors {

  public static Cursor createDotCursor(int size, String name, Color borderColor, Color fillColor) {
    int diameter = size;
    int c = size / 2;
    BufferedImage im = new BufferedImage(size + 4, size + 4, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = im.createGraphics();
    Ellipse2D circle = new Ellipse2D.Float(1, 1, diameter, diameter);
    Components.antialias(g);
    g.setColor(fillColor);
    g.fill(circle);
    g.setStroke(Strokes.THIN_STROKE);
    g.setColor(borderColor);
    g.draw(circle);
    Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(im, new Point(c, c), name);
    return cursor;
  }

  public static Cursor createArrowCursor(Vec vec, String name, Color black) {
    // find the various points involved with the arrow.
    // the shaft goes from 'start' to 'tip'.
    // point 'spot' is like 60% of the way along the shaft
    // points 'left' and 'right' are normal to the shaft, vec.mag()/8 from spot
    // point 'm' is the midpoint of the shaft. That's the cursor hotspot.

    Pt start = new Pt(0, 0);
    Pt tip = start.getTranslated(vec);
    Pt spot = start.getTranslated(vec.getScaled(0.6));
    Vec norm = vec.getNormal().getUnitVector().getVectorOfMagnitude(vec.mag() * 0.6);
    Pt left = spot.getTranslated(norm);
    Pt right = spot.getTranslated(norm.getFlip());
    Pt m = Functions.getMean(left, right);
    BoundingBox bb = new BoundingBox();
    bb.addAll(start, tip, left, right);
    int w = bb.getWidthInt();
    int h = bb.getHeightInt();
    double minX = bb.getMinX();
    double minY = bb.getMinY();
    // translate all points by -minX, -minY
    start = start.getTranslated(-minX, -minY);
    tip = tip.getTranslated(-minX, -minY);
    spot = spot.getTranslated(-minX, -minY);
    left = left.getTranslated(-minX, -minY);
    right = right.getTranslated(-minX, -minY);
    m = m.getTranslated(-minX, -minY);
    Path2D path = new Path2D.Float();
    path.moveTo(start.x, start.y);
    path.lineTo(tip.x, tip.y);
    path.moveTo(right.x, right.y);
    path.lineTo(tip.x, tip.y);
    path.lineTo(left.x, left.y);
    BufferedImage im = new BufferedImage(w + 2, h + 2, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = im.createGraphics();
    Components.antialias(g);
    g.setColor(Color.black);
    g.setStroke(Strokes.THIN_STROKE);
    g.draw(path);
    Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(im, new Point(m.ix(), m.iy()),
        name);
    return cursor;
  }

  public static Cursor createMagnifyingGlassCursor(int radius, int handleLength, String name,
      Color black, Color white) {
    Pt center = new Pt(0, 0);
    Pt circTopLeft = new Pt(-radius, -radius);
    Vec downLeft = new Vec(-1, 1).getVectorOfMagnitude(radius);
    Pt handleTouch = center.getTranslated(downLeft);
    Pt handleEnd = handleTouch.getTranslated(downLeft.getVectorOfMagnitude(radius * 1.5));
    Pt threeOclock = center.getTranslated(radius, 0);
    Pt sixOclock = center.getTranslated(0, radius);
    BoundingBox bb = new BoundingBox();
    bb.addAll(circTopLeft, center, handleTouch, handleEnd, threeOclock, sixOclock);
    double minX = bb.getMinX() - 1;
    double minY = bb.getMinY() - 1;
    // translate all by -minX, minY
    center = center.getTranslated(-minX, -minY);
    handleTouch = handleTouch.getTranslated(-minX, -minY);
    handleEnd = handleEnd.getTranslated(-minX, -minY);
    Ellipse2D circ = new Ellipse2D.Float((float) center.x - radius, (float) center.y - radius,
        radius * 2, radius * 2);
    Line2D handle = new Line2D.Float(handleTouch.fx(), handleTouch.fy(), handleEnd.fx(),
        handleEnd.fy());
    bug("image width will be: " + bb.getWidthInt() + " x " + bb.getHeightInt());
    BufferedImage im = new BufferedImage(bb.getWidthInt() + 2, bb.getHeightInt() + 2,
        BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = im.createGraphics();
    Components.antialias(g);
    g.setColor(Color.black);
    g.setStroke(Strokes.THIN_STROKE);
    g.draw(circ);
    g.draw(handle);
    Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(im,
        new Point(handleTouch.ix(), handleTouch.iy()), name);
    return cursor;
  }
}
