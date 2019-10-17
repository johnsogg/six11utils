// $Id: FrontEnd.java 178 2011-10-28 18:44:17Z gabe.johnson@gmail.com $

package org.six11.util.layout;

import org.six11.util.adt.Graph;
import org.six11.util.adt.Graph.Edge;
import org.six11.util.adt.Graph.EdgeCallback;
import org.six11.util.adt.Graph.Node;
import org.six11.util.adt.Graph.NodeCallback;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

/**
 * FrontEnd is a layout manager/container that is designed to work with an XML input file that
 * describes the relative locations of various components.
 * 
 * Note that XML is not directly supported in this class (because in many situations you don't want
 * to have to package JDOM up with your app just to use this layout manager). If you want to use
 * XML, use FrontEndXML, which is a subclass of this.
 * 
 * To use this class, first add your components to using add(Component, String), where the String is
 * the name that will forever be used to identify the component you just added. Example:
 * 
 * <pre>
 * fe.add(left, &quot;Left&quot;);
 * fe.add(mid, &quot;Mid&quot;);
 * fe.add(right, &quot;Right&quot;);
 * </pre>
 * 
 * Next (and it is important that this happens afterwards), you must give the FrontEnd a list of
 * rules concerning how things are related. You can do this in one of two ways:
 * 
 * One way is to do it manually with the addRule() methods. Example:
 * 
 * <pre>
 * fe.addRule(FrontEnd.ROOT, FrontEnd.E, &quot;Right&quot;, FrontEnd.E, 4);
 * fe.addRule(&quot;Left&quot;, FrontEnd.E, &quot;Mid&quot;, FrontEnd.W);
 * </pre>
 * 
 * The first line means "Wherever Root's east anchor is, make Right's east anchor be 4 pixels away."
 * (Note that the pixel distance is in the direction of the relation, so if we were going from south
 * to north, this would mean 4 pixels above). The second line means "Wherever Left's east anchor is,
 * make Mid's west anchor line up with it exactly."
 * 
 * The other way to assign relationship rules is to use an XML file. Use the FrontEndXML class for
 * this. Like the manual method, the names that you gave your components in the add() section will
 * be used here. To use an XML file, call either
 * 
 * <pre>
 * load(String)
 * </pre>
 * 
 * or
 * 
 * <pre>
 * load(InputStream)
 * </pre>
 * 
 * . Here is a snippet of XML that is equivalent to the addRule statements above:
 * 
 * <pre>
 *  &lt;frontend&gt;
 *    &lt;rule parent=&quot;ROOT&quot; parentAnchor=&quot;E&quot; child=&quot;Right&quot; childAnchor=&quot;East&quot; offset=&quot;4&quot; /&gt;
 *    &lt;rule parent=&quot;Left&quot; parentAnchor=&quot;E&quot; child=&quot;Mid&quot;   childAnchor=&quot;West&quot; /&gt;
 *  &lt;/frontend&gt;
 * </pre>
 * 
 * The benefit to using the XML file is that you can use an external tool to work out the layout (or
 * directly edit the XML). This helps keep your GUI code cleaner, and it also means you don't have
 * to recompile anything when you want to tweak the layout.
 */
public class FrontEnd extends JComponent {
  // block is put here to avoid compiler 'illegal forward reference'
  protected static final String CARDINAL_N = "N";
  protected static final String CARDINAL_E = "E";
  protected static final String CARDINAL_W = "W";
  protected static final String CARDINAL_S = "S";

  /**
   * Name of the root component.
   */
  public static final String ROOT = "ROOT";

  /**
   * Anchor for referring to the bottom (south) of some component.
   */
  public static final Anchor S = Anchor.makeAnchor("South", CARDINAL_S, CARDINAL_N);

  /**
   * Anchor for referring to the horizontal center of some component.
   */
  public static final Anchor H = Anchor.makeAnchor("Horizontal Center", "H", "H");

  /**
   * Anchor for referring to the vertical center of some component.
   */
  public static final Anchor V = Anchor.makeAnchor("Vertical Center", "V", "V");

  /**
   * Anchor for referring to the right (east) of some component.
   */
  public static final Anchor E = Anchor.makeAnchor("East", CARDINAL_E, CARDINAL_W);

