// $Id: Lists.java 264 2012-02-29 02:44:39Z gabe.johnson@gmail.com $

package org.six11.util.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 **/
public abstract class Lists {

  public static boolean hasOverlap(Collection<?> bunchA, Collection<?> bunchB) {
    boolean ret = false;
    for (Object t : bunchA) {
      if (bunchB.contains(t)) {
        ret = true;
        break;
      }
    }
    return ret;
  }

  public static <T> boolean isSameObject(List<T> list, int idx, T obj) {
    return list.size() > idx && list.get(idx) == obj;
  }

  public static <T> T getLast(List<T> list) {
    return list.get(list.size() - 1);
  }

  public static void setLast(List<Object> list, Object obj) {
    if (list.size() == 0) {
      list.add(obj);
    } else {
      list.set(list.size() - 1, obj);
    }
  }

  public static boolean isLast(List<?> list, Object obj) {
    return (list.size() > 0 && getLast(list).equals(obj));
  }

  /**
   * Same as a.containsAll(b) && b.containsAll(a);
   */
  public static boolean areListsSame(List<?> a, List<?> b) {
    return a.containsAll(b) && b.containsAll(a);
  }

  /**
   * Computes out[i] = in[i] - in[i-1], where out[0] = 0. The return list is the same size as the
   * input list.
   */
  public static List<Double> getDeltaList(List<Double> in) {
    List<Double> ret = new ArrayList<Double>();
    ret.add(0.0);
    for (int i = 1; i < in.size(); i++) {
      ret.add(in.get(i) - in.get(i - 1));
    }
    return ret;
  }

  public static <T> T getOne(Collection<T> items) {
    T ret = null;
    if (!items.isEmpty()) {
      ret = items.iterator().next();
    }
    return ret;
  }

  public static <T> T removeOne(Set<T> items) {
    T ret = null;
    if (!items.isEmpty()) {
      ret = items.iterator().next();
      items.remove(ret);
    }
    return ret;
  }

  public static <T> Collection<T> intersect(Collection<T> groupA, Collection<T> groupB) {
    Collection<T> ret = new HashSet<T>();
    for (T t : groupA) {
      if (groupB.contains(t)) {
        ret.add(t);
      }
    }
    return ret;
  }

  public static <T> Set<T> makeSet(T... ts) {
    Set<T> ret = new HashSet<T>();
    for (T t : ts) {
      ret.add(t);
    }
    return ret;
  }

  public static <T> boolean areSetsEqual(Collection<T> a, Set<T> b) {
    boolean ret = false;
    if (a == null && b == null) {
      ret = true;
    } else if (a == null || b == null) {
      ret = false;
    } else if (a.size() == b.size() && a.containsAll(b)) {
      ret = true;
    }
    return ret;
  }
}
