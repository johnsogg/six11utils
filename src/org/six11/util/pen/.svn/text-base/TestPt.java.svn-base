// $Id$

package org.six11.util.pen;

import junit.framework.TestCase;
import java.awt.Color;

/**
 * 
 **/
public class TestPt extends TestCase {

  protected void setUp() {

  }

  public void testCopy() {
    Pt a = new Pt(4.5, 7.4, 12321L);
    a.setDouble("curve", 0.042);
    Pt b = a.copy();
    assertNotSame(a, b);
    assertEquals(a, b);
    assertEquals(0.042, b.getDouble("curve"));
  }

  public void testCompare() {
    Pt a = new Pt(4, 5, 10L);
    Pt b = new Pt(4, 5, 20L);
    Pt c = new Pt(5, 4, 20L);
    assertTrue(a.compareTo(b) < 0);
    assertTrue(b.compareTo(a) > 0);
    assertTrue(b.compareTo(c) == 0);
    assertTrue(b.compareTo(b) == 0);
  }

  public void testEquals() {
    Pt a = new Pt(4, 5, 1000L);
    Pt b = new Pt(4, 5, 1000L);
    Pt c = new Pt(4, 5, 2000L);
    Pt d = new Pt(5, 4, 2000L);
    assertNotSame(a, b);
    assertEquals(a, b);
    assertFalse(a.equals(c));
    assertFalse(c.equals(d));
  }

  public void testTime() {
    Pt a = new Pt(4, 5, 10L);
    Pt b = new Pt(4, 5, 20L);
    assertEquals(10L, a.getTime());
    assertFalse(a.getTime() == b.getTime());
    a.setTime(20L);
    assertEquals(a.getTime(), b.getTime());
  }

  public void testAttribs() {
    // plain objects
    Color blue = Color.BLUE;
    Pt a = new Pt(4, 5, 10L);
    assertNull(a.getAttribute("color"));
    a.setAttribute("color", blue);
    assertEquals(blue, a.getAttribute("color"));
    try {
      a.getDouble("curvature"); // not there yet
    } catch (Exception expected) {
      // ignore
    }
    a.setDouble("curvature", 0.04);
    double d = a.getDouble("curvature");
    assertEquals(0.04, d);
    a.setString("name", "value");
    assertEquals("value", a.getString("name"));
    assertFalse("incorrect".equals(a.getString("name")));
    a.setString("name", "another value");
    assertEquals("another value", a.getString("name"));
  }

}
