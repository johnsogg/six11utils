// $Id$

package org.six11.util.pen;

import java.awt.event.MouseEvent;
import java.util.EventObject;

import org.six11.util.Debug;

/**
 * A Pen Event. Possible types are taps and drags, as well as idle (indicating the pen is no longer
 * being used and the stroke, tap, or selection is done), and flow selection.
 * 
 * The constructor for PenEvent is private. You must use one of the factory methods to build your
 * pen events.
 **/
public class PenEvent extends EventObject {

  public enum Type {
    Flow, Tap, Drag, Idle, Down, Enter, Exit, Hover
  }

  private Type type; // type of event (on of the constants above)

  // various interesting points related to the pen event
  private Pt pt; // most recent pen location
  private Pt ptPrevious; // previous pen location (e.g. for drawing)
  private Pt ptFlow; // flow selection center point

  private MouseEvent mouseEvent; // the AWT mouse event that started this

  private long timestamp; // timestamp of when the event was created

  private int fsPhase; // number of times the flow selection has been

  // entered since last idle.

  private PenEvent(Object source) {
    super(source);
    timestamp = System.currentTimeMillis();
  }

  public MouseEvent getMouseEvent() {
    return mouseEvent;
  }

  /**
   * Returns the event type.
   */
  public Type getType() {
    return type;
  }

  /**
   * Returns the location the pen was at when the event was fired.
   */
  public Pt getPt() {
    return pt;
  }

  /**
   * In the case of a 'drag' event, this is the location that the pen was in right before it's
   * current location.
   */
  public Pt getPtPrevious() {
    return ptPrevious;
  }

  /**
   * In the case of a 'flow' or 'drag' event, this is the current center of the flow operation. When
   * you are dragging, this is the center of the region that you must stay in for some period of
   * time in order to begin flow selection.
   */
  public Pt getPtFlow() {
    return ptFlow;
  }

  /**
   * The timestamp in milliseconds of when the event was generated.
   */
  public long getTimestamp() {
    return timestamp;
  }

  /**
   * Returns the number of times a unique flow selection phase has been entered. For example if you
   * push the pen down and start dragging, the flow selection phase is zero.
   */
  public int getFlowSelectionPhase() {
    return fsPhase;
  }

  public static PenEvent buildDownEvent(Object source, Pt pt) {
    return buildDownEvent(source, pt, null);
  }

  public static PenEvent buildDownEvent(Object source, Pt pt, MouseEvent ev) {
    PenEvent ret = new PenEvent(source);
    ret.type = Type.Down;
    ret.pt = pt;
    ret.mouseEvent = ev;
    return ret;
  }

  /**
   * Make a new flow selection event for Type.Flow.
   */
  public static PenEvent buildFlowEvent(Object source, Pt pt, int fsPhase, Pt flowPt, MouseEvent ev) {
    PenEvent ret = new PenEvent(source);
    ret.type = Type.Flow;
    ret.pt = pt;
    ret.ptFlow = flowPt;
    ret.fsPhase = fsPhase;
    ret.mouseEvent = ev;
    return ret;
  }

  /**
   * Make a new flow selection event for Type.Tap.
   */
  public static PenEvent buildTapEvent(Object source, Pt pt, MouseEvent ev) {
    PenEvent ret = new PenEvent(source);
    ret.type = Type.Tap;
    ret.pt = pt;
    ret.mouseEvent = ev;
    return ret;
  }

  public static PenEvent buildTapEvent(Object source, Pt pt) {
    return buildTapEvent(source, pt, null);
  }

  /**
   * Make a new flow selection event for Type.Drag.
   */
  public static PenEvent buildDragEvent(Object source, Pt pt, Pt ptPrevious, int fsPhase,
      Pt flowPt, MouseEvent ev) {
    PenEvent ret = new PenEvent(source);
    ret.type = Type.Drag;
    ret.pt = pt;
    ret.ptPrevious = ptPrevious;
    ret.ptFlow = flowPt;
    ret.fsPhase = fsPhase;
    ret.mouseEvent = ev;
    return ret;
  }

  public static PenEvent buildDragEvent(Object source, Pt pt) {
    return buildDragEvent(source, pt, null, 0, null, null);
  }

  public static PenEvent buildHoverEvent(Object source, MouseEvent ev) {
    PenEvent ret = buildHoverEvent(source, new Pt(ev));
    ret.mouseEvent = ev;
    return ret;
  }

  public static PenEvent buildHoverEvent(Object source, Pt pt) {
    PenEvent ret = new PenEvent(source);
    ret.type = Type.Hover;
    ret.pt = pt;
    return ret;

  }

  /**
   * Make a new flow selection event for Type.Idle.
   */
  public static PenEvent buildIdleEvent(Object source, MouseEvent ev) {
    PenEvent ret = new PenEvent(source);
    ret.type = Type.Idle;
    ret.pt = null;
    ret.ptPrevious = null;
    ret.ptFlow = null;
    ret.mouseEvent = ev;
    return ret;
  }
  
  public static PenEvent buildIdleEvent(Object source, Pt pt) {
    PenEvent ret = new PenEvent(source);
    ret.type = Type.Idle;
    ret.pt = pt;
    ret.ptPrevious = null;
    ret.ptFlow = null;
    return ret;
  }

  public static PenEvent buildExitEvent(Object source, MouseEvent ev) {
    PenEvent ret = new PenEvent(source);
    ret.type = Type.Exit;
    ret.mouseEvent = ev;
    return ret;
  }
  
  public static PenEvent buildExitEvent(Object source, Pt pt) {
    PenEvent ret = new PenEvent(source);
    ret.type = Type.Exit;
    ret.pt = pt;
    ret.mouseEvent = null;
    return ret;
  }

  public static PenEvent buildEnterEvent(Object source, MouseEvent ev) {
    PenEvent ret = new PenEvent(source);
    ret.type = Type.Enter;
    ret.mouseEvent = ev;
    return ret;
  }
  
  public static PenEvent buildEnterEvent(Object source, Pt pt) {
    PenEvent ret = new PenEvent(source);
    ret.type = Type.Enter;
    ret.pt = pt;
    ret.mouseEvent = null;
    return ret;
  }

  /**
   * Returns a nicely formatted debugging string.
   */
  public String toString() {
    return "[PenEvent " + type + " pt: " + Debug.num(pt) + " prevPt: " + Debug.num(ptPrevious)
        + " flowPt: " + Debug.num(ptFlow) + " fsPhase: " + Debug.num(fsPhase) + "]";
  }

}
