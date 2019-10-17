package org.six11.util.adt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class SetMap<K, V> {

  public static void main(String[] args) {
    SetMap<Thing, String> sm = new SetMap<Thing, String>();
    Set<Thing> empty = new HashSet<Thing>();
    Set<Thing> ab = new HashSet<Thing>();
    Set<Thing> a = new HashSet<Thing>();
    Set<Thing> bc = new HashSet<Thing>();
    Set<Thing> abc = new HashSet<Thing>();
    Set<Thing> notIncluded = new HashSet<Thing>();
    Set<Thing> unrelated = new HashSet<Thing>();

    ab.add(new Thing("a"));
    ab.add(new Thing("b"));

    a.add(new Thing("a"));

    bc.add(new Thing("b"));
    bc.add(new Thing("c"));

    abc.add(new Thing("a"));
    abc.add(new Thing("b"));
    abc.add(new Thing("c"));

    notIncluded.add(new Thing("a"));
    notIncluded.add(new Thing("b"));

    unrelated.add(new Thing("z"));
    unrelated.add(new Thing("b"));

    sm.put(bc, "Green");
    sm.put(ab, "Dreams");
    sm.put(empty, "Empty");
    sm.put(a, "Sleep");
    sm.put(abc, "Furiously");

    if (sm.containsKey(bc)) {
      System.out.println("It contains bc. Value should be Green: " + sm.get(bc));
    } else {
      System.out.println("FAIL: bc");
    }
    if (sm.containsKey(ab)) {
      System.out.println("It contains ab. Value should be Dreams: " + sm.get(ab));
    } else {
      System.out.println("FAIL: ab");
    }
    if (sm.containsKey(a)) {
      System.out.println("It contains a. Value should be Sleep: " + sm.get(a));
    } else {
      System.out.println("FAIL: a");
    }
    if (sm.containsKey(abc)) {
      System.out.println("It contains abc. Value should be Furiously: " + sm.get(abc));
    } else {
      System.out.println("FAIL: abc");
    }
    if (sm.containsKey(empty)) {
      System.out.println("It contains empty. Value should be Empty: " + sm.get(empty));
    } else {
      System.out.println("FAIL: empty");
    }
    if (sm.containsKey(notIncluded)) {
      System.out.println("It contains notIncluded. Value should be Dreams: " + sm.get(notIncluded));
    } else {
      System.out.println("FAIL: empty");
    }
    if (sm.containsKey(unrelated)) {
      System.out.println("FAIL: unrelated");
    } else {
      System.out.println("It did not contain unrelated, nor should it have.");
    }

    Set<Thing> abfg = new HashSet<Thing>();
    abfg.add(new Thing("a"));
    abfg.add(new Thing("b"));
    abfg.add(new Thing("f"));
    abfg.add(new Thing("g"));
    sm.put(abfg, "Huzzah");

    Set<Thing> abf = new HashSet<Thing>();
    abf.add(new Thing("a"));
    abf.add(new Thing("b"));
    abf.add(new Thing("f"));

    if (sm.containsKey(abfg)) {
      System.out.println("It contains abfg. Value should be Huzzah: " + sm.get(abfg));
    } else {
      System.out.println("FAIL: abfg");
    }

    if (sm.containsKey(abf)) {
      System.out.println("FAIL: abf.");
    } else {
      System.out.println("It did not contain abf, nor should it have.");
    }

    sm.put(ab, "Once I was Dreams but now I am this sundry line.");
    System.out.println(sm.get(ab));

    System.out.println("There are " + sm.keySet().size() + " keys.");
    for (Set<Thing> keys : sm.keySet()) {
      StringBuilder buf = new StringBuilder();
      for (Thing t : keys) {
        buf.append(t + " ");
      }
      System.out.println("Key:" + buf.toString());
    }
    
    SetMap<Thing, String> sm2 = new SetMap<Thing, String>();
    System.out.println("A newly made setmap has " + sm2.keySet().size() + " keys.");
    sm2.put(new HashSet<Thing>(), "nothing.");
    System.out.println("Inserted one thing, it now has " + sm2.keySet().size() + " keys.");
  }

  Node root;
  private Comparator<? super K> comparator = new Comparator<K>() {

    public int compare(K o1, K o2) {
      int ret = 0;
      if (o1.hashCode() < o2.hashCode()) {
        ret = 1;
      } else if (o1.hashCode() > o2.hashCode()) {
        ret = -1;
      }
      return ret;
    }

  };

  public SetMap() {
    root = new Node(null);
  }

  public V get(Set<K> set) {
    List<K> inOrder = new ArrayList<K>(set);
    Collections.sort(inOrder, comparator);
    Node n = root.search(inOrder);
    V ret = null;
    if (n != null && n.valid) {
      ret = n.data;
    }
    return ret;
  }

  public boolean containsKey(Set<K> set) {
    List<K> inOrder = new ArrayList<K>(set);
    Collections.sort(inOrder, comparator);
    Node n = root.search(inOrder);
    return n != null && n.valid;
  }

  public void put(Set<K> set, V v) {
    List<K> inOrder = new ArrayList<K>(set);
    Collections.sort(inOrder, comparator);
    root.insert(inOrder, v);
  }

  public Set<Set<K>> keySet() {
    Set<Set<K>> ret = new HashSet<Set<K>>();
    Stack<Node> toTop = new Stack<Node>();
    toTop.push(root);
    root.collect(ret, toTop);
    if (root.valid) {
      ret.add(new HashSet<K>());
    }
    return ret;
  }

  class Node {

    List<K> paths; // paths correspond with childNodes, index-wise.
    List<Node> childNodes;
    K myKey;
    V data;
    boolean valid;

    public Node(K myKey) {
      this.paths = new ArrayList<K>();
      this.childNodes = new ArrayList<Node>();
      this.myKey = myKey;
      this.data = null;
      this.valid = false;
    }

    public Node search(List<K> inOrder) {
      Node ret = null;
      if (inOrder.size() == 0) {
        ret = this;
      } else {
        K next = inOrder.remove(0);
        if (paths.contains(next)) {
          Node nextNode = childNodes.get(paths.indexOf(next));
          ret = nextNode.search(inOrder);
        }
      }
      return ret;
    }

    public void collect(Set<Set<K>> leaves, Stack<Node> toTop) {
      if (valid) {
        Set<K> lineage = new HashSet<K>();
        for (Node n : toTop) {
          if (n.myKey != null) {
            lineage.add(n.myKey);
          }
        }
        leaves.add(lineage);
      }
      for (Node child : childNodes) {
        toTop.push(child);
        child.collect(leaves, toTop);
        toTop.pop();
      }
    }

    public void insert(List<K> inOrder, V value) {
      if (inOrder.size() == 0) {
        data = value;
        valid = true;
      } else {
        K next = inOrder.remove(0);
        if (paths.contains(next)) {
          childNodes.get(paths.indexOf(next)).insert(inOrder, value);
        } else {
          // no path found--create it.
          Node newNode = new Node(next);
          childNodes.add(newNode);
          paths.add(next);
          newNode.insert(inOrder, value);
        }
      }
    }
  }
}

class Thing {
  String name;

  Thing(String n) {
    name = n;
  }

  public String toString() {
    return "Thing: " + name;
  }

  public int hashCode() {
    return name.hashCode();
  }

  public boolean equals(Object o) {
    return (o instanceof Thing && ((Thing) o).name.equals(name));
  }
}