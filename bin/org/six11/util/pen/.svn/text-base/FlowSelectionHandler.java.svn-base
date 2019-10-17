// $Id$

package org.six11.util.pen;

import javax.swing.Timer;

import org.six11.util.data.FSM;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.Component;

import java.util.List;
import java.util.ArrayList;

/**
 * FlowSelectionHandler is an adapter that converts MouseEvents into PenEvents. To use this class,
 * simply create a FlowSelectionHandler instance and hook it up to any AWT component with the
 * listenTo(Component) method.
 * 
 * Then, register a PenListener to that instance of FlowSelectionHandler. Your PenListener will
 * receive the various types of pen events (including flow selection) without having to know
 * anything about a mouse.
 * 
 * There is a drawn representation of this FSM on page 91 of my personal notebook if you want to see
 * what it looks like.
 **/
public class FlowSelectionHandler {

  /**
   * The default timeout for transitioning into a flow selection phase.
   */
  public final static int DEFAULT_FS_TIMEOUT = 900;

  /**
   * When in a flow selection phase, flow events will by default be generated at this rate (in
   * milliseconds).
   */
  public final static int DEFAULT_FS_TICK = 45;

  /**
   * The default distance that must be moved in order to initiate a move. Consider the "speed limit"
   * for determining if you are flow selecting or drawing ink to be the movement threshold divided
   * by the flow selection timeout.
   */
  public final static double DEFAULT_MOVE_THRESHOLD = 10.0;

  /**
   * The default timeout in milliseconds for determining if a down/up sequence should be considered
   * a tap. To be a tap, the pen must not move more than moveThreshold and it must not remain down
   * for longer than tapTimeout (which defaults to DEFAULT_TAP_TIMEOUT).
   */
  public final static int DEFAULT_TAP_TIMEOUT = 200;

  private int fsTick = DEFAULT_FS_TICK;
  private int tapTimeout = DEFAULT_TAP_TIMEOUT;
  private double moveThreshold = DEFAULT_MOVE_THRESHOLD;
  private FSM fsm;
  private ActionListener timeoutTimerTarget;
  private ActionListener tickTimerTarget;
  private Timer timeoutTimer;
  private Timer tickTimer;
  private int fsPhase;

  private Pt flowPt; // location flow selection will use to judge movement
  private Pt ptOldest; // the older of the most two recent points
  private Pt ptNewest; // the newer of the most two recent points
  private Pt ptDown; // the location of the last pendown

  private int[] fsTimeout;

  private List<PenListener> penListeners;

  private ScaleSource scaleSource;

  /**
   * Make a new flow selection handler with the default flow selection timeout.
   */
  public FlowSelectionHandler() {
    this(new int[] {
      DEFAULT_FS_TIMEOUT
    });
  }

