// $Id$

package org.six11.util.gui;

import java.awt.BasicStroke;
import java.util.HashMap;
import java.util.Map;

/**
 * This provides a number of static Stroke implementations for convenience.
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 * 
 */
public abstract class Strokes {

  private static Map<Float, BasicStroke> simpleStrokes = new HashMap<Float, BasicStroke>();
  /**
   * A thick dashed line to go around the outside of a big component.
   */
  public final static BasicStroke DASHED_BORDER_STROKE = new BasicStroke(3f, // pen thickness
      BasicStroke.CAP_BUTT, // cap
      BasicStroke.JOIN_MITER, // join
      1f, // miter limit
      new float[] {
          7, 7
      }, // dash
      7); // dash phase

  public final static BasicStroke BOLD_STROKE = new BasicStroke(5f, // pen thickness
      BasicStroke.CAP_ROUND, // CAP
      BasicStroke.JOIN_ROUND, // JOIN
      1f); // miter limit
  
  public final static BasicStroke MEDIUM_STROKE = new BasicStroke(3.5f, // pen thickness
      BasicStroke.CAP_ROUND, // CAP
      BasicStroke.JOIN_ROUND, // JOIN
      1f); // miter limit

  public static final BasicStroke THIN_STROKE = new BasicStroke(2f, // pen thickness
      BasicStroke.CAP_BUTT, // CAP
      BasicStroke.JOIN_MITER, // JOIN
      1f); // miter limit
  
  public static final BasicStroke VERY_THIN_STROKE = new BasicStroke(1f, // pen thickness
      BasicStroke.CAP_BUTT, // CAP
      BasicStroke.JOIN_MITER, // JOIN
      1f); // miter limit
  
  public static final BasicStroke THIN_DASHED_STROKE = new BasicStroke(2f, // pen thickness
      BasicStroke.CAP_BUTT, // cap
      BasicStroke.JOIN_MITER, // join
      1f, // miter limit
      new float[] {
          3.5f, 3.5f
      }, // dash
      3.5f); // dash phase)

  public static BasicStroke get(float thickness) {
    if (!simpleStrokes.containsKey(thickness)) {
      simpleStrokes.put(thickness, new BasicStroke(thickness, BasicStroke.CAP_ROUND,
          BasicStroke.JOIN_ROUND, 1f));
    }
    return simpleStrokes.get(thickness);
  }
}