  /**
   * Anchor for referring to the left (west) of some component.
   */
  public static final Anchor W = Anchor.makeAnchor("West", CARDINAL_W, CARDINAL_E);

  /**
   * Anchor for referring to the top (north) of some component.
   */
  public static final Anchor N = Anchor.makeAnchor("North", CARDINAL_N, CARDINAL_S);
  protected Object debugID;
  protected boolean inLayout = false;
  protected Map<String, Component> map; // 
  protected Map<String, Node> named; //  Key is (name + anchor.toString()),
                                     // eg "ROOTS"
  protected Map<String, Box> boxes; // 
  protected Graph graph;
  protected Graph vertPrefGraph;
  protected Graph horizPrefGraph;
  protected boolean computePref;
  protected Dimension preferredSize;
  protected boolean debugging;
  protected List<ResizeListener> resizeListeners;

  /**
   * Resets named anchors.
   */
  protected NodeCallback onWhite = new NodeCallback() {
    public void run(Node n) {
      NamedAnchor na = (NamedAnchor) n.data;

      if (!inLayout && !na.name.equals(ROOT)) {
        na.coordinate = 0;
      }
    }
  };

  /**
   * Called when an edge cycle is detected.
   */
  protected EdgeCallback onCycle = new EdgeCallback() {
    public void run(Edge e) {
    }
  };

  /**
   * Called when there is a collision in parsing and a second look is in order. This may indicate a
   * problem, but it may be OK too.
   */
  protected EdgeCallback onPotentialOverconstraint = new EdgeCallback() {
    public void run(Edge e) {
      if (inLayout) {
        traverseEdgeForLayout(e);
      } else {
        traverseEdgeForCorrectness(e);
      }
    }
  };

  /**
   * Create a new FrontEnd layout with no constraints or components. See the constructor with the
   * debugID for more info.
   */
  public FrontEnd() {
    this("FrontEnd Layout");
  }

  /**
   * Create a new FrontEnd with a debugging ID. The other constructor defers to this.
   */
  protected FrontEnd(Object debugID) {
    this.debugID = debugID;
    this.map = new HashMap<String, Component>(); // 
    this.named = new HashMap<String, Node>(); // 
    this.boxes = new HashMap<String, Box>(); // 
    boxes.put(ROOT, new Box(ROOT));
    makeNode(ROOT, N);
    makeNode(ROOT, S);
    makeNode(ROOT, W);
    makeNode(ROOT, E);
    graph = new Graph();
    setLayout(new FrontEndLayout());
    resizeListeners = new ArrayList<ResizeListener>();
  }

  public void setDebugging(boolean debugging) {
    this.debugging = debugging;
  }

  public void addResizeListener(ResizeListener listener) {
    if (!resizeListeners.contains(listener)) {
      resizeListeners.add(listener);
    }
  }

  public void removeResizeListener(ResizeListener listener) {
    resizeListeners.remove(listener);
  }

  /**
   * Like the other addRule methods, but instead of using a direct reference to anchors, you provide
   * the anchor's name.
   */
  public void addRule(String parentName, String parentAnchorStr, String childName,
      String childAnchorStr, int offset) {
    addRule(parentName, Anchor.findAnchor(parentAnchorStr), childName,
        Anchor.findAnchor(childAnchorStr), offset);
  }

  /**
   * Constrains two named components anchors so that they are joined with no space between them.
   */
  public void addRule(String parentName, Anchor parentAnchor, String childName, Anchor childAnchor) {
    addRule(parentName, parentAnchor, childName, childAnchor, 0);
  }

  /**
   * Constrains two named components so that they are joined with some amount of offset between
   * them. <b>Note</b> that the components to which you are referring should have already been added
   * to the layout using the add(Component, String) method.
   */
  public void addRule(String parentName, Anchor parentAnchor, String childName, Anchor childAnchor,
      int offset) {
    if (!boxes.containsKey(parentName)) {
      boxes.put(parentName, new Box(parentName));
    }

    if (!boxes.containsKey(childName)) {
      boxes.put(childName, new Box(childName));
    }

    Node parent = makeNode(parentName, parentAnchor);
    graph.addNode(parent); // no effect if already there

    Node child = makeNode(childName, childAnchor);
    graph.addNode(child);

    Edge parEdge = makeEdge(parent, child, false, offset);
    graph.addEdge(parEdge);

    Node complement = makeComplement(child);

    if (!graph.containsEdge(complement, child)) {
      graph.addNode(complement);

      Edge compEdge = makeEdge(child, complement, true, offset);
      graph.addEdge(compEdge);
    }

    invalidate();
    validate();
  }

