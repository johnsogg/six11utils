package org.six11.util.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import static java.lang.Math.abs;
import static org.six11.util.Debug.num;
import static org.six11.util.Debug.bug;

public abstract class ClusterThing<T> {

  public abstract static class ClusterFilter<T> {
    public abstract boolean accepts(Cluster<T> cluster);
  }

  public static class Cluster<T> {

    // a compound cluster has some samples inside it, kept in an array.
    List<T> points;

    // cluster has center: center of mass of samples.
    double center;

    // cluster has exemplar: the closest sample to center
    T exemplar;
    
    // cluster has minimum and maximum valued members
    T minimum, maximum;

    // cluster has radius r, which is the max dist from r to any point in the cluster
    double radius;

    // a cluster rank increases from the leaves to the root.
    int rank;

    // Clusters might be composed of sub-clusters. Retain references to them.
    Cluster<T> a, b;
    
    ClusterThing<T> ct;

    // Create a cluster out of some set of samples.
    public Cluster(int rank, Collection<T> samps, ClusterThing<T> ct) {
      this.ct = ct;
      build(rank, samps);
    }

    public Cluster(T root, ClusterThing<T> ct) {
      this.ct = ct;
      Collection<T> s = new ArrayList<T>();
      s.add(root);
      build(0, s);
    }

    public Cluster(int rank, Cluster<T> clusterA, Cluster<T> clusterB, ClusterThing<T> ct) {
      this.ct = ct;
      this.a = clusterA;
      this.b = clusterB;
      List<T> samps = new ArrayList<T>(); // combine data from both clusters
      samps.addAll(a.points);
      samps.addAll(b.points);
      build(rank, samps);
    }
    
    public String toString() {
      return "Cluster[center=" + num(center) + ", exemplar=" + num(ct.query(exemplar)) + ", radius=" + num(radius) + "]";
    }
    
    public double getMinMaxRatio() {
      return ct.query(minimum) / ct.query(maximum);
    }

    public int getRank() {
      return rank;
    }
    
    public Cluster<T> getChildA() {
      return a;
    }
    
    public Cluster<T> getChildB() {
      return b;
    }
    
    public List<T> getMembers() {
      return new ArrayList<T>(points);
    }

    private final void build(int rank, Collection<T> samps) {
      this.rank = rank;
      this.points = new ArrayList<T>();
      points.addAll(samps);

      // set the cluster center-of-mass
      double sum = 0;
      for (int i = 0; i < points.size(); i++) {
        sum = sum + ct.query(points.get(i));
      }
      center = sum / points.size();

      // find the sample that is closest to and farthest from this center of mass
      double closest = Double.MAX_VALUE;
      double smallest = Double.MAX_VALUE;
      double largest = -Double.MAX_VALUE;
      radius = 0;
      for (T point : points) {
        double thisValue = ct.query(point);
        double d = abs(thisValue - center);
        if (d < closest) {
          exemplar = point;
          closest = d;
        }
        if (thisValue < smallest) {
          minimum = point;
          smallest = thisValue;
        }
        if (thisValue > largest) {
          maximum = point;
          largest = thisValue;
        }
        radius = Math.max(radius, d);
      }

      // vomit out the rank and radius
      // bug("Cluster " + exemplar.getLabel() + " rank " + rank + " has radius: " +
      // Debug.num(radius));
    }

    // dist between two clusters is the dist between their exemplars, not center of mass. This
    // actually returns the squared distance, but since it would not make a difference in the end, I
    // will spare the cycles and not do the square root.
    public double dist(Cluster<T> other) {
      return abs(ct.query(exemplar) - ct.query(other.exemplar));
      //      return exemplar.pcaSquaredDist(other.exemplar);
    }

    public double getRadius() {
      return radius;
    }

    public T getCenter() {
      return exemplar;
    }

    public Cluster<T> getSubclusterA() {
      return a;
    }

    public Cluster<T> getSubclusterB() {
      return b;
    }
    
    public double getCenterOfMass() {
      return center;
    }
    
    public T getMin() {
      return minimum;
    }
    
    public T getMax() {
      return maximum;
    }
  }

  private Set<T> samples;
  private List<Cluster<T>> rankedClusters;
  
