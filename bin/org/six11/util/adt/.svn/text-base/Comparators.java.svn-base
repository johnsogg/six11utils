// $Id$

package org.six11.util.adt;

import java.util.Comparator;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public abstract class Comparators {

  public static Comparator<String> STRING_AS_NUMBERS = new Comparator<String>() {
    public int compare(String o1, String o2) {
      Integer intA = new Integer(o1);
      Integer intB = new Integer(o2);
      return intA.compareTo(intB);
    }
  };
  
  public static Comparator<String> STRING_REVERSE = new Comparator<String>() {
    public int compare(String o1, String o2) {
      return o2.compareTo(o1);
    }
  };
}
