package org.six11.util.mesh;

import java.util.HashSet;
import java.util.Set;

import org.six11.util.Debug;
import org.six11.util.pen.Pt;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class HalfEdge {

  private Pt vertex;
  private HalfEdge pair;
  private Triangle face;
  private HalfEdge next;
  private boolean boundary;

  String id;
  private static int ID_COUNTER = 0;

  public HalfEdge(Pt where, Triangle triangle) {
    this.vertex = where;
    this.face = triangle;
    this.id = "" + ID_COUNTER++;
  }

  /**
   * Sets the pair of this half-edge, as well as informing <tt>pairEdge</tt> to point to this
   * half-edge. In other words, to establish a pairing between half-edges a and b, you only need to
   * do a.setPair(b), which takes care of b.setPair(a) for you.
   */
  public void setPair(HalfEdge pairEdge) {
    this.pair = pairEdge;
    if (pairEdge.getPair() != this) {
      pairEdge.setPair(this);
    }
  }

  public String toString() {
    String pairID = pair == null ? "?" : pair.id;
    String triID = face == null ? "?" : face.id;
    String nextID = next == null ? "?" : next.id;
    String ptID = vertex == null ? "?" : vertex.getID() + "";
    return "[Edge " + id + " (point: " + ptID + ", pair: " + pairID + ", face: " + triID
        + ", next: " + nextID + ")]";
  }

  /**
   * Returns the pair of this half-edge. The pair traverses the same two points, but on another
   * triangle, and it points in the opposite direction.
   */
  public HalfEdge getPair() {
    return pair;
  }

  public String getVertexOrderString() {
    String ret = "<null pair>";
    if (getPair() != null) {
      ret = getPair().getPoint().getID() + " -> " + getPoint().getID();
    }
    return ret;
  }
  
  /**
   * Returns the successor half-edge. If your triangle is well-formed, you should be able to call
   * getNext() on each successive half-edge and eventually get back to where you started.
   */
  public HalfEdge getNext() {
    return next;
  }

  /**
   * Sets the successor half-edge.
   */
  public void setNext(HalfEdge nextEdge) {
    this.next = nextEdge;
  }

  @SuppressWarnings("unused")
  private static void bug(String what) {
    Debug.out("HalfEdge", what);
  }

  /**
   * Returns the end point of this half-edge.
   */
  public Pt getPoint() {
    return vertex;
  }


  /**
   * Returns the start point of this half-edge. This is not a direct operation, since the half-edge
   * doesn't directly store it's starting point. It traverses each successive half-edge until it
   * finds the edge that points to this one. The end point of that edge is this edge's start point.
   */
  public Pt getStartPoint() {
    Pt ret = null;
    HalfEdge he = getPreviousEdge();
    if (he != null) {
      ret = he.getPoint();
    }
    return ret;
  }

  /**
   * Returns next.getNext(), or null if it can't be reached.
   */
  private HalfEdge getPreviousEdge() {
    HalfEdge ret = null;
    if (next != null && next.getNext() != null) {
      ret = next.getNext();
    }
    return ret;
  }

  /**
   * Sets the triangle for this half-edge.
   */
  public void setFace(Triangle t) {
    face = t;
  }

  /**
   * Returns the triangle this half-edge is related to.
   */
  public Triangle getFace() {
    return face;
  }

  public Set<Triangle> getTriangles() {
    Set<Triangle> ret = new HashSet<Triangle>();
    ret.add(getFace());
    if (getPair() != null) {
      ret.add(getPair().getFace());
    }
    return ret;
  }

  public String getId() {
    return id;
  }

  public void setBoundary(boolean b) {
    boundary = b;
  }
  
  public boolean isBoundary() {
    return boundary;
  }
}
