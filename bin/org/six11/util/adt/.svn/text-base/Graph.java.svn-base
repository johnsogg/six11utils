// $Id$

package org.six11.util.adt;

import java.util.List;
import java.util.Queue;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * An extendable Graph datastructure. This provides a starting point for a host of graph
 * datastructure applications, such as scheduling algorithms, decision trees, resource conflict
 * resolution, etc. It also may make you better looking.
 * 
 * The code here is a translation of the algorithms described in "Introduction to Algorithms" by
 * Cormen, Leiserson, and Rivest (aka "Big Red"). I've also added my own special sauce--callbacks (a
 * la Runnables) that you can use to follow the state of progress.
 **/
public class Graph {

  public final static int STATE_NONE = 0;
  public final static int STATE_BFS = 1;
  public final static int STATE_DFS = 2;

  public final static int WHITE = 0;
  public final static int GRAY = 1;
  public final static int BLACK = 2;

  public final static int UNKNOWN = 0;
  public final static int TREE = 1;
  public final static int BACK = 2;
  public final static int FORWARD = 3;
  public final static int CROSS = 4;

  // these contain all the nodes and edges
  protected List<Node> nodes;
  protected List<Edge> edges;

  // tells you if the graph edges are directed or undirected
  protected boolean directed;

  // search state indicates what the last search was, which tells you
  // what information you may be able to find out. a STATE_* value.
  transient protected int searchState = STATE_NONE;

  // if edgeDataValid is true, this tells you if the graph has ...
  transient protected boolean cycles;
  transient protected List<Edge> cross;
  transient protected boolean forward;
  transient protected boolean tree;

  // this tells us if the value of the above edge data vars can be
  // trusted. if not, we have to directly look at the set of nodes.
  transient protected boolean edgeDataValid;

  transient protected List<Node> startNodes;

  /**
   * Create a directed graph.
   */
  public Graph() {
    nodes = new ArrayList<Node>();
    edges = new ArrayList<Edge>();
    directed = true;
  }

  public void setDirected(boolean directed) {
    this.directed = directed;
  }

  public abstract static class NodeCallback {
    public abstract void run(Node n);
  }

  public static class Node {
    public Object data;

    private int state = WHITE;

    private int d = 0;
    private int f = 0;

    private Node p;
    private boolean child;

    // NodeCallbacks to invoke when entering a state.
    private Map<Integer, NodeCallback> actions;

    public Node(Object data) {
      this.data = data;
      this.actions = new HashMap<Integer, NodeCallback>();
    }

    protected boolean isVisited() {
      return state > WHITE;
    }

    protected void setState(int state) {
      if (this.state == state || state < WHITE || state > BLACK) {
        return;
      }
      this.state = state;
      // Debug.out("Graph", "Changed state of " + this + "#" + hashCode() + " to " + getStateStr());
      if (state == WHITE) {
        d = 0;
        f = 0;
        p = null;
        child = false;
      }
      if (actions.get(state) != null) {
        actions.get(state).run(Node.this);
      }
    }

    public int getState() {
      return state;
    }

    public String getStateStr() {
      String ret = "UNKNOWN";
      switch (state) {
        case WHITE:
          ret = "White";
          break;
        case GRAY:
          ret = "Gray";
          break;
        case BLACK:
          ret = "Black";
          break;
      }
      return ret;
    }

    public int getDistance() {
      return d;
    }

    public int getDiscovered() {
      return d;
    }

    public int getFinished() {
      return f;
    }

    protected void setDistance(int d) {
      this.d = d;
    }

    protected void setDiscovered(int t) {
      d = t;
    }

    protected void setFinished(int t) {
      f = t;
    }

    public Node getPredecessor() {
      return p;
    }

    protected void setPredecessor(Node p) {
      this.p = p;
      // Debug.out("Graph", "Changed parent of " + this + "#" + hashCode() + " to " + p);
    }

    protected void setChild() {
      child = true;
    }

    protected boolean isChild() {
      return child;
    }

    protected boolean isAncestor(Node a) {
      boolean ret = false;
      if (a != null && p != null) {
        ret = a.equals(p) || p.isAncestor(a);
      }
      return ret;
    }

    public void setAction(int state, NodeCallback cb) {
      actions.put(state, cb);
    }