  protected void traverseEdgeForLayout(Edge e) {
    NamedAnchor parentNA = (NamedAnchor) e.a.data;
    NamedAnchor childNA = (NamedAnchor) e.b.data;
    EdgeData edgeData = (EdgeData) e.data;
    int dist = edgeData.getDistance(1.0);

    boolean wrong = isWrongWay(parentNA, childNA, edgeData);

    if (wrong) {
      dist *= -1;
    }

    childNA.coordinate = parentNA.coordinate + dist;
    childNA.prefCoordinate = parentNA.prefCoordinate + dist;

    if (boxes.containsKey(childNA.name)) {
      ((Box) boxes.get(childNA.name)).set(childNA.anchor, childNA.coordinate);
    }
  }

  protected boolean isWrongWay(NamedAnchor p, NamedAnchor c, EdgeData d) {
    boolean ret = false;

    if (d.isDerived()) {
      // derived edges that go S->N or E->W are 'wrong way' and should
      // be interpreted as negative
      ret = p.anchor.equals(S) || p.anchor.equals(E);
    } else if (p.name.equals(ROOT) && ((p.anchor.equals(S)) || p.anchor.equals(E))) {
      ret = true;
    } else if (!p.name.equals(c.name)) {
      // explicit edges for distinct boxes that go p.N->c.S or
      // p.W->c.E are 'wrong way'
      ret = ((p.anchor.equals(N) && c.anchor.equals(S)) || (p.anchor.equals(W) && c.anchor
          .equals(E)));
    }

    return ret;
  }

  protected void traverseEdgeForCorrectness(Edge e) {
    List<Edge> entering = graph.findEdgesEntering(e.b);
    boolean oneExplicit = false;
    Edge f;

    for (Iterator<Edge> it = entering.iterator(); it.hasNext();) {
      f = (Edge) it.next();

      if (!((EdgeData) f.data).isDerived()) {
        oneExplicit = true;

        break;
      }
    }

    if (oneExplicit) {
      for (Iterator<Edge> it = entering.iterator(); it.hasNext();) {
        f = (Edge) it.next();

        if (((EdgeData) f.data).isDerived()) {
          graph.removeEdge(f);
        }
      }
    }

    if (computePref) {
      NamedAnchor a = (NamedAnchor) e.a.data;
      NamedAnchor b = (NamedAnchor) e.b.data;
      EdgeData dist = (EdgeData) e.data;
      Graph g = dist.isVertical() ? vertPrefGraph : horizPrefGraph;
      PrefData start = makePrefData(a, g);
      PrefData end = makePrefData(b, g);

      if (!start.equals(end)) {
        PrefData edge = new PrefData();
        edge.name = "edge between " + start.name + " and " + end.name;
        edge.dist = dist.getDistance(1.0);
        edge.sameAnchor = a.anchor.equals(b.anchor);

        Component comp = (Component) map.get(end.name);

        if (dist.isVertical()) {
          end.dist = comp.isVisible() ? comp.getPreferredSize().height : 0;
        } else {
          end.dist = comp.isVisible() ? comp.getPreferredSize().width : 0;
        }

        Node sn = findOrMakePrefNode(start, g);
        Node en = findOrMakePrefNode(end, g);
        Edge ee = new Edge(sn, en, edge);
        ee.setAction(Graph.TREE, new TreeCB());
        ee.setAction(Graph.BACK, new BackCB());
        g.addNode(sn);
        g.addNode(en);
        g.addEdge(ee);
      }
    }
  }

  /**
   * Used during preferred-size computation.
   */
  protected Node findOrMakePrefNode(PrefData pd, Graph g) {
    Node ret = null;
    List<Node> list = g.getNodes(pd);

    if (list.size() == 0) {
      ret = new Node(pd);

      NodeCallback nodeCB = new NodeCB();
      ret.setAction(Graph.GRAY, nodeCB);
    } else if (list.size() == 1) {
      ret = (Node) list.get(0);
    }

    return ret;
  }

