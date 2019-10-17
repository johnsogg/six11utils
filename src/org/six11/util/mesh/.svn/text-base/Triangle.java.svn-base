package org.six11.util.mesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.six11.util.Debug;
import org.six11.util.pen.Functions;
import org.six11.util.pen.Pt;
import org.six11.util.pen.Vec;

import static org.six11.util.pen.Functions.eq;
import static org.six11.util.pen.Functions.gt;
import static org.six11.util.pen.Functions.lt;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class Triangle {

  private HalfEdge edge;
  public String id;
  private List<Pt> cachedPoints;
  private Pt cachedCentroid;
  private static int ID_COUNTER = 0;
  Where meshLocation = Where.Unknown;

  /**
   * @param vertA
   * @param vertB
   * @param vertC
   */
  public Triangle(Pt vertA, Pt vertB, Pt vertC) {
    id = "" + ID_COUNTER++;
    HalfEdge edgeA = new HalfEdge(vertA, this);
    edge = edgeA;
    HalfEdge edgeB = new HalfEdge(vertB, this);
    HalfEdge edgeC = new HalfEdge(vertC, this);
    edgeA.setNext(edgeB);
    edgeB.setNext(edgeC);
    edgeC.setNext(edgeA);
  }

  /**
   * 
   */
  protected Triangle(HalfEdge edge) {
    this.edge = edge;
    this.id = "" + ID_COUNTER++;
    if (edge.getFace() != null) {
      meshLocation = edge.getFace().getMeshLocation();
    }
  }

  private static void bug(String what) {
    Debug.out("Triangle", what);
  }

  void setLocation(Where where) {
    meshLocation = where;
  }

  public Where getMeshLocation() {
    return meshLocation;
  }

  public String toString() {
    return "Triangle " + id + " (edge: " + (edge == null ? "?" : edge.id) + ")";
  }

  public Set<Triangle> getAdjacentTriangles() {
    Set<Triangle> ret = new HashSet<Triangle>();
    Triangle t;
    HalfEdge cursor = edge;
    do {
      t = getOpposingTriangle(cursor);
      if (t != null) {
        ret.add(t);
      }
      cursor = cursor.getNext();
    } while (cursor != edge);
    return ret;
  }

  private Triangle getOpposingTriangle(HalfEdge cursor) {
    Triangle ret = null;
    if (cursor.getPair() != null) {
      ret = cursor.getPair().getFace();
    }
    return ret;
  }

  public HalfEdge getCommonEdge(Triangle neighbor) {
    HalfEdge ret = null;
    HalfEdge cursorThis = edge;
    outside: {
      do {
        HalfEdge cursorThat = neighbor.edge;
        do {
          if (cursorThis.getPair() == cursorThat) {
            ret = cursorThis;
            break outside;
          }
          cursorThat = cursorThat.getNext();
        } while (cursorThat != neighbor.edge);
        cursorThis = cursorThis.getNext();
      } while (cursorThis != edge);
    }
    return ret;
  }

  public List<Pt> getPoints() {
    if (cachedPoints == null) {
      cachedPoints = new ArrayList<Pt>();
      cachedPoints.add(edge.getPoint());
      cachedPoints.add(edge.getNext().getPoint());
      cachedPoints.add(edge.getNext().getNext().getPoint());
    }
    return cachedPoints;
  }

  public Pt getCentroid() {
    if (cachedCentroid == null) {
      List<Pt> pts = getPoints();
      double cx = (pts.get(0).x + pts.get(1).x + pts.get(2).x) / 3.0;
      double cy = (pts.get(0).y + pts.get(1).y + pts.get(2).y) / 3.0;
      cachedCentroid = new Pt(cx, cy);
    }
    return cachedCentroid;
  }

  public HalfEdge getEdge() {
    return edge;
  }

  /**
   * Returns the barycentric coordinates for the given point using this triangle as a basis. The
   * 'origin' is point 0. The first return coordinate is in the direction of point 1, the second
   * return coordinate is in the direction of point 2. For example, if the result is [0, 0.5], it
   * means the input point is halfway between points 0 and 2.
   * 
   * This function is translated (with slight modifications) from
   * http://www.blackpawn.com/texts/pointinpoly/default.html.
   **/
  public double[] getBarycentricCoordinates(Pt pt) {
    List<Pt> pts = getPoints();
    Pt a = pts.get(0);
    Pt b = pts.get(1);
    Pt c = pts.get(2);
    Vec v0 = new Vec(a, c); // v0 = C - A
    Vec v1 = new Vec(a, b); // v1 = B - A
    Vec v2 = new Vec(a, pt); // v2 = P - A

    double dot00 = v0.dot(v0);
    double dot01 = v0.dot(v1);
    double dot02 = v0.dot(v2);
    double dot11 = v1.dot(v1);
    double dot12 = v1.dot(v2);

    double invDenom = 1.0 / (dot00 * dot11 - dot01 * dot01);

    double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
    double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

    return new double[] {
        u, v
    };
  }

  public Where whereIsPoint(Pt pt, boolean showDebug) {

    // There are eight possibilities:
    // 1,2,3: pt is one of the three vertexes
    // 4,5,6: pt is on one of the three edges
    // 7: pt is strictly inside the triangle
    // 8: pt is strictly outside the triangle.
    Where ret = Where.Outside;
    if (hasVertex(pt)) {
      ret = Where.Coincidental;
    } else {
      double[] barycentric = getBarycentricCoordinates(pt);
      double u = barycentric[0];
      double v = barycentric[1];
      double T = Functions.EQ_TOL;
      if /*   */(eq(u, 0, T) && gt(v, 0, T) && v <= 1) {
        ret = Where.Boundary;
      } else if (gt(u, 0, T) && eq(v, 0, T) && u <= 1) {
        ret = Where.Boundary;
      } else if (gt(u, 0, T) && gt(v, 0, T) && eq(u + v, 1, T)) {
        ret = Where.Boundary;
      } else if (gt(u, 0, T) && gt(v, 0, T) && lt(u + v, 1, T)) {
        ret = Where.Inside;
      }
      if (showDebug) {
        bug("Point is " + ret + " (barycentric coords: " + Debug.num(u) + ", " + Debug.num(v) + ")");
      }
    }
    return ret;
  }

  /**
   * Returns true if the given point is the same in-memory point as the one provided.
   */
  public boolean hasVertex(Pt pt) {
    return edge.getPoint() == pt || edge.getNext().getPoint() == pt
        || edge.getNext().getNext().getPoint() == pt;
  }

  /**
   * Accumulates the points in this triangle and the opposing one, starting at pt.
   * 
   * @return a list consisting of four points. Index zero is the input point. Index 1 and 3 are
   *         shared by both triangles. Index 2 is the far point of the opposite triangle.
   */
  public List<Pt> getQuadrangle(Pt pt) {
    List<Pt> ret = null;
    if (hasVertex(pt)) {
      HalfEdge cursor = edge;
      while (cursor.getPoint() != pt) {
        cursor = cursor.getNext();
      }
      HalfEdge opposite = cursor.getNext().getNext().getPair();
      if (opposite != null) {
        ret = new ArrayList<Pt>();
        ret.add(cursor.getPoint());
        ret.add(opposite.getPoint());
        ret.add(opposite.getNext().getPoint());
        ret.add(opposite.getNext().getNext().getPoint());
      }
    }
    return ret;
  }

  /**
   * Assuming the given triangle really does share an edge with this one, there are two shared
   * points. Return them in no particular order.
   */
  public Pt[] getShared(Triangle opposing) {
    Pt[] ret = new Pt[2];
    HalfEdge cursor = edge;
    do {
      if (cursor.getPair() != null && cursor.getPair().getFace() == opposing) {
        ret[0] = cursor.getPoint();
        ret[1] = cursor.getPair().getPoint();
        break;
      }
      cursor = cursor.getNext();
    } while (cursor != edge);
    return ret;
  }

  public HalfEdge getEdgeContaining(Pt pt) {
    double[] barycentric = getBarycentricCoordinates(pt);
    double u = barycentric[0];
    double v = barycentric[1];
    HalfEdge ret = null;
    if (Functions.eq(0, u, Functions.EQ_TOL)) {
      ret = edge.getNext();
    } else if (Functions.eq(0, v, Functions.EQ_TOL)) {
      ret = edge;
    } else if (u > 0 && v > 0 && Functions.eq(1, u + v, Functions.EQ_TOL)) {
      ret = edge.getNext().getNext();
    } else {
      bug("Couldn't find edge using barycentric coordiantes even though I "
          + "thought I should have been able to: " + Debug.num(barycentric));
    }
    return ret;
  }

  public String getVertIds() {
    List<Pt> p = getPoints();
    Collections.sort(p, Pt.sortByT);
    StringBuilder buf = new StringBuilder();
    for (Pt pt : p) {
      buf.append(pt.getID() + " ");
    }
    return ("[" + buf.toString().trim() + "]");
  }

  public double getArea() {
    List<Pt> pts = getPoints();
    Vec a = new Vec(pts.get(0), pts.get(1));
    Vec b = new Vec(pts.get(0), pts.get(2));
    return Math.abs(Functions.getDeterminant(a, b) / 2);
  }

  /**
   * Returns true if this triangle involves ANY of the points in the provided list.
   */
  public boolean involvesPoints(List<Pt> others) {
    boolean ret = false;
    for (Pt other : others) {
      if (hasVertex(other)) {
        ret = true;
        break;
      }
    }
    return ret;
  }
}