    public boolean equals(Object other) {
      boolean ret = false;
      if (other instanceof Node) {
        Node n = (Node) other;
        ret = data.equals(n.data);
      }
      return ret;
    }

    public String toString() {
      return (data == null ? super.toString() : data.toString());
    }
  }

  public abstract static class EdgeCallback {
    public abstract void run(Edge e);
  }

  public static class Edge {
    public Node a;
    public Node b;

    public Object data;
    private int mode;
    private Map<Integer, EdgeCallback> actions;

    public Edge(Node a, Node b, Object data) {
      this.a = a;
      this.b = b;
      this.data = data;
      actions = new HashMap<Integer, EdgeCallback>();
    }

    public void setAction(int state, EdgeCallback cb) {
      actions.put(state, cb);
    }

    public boolean equals(Object other) {
      boolean ret = false;
      if (other instanceof Edge) {
        Edge e = (Edge) other;
        ret = (a.equals(e.a) && b.equals(e.b) && data.equals(e.data));
      }
      return ret;
    }

    protected void setMode(int mode) {
      if (this.mode != mode) {
        this.mode = mode;
        if (actions.get(mode) != null) {
          actions.get(mode).run(Edge.this);
        }
      }
    }

    public int getMode() {
      return mode;
    }

    public String getModeStr() {
      String ret = "UNKNOWN";
      switch (mode) {
        case FORWARD:
          ret = "Forward";
          break;
        case TREE:
          ret = "Tree";
          break;
        case BACK:
          ret = "Back";
          break;
        case CROSS:
          ret = "Cross";
          break;

      }
      return ret;
    }

    public boolean isKnown() {
      return mode != UNKNOWN;
    }

    public boolean isTree() {
      return mode == TREE;
    }

    public boolean isBack() {
      return mode == BACK;
    }

    public boolean isForward() {
      return mode == FORWARD;
    }

    public boolean isCross() {
      return mode == CROSS;
    }

    public String toString() {
      String ns = a + "->" + b + " ";
      return (data == null ? ns + super.toString() : ns + data.toString());
    }
  }

  public void addNode(Node n) {
    if (!nodes.contains(n)) {
      nodes.add(n);
      clearState();
    }
  }

  public void removeNode(Node n) {
    if (nodes.contains(n)) {
      nodes.remove(n);
      clearState();
    }
  }

  /**
   * Tells you if this graph contains a node whose state and data are both equal (== or equals(..))
   * to the given Node.
   */
  public boolean containsNode(Node n) {
    return nodes.contains(n);
  }

  public boolean containsEdge(Edge e) {
    return edges.contains(e);
  }

  public boolean containsEdge(Node a, Node b) {
    boolean ret = false;
    for (Edge e : edges) {
      if (e.a.equals(a) && e.b.equals(b) || (!directed && e.b.equals(a) && e.a.equals(b))) {
        ret = true;
        break;
      }
    }
    return ret;
  }

  public void addEdge(Edge e) {
    if (!edges.contains(e)) {
      edges.add(e);
      clearState();
    }
  }

  public void removeEdge(Edge e) {
    if (edges.contains(e)) {
      edges.remove(e);
      clearState();
    }
  }

  protected void clearState() {
    searchState = STATE_NONE;
    edgeDataValid = false;
    startNodes = null;
  }

  public void bfs(Queue<Node> q) {
    clearState();
    for (Node n : nodes) {
      n.setState(q.contains(n) ? GRAY : WHITE);
    }
    for (Edge e : edges) {
      e.setMode(UNKNOWN);
    }
    for (Node n : q) {
      n.setDistance(0);
      n.setPredecessor(null);
    }
    while (!q.isEmpty()) {
      Node n = q.remove();
      List<Node> out = findNextNodes(n);
      for (Node m : out) {
        Edge e = findEdge(n, m);
        if (e != null) {
          if (m.getState() == WHITE) {
            e.setMode(TREE);
          } else if (m.getState() == GRAY) {
            e.setMode(BACK);
          }
        }
        if (!m.isVisited()) {
          m.setDistance(n.getDistance() + 1);
          m.setPredecessor(n);
          m.setState(GRAY);
          q.offer(m);
        }
      }
      n.setState(BLACK);
    }
    searchState = STATE_BFS;
  }

