// $Id$

package org.six11.util.pen;

import junit.framework.TestCase;

import org.six11.util.Debug;

/**
 * 
 **/
public class TestSequence extends TestCase {

  static {
    Debug.useColor = false;
  }
  protected Sequence seq;

  protected void setUp() {

  }

  public void testAdd() {
    seq = makeSequence();
    assertTrue(seq.size() == 4);
  }

  public void testGet() {
    Sequence seq = makeSequence();
    Pt a = new Pt(7.0, 7.0);
    Pt b = new Pt(8.0, 8.0);
    seq.add(a);
    seq.add(b);
    Pt shouldBeA = seq.get(4);
    Pt shouldBeB = seq.get(5);
    assertEquals(a, shouldBeA);
    assertEquals(b, shouldBeB);
    try {
      seq.get(6);
      fail("Should not be able to get to index 6");
    } catch (IndexOutOfBoundsException ok) { /* expected */
    }
  }

  public void testIterate() {
    Sequence seq = new Sequence();
    // (0,0), (1,1), (2,4), (3,9), .., (99, 9801)
    for (int i = 0; i < 100; i++) {
      seq.add(f((double) i)); // x, x^2
    }
    for (Pt pt : seq) {
      assertEquals(pt.getX() * pt.getX(), pt.getY());
    }

    Sequence smallSequence = makeSequence();
    int forwardCount = 0;
    for (Pt pt : smallSequence.forward(0)) {
      assertEquals(smallSequence.get(forwardCount), pt);
      forwardCount++;
    }
    assertEquals(4, forwardCount);

    int where = 50;
    int count = 0;
    for (Pt pt : seq.forward(where)) {
      assertEquals(((double) where) * ((double) where), pt.getY());
      where++;
      count++;
    }
    assertEquals(50, count);

    where = 50;
    count = 0;
    for (Pt pt : seq.backward(where)) {
      assertEquals(((double) where) * ((double) where), pt.getY());
      where--;
      count++;
    }
    assertEquals(-1, where);

    int low = 20;
    int high = 60;
    count = 0;
    double shouldBe;
    for (Pt pt : seq.from(low, high)) {
      shouldBe = (double) (low + count);
      assertEquals(shouldBe * shouldBe, pt.getY());
      count++;
    }
    assertEquals(41, count);

    low = 30;
    high = 40;
    count = 0;
    for (Pt pt : seq.from(high, low)) { // note: backwards!
      shouldBe = (double) (high - count);
      assertEquals(shouldBe * shouldBe, pt.getY());
      count++;
    }
    assertEquals(11, count);
  }

  public void testIndexOf() {
    Sequence seq = makeSequence();
    Pt a = new Pt(7.0, 7.0);
    Pt b = new Pt(8.0, 8.0);
    seq.add(a);
    seq.add(b);
    int shouldBeFour = seq.indexOf(a);
    int shouldBeFive = seq.indexOf(b);
    assertEquals(4, shouldBeFour);
    assertEquals(5, shouldBeFive);
    int shouldBeNeg = seq.indexOf(new Pt(-1.0, -1.0));
    assertEquals(-1, shouldBeNeg);
    shouldBeNeg = seq.indexOf(null);
    assertEquals(-1, shouldBeNeg);
  }

  public void testNormalize() {
    Sequence xsquared = new Sequence();
    for (int i = 0; i < 10; i++)
      xsquared.add(f(i));
    assertEquals(10, xsquared.size());
    double dist = 1.0;
    Sequence normal = Functions.getNormalizedSequence(xsquared, dist);
    assertNotNull(normal);
    assertTrue(normal.size() > xsquared.size());
    Pt prev = null;
    for (Pt pt : normal) {
      if (prev != null && !pt.equals(normal.getLast())) {
        assertEquals(dist, pt.distance(prev), 0.001); // 3rd arg is tolerance
      }
      prev = pt;
    }
  }

  public Sequence testGetSpline() {
    Sequence base = new Sequence();
    for (int i = 1; i <= 100; i++) {
      base.add(f(i));
    }
    return Functions.getSpline(base, 4, 10);
  }

  public void testGetPointAtDistance() {
    Sequence seq = new Sequence();
    seq.add(new Pt(5, 0)); // a 0.0
    seq.add(new Pt(5, 1)); // b 1.0
    seq.add(new Pt(5, 2)); // c 2.0
    seq.add(new Pt(5, 3)); // d 3.0
    seq.add(new Pt(5, 4)); // e 4.0

    Pt target;
    // test forward, b --> (point between d and e)
    target = Functions.getPointAtDistance(seq, 1, 1.0, 1, 3.5);
    assertEquals(new Pt(5.0, 3.5), target);

    // test backward, e --> (point between b and a)
    target = Functions.getPointAtDistance(seq, 4, 3.5, -1, 7.0);
    assertEquals(new Pt(5.0, 0.5), target);

    // test forward b --> (point between b and c)
    // test backward, c --> (point between b and c)

    // test extrapolation, c --> (point beyond e)
    target = Functions.getPointAtDistance(seq, 2, 2.0, 1, 6.0);
    assertEquals(new Pt(5.0, 6.0), target);

    // test extrapolation, e --> (point beyond a)
    target = Functions.getPointAtDistance(seq, 4, 0.0, -1, 6.0);
    assertEquals(new Pt(5.0, -2.0), target);

    // test point on sequence a --> c
    // test point on sequence d --> a
  }

