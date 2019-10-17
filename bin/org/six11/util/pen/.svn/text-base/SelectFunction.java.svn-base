// $Id$

package org.six11.util.pen;

/**
 * An interface for a hunk of code that takes care of selecting points
 * along a sequence. 
 */
public interface SelectFunction {

  /**
   * This method is called one time per selection process. The calling
   * code should have established the basic distance from the
   * epicenter to each point along the sequence. The augmentDistance
   * function is your big chance to do something fancy with the
   * 'selection distance' attribute on each Pt. For example, say you
   * wanted selection to move more slowly over a curved area. You
   * could add some amount to the distance at each point to make it
   * seem as though those points are farther away than they are. The
   * provided point should be on the sequence. Also remember that if
   * you make point P[i] some distance d further away, P[i+1] should
   * also be moved at least distance d away.
   */
  public void augmentDistance(Sequence seq, Pt pointOnSequence);

  /**
   * Selects each point the given sequence as a function of each
   * point's "selection distance" value and the "maxDistance" value
   * provided here. Fully selected points will have "selection
   * strength" attribute values of 1.0, while completely unselected
   * points have 0.0.
   */
  public void select(Sequence seq, double maxDistance);

  /**
   * Turns off selection. Some requirements: on exit, "selection
   * strength" is zero, and "selection drawn" has been removed.
   */
  public void deselect(Sequence seq);
}
