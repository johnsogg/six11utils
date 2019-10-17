// $Id$

package org.six11.util.adt;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * A simple adjacency table structure. This is similar to a graph, except this does not contain
 * methods for performing searches.
 **/
public class AdjacencyTable<K, V> {

  // This is implemented internally by keeping two independent maps of
  // entries, but users of this class don't need to worry about that.
  // 
  // Adding the relation (A, B, value1) means the two keymaps now look
  // thusly:
  //
  // key1Entries: [A, value1]
  // key2Entries: [B, value1]
  //
  // If we remove the relation A->B, we do it with remove(A, B), and
  // the keymaps end up both being empty. Note that this is not the
  // same as remove(B, A). The relation B->A is unrelated to A-B aside
  // from the fact that they have the same keys in opposing roles.
  //
  // The invariant is that for any relation A->B, the calls getKey1(B)
  // and getKey2(A) should both contain references to each other. You
  // should get this for free, so don't think too hard about this, but
  // do test it to make sure that is really the case.

  /**
   * A simple structure that nails together key1, key2, and val.
   */
  public static class Entry<K, V> {
    public K key1, key2;
    public V val;

    Entry(K key1, K key2, V val) {
      this.key1 = key1;
      this.key2 = key2;
      this.val = val;
    }
  }

  /**
   * Map relating key1 to entries.
   */
  protected Map<K, List<Entry<K, V>>> key1Entries;

  /**
   * Map relating key2 to entries.
   */
  protected Map<K, List<Entry<K, V>>> key2Entries;

  public AdjacencyTable() {
    this.key1Entries = new HashMap<K, List<Entry<K, V>>>();
    this.key2Entries = new HashMap<K, List<Entry<K, V>>>();
  }

  public Collection<List<Entry<K, V>>> getEntries() {
    return key1Entries.values();
  }

  /**
   * Adds a new entry to the table that indicates a directed relation key1 -> key2 with the
   * "edge value" provided. If there is already such a relation, this call is the same as calling
   * update() with the same arguments.
   */
  public void add(K key1, K key2, V value) {
    Entry<K, V> e = new Entry<K, V>(key1, key2, value);
    List<Entry<K, V>> valueList;
    if (!key1Entries.containsKey(key1)) {
      valueList = new ArrayList<Entry<K, V>>();
      key1Entries.put(key1, valueList);
    }
    valueList = key1Entries.get(key1);
    valueList.add(e);

    if (!key2Entries.containsKey(key2)) {
      valueList = new ArrayList<Entry<K, V>>();
      key2Entries.put(key2, valueList);
    }
    valueList = key2Entries.get(key2);
    valueList.add(e);
  }

  /**
   * Updates an existing entry that has the given keys, setting its value to the one provided. If
   * there is no such relation, this throws a java.util.NoSuchElementException.
   */
  public void update(K key1, K key2, V value) {
    /*
     * Dear The Gabe,
     * 
     * I just noticed something about this... in both the 'add' and this 'update' method, we made it
     * so that the Value Lists in key1Entries and key2Entries have Entry elements that have the same
     * key order. I mean if there is an relationship between 'A'(key1) and 'B'(key2) with a value
     * say 10, the two maps looks like this ( key : List )
     * 
     * A : {A,B,10} --> key1Entries with 'A' as the key. B : {A,B,10} --> key2Entries with 'B' as
     * the key.
     * 
     * It seems like this is okay, but I just thought I'd point it out because it seemed a little
     * strange to me, I thought maybe it should be like this:
     * 
     * A : {A,B,10} --> key1Entries B : {B,A,10} --> key2Entries
     * 
     * On to the update method... This was my thinking; I created a new 'Entry' with the arguments
     * provided. Then using the HashMap's .get(K) I got the List out of the Map--key1Entries.
     * Iterating over that loop, I'm checking for Entries with a .key2 value the same as the key2
     * arguemnt passed in. When it was found, I set that old Entry 'en' to be the new one 'e'. I
     * think that there should only be one such entry, so I had it break out after that. Then I had
     * it do the same thing for the the Value List in the key2Entries looking for a match on key1.
     */
    try {
      Entry<K, V> e = new Entry<K, V>(key1, key2, value);
      List<Entry<K, V>> el = key1Entries.get(key1);
      for (Entry<K, V> en : el) {
        if (en.key2 == key2) {
          en = e;
          break;
        }
      }
      el = key1Entries.get(key2);
      for (Entry<K, V> en : el) {
        if (en.key1 == key1) {
          en = e;
        }
      }
    } catch (java.util.NoSuchElementException noEntry) {
      noEntry.printStackTrace();
    }
  }

