package org.six11.util.data;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import static org.six11.util.Debug.bug;
import static org.six11.util.Debug.num;

public class RankedList<T> {

  private SortedSet<RankedNode> data;

  private class RankedNode implements Comparable<RankedNode> {
    double val;
    T obj;

    RankedNode(double val, T obj) {
      this.val = val;
      this.obj = obj;
    }

    public int compareTo(RankedNode that) {
      int ret = 0;
      if (this.val <= that.val) {
        ret = -1;
      } else if (this.val > that.val) {
        ret = 1;
      }
      return ret;
    }
  }

  public RankedList() {
    data = new TreeSet<RankedNode>();
  }

  public void add(double val, T obj) {
    data.add(new RankedNode(val, obj));
  }

  public double[] getRanks() {
    double[] ret = new double[data.size()];
    int counter = 0;
    for (RankedNode rn : data) {
      ret[counter++] = rn.val;
    }
    return ret;
  }

  public List<T> getData() {
    List<T> ret = new ArrayList<T>();
    for (RankedNode rn : data) {
      ret.add(rn.obj);
    }
    return ret;
  }

  public int size() {
    return data.size();
  }

  public double getHighestScore() {
    double ret = 0;
    if (size() > 0) {
      ret = data.last().val;
    }
    return ret;
  }
  
  public double getLowestScore() {
    double ret = 0;
    if (size() > 0) {
      ret = data.first().val;
    }
    return ret;
  }
  
  public List<T> getHigherThan(double thresh) {
    List<T> ret = new ArrayList<T>();
    for (RankedNode rn : data) {
      if (rn.val > thresh) {
        ret.add(rn.obj);
      }
    }
    return ret;
  }
  
  public List<T> getLessThan(double thresh) {
    List<T> ret = new ArrayList<T>();
    for (RankedNode rn : data) {
      if (rn.val < thresh) {
        ret.add(rn.obj);
      }
    }
    return ret;
  }

}
