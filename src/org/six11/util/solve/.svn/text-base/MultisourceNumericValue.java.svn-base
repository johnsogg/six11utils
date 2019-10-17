package org.six11.util.solve;

import static org.six11.util.Debug.num;
//import static org.six11.util.Debug.bug;

import java.util.HashSet;
import java.util.Set;

public class MultisourceNumericValue extends NumericValue {

  Set<Source> sources;
  String variableName;

  public abstract static class Source {
    public abstract double getValue();
  }

  public MultisourceNumericValue(Source... values) {
    this.sources = new HashSet<Source>();
    for (Source v : values) {
      addValue(v);
    }
  }

  public void setValue(Source val) {
    sources.clear();
    addValue(val);
  }

  public void addValue(Source val) {
    sources.add(val);
  }

  public Set<Source> getSources() {
    return sources;
  }

  @Override
  public double getValue() {
    double ret = 0;
    if (sources.size() > 0) {
      double sum = 0;
      for (Source s : sources) {
        sum = sum + s.getValue();
      }
      ret = sum / sources.size();
    }
    return ret;
  }

  @Override
  public String toString() {
    String ret = variableName;
    if (ret == null) {
      ret = "=" + num(getValue(), 4) + "(" + sources.size() + " samples)";
    }
    return ret;
  }

}
