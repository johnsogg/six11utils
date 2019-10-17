// $Id: Hausdorff.java 12 2009-11-09 22:58:47Z gabe.johnson $

package org.six11.util.pen;

import java.awt.geom.Rectangle2D;

import org.six11.util.Debug;

/**
 * Hausdorff spatial similarity function, taken from 
 * http://www.alexandria.ucsb.edu/~gjanee/archive/2003/similarity.html
 **/
public class Hausdorff {
  
  private static class Box {
    Pt min, max;
    Box(Pt min, Pt max) {
      this.min = min;
      this.max = max;
    }
  }

  static double getHausdorffDistance(Rectangle2D rectA, Rectangle2D rectB) {
    Pt min = new Pt(rectA.getX(), rectA.getY());
    Pt max = new Pt(rectA.getX() + rectA.getWidth(), 
		    rectA.getY() + rectA.getHeight());
    Box p = new Box (min, max);
    min = new Pt(rectB.getX(), rectB.getY());
    max = new Pt(rectB.getX() + rectB.getWidth(), 
		 rectB.getY() + rectB.getHeight());
    Box q = new Box (min, max);

    return Math.max(h(p, q), h(q, p));
  }

  static double h(Box p, Box q) {
    // I probably could do something with Collections.min() as well
    double a = dist(new Pt(p.min.getX(), p.min.getY()), q);
    double b = dist(new Pt(p.min.getX(), p.max.getY()), q);
    double c = dist(new Pt(p.max.getX(), p.min.getY()), q);
    double d = dist(new Pt(p.max.getX(), p.max.getY()), q);
    double e = Math.max(a,b);
    double f = Math.max(c,d);
    return Math.max(e,f);
  }

  static double dist(Pt p, Box Q) {
    Pt q = new Pt(Math.min(Math.max(p.getX(), Q.min.getX()), Q.max.getX()),
		  Math.min(Math.max(p.getY(), Q.min.getY()), Q.max.getY()));
    return Math.sqrt((p.getX() - q.getX()) * (p.getX() - q.getX()) +
		     (p.getY() - q.getY()) * (p.getY() - q.getY()));
  }

  /**
   * To get the Hausdorff distance between two rectangles, provide
   * eight numbers: four for each rectangle. For each rectangle, the
   * first two are the top left x,y coordinates, the next two points
   * are the bottom right x,y coordinates.
   */
  public static void main(String[] args) {
    double pX1 = Double.parseDouble(args[0]);
    double pY1 = Double.parseDouble(args[1]);
    double pX2 = Double.parseDouble(args[2]);
    double pY2 = Double.parseDouble(args[3]);

    double qX1 = Double.parseDouble(args[4]);
    double qY1 = Double.parseDouble(args[5]);
    double qX2 = Double.parseDouble(args[6]);
    double qY2 = Double.parseDouble(args[7]);

    Rectangle2D p = new Rectangle2D.Double(pX1, pY1, (pX2 - pX1), (pY2 - pY1));
    Rectangle2D q = new Rectangle2D.Double(qX1, qY1, (qX2 - qX1), (qY2 - qY1));
    
    double hdist = Functions.getHausdorffDistance(p, q);
    Debug.out("Hausdorff", "Hausorff distance for " + Debug.num(p) + 
	      " and " + Debug.num(q) + ": "   + Debug.num(hdist));
  }

}