  protected PrefData makePrefData(NamedAnchor a, Graph g) {
    PrefData pd = new PrefData();
    PrefData ret = null;

    if (a.name.equals(ROOT)) {
      if (a.anchor.equals(N)) {
        pd.name = CARDINAL_N;
      } else if (a.anchor.equals(S)) {
        pd.name = CARDINAL_S;
      } else if (a.anchor.equals(W)) {
        pd.name = CARDINAL_W;
      } else if (a.anchor.equals(E)) {
        pd.name = CARDINAL_E;
      }
    } else {
      pd.name = a.name;
    }

    List<Node> list = g.getNodes(pd);

    if (list.size() == 0) {
      ret = pd;
    } else if (list.size() == 1) {
      ret = (PrefData) ((Node) list.get(0)).data;
    }

    return ret;
  }

  public static Dimension parseDimension(String wwXhh) {
    Dimension ret = new Dimension(0, 0);

    try {
      ret.width = Integer.parseInt(wwXhh.substring(0, wwXhh.indexOf("x")));
      ret.height = Integer.parseInt(wwXhh.substring(wwXhh.indexOf("x") + 1));
    } catch (Exception ex) {
    }

    return ret;
  }

  public static Color parseColor(String rrggbb) {
    Color ret = new Color(0, 0, 0);
    String val;

    if (rrggbb.startsWith("#")) {
      val = rrggbb.substring(1);
    } else {
      val = rrggbb;
    }

    if (val.length() != 6) {

    } else {
      try {
        int r = Integer.parseInt(val.substring(0, 2), 16);
        int g = Integer.parseInt(val.substring(2, 4), 16);
        int b = Integer.parseInt(val.substring(4, 6), 16);
        ret = new Color(r, g, b);
      } catch (Exception ex) {
      }
    }

    return ret;
  }

  /**
   * Debugging method; returns something like "(42,611)".
   */
  public static String point(Point p) {
    return "(" + p.x + "," + p.y + ")";
  }

  /**
   * Debugging method; returns something like "425x392"
   */
  public static String dim(Dimension d) {
    return (d == null) ? "null dimension" : (d.width + "x" + d.height);
  }

  /**
   * Debugging method; returns something like "(100,240) 425x392"
   */
  public static String rect(Rectangle r) {
    return "(" + r.x + "," + r.y + ") " + r.width + "x" + r.height;
  }

  /**
   * Returns the graph used to hold the bulk of the component's size and location data.
   */
  protected Graph getGraph() {
    return graph;
  }

  /**
     *
     */
  protected Edge makeEdge(Node a, Node b, boolean derived, int offset) {
    Edge ret;
    NamedAnchor parentNA = (NamedAnchor) a.data;

    if (derived) {
      ret = new Edge(a, b, new EdgeData((Component) map.get(parentNA.name), parentNA.isVertical()));
    } else {
      ret = new Edge(a, b, new EdgeData(offset, parentNA.isVertical()));
    }

    ret.setAction(Graph.BACK, onCycle);
    ret.setAction(Graph.FORWARD, onPotentialOverconstraint);
    ret.setAction(Graph.CROSS, onPotentialOverconstraint);
    ret.setAction(Graph.TREE, onPotentialOverconstraint);

    return ret;
  }

  protected Node makeNode(String name, Anchor anchor) {
    Node ret = getNode(name, anchor);

    if (ret == null) {
      String key = (name + anchor);
      NamedAnchor na = new NamedAnchor(name, anchor);
      ret = new Node(na);
      ret.setAction(Graph.WHITE, onWhite);
      named.put(key, ret);
    }

    return ret;
  }

  protected Node getNode(String name, Anchor anchor) {
    return (Node) named.get(name + anchor);
  }

  protected Node makeComplement(Node n) {
    NamedAnchor na = (NamedAnchor) n.data;
    NamedAnchor compl = na.getComplement();

    return makeNode(compl.name, compl.anchor);
  }

  public static class ResizeListener {
    public void componentPreferredSizeChanged() {
    }
  }

