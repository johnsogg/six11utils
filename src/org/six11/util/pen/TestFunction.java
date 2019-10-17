// $Id: TestFunction.java 37 2010-01-18 16:55:13Z gabe.johnson@gmail.com $

package org.six11.util.pen;

import junit.framework.TestCase;

import org.six11.util.Debug;

/**
 * 
 **/
public class TestFunction extends TestCase {
  
  public void assertTolerance(double actualValue, double expectedValue, double tolerance) {
    double err = Math.abs(actualValue - expectedValue);
    if (err > tolerance) {
      Debug.stacktrace("TestFunction: tolerance failed (" + actualValue + 
		       " != " + expectedValue + " (tol: " + tolerance + ")", 3);
    }
  }

  public void testRotatePointAboutPivot() {
    Pt point = new Pt(10d, 8d);
    Pt pivot = new Pt(8d, 8d);
    Pt rotated = Functions.rotatePointAboutPivot(point, pivot, Math.toRadians(90d));
    assertTolerance(rotated.getX(), 8.0, 0.001);
    assertTolerance(rotated.getY(), 10.0, 0.001);
  }
  
  public void testGetAngleBetweenVectors() {
    Pt o = new Pt(0, 0);
    Pt pt1 = new Pt(1, 1);
    Pt pt2 = new Pt(2, 1);
    Pt pt3 = new Pt(3, 3);
    Pt pt4 = new Pt(4, 5);
    Vec v1 = new Vec(pt1.x - o.x, pt2.y - o.y);
    Vec v2 = new Vec(pt2.x - pt1.x, pt2.y - pt1.y);
    Vec v3 = new Vec(pt3.x - pt2.x, pt3.y - pt2.y);
    Vec v4 = new Vec(pt4.x - pt3.x, pt4.y - pt3.y);
    
    double a1 = Functions.getAngleBetween(v1, v2);
    assertTrue(a1 < 0.0);
    double a2 = Functions.getAngleBetween(v2, v3);
    assertTrue(a2 > 0.0);
    double a3 = Functions.getAngleBetween(v3, v4);
    assertEquals(0.0, a3, 0.001);
    // make sure opposite of v1, v2 has opposite value.
    double a4 = Functions.getAngleBetween(v2, v1);
    assertEquals(-1.0 * a1, a4, 0.001);
  }
}