  /**
   * Returns the value associated with the given keys, or null if there is no such relation.
   */
  public V get(K key1, K key2) {
    V ret = null;
    List<Entry<K, V>> el = key1Entries.get(key1);
    for (Entry<K, V> en : el) {
      if (en.key2 == key2) {
        ret = en.val;
        break;
      }
    }
    return ret;
  }

  /**
   * Returns a list of keys that reference the given key. For example, if the adjacency table is
   * this:
   * 
   * A -> B A -> C B -> D E -> B
   * 
   * ... then a call to getKey1(B) returns the list [A, E] and a call to getKey2(B) returns the list
   * [D]. In the case when there are no related entries, this method returns an empty, non-null
   * List.
   */
  public List<K> getKey1(K key2) {
    List<K> ret = new ArrayList<K>();
    List<Entry<K, V>> refs = key2Entries.get(key2);
    // should have some way to handle a null List of Entries.
    for (Entry<K, V> en : refs)
      ret.add(en.key1);
    return ret;
  }

  /**
   * Returns a list of keys that the provided key points to. See getKey1.
   */
  public List<K> getKey2(K key1) {
    List<K> ret = new ArrayList<K>();
    List<Entry<K, V>> refs = key1Entries.get(key1);
    // should have some way to handle a null List of Entries.
    for (Entry<K, V> en : refs)
      ret.add(en.key2);
    return ret;
  }

  /**
   * Removes the relation key1 -> key2.
   */
  public void remove(K key1, K key2) {
    List<Entry<K, V>> refs = new ArrayList<Entry<K, V>>();
    if (key1Entries.containsKey(key1) && key2Entries.containsKey(key2)) {
      refs = key1Entries.get(key1);
      for (Entry<K, V> en : refs)
        if (en.key2 == key2)
          refs.remove(en);
      // if(!key2Entries.containsKey(key2)) {
      refs = key2Entries.get(key2);
      for (Entry<K, V> en : refs)
        if (en.key1 == key1)
          refs.remove(en);
    }
  }

  /**
   * Returns a shallow copy. This copies the keymaps into different objects, however the values are
   * the same objects in memory. To get a deep copy, first create a shallow copy, then iterate
   * through the entries and make a deep copy of the values.
   */
  public AdjacencyTable<K, V> copy() {
    AdjacencyTable<K, V> other = new AdjacencyTable<K, V>();
    for (List<Entry<K, V>> entries : getEntries()) {
      for (Entry<K, V> entry : entries) {
        other.add(entry.key1, entry.key2, entry.val);
      }
    }
    return other;
  }

  private boolean assertSymmetric(K key1, K key2) {
    boolean key1okay = false;
    boolean key2okay = false;
    List<Entry<K, V>> l = key1Entries.get(key1);
    if (l != null) {
      for (Entry<K, V> en : l) {
        if (en.key2 == key2) {
          key1okay = true;
          break;
        }
      }
    }

    l = key2Entries.get(key2);
    if (l != null) {
      for (Entry<K, V> en : l) {
        if (en.key1 == key1) {
          key2okay = true;
          break;
        }
      }
    }
    return (key1okay == key2okay);
  }

  @Test
  public void TestAdd() {
    AdjacencyTable<String, Integer> addTest = new AdjacencyTable<String, Integer>();
    addTest.add("A", "B", 2);
    addTest.add("A", "C", 1);
    addTest.add("B", "D", 3);
    addTest.add("C", "D", 4);
    addTest.add("C", "E", 5);
    addTest.add("E", "A", 6);

    addTest.update("C", "D", 8);
    addTest.update("E", "A", 10);

    addTest.get("E", "A");

    assertTrue(addTest.assertSymmetric("A", "B"));
    assertTrue(addTest.assertSymmetric("B", "D"));
    assertTrue(addTest.assertSymmetric("B", "E"));
    assertTrue(addTest.assertSymmetric("C", "D"));
    assertTrue(addTest.assertSymmetric("E", "A"));
    assertTrue(addTest.assertSymmetric("D", "B"));

  }
}