  /**
   * Used during preferred-size computation.
   */
  class TreeCB extends EdgeCallback {
    public void run(Edge e) {
      // one is gray, one is white. If the gray node is not a
      // cardinal, copy the gray one's running value to the white.
      Node gray = ((e.a.getState() == Graph.GRAY) ? e.a : e.b);
      Node white = ((e.a.getState() == Graph.WHITE) ? e.a : e.b);
      PrefData edgePD = (PrefData) e.data;
      PrefData grayPD = (PrefData) gray.data;
      PrefData whitePD = (PrefData) white.data;

      if (!grayPD.isCardinal()) {
        whitePD.runningValue = grayPD.runningValue;
      }

      if (edgePD.sameAnchor) {
        // thisOut("Wait, back up " + grayPD.dist + " px");
        whitePD.runningValue -= grayPD.dist;
      }

    }
  }

  class BackCB extends EdgeCallback {
    public void run(Edge e) {
      // both are gray. The 'younger one' has a more recent discovery
      // time. If younger's running value is greater than older's
      // running value, set older's equal to younger's.
      Node young = ((e.a.getDiscovered() > e.b.getDiscovered()) ? e.a : e.b);
      Node old = ((e.a.getDiscovered() <= e.b.getDiscovered()) ? e.a : e.b);
      PrefData youngPD = (PrefData) young.data;
      PrefData oldPD = (PrefData) old.data;

      if (youngPD.runningValue > oldPD.runningValue) {
        oldPD.runningValue = youngPD.runningValue;
      }

    }
  }

  class NodeCB extends NodeCallback {
    public void run(Node n) {
      PrefData d = (PrefData) n.data;
      d.runningValue += d.dist;
    }
  }

  /**
   * Package-private class for use during preferred-size computation.
   */
  class PrefData {
    String name;
    int dist;
    int runningValue;
    boolean sameAnchor;

    public boolean equals(Object other) {
      boolean ret = false;

      if (other instanceof PrefData) {
        ret = name.equals(((PrefData) other).name);
      }

      return ret;
    }

    public String toString() {
      if (dist == 0) {
        return name;
      } else {
        return name + " (" + ((dist > 0) ? (dist + "px") : "") + ")";
      }
    }

    boolean isCardinal() {
      return name.equals(CARDINAL_N) || name.equals(CARDINAL_E) || name.equals(CARDINAL_W)
          || name.equals(CARDINAL_S);
    }
  }

  /**
   * Package-private class that implements the LayoutManager2 interface.
   */
  class FrontEndLayout implements LayoutManager2 {
    /**
     * Deferrs to addLayoutComponent(Component, String)
     */
    public void addLayoutComponent(String name, Component comp) {
      addLayoutComponent(comp, name);
    }

    /**
     * Not implemented, since removing components from a layout that is based on the relationships
     * between components would bring the world crashing down. If you need to simulate removing a
     * component, use a Holder.
     */
    public void removeLayoutComponent(Component comp) {
    }

    /**
     * Returns the preferred dimensions for this layout for all the visible components contained in
     * the parent container.
     * 
     * This essentially equates the the preferred size of all the visible components within the
     * layout, plus whatever padding may be specified between them, plus insets surrounding the
     * parent.
     */
    public Dimension preferredLayoutSize(Container parent) {
      if (preferredSize == null) {
        performLayout(parent, false);
      }

      return preferredSize;
    }

    /**
     * returns preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container parent) {
      return preferredLayoutSize(parent);
    }

    /**
     * Puts each visible component contained in the parent in its place.
     */
    public void layoutContainer(Container parent) {
      performLayout(parent, true);
    }