  Comparator<Cluster<T>> clusterSorter = new Comparator<Cluster<T>>() {
    public int compare(Cluster<T> a, Cluster<T> b) {
      double vA = query(a.exemplar);
      double vB = query(b.exemplar);
      int ret = 0;
      if (vA < vB) {
        ret = -1;
      } else if (vA > vB) {
        ret = 1;
      }
      return ret;
    };
  };
  
  public ClusterFilter<T> getRatioFilter(final double threshold) {
    return new ClusterFilter<T>() {
      public boolean accepts(Cluster<T> cluster) {
        return cluster.getMinMaxRatio() > threshold;
      }
    };
  }

  public ClusterThing() {
    samples = new HashSet<T>();
    rankedClusters = new ArrayList<Cluster<T>>();
  }

  public abstract double query(T minimum);

  public void add(T s) {
    samples.add(s);
  }

  public void computeClusters() {
    List<Cluster<T>> remainingClusters = new ArrayList<Cluster<T>>();
    rankedClusters.clear();
    for (T s : samples) {
      Cluster<T> cluster = new Cluster<T>(s, this);
      remainingClusters.add(cluster);
      rankedClusters.add(cluster);
    }
    int nextRank = 1;
    while (remainingClusters.size() > 1) {
      merge(nextRank, remainingClusters);
      nextRank++;
    }
  }

  private void merge(int nextRank, List<Cluster<T>> remainingClusters) {
    // one iteration of the clustering algorithm. Determine which two clusters are the closest, call
    // them (a) and (b). Make a new cluster (ab), remove (a) and (b) individually from the
    // remainingClusters list, and add (ab).
    double nearestDist = Double.MAX_VALUE;
    Cluster<T> nearestA = null;
    Cluster<T> nearestB = null;
    for (int i = 0; i < remainingClusters.size(); i++) {
      Cluster<T> a = remainingClusters.get(i);
      for (int j = i + 1; j < remainingClusters.size(); j++) {
        Cluster<T> b = remainingClusters.get(j);
        double thisDist = a.dist(b);
        if (thisDist < nearestDist) {
          nearestDist = thisDist;
          nearestA = a;
          nearestB = b;
        }
      }
    }
    if (nearestA != null && nearestB != null) {
      Cluster<T> ab = new Cluster<T>(nextRank, nearestA, nearestB, this);
      rankedClusters.add(0, ab);
      remainingClusters.remove(nearestA);
      remainingClusters.remove(nearestB);
      remainingClusters.add(ab);
    }
  }

  public List<Cluster<T>> getClusters(int n) {
    List<Cluster<T>> ret = new ArrayList<Cluster<T>>();
    for (int i = 0; i < n; i++) {
      ret.add(rankedClusters.get(i));
    }
    return ret;
  }

  public int size() {
    return samples.size();
  }

  public List<Cluster<T>> search(T t) {
    double worstScore = Double.MAX_VALUE;
    List<Cluster<T>> ret = new ArrayList<Cluster<T>>();
    Stack<Cluster<T>> todo = new Stack<Cluster<T>>();
    todo.push(rankedClusters.get(0));
    int numAvoided = 0;
    while (!todo.isEmpty()) {
      Cluster<T> cluster = todo.pop();
      double dist = abs(query(t) - query(cluster.exemplar));
      if (ret.isEmpty() || dist < worstScore) {
        ret.add(cluster);
        worstScore = dist;
        if (cluster.a != null) {
          todo.push(cluster.a);
        }
        if (cluster.b != null) {
          todo.push(cluster.b);
        }
      } else {
        numAvoided = numAvoided++;
      }
    }
    // sort the return list by distance from t
    Collections.sort(ret, clusterSorter);
    return ret;
  }

  public double getRadius() {
    return rankedClusters.get(0).radius;
  }
  
  public Cluster<T> getRootCluster() {
    return rankedClusters.get(0);
  }

  public List<Cluster<T>> search(ClusterFilter<T> filter) {
    List<Cluster<T>> ret = new ArrayList<Cluster<T>>();
    Stack<Cluster<T>> todo = new Stack<Cluster<T>>();
    todo.push(getRootCluster());
    while (!todo.isEmpty()) {
      Cluster<T> cluster = todo.pop();
      if (filter.accepts(cluster)) {
        ret.add(cluster);
      } else {
        if (cluster.a != null) {
          todo.push(cluster.a);
        }
        if (cluster.b != null) {
          todo.push(cluster.b);
        }
      }
    }
    return ret;
  }
}