  public void dfs(List<Node> these) {
    clearState();
    for (Node n : nodes) {
      n.setState(WHITE);
      n.setPredecessor(null);
    }
    for (Edge e : edges) {
      e.setMode(UNKNOWN);
    }
    int time = 0;
    time = dfs(null, these, time);
    searchState = STATE_DFS;
  }

  public void dfs() {
    dfs(nodes);
  }

  protected int dfs(Node p, List<Node> out, int time) {
    for (Node n : out) {
      Edge e = findEdge(p, n);
      if (e != null) {
        n.setChild();
      }
      if (n.getState() == WHITE) {
        if (e != null) {
          e.setMode(1);
        }
        n.setPredecessor(p);
        time = dfsVisit(n, ++time);
      } else if (p != null && n.getState() == GRAY && e != null) {
        e.setMode(2);
      } else if (p != null && e != null) {
        if (n.isAncestor(p)) {
          e.setMode(3);
        } else {
          e.setMode(4);
        }
      } else if (e != null && e.getMode() == 0) {
        System.out.println(" --- ERROR  ===========---------");
        System.out.println(" - n.state: " + n.getState());
        System.out.println(" - e: " + e);
        System.out.println(" - p: " + p);
        System.out.println(" ----------============---------");
      }
    }
    return time;
  }

  protected int dfsVisit(Node n, int time) {
    n.setState(GRAY);
    n.setDiscovered(++time);
    List<Node> out = findNextNodes(n);
    dfs(n, out, time);
    n.setState(BLACK);
    n.setFinished(++time);
    return time;
  }

  public List<Node> findNextNodes(Node n) {
    List<Node> ret = new ArrayList<Node>();
    for (Edge e : edges) {
      if (e.a.equals(n)) {
        ret.add(e.b);
      }
      if (!directed && e.b.equals(n)) {
        ret.add(e.a);
      }
    }
    return ret;
  }

  public Edge findEdge(Node a, Node b) {
    Edge ret = null;
    if (a != null && b != null) {
      for (Edge e : edges) {
        if (e.a.equals(a) && e.b.equals(b)) {
          ret = e;
          break;
        } else if (!directed && e.a.equals(b) && e.b.equals(a)) {
          ret = e;
          break;
        }
      }
    }
    return ret;
  }

  public List<Edge> findEdgesEntering(Node n) {
    List<Edge> ret = new ArrayList<Edge>();
    for (Edge e : edges) {
      if (e.b.equals(n)) {
        ret.add(e);
      }
    }
    return ret;
  }

  public List<Node> getNodes() {
    return nodes;
  }

  public List<Node> getNodes(Object data) {
    List<Node> ret = new ArrayList<Node>();
    for (Node n : nodes) {
      if (n.data.equals(data)) {
        ret.add(n);
      }
    }
    return ret;
  }

  public List<Edge> getEdges() {
    return edges;
  }

  public List<Edge> getCrossEdges() {
    computeEdgeData();
    return cross;
  }

  public List<Node> findStartNodes() {
    List<Node> ret;
    if (searchState != STATE_DFS) {
      dfs();
      ret = findStartNodes();
    } else {
      if (startNodes == null) {
        startNodes = new ArrayList<Node>();
        for (Node n : nodes) {
          if (!n.isChild()) {
            startNodes.add(n);
          }
        }
      }
      ret = startNodes;
    }
    return ret;
  }

  public boolean hasCycles() {
    computeEdgeData();
    return cycles;
  }

  public boolean hasForward() {
    computeEdgeData();
    return forward;
  }

  public boolean hasCross() {
    computeEdgeData();
    return !cross.isEmpty();
  }

  public boolean hasTree() {
    computeEdgeData();
    return tree;
  }

  protected void computeEdgeData() {
    if (searchState != STATE_DFS) {
      dfs();
    }
    if (edgeDataValid)
      return;
    cycles = tree = forward = false;
    cross = new ArrayList<Edge>();
    for (Edge e : edges) {
      if (e.isBack()) {
        cycles = true;
      }
      if (e.isTree()) {
        tree = true;
      }
      if (e.isForward()) {
        forward = true;
      }
      if (e.isCross()) {
        cross.add(e);
      }
    }
    edgeDataValid = true;
  }
}