    /**
     * Used during preferred-size and for layout itself. When doing just preferred-size computing,
     * set applyBounds to false in order to avoid changing the positions/sizes of components.
     */
    protected void performLayout(Container parent, boolean applyBounds) {
      // thisOut("Laying out " + parent);
      // thisOut("I have " + map.size() + " nodes: " + map);
      int prefWidth = (preferredSize == null) ? (-1) : preferredSize.width;
      int prefHeight = (preferredSize == null) ? (-1) : preferredSize.height;
      boolean fireResize = false;

      Dimension ps = parent.getSize();
      Box b;

      for (Iterator<Box> it = boxes.values().iterator(); it.hasNext();) {
        b = (Box) it.next();
        b.clear();
      }

      ((Box) boxes.get(ROOT)).set(N, 0);
      ((Box) boxes.get(ROOT)).set(W, 0);
      ((Box) boxes.get(ROOT)).set(S, ps.height);
      ((Box) boxes.get(ROOT)).set(E, ps.width);

      ((NamedAnchor) ((Node) named.get(ROOT + N)).data).coordinate = 0;
      ((NamedAnchor) ((Node) named.get(ROOT + W)).data).coordinate = 0;
      ((NamedAnchor) ((Node) named.get(ROOT + S)).data).coordinate = ps.height;
      ((NamedAnchor) ((Node) named.get(ROOT + E)).data).coordinate = ps.width;

      if ((preferredSize == null) || (vertPrefGraph == null) || (horizPrefGraph == null)) {
        vertPrefGraph = new Graph();
        horizPrefGraph = new Graph();
        computePref = true;
      }

      graph.bfs(new LinkedList<Node>(graph.findStartNodes()));

      if (computePref) {
        vertPrefGraph.dfs();

        int vMax = extractMaxCardinal(vertPrefGraph);
        horizPrefGraph.dfs();

        int hMax = extractMaxCardinal(horizPrefGraph);
        preferredSize = new Dimension(hMax, vMax);
        fireResize = ((hMax != prefWidth) || (vMax != prefHeight));

      }

      computePref = false;

      inLayout = true;

      try {
        graph.dfs(graph.findStartNodes());

        if (applyBounds) {
          for (Iterator<Box> it = boxes.values().iterator(); it.hasNext();) {
            b = (Box) it.next();

            if (!b.name.equals(ROOT) && b.isDone()) {
              Component c = (Component) map.get(b.name);
              c.setBounds(b.getBounds());
            }
          }
        }
      } finally {
        inLayout = false;
      }

      if (fireResize && (resizeListeners.size() > 0)) {
        ResizeListener rl;

        for (Iterator<ResizeListener> it = resizeListeners.iterator(); it.hasNext();) {
          rl = (ResizeListener) it.next();
          rl.componentPreferredSizeChanged();
        }
      }
    }

    /**
     * Returns the largest PrefData.runningValue among all of the cardinal directions in the given
     * graph. Each graph should only contain N/S directions or E/W directions, but not both.
     */
    protected int extractMaxCardinal(Graph sizeGraph) {
      int max = 0;
      Node node;
      PrefData pd;

      for (Iterator<Node> it = sizeGraph.getNodes().iterator(); it.hasNext();) {
        node = it.next();
        pd = (PrefData) node.data;

        if (pd.isCardinal()) {
          max = Math.max(max, pd.runningValue);
        }
      }

      return max;
    }

    /**
     * Associates the name (must be a String to be effective) with the component. When a container
     * is layed out, this name will be used to find the component.
     */
    public void addLayoutComponent(Component comp, Object constraint) {
      if (constraint instanceof String) {
        String name = (String) constraint;
        map.put(name, comp);
      }
    }

    /**
     * There is no limit to the size of a FrontEnd other than what can fit into an int.
     */
    public Dimension maximumLayoutSize(Container target) {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Returns 0.5f, indicating it should be horizontally centered within it's parent.
     */
    public float getLayoutAlignmentX(Container target) {
      return 0.5f;
    }

    /**
     * Returns 0.5f, indicating it should be vertically centered within it's parent.
     */
    public float getLayoutAlignmentY(Container target) {
      return 0.5f;
    }

    /**
     * De-caches information related to layout so it is forced to compute things from scratch.
     */
    public void invalidateLayout(Container target) {
      vertPrefGraph = null;
      horizPrefGraph = null;
      preferredSize = null;
    }
  }

  public static class Box {
    private String name;
    private int n;
    private int s;
    private int w;
    private int e;
    private Rectangle bounds;

    protected Box(String name) {
      this.name = name;
    }

    protected void clear() {
      n = -1;
      s = -1;
      w = -1;
      e = -1;
      bounds = null;
    }

    protected void set(Anchor anchor, int coordinate) {
      if (anchor.equals(N)) {
        n = coordinate;
      } else if (anchor.equals(S)) {
        s = coordinate;
      } else if (anchor.equals(W)) {
        w = coordinate;
      } else if (anchor.equals(E)) {
        e = coordinate;
      }

      bounds = null;
    }

    protected boolean isDone() {
      return (n >= 0) && (s >= 0) && (e >= 0) && (w >= 0);
    }

    public boolean isRoot() {
      return name.equals(ROOT);
    }

