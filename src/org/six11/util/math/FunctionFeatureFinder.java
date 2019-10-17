// $Id: FunctionFeatureFinder.java 23 2009-11-24 21:09:08Z gabe.johnson $

package org.six11.util.math;

import org.six11.util.Debug;
import org.six11.util.data.GaussianHat;

/**
 * 
 **/
public class FunctionFeatureFinder {
  
  public final static int MAXIMUM = 0;
  public final static int MINIMUM = 1;
  public final static int ZERO = 2;
  public final static int TARGET_VALUE = 3;
  
  private GaussianHat hat;
  private int maxIterations;
  private double inputTolerance;
  private double outputTolerance;

  public static void main(String[] args) {
    FunctionFeatureFinder hc = new FunctionFeatureFinder(100, 0.000001, 0.000001);
    Function f = new Function() {
	public double eval(double x) {
	  return Math.sin(x) + 0.5d;
	}
      };
    double in;
    in = hc.seekTarget(f, 3.2, 4.0, 0d);
    System.out.println("Root: f(" + Debug.num(in, 5) + ") = " + Debug.num(f.eval(in), 5));
    in = hc.seekMax(f, 3.5);
    System.out.println("Max:  f(" + Debug.num(in, 5) + ") = " + Debug.num(f.eval(in), 5));
    in = hc.seekMin(f, 3.5);
    System.out.println("Min:  f(" + Debug.num(in, 5) + ") = " + Debug.num(f.eval(in), 5));
  }

  /**
   * Make a new hill climbing object. 
   */
  public FunctionFeatureFinder(int maxIterations, double inputTolerance, double outputTolerance) {
    hat = new GaussianHat(0d);
    this.maxIterations = maxIterations;
    this.inputTolerance = inputTolerance;
    this.outputTolerance = outputTolerance;
  }

  public double seekMin(Function f, double initialInput) {
    return seekMinOrMax(f, initialInput, 0.01, MINIMUM);    
  }

  public double seekMax(Function f, double initialInput) {
    return seekMinOrMax(f, initialInput, 0.01, MAXIMUM);    
  }


  double seekMinOrMax(Function f, double x0, double stepSize, int mode) {
    // 1. from x0, pick a direction based on f(x0 +- delta)
    // 2. move in that direction until the answer gets worse
    // 3. if Math.abs(f(xn+1) - f(xn)) < outputTolerance, return xn
    // 4. otherwise cut delta by maxIterations and recurse.
    // 5. stop after maxIterations should it be reached.

    double x = x0;
    double here  = f.eval(x);
    double left  = f.eval(x - stepSize);
    double right = f.eval(x + stepSize);

    double delta = stepSize;
    boolean insufficientSampling = false;
    if (beats(left, here, mode) && beats(right, here, mode)) {
      if (beats(left, right, mode)) {
	delta = -stepSize;
      } else {
	delta = stepSize;
      }
    } else if (beats(here, left, mode) && beats(here, right, mode)) {
      insufficientSampling = true; // won't be able to do anything. cut delta and try again.
    } else if (beats(left, here, mode)) {
      delta = -stepSize;
    } else { // (right beats here)
      delta = stepSize;
    }
    
    int iteration = 0;
    if (!insufficientSampling) {
      boolean better = true;
      while (better && iteration < maxIterations) {
	double f0 = f.eval(x);
	double f1 = f.eval(x+delta);
	if (beats(f0, f1, mode)) {
	  better = false;
	} else {
	  if (Math.abs(f0 - f1) < outputTolerance) {
	    return x;
	  }
	  x = x + delta;
	  iteration++;
	}
      }
    }
    
    if (iteration != maxIterations) {
      // reduce the step size only when necessary.
      stepSize = stepSize / 2d;
    }
    
    return seekMinOrMax(f, x, stepSize, mode);
  }

  boolean beats(double a, double b, int type) {
    if (type == MAXIMUM) {
      return a > b;
    } else {
      return a < b;
    }
  }

  /**
   * This returns the value x such that f(x) = target. This assumes
   * that your target value is between f(lowerBound) and f(upperBound)
   */
  public double seekTarget(Function f, double lowerBound, double upperBound, 
		     double target) {
    double fa = f.eval(lowerBound);
    double fb = f.eval(upperBound);
    // ensure target is in the range.
    if (isValueBetween(fa, fb, target)) {
    } else {
      Debug.out("FunctionFeatureFinder", "The target value (" + target + ") can't be found in the function in the range");
      return 0d;
    }

    // ok, all set.
    int iteration = 0;
    Interval inputInterval = new Interval(lowerBound, upperBound);
    Interval outputInterval = new Interval(f.eval(lowerBound), f.eval(upperBound));
    while (iteration < maxIterations &&
	   inputInterval.getSize() > inputTolerance &&
	   outputInterval.getSize() > outputTolerance) {
      inputInterval = seekTarget(f, inputInterval, target);
      outputInterval = new Interval(f.eval(lowerBound), f.eval(upperBound));
      iteration++;
    }
    double input = inputInterval.getMid();
    f.eval(input);
    return input;
  }

  /**
   * This will return an interval where the answer is likely to be.
   */
  Interval seekTarget(Function f, Interval inputRange, double target) {
    double a = inputRange.getA();
    double b = inputRange.getB();
    hat.setMean((a + b) / 2d);
    hat.setStdDev(Math.abs(a-b)/10d);
    double c = hat.getDouble();

    double fa = f.eval(a);
    double fc = f.eval(c);

    // This assumes that the input interval contains the number.
    if (isValueBetween(fa, fc, target)) {
      b = c; // use a, c
    } else {
      a = c; // use c, b
    }
    return new Interval(a,b);
  }

  private boolean isValueBetween(double a, double b, double t) {
    // we don't know if a < b or if b < a. Either way, 't' must be
    // between them.
    boolean ret;
    if (a < b) {
      ret = (a <= t && t < b);
    } else {
      ret = (b <= t && t < a);
    }
    return ret;
  }

}