  public void testGetLast() {
    Sequence seq = makeSequence();
    assertEquals(seq.getLast(), new Pt(3.0, 1.8));
  }

  public void testCopy() {
    Sequence original = makeSequence();
    Sequence duplicate = original.copy();
    assertEquals(original.size(), duplicate.size());
    for (int i = 0; i < original.size(); i++) {
      assertEquals(original.get(i), duplicate.get(i));
    }
  }

  public void testLength() {
    Sequence square = new Sequence();
    square.add(new Pt(0, 0));
    square.add(new Pt(100, 0));
    square.add(new Pt(100, 100));
    square.add(new Pt(0, 100));
    square.add(new Pt(0, 0));
    assertEquals(400.0, square.length());
  }

  public void testNearestPointOnSequence() {
    Sequence seq = makeSequence();
    Pt pt;
    pt = Functions.getNearestPointOnSequence(new Pt(1, 1), seq);
    assertEquals(seq.get(1), pt);
    pt = Functions.getNearestPointOnSequence(new Pt(1.1, 1.1), seq);
    assertEquals(seq.get(1), pt);
    pt = Functions.getNearestPointOnSequence(new Pt(-10, -10), seq);
    assertEquals(seq.get(0), pt);
    pt = Functions.getNearestPointOnSequence(new Pt(5, 1.5), seq);
    assertEquals(seq.get(3), pt);
  }

  public void testDistBetweenPoints() {
    Sequence seq = makeSequence();
    double d;
    d = Functions.getMinDistBetweenPointsOnSequence(seq, seq.get(0), seq.get(1));
    assertEquals(1.4142, d, 0.001);
    d = Functions.getMinDistBetweenPointsOnSequence(seq, seq.get(0), seq.get(3));
    assertEquals(3.5763, d, 0.001);
    d = Functions.getMinDistBetweenPointsOnSequence(seq, seq.get(3), seq.get(0));
    assertEquals(3.5763, d, 0.001);
    d = Functions.getMinDistBetweenPointsOnSequence(seq, seq.get(3), seq.get(3));
    assertEquals(0.0, 0.0);
  }

  public void testSetDistancesOnSequence() {
    Sequence seq = makeSequence();
    // test both endpoints and a mid point
    Functions.setDistancesOnSequence("start", seq, new Pt(-10, -10));
    assertEquals(0.0, seq.get(0).getDouble("start"), 0.001);
    assertEquals(1.4142, seq.get(1).getDouble("start"), 0.001);
    assertEquals(2.5322, seq.get(2).getDouble("start"), 0.001);
    assertEquals(3.5763, seq.get(3).getDouble("start"), 0.001);

    Functions.setDistancesOnSequence("end", seq, new Pt(5, 1.5));
    assertEquals(3.5763, seq.get(0).getDouble("end"), 0.001);
    assertEquals(2.1621, seq.get(1).getDouble("end"), 0.001);
    assertEquals(1.0440, seq.get(2).getDouble("end"), 0.001);
    assertEquals(0.0, seq.get(3).getDouble("end"), 0.001);

    Functions.setDistancesOnSequence("mid", seq, new Pt(1.1, 1.1));
    assertEquals(1.4142, seq.get(0).getDouble("mid"), 0.001);
    assertEquals(0.0, seq.get(1).getDouble("mid"), 0.001);
    assertEquals(1.1180, seq.get(2).getDouble("mid"), 0.001);
    assertEquals(2.1621, seq.get(3).getDouble("mid"), 0.001);
  }

  protected Sequence makeSequence() {
    Sequence ret = new Sequence();
    ret.add(new Pt(0.0, 0.0));
    ret.add(new Pt(1.0, 1.0));
    ret.add(new Pt(2.0, 1.5));
    ret.add(new Pt(3.0, 1.8));
    return ret;
  }

  private static Pt f(double x) {
    return new Pt(x, x * x);
  }
  
//  public void testReadWriteSequence() {
//    Sequence seq = makeSequence();
//    try {
//      String tmpFile = File.createTempFile("TestSequence", ".test").getAbsolutePath();
//      seq.writeToFile(tmpFile);
//      Sequence readFromDisk = Sequence.loadFromFile(tmpFile);
//      assertEquals(seq.size(), readFromDisk.size());
//      for (int i=0; i < seq.size(); i++) {
//        assertEquals(seq.getPoints().get(i), readFromDisk.getPoints().get(i));
//      }
//    } catch (IOException ex) {
//      ex.printStackTrace();
//    }
//  }

//  private void bug(String what) {
//    Debug.out("TestSequence", what);
//  }
}