    public Rectangle getBounds() {
      if (bounds == null) {
        bounds = new Rectangle(w, n, (e - w), (s - n));
      }

      return bounds;
    }

    public String getName() {
      return name;
    }
  }

  protected static class EdgeData {
    private boolean derived;
    private int distance;
    private Component comp;
    private boolean vert;
    private double lastMultiplier = -1.0;

    protected EdgeData(Component comp, boolean vert) {
      this.derived = true;
      this.comp = comp;
      this.vert = vert;
    }

    protected EdgeData(int distance, boolean vert) {
      this.derived = false;
      this.distance = distance;
      this.vert = vert;
    }

    protected int getPrefDistance(double multiplier) {
      if (comp != null) {
        Dimension dim = comp.getPreferredSize();

        return (int) (multiplier * (vert ? dim.height : dim.width));
      } else {
        return distance;
      }
    }

    protected int getDistance(double multiplier) {
      if ((multiplier != lastMultiplier) && derived) {
        Dimension dim = comp.getPreferredSize();
        distance = (int) (multiplier * (vert ? dim.height : dim.width));
      }

      return distance;
    }

    protected boolean isDerived() {
      return derived;
    }

    protected boolean isVertical() {
      return vert;
    }

    public String toString() {
      return "" + getDistance(1.0);
    }

    public void addDistance(int distance) {
      this.distance += distance;
    }

    public static boolean linksRoot(Edge e) {
      return (((NamedAnchor) e.a.data).isRoot() || ((NamedAnchor) e.b.data).isRoot());
    }
  }

  protected static class NamedAnchor {
    String name;
    Anchor anchor;
    NamedAnchor complement;
    int coordinate;
    int prefCoordinate;
    boolean vertical; // true if N/S, false otherwise

    NamedAnchor(String name, Anchor anchor) {
      this.name = name;
      this.anchor = anchor;
      vertical = (anchor.equals(N) || anchor.equals(V) || anchor.equals(S));
    }

    protected boolean isRoot() {
      return name.equals(ROOT);
    }

    // /**
    // * If the natural flow of a-> is backwards (going right ot left or
    // * down to up) this returns true.
    // */
    // protected boolean isBackwards(Node a, Node b) {
    // return false;
    // }

    /**
     * If the connection between 'this' and 'other' goes from N to S or W to E this returns true.
     * Otherwise returns false.
     */
    protected boolean isNSWE(NamedAnchor other) {
      return ((anchor.equals(N) && other.anchor.equals(S)) || (anchor.equals(W) && other.anchor
          .equals(E)));
    }

    protected boolean isVertical() {
      return vertical;
    }

    protected boolean isHorizontal() {
      return !vertical;
    }

    public boolean equals(Object other) {
      boolean ret = false;

      if (other instanceof NamedAnchor) {
        NamedAnchor a = (NamedAnchor) other;
        ret = name.equals(a.name) && anchor.equals(a.anchor);
      }

      return ret;
    }

    protected NamedAnchor getComplement() {
      if (complement == null) {
        complement = new NamedAnchor(name, anchor.getComplement());
      }

      return complement;
    }

    public String toString() {
      return name + "." + anchor;
    }
  }

  public static class Anchor {
    private static Map<String, Anchor> anchors = new HashMap<String, Anchor>(); // 
    String name;
    String shortName;
    String complementShortName;

    private Anchor(String name, String shortName, String complementShortName) {
      this.name = name;
      this.shortName = shortName;
      this.complementShortName = complementShortName;
    }

    protected static Anchor makeAnchor(String name, String shortName, String complementShortName) {
      Anchor ret = null;

      if (anchors.containsKey(shortName)) {
        ret = (Anchor) anchors.get(shortName);
      } else {
        ret = new Anchor(name, shortName, complementShortName);
        anchors.put(shortName, ret);
      }

      return ret;
    }

    protected Anchor getComplement() {
      return (Anchor) anchors.get(complementShortName);
    }

    protected static Anchor findAnchor(String shortName) {
      return (Anchor) anchors.get(shortName);
    }

    public String toString() {
      return shortName;
    }

    public boolean equals(Object other) {
      boolean ret = false;

      if (other instanceof Anchor) {
        ret = ((Anchor) other).name.equals(name);
      }

      return ret;
    }

    public int hashCode() {
      return name.hashCode();
    }
  }
}
