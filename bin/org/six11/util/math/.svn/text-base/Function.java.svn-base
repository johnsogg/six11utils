// $Id$

package org.six11.util.math;

public abstract class Function {
  public abstract double eval(double x);

  public FunctionData getFunctionData(double lowerBound, 
				      double upperBound, double stepSize) {
    double x = lowerBound;
    FunctionData data = new FunctionData();
    double f;
    while (x <= upperBound) {
      f = eval(x);
      if (f < data.f_min) {
	data.x_min = x;
	data.f_min = f;
      }
      if (f > data.f_min) {
	data.x_max = x;
	data.f_max = f;
      }
      x = x + stepSize;
    }
    return data;
  }

  public static class FunctionData {
    public double x_min, f_min;
    public double x_max, f_max;
    public FunctionData() {
      x_min = Double.MAX_VALUE;
      f_min = Double.MAX_VALUE;
      x_max = Double.MIN_VALUE;
      f_max = Double.MIN_VALUE;
    }
  }
}
