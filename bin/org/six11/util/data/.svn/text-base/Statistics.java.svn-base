// $Id$

package org.six11.util.data;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import org.six11.util.Debug;

/**
 * 
 **/
public class Statistics {
  private boolean dirty = false;
  private List<Double> data;

  private double average;
  private double sum;
  private double variance;
  private double variation;
  private double min;
  private double max;
  private double median;

  private int maximumN = Integer.MAX_VALUE;

  public Statistics() {
    data = new ArrayList<Double>();
  }

  public Statistics(List<Double> source) {
    data = new ArrayList<Double>(source);
    dirty = true;
  }

  /**
   * Clears the data. On return, the Statistics instance is like a new one, except the maximum N (if
   * previously set) is retained.
   */
  public void clear() {
    data.clear();
    dirty = true;
  }

  /**
   * Set the maximum number of values this statistics instance will retain. This is useful, for
   * example, if you are interested in statistics on the recent portion of a data stream.
   */
  public void setMaximumN(int maxN) {
    maximumN = maxN;
  }

  public void addData(double d) {
    data.add(d);
    if (data.size() > maximumN) {
      data.remove(0);
    }
    dirty = true;
  }

  public double getMax() {
    calc();
    return max;
  }

  public double getMin() {
    calc();
    return min;
  }

  public int getN() {
    return data.size();
  }

  public double getMean() {
    calc();
    return average;
  }

  public double getSum() {
    calc();
    return sum;
  }

  public double getVariation() {
    calc();
    return variation;
  }

  public double getVariance() {
    calc();
    return variance;
  }

  public double getMedian() {
    calc();
    return median;
  }

  public double getStdDev() {
    calc();
    return Math.sqrt(variance);
  }

  public double getStdDevDistance(double x) {
    calc();
    return (x - getMean()) / getStdDev();
  }

  public int countOutliersStdDev(double outlierThreshold) {
    int ret = 0;
    calc();
    for (double d : data) {
      double dist = getStdDevDistance(d);
      if (dist > outlierThreshold) {
        ret++;
      }
    }
    return ret;
  }

  public void calc() {
    if (dirty) {
      double n = (double) data.size();
      max = Double.MIN_VALUE;
      min = Double.MAX_VALUE;
      sum = 0.0;
      variation = 0.0;
      for (double d : data) {
        sum += d;
        min = Math.min(d, min);
        max = Math.max(d, max);
      }
      average = sum / n;
      for (double d : data) {
        variation += Math.pow(d - average, 2.0);
      }
      variance = variation / n;

      // calculate median
      List<Double> copy = new ArrayList<Double>(data);
      Collections.sort(copy);
      if (copy.size() == 1) {
        median = copy.get(0);
      } else if ((copy.size() % 2) == 0) {
        median = copy.get(copy.size() / 2);
      } else {
        double v1 = copy.get(copy.size() / 2);
        double v2 = copy.get((copy.size() / 2) + 1);
        median = (v1 + v2) / 2.0;
      }
    }
    dirty = false;
  }

  public List<Double> getDataListSorted() {
    List<Double> copy = getDataList();
    Collections.sort(copy);
    return copy;
  }

  public List<Double> getDataList() {
    return new ArrayList<Double>(data);
  }

  @SuppressWarnings("unused")
  private static void bug(String what) {
    Debug.out("Statistics", what);
  }

  public static double minimum(double... vals) {
    double ret = Double.MAX_VALUE;
    for (double d : vals) {
      ret = Math.min(ret, d);
    }
    return ret;
  }

  public static double maximum(double... vals) {
    double ret = Double.MIN_VALUE;
    for (double d : vals) {
      ret = Math.max(ret, d);
    }
    return ret;
  }

  public static double mean(double... vals) {
    double ret = 0d;
    double count = 0d;
    for (double v : vals) {
      count += v;
    }
    if (vals.length > 0) {
      ret = count / vals.length;
    }
    return ret;
  }

  public void printDebug() {
//    private double average;
//    private double sum;
//    private double variance;
//    private double variation;
//    private double min;
//    private double max;
//    private double median;
    System.out.println("        n: " + getN());
    System.out.println("  average: " + getMean());
    System.out.println("      sum: " + getSum());
    System.out.println(" variance: " + getVariance());
    System.out.println("variation: " + getVariation());
    System.out.println("      min: " + getMin());
    System.out.println("      max: " + getMax());
    System.out.println("   median: " + getMedian());
    System.out.println("  std_dev: " + getStdDev());
  }

}