  /**
   * Make a new flow selection handler with the given flow selection timeouts.
   */
  public FlowSelectionHandler(int[] timeouts) {

    fsTimeout = timeouts;

    fsm = new FSM("Flow Selection Handler");

    timeoutTimerTarget = new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        fsm.addEvent("timeout");
      }
    };
    tickTimerTarget = new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        fire(PenEvent.buildFlowEvent(this, ptNewest, fsPhase, flowPt, null));
      }
    };
    timeoutTimer = new Timer(getCurrentTimeout(), timeoutTimerTarget);
    tickTimer = new Timer(fsTick, tickTimerTarget);

    fsm.addState("Idle", new Runnable() {
      public void run() {
        fsPhase = 0;
        tickTimer.stop();
        timeoutTimer.stop();
        timeoutTimer.setInitialDelay(getCurrentTimeout());
        if (ptDown != null && ptNewest != null && ptDown.distance(ptNewest) < moveThreshold
            && (ptNewest.getTime() - ptDown.getTime()) < tapTimeout) {

          fire(PenEvent.buildTapEvent(this, ptDown, null));
        }
        ptDown = null;
        fire(PenEvent.buildIdleEvent(this, ptNewest));
      }
    }, null, null);
    fsm.addState("Init Draw", new Runnable() {
      public void run() {
        resetDrawingMode();
      }
    }, null, null);
    fsm.addState("Draw Ink");
    fsm.addState("Init FS", new Runnable() {
      public void run() {
        initFS();
      }
    }, null, null);
    fsm.addState("FS Check", new Runnable() {
      public void run() {
        boolean tooFar = checkFlowSelectionBubble();
        if (tooFar) {
          fsm.addEvent("moved too far");
        } else {
          fsm.addEvent("not move too far");
        }
      }
    }, null, null);
    fsm.addState("FS Idle");

    FSM.Transition trans;
    trans = new FSM.Transition("down", "Idle", "Init Draw");
    fsm.addTransition(trans);
    trans = new FSM.Transition("drag", "Init Draw", "Draw Ink") {
      public void doBeforeTransition() {
        drawInk();
      }
    };
    fsm.addTransition(trans);
    trans = new FSM.Transition("drag", "Draw Ink", "Draw Ink") {
      public void doBeforeTransition() {
        drawInk();
      }
    };
    fsm.addTransition(trans);
    fsm.addTransition(new FSM.Transition("timeout", "Init Draw", "Init FS"));
    fsm.addTransition(new FSM.Transition("timeout", "Draw Ink", "Init FS"));
    fsm.addTransition(new FSM.Transition("drag", "Init FS", "FS Check"));
    fsm.addTransition(new FSM.Transition("moved too far", "FS Check", "Init Draw"));
    fsm.addTransition(new FSM.Transition("not move too far", "FS Check", "FS Idle"));
    fsm.addTransition(new FSM.Transition("drag", "FS Idle", "FS Check"));
  }

  public void setScaleSource(ScaleSource src) {
    scaleSource = src;
  }

  public ScaleSource getScaleSource() {
    return scaleSource;
  }

  /**
   * Establishes a connection between the given component and this flow selection handler. This is
   * necessary to turn mouse events from the given component into pen events from this handler.
   */
  public void listenTo(Component comp) {
    MouseThing mt = new FSMouseThing();
    comp.addMouseListener(mt);
    comp.addMouseMotionListener(mt);
  }

  /**
   * Sets the flow selection timeouts in milliseconds. Every time the user enters a flow selection
   * phase, a counter is incremented. This counter corresponds to the indexes of the values below.
   * For example, the first timeout value for the first flow selection phase is timeout[0], the
   * second is timeout[1], and so on. When the array runs out of values, the last value is used for
   * the rest of the flow selection phases.
   * 
   * This is useful if you want to have a short timeout for the first incarnation, but longer
   * timeouts for the future. You can essentially turn off flow selection by setting a value to
   * Integer.MAX_VALUE.
   */
  public void setTimeouts(int[] timeouts) {
    fsTimeout = timeouts;
  }

  /**
   * Gives you the current flow selection timeouts (in milliseconds) as documented in setTimeouts.
   * By default this contains a single entry, DEFAULT_FS_TIMEOUT, which as of the time I write this
   * comment is 900.
   */
  public int[] getTimeouts() {
    return fsTimeout;
  }

  /**
   * Adds a timeout to the end of the current list of timeouts (in milliseconds).
   */
  public void addTimeout(int t) {
    int[] n = new int[fsTimeout.length + 1];
    for (int i = 0; i < fsTimeout.length; i++) {
      n[i] = fsTimeout[i];
    }
    n[n.length - 1] = t;
    fsTimeout = n;
  }

  /**
   * Sets the number of milliseconds that flow selection events are generated.
   */
  public void setFlowSelectionTick(int fsTick) {
    this.fsTick = fsTick;
  }

  /**
   * Gives you the number of milliseconds between elapsed flow selection events during a flow
   * selection phase. By default this is DEFAULT_MOVE_THRESHOLD.
   */
  public int getFlowSelectionTick() {
    return fsTick;
  }

  /**
   * Sets the movement threshold in pixels that determines if the pen is being held still or not.
   */
  public void setMoveThreshold(double moveThreshold) {
    this.moveThreshold = moveThreshold;
  }

  /**
   * Gives you the movement threshold in pixels that determines if the pen is being held still or
   * not. By default this is DEFAULT_MOVE_THRESHOLD.
   */
  public double getMoveThreshold() {
    return moveThreshold;
  }

  private void initFS() {
    fsPhase++;
    flowPt = ptNewest;
    timeoutTimer.stop();
    timeoutTimer.setInitialDelay(getCurrentTimeout());
    tickTimer.restart();
  }

  private int getCurrentTimeout() {
    int ret = 0;
    if (fsPhase < fsTimeout.length) {
      ret = fsTimeout[fsPhase];
    } else {
      ret = fsTimeout[fsTimeout.length - 1];
    }
    return ret;
  }

  private void resetDrawingMode() {
    flowPt = ptNewest;
    timeoutTimer.restart();
    tickTimer.stop();
  }

  private void drawInk() {
    boolean tooFar = checkFlowSelectionBubble();
    if (tooFar) {
      resetDrawingMode();
    } else {
      fire(PenEvent.buildDragEvent(this, ptNewest, ptOldest, fsPhase, flowPt, null));
    }
  }

  /**
   * Consider flowPt to be the center of a bubble of radius moveThreshold, with ptNewest the current
   * pen location. If ptNewest is outside of the bubble, the pen is moving fast enough that a flow
   * selection is not in progress. This returns true if ptNewest and flowPt both exist and they are
   * farther apart than moveThreshold.
   */
  private boolean checkFlowSelectionBubble() {
    boolean ret = false;
    if (ptNewest != null && flowPt != null) {
      if (ptNewest.distance(flowPt) > moveThreshold) {
        ret = true;
      }
    }
    return ret;
  }

  private void pushPt(Pt pt) {
    if (scaleSource != null) {
      pt.scale(scaleSource.getScaleFactor());
    }
    ptOldest = ptNewest;
    ptNewest = pt;
  }

  private class FSMouseThing extends MouseThing {

    public void mousePressed(MouseEvent ev) {
      pushPt(new Pt(ev));
      ptDown = ptNewest;
      fsm.addEvent("down");
    }

    public void mouseReleased(MouseEvent ev) {
      pushPt(new Pt(ev));
      fsm.setState("Idle");
    }

    public void mouseDragged(MouseEvent ev) {
      pushPt(new Pt(ev));
      fsm.addEvent("drag");
    }
  }

  /**
   * Adds a PenListener to this. Note: duplicates won't be registered.
   */
  public void addPenListener(PenListener pl) {
    if (penListeners == null) {
      penListeners = new ArrayList<PenListener>();
    }
    if (!penListeners.contains(pl)) {
      penListeners.add(pl);
    }
  }

  /**
   * Removes the given pen listener (if it is registered in the first place).
   */
  public void removePenListener(PenListener pl) {
    if (penListeners != null) {
      penListeners.remove(pl);
    }
  }

  /**
   * Removes all pen listeners.
   */
  public void removeAllPenListeners() {
    if (penListeners != null) {
      penListeners.clear();
    }
  }

  /**
   * Loops through all pen listeners and hands off the given pen event to the listener's
   * handlePenEvent method.
   */
  protected void fire(PenEvent ev) {
    if (penListeners != null) {
      for (PenListener pl : penListeners) {
        pl.handlePenEvent(ev);
      }
    }
  }

}
