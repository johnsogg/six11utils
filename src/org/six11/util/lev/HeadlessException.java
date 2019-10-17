// $Id: HeadlessException.java 12 2009-11-09 22:58:47Z gabe.johnson $

package org.six11.util.lev;

/**
 * An exception noting that there is no graphical environment available, or
 * that the graphical component (view) of a module is not available.
 */
public class HeadlessException extends Exception {
  /**
   * Simply calls the superconstructor.
   */
  public HeadlessException() {
    super();
  }
}
