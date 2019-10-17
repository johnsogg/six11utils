package org.six11.util.spud;

import org.six11.util.Debug;
import org.six11.util.args.Arguments;
import org.six11.util.pen.Pt;
import org.six11.util.pen.Vec;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class Test {

  public static void main(String[] in) {
    Arguments args = new Arguments();
    args.parseArguments(in);
    Debug.useColor = args.hasFlag("debug-color");
    if (args.hasFlag("test")) {
      int num = Integer.parseInt(args.getValue("test"));
      switch (num) {
        case 1:
          test1();
          break;
        case 2:
          test2();
          break;
        case 3:
          test3();
          break;
        case 4:
          test4();
          break;
        case 5:
          test5();
          break;
        case 6:
          test6();
          break;
        case 7:
          test7();
          break;
        case 8:
          test8();
          break;
        case 9:
          test9();
          break;
        default:
          bug("No such test number: " + num);
      }
    } else {
      // change this to whatever you want the default to be.
      test6();
    }
  }

  private static void test1() {
    // this tests point-at-location
    ConstraintModel model = new ConstraintModel();
    Pt ptA = new Pt(20, 30);
    CPoint cA = new CPoint();
    model.addConstraint(new CPointLocation(cA, ptA));

    Pt ptB = new Pt(40, 40);
    CPoint cB = new CPoint();
    model.addConstraint(new CPointLocation(cB, ptB));

    bug("Before solving, model is:" + model.getMondoDebugString());
    model.solve();
    bug("After solving, model is:" + model.getMondoDebugString());
    bug("first point: " + Debug.num(cA.getPt()));
    bug("other point: " + Debug.num(cB.getPt()));
  }

  private static void test2() {
    // this tests vector-in-direction
    ConstraintModel model = new ConstraintModel();
    Pt ptA = new Pt(20, 30);
    CPoint cA = new CPoint();
    model.addConstraint(new CPointLocation(cA, ptA));

    Vec up = new Vec(0, 1);
    CVec vA = new CVec();
    model.addConstraint(new CVecDirection(up, vA));
    bug("Before solving, model is:" + model.getMondoDebugString());
    model.solve();
    bug("After solving, model is:" + model.getMondoDebugString());
    bug("the point: " + Debug.num(cA.getPt()));
    bug("the direction: " + Debug.num(vA.getDir()));
  }

  private static void test3() {
    // this tests line-definition
    ConstraintModel model = new ConstraintModel();
    CLine lineA = new CLine();
    
    // line passes through (20, 30) and is in direction <0, 1>
    model.addConstraint(new CLineDefinition(lineA, new CPoint(new Pt(20, 30)),  new CVec(0, 1)));
    bug("Before solving, model is:" + model.getMondoDebugString());
    model.solve();
    bug("After solving, model is:" + model.getMondoDebugString());
    bug("the line: " + lineA.getDebugString());
  }

  private static void test4() {
    // tests point-on-line and finding point by combining solution spaces from constraints
    ConstraintModel model = new ConstraintModel();

    // ptA = (20, 30)
    CPoint cA = new CPoint(new Pt(20, 30));

    // up = <0, 1>
    CVec vA = new CVec(0, 1);
    
    // lineA = [ptA, up]
    CLine lineA = new CLine();
    model.addConstraint(new CLineDefinition(lineA, cA, vA));

    // ptB = (50, 70)
    CPoint cB = new CPoint(new Pt(50, 70));

    // left = <1, 0>
    CVec vB = new CVec(1, 0);
    
    // lineB = [ptB, left]
    CLine lineB = new CLine();
    model.addConstraint(new CLineDefinition(lineB, cB, vB));

    // point unknown on lineA
    // point unknown on lineB
    Geom unknown = new CPoint();
    model.addConstraint(new CPointOnLine(lineA, unknown));
    model.addConstraint(new CPointOnLine(lineB, unknown));

    bug("Before solving, model is:" + model.getMondoDebugString());
    model.solve();
    bug("After solving, model is:" + model.getMondoDebugString());
    bug("the unknown point: " + unknown.getDebugString());
  }

  private static void test5() {
    // this tests perpendicular lines

    ConstraintModel model = new ConstraintModel();

    // ptA = 10, 50
    Geom ptA = new CPoint(new Pt(10, 50));

    // up = <0, 1>
    CVec up = new CVec(0, 1);

    // lineA = [ptA, up]
    CLine lineA = new CLine();
    model.addConstraint(new CLineDefinition(lineA, ptA, up));

    // ptB = 70, 15
    Geom ptB = new CPoint(new Pt(70, 15));

    // ptA is on lineA
    model.addConstraint(new CPointOnLine(lineA, ptA));

    // lineA perp lineB
    CLine lineB = new CLine();
    model.addConstraint(new CPerpendicularLines(lineA, lineB));

    // ptB is on lineB
    model.addConstraint(new CPointOnLine(lineB, ptB));

    // point unknown on lineA
    // point unknown on lineB
    Geom unknown = new CPoint();
    model.addConstraint(new CPointOnLine(lineA, unknown));
    model.addConstraint(new CPointOnLine(lineB, unknown));

    // Cross fingers and solve.
    bug("Before solving, model is:" + model.getMondoDebugString());
    model.solve();
    bug("After solving, model is:" + model.getMondoDebugString());
    bug("the point on both lines: " + unknown.getDebugString());
  }

  private static void test6() {
    // this tests distance-from (and circles and pointsets)

    ConstraintModel model = new ConstraintModel();

    // ptA = 10, 50
    CPoint ptA = new CPoint(new Pt(10, 50));

    // up = <0, 1>
    CVec up = new CVec(0, 1);

    // lineA = [ptA, up]
    CLine lineA = new CLine();
    model.addConstraint(new CLineDefinition(lineA, ptA, up));

    // dist = 20
    CDouble dist = new CDouble();
    model.addConstraint(new CDoubleDefinition(dist, 30));

    // ptB and ptA are 'dist' units apart
    CPoint ptB = new CPoint();
    model.addConstraint(new CDistance(ptA, ptB, dist));

    // ptB is on lineA
    model.addConstraint(new CPointOnLine(lineA, ptB));

    // solve
    bug("Before solving, model is:" + model.getMondoDebugString());
    model.solve();
    bug("After solving, model is:" + model.getMondoDebugString());
    bug("the point should have two possible solutions: " + ptB.getDebugString());
  }

  private static void test7() {
    // this tests distance-from
    ConstraintModel model = new ConstraintModel();

    // ptA = 50, 50
    CPoint ptA = new CPoint(new Pt(50, 50));

    // dist = 30
    CDouble dist = new CDouble(30);
//    model.addConstraint(new CDoubleDefinition(dist, 30));

    // ptA and ptB are 'dist' units apart
    CPoint ptB = new CPoint();
    model.addConstraint(new CDistance(ptA, ptB, dist));

    // solve
    bug("Before solving, model is:" + model.getMondoDebugString());
    model.solve();
    bug("After solving, model is:" + model.getMondoDebugString());
    bug("the point should have a circle describing possible solutions: " + ptB.getDebugString());
  }

  private static void test8() {
    // this tests distance-along-line. Good for getting midpoints, thirds, or scalar 
    // distances along lines.
    ConstraintModel model = new ConstraintModel();

    // ptA = 100, 100
    CPoint ptA = new CPoint(new Pt(100, 100));

    // ptB = 200, 200
    CPoint ptB = new CPoint(new Pt(200, 200));

    // ptA and ptB are on lineA
    CLine lineA = new CLine();
    model.addConstraint(new CPointOnLine(lineA, ptA));
    model.addConstraint(new CPointOnLine(lineA, ptB));

    // ptM = midpoint of A, B
    CPoint ptM = new CPoint();
    model.addConstraint(new CPointAlongSegment(ptA, ptB, ptM, 0.5));

    // ptQ = 60% of distance from M to B
    CPoint ptQ = new CPoint();
    model.addConstraint(new CPointAlongSegment(ptM, ptB, ptQ, 0.6));

    // ptA = 20% of distance from R to M. This tests if it can solve for R given A and M known.
    CPoint ptR = new CPoint();
    model.addConstraint(new CPointAlongSegment(ptR, ptM, ptA, 0.2));

    // solve
    model.solve();

    bug("point M should be halfway between " + ptA.getDebugString() + " and "
        + ptB.getDebugString() + ": " + ptM.getDebugString());

    bug("point Q should be 60% between " + ptM.getDebugString() + " and " + ptB.getDebugString()
        + ": " + ptQ.getDebugString());

    bug("point A should be 20% between " + ptR.getDebugString() + " and " + ptM.getDebugString()
        + ": " + ptA.getDebugString());
  }

  private static void test9() {
    // tests parallel lines.
    ConstraintModel model = new ConstraintModel();

    // ptA = 10, 50
    Geom ptA = new CPoint(new Pt(10, 50));

    // up = <0, 1>
    CVec up = new CVec(0, 1);

    // lineA = [ptA, up]
    CLine lineA = new CLine();
    model.addConstraint(new CLineDefinition(lineA, ptA, up));

    // ptB = 70, 15
    Geom ptB = new CPoint(new Pt(70, 15));

    // ptA is on lineA
    model.addConstraint(new CPointOnLine(lineA, ptA));

    // lineA parallel to lineB
    CLine lineB = new CLine();
    model.addConstraint(new CParallelLines(lineA, lineB));

    // ptB is on lineB
    model.addConstraint(new CPointOnLine(lineB, ptB));

    // Cross fingers and solve.
    bug("Before solving, model is:" + model.getMondoDebugString());
    model.solve();
    bug("After solving, model is:" + model.getMondoDebugString());
    bug("Should be parallel: " + lineA.getDebugString() + ", " + lineB.getDebugString());
  }

  private static void bug(String what) {
    Debug.out("Test", what);
  }

}
