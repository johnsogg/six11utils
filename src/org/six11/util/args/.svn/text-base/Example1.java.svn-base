// $Id$
package org.six11.util.args;

/**
 * Very simple demonstration of the Arguments class. This shows how you can quickly start using
 * Arguments without needing to configure anything.
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class Example1 {

  public static void main(String[] args) {
    Arguments a = new Arguments(args);
    if (a.hasFlag("foo")) {
      System.out.println("You provided the 'foo' flag.");
    } else {
      System.out.println("Maybe try passing in the 'foo' flag.");
    }
    if (a.hasValue("foo")) {
      System.out.println("Huzzah! You provided a value for foo: " + a.getValue("foo"));
    } else {
      System.out.println("You can assign foo a value like this: --foo=blahblah");
    }
  }

}
