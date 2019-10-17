// $Id: GraphTest.java 12 2009-11-09 22:58:47Z gabe.johnson $

package org.six11.util.adt;

import org.six11.util.adt.Graph.Node;
import org.six11.util.adt.Graph.Edge;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;

/**
 * This simple command line demo creates a small graph and outputs
 * textual information about it. This includes a summary of the nature
 * of edges (back/cycles, and so on), a graph adjacency structure, and
 * a listing of edge types.
 *
 * The graph itself is taken from my college algorithms textbook:
 * "Introduction to Algorithms" by Cormen, Leiserson, and Rivest, page
 * 479.
 **/
public class GraphTest {

  public static void main(String[] args) {
    new GraphTest().go();
  }

  public void go() {
    Graph graph = new Graph();
    
    Node u, v, w, x, y, z;

    graph.addNode(u = new Node("u"));
    graph.addNode(v = new Node("v"));
    graph.addNode(w = new Node("w"));
    graph.addNode(x = new Node("x"));
    graph.addNode(y = new Node("y"));
    graph.addNode(z = new Node("z"));
    
    graph.addEdge(new Edge(u, v, "u/v"));
    graph.addEdge(new Edge(u, x, "u/x"));
    graph.addEdge(new Edge(y, x, "y/x"));
    graph.addEdge(new Edge(w, y, "w/y"));
    graph.addEdge(new Edge(x, v, "x/v"));
    graph.addEdge(new Edge(v, y, "v/y"));
    graph.addEdge(new Edge(w, z, "w/z"));
    graph.addEdge(new Edge(z, z, "z/z"));

    Queue<Node> q = new LinkedList<Node>();
    q.offer(u);
    graph.bfs(q);
    graphDiagnostics(graph);

  }

  public static void graphDiagnostics(Graph graph) {

    System.out.println("Does the graph have...");
    System.out.println("  cycles (back edges)? " + graph.hasCycles());
    System.out.println("  forward edges? " + graph.hasForward());
    System.out.println("  cross edges? " + graph.hasCross());
    System.out.println("  tree edges? " + graph.hasTree());
    
    List<Node> tops = graph.findStartNodes();
    List<Node> all = graph.getNodes();
    List<Node> next;
    StringBuffer buf = new StringBuffer();
    for (Node n : tops) {
      buf.append(n + " ");
    }
    System.out.println();
    System.out.println("Graph Adjacency: (top nodes are " + 
		       buf.toString().trim() + ")");
    for (Node n : all) {
      next = graph.findNextNodes(n);
      buf.setLength(0);
      for (Node m : next) {
	buf.append(m + " ");
      }
      System.out.println("  " + n + " | " + buf);
    }

    System.out.println();
    System.out.println("Edges: ");
    List<Edge> edges = graph.getEdges();
    String kind;
    for (Edge e : edges) {
      kind = "?";
      if (e.isKnown()) {
	if (e.isTree()) {
	  kind = "tree";
	} else if (e.isBack()) {
	  kind = "back";
	} else if (e.isForward()) {
	  kind = "forward";
	} else if (e.isCross()) {
	  kind = "cross";
	}
      }
      System.out.println("  " + e + " (a " + kind + " edge)");
    }
  }

}
