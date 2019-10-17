package org.six11.util.solve;

import static org.six11.util.Debug.num;

public class NumericValue {

  private int numValues;
  private double val;
  protected String variableName;

  public NumericValue(double... vals) {
    for (double v : vals) {
      addValue(v);
    }
  }
  
  public void setValue(double v) {
    val = v;
    numValues = 1;
  }

  public void addValue(double v) {
    numValues++;
    val = (val + v);
  }
  
  public double getValue() {
    return val / (double) numValues;
  }
  
  public void setVariableName(String n) {
    this.variableName = n;
  }

  public String getVariableName() {
    return variableName;
  }

  public String toString() {
    String ret = variableName;
    if (ret == null) {
      ret = "" + num(val, 4);
    }
    return ret;
  }

  
}
