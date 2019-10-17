// $Id: Interval.java 193 2011-11-21 01:54:21Z gabe.johnson@gmail.com $

package org.six11.util.math;

import static org.six11.util.data.Statistics.maximum;
import static org.six11.util.data.Statistics.minimum;
import static org.six11.util.Debug.num;
import static java.lang.Math.min;
import static java.lang.Math.max;

/**
 * A simple class for representing a numeric range that supports interval arithmetic.
 **/
public class Interval {
  private double a, b;

  public Interval(double a, double b) {
    this.a = min(a, b);
    this.b = max(a, b);
  }

  public Interval add(Interval o) {
    return new Interval(a + o.a, b + o.b);
  }

  public Interval sub(Interval o) {
    return new Interval(a - o.b, b - o.a);
  }

  public Interval mult(Interval o) {
    double newA = minimum(a * o.a, a * o.b, b * o.a, b * o.b);
    double newB = maximum(a * o.a, a * o.b, b * o.a, b * o.b);
    return new Interval(newA, newB);
  }

  public Interval divideBy(Interval o) {
    if (o.includesZero()) {
      throw new ArithmeticException("The interval " + o
          + " includes zero and may not appear in the denominator of a division expression.");
    }
    double newA = minimum(a / o.a, a / o.b, b / o.a, b / o.b);
    double newB = maximum(a / o.a, a / o.b, b / o.a, b / o.b);
    return new Interval(newA, newB);
  }

  public boolean includesZero() {
    return (a <= 0d && b >= 0d);
  }

  public double getA() {
    return a;
  }

  public double getB() {
    return b;
  }

  public double getMid() {
    return (a + b) / 2d;
  }

  public double getSize() {
    return b - a;
  }
  
  public boolean contains(double value) {
    return (a <= value && value <= b);
  }
  
  public String toString() {
    return num(a) + " to " + num(b);
  }

}
