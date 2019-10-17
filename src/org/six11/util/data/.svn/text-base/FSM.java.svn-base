// $Id: FSM.java 12 2009-11-09 22:58:47Z gabe.johnson $

package org.six11.util.data;

import org.six11.util.Debug;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * A programmable Finite State Machine implementation. To use this class, establish any number of
 * states with the 'addState' method. Next, add some FSM.Transition objects (the Transition class is
 * designed to be used as an superclass for your anonymous implementation). Each Transition object
 * has two useful methods that can be defined by your implementation: doBeforeTransition and
 * doAfterTransition. To drive your FSM, simply give it events using the addEvent method with the
 * name of an event. If there is an appropriate transition for the current state, the transition's
 * doBefore/doAfter methods are called and the FSM is put into the new state. It is legal (and
 * highly useful) for the start/end states of a transition to be the same state.
 **/
public class FSM { // This class implements a Flying Spaghetti Monster

  protected String name;
  protected String currentState;
  protected Map<String, State> states;
  protected List<ChangeListener> changeListeners;
  protected boolean debug;

  /**
   * Create a blank FSM with the given name (which is arbitrary).
   */
  public FSM(String name) {
    this.name = name;
    this.states = new HashMap<String, State>();
    this.currentState = null;
    this.changeListeners = new ArrayList<ChangeListener>();
  }

  /**
   * Turn debugging on/off.
   */
  public void setDebugMode(boolean debug) {
    this.debug = debug;
  }

  /**
   * Report the current state of the finite state machine.
   */
  public String getState() {
    return currentState;
  }

  /**
   * Adds a new state with no entry or exit code.
   */
  public void addState(String state) {
    addState(state, null, null, null);
  }

  /**
   * Establish a new state the FSM is aware of. If the FSM does not currently have any states, this
   * state becomes the current, initial state. This is the only way to put the FSM into an initial
   * state.
   * 
   * The entryCode, exitCode, and alwaysRunCode are Runnables that the FSM executes during the
   * course of a transition. entryCode and exitCode are run only if the transition is between two
   * distinct states (i.e. A->B where A != B). alwaysRunCode is executed even if the transition is
   * re-entrant (i.e. A->B where A = B).
   **/
  public void addState(String state, Runnable entryCode, Runnable exitCode, Runnable alwaysRunCode) {
    boolean isInitial = (states.size() == 0);
    if (!states.containsKey(state)) {
      states.put(state, new State(entryCode, exitCode, alwaysRunCode));
    }
    if (isInitial) {
      setState(state);
    }
  }

  public void setStateEntryCode(String state, Runnable entryCode) {
    states.get(state).entryCode = entryCode;
  }

  public void setStateExitCode(String state, Runnable exitCode) {
    states.get(state).exitCode = exitCode;
  }

  public void setStateAlwaysRunCode(String state, Runnable alwaysRunCode) {
    states.get(state).alwaysRunCode = alwaysRunCode;
  }

  /**
   * There are cases where a state is meant to be transitional, and the FSM should always
   * immediately transition to some other state. In those cases, use this method to specify the
   * start and end states. After the startState has fully transitioned (and any change events have
   * been fired) the FSM will check to see if there is another state that the FSM should
   * automatically transition to. If there is one, addEvent(endState) is called.
   * 
   * Note: this creates a special transition in the lookup table called "(auto)".
   */
  public void setAutoTransition(String startState, String endState) {
    if (debug) {
      Debug.out("FSM", "Establishing auto transition for " + startState + " -> " + endState);
    }
    states.get(startState).autoTransitionState = endState;
    addTransition(new Transition("(auto)", startState, endState));
  }

  /**
   * Sets the current state without following a transition. This will cause a change event to be
   * fired.
   */
  public void setState(String state) {
    setState(state, true);
  }

  /**
   * Sets the current state without followign a transition, and optionally causing a change event to
   * be triggered. During state transitions (with the 'addEvent' method), this method is used with
   * the triggerEvent parameter as false.
   * 
   * The FSM executes non-null runnables according to the following logic, given start and end
   * states A and B:
   * 
   * <ol>
   * <li>If A and B are distinct, run A's exit code.</li>
   * <li>Record current state as B.</li>
   * <li>Run B's "alwaysRunCode".</li>
   * <li>If A and B are distinct, run B's entry code.</li>
   * </ol>
   */
  public void setState(String state, boolean triggerEvent) {
    boolean runExtraCode = !state.equals(currentState);
    if (runExtraCode && currentState != null) {
      states.get(currentState).runExitCode();
    }
    currentState = state;
    states.get(currentState).runAlwaysCode();
    if (runExtraCode) {
      states.get(currentState).runEntryCode();
    }
    if (triggerEvent) {
      fireChangeEvent();
    }
  }

  /**
   * Establish a new transition. You might use this method something like this:
   * 
   * <pre>
   * fsm.addTransition(new FSM.Transition("someEvent", "firstState", "secondState") { 
   *  public void doBeforeTransition() { 
   *    System.out.println("about to transition..."); 
   *  } 
   *  public void doAfterTransition() { 
   *    fancyOperation(); 
   *  }
   * });
   */
  public void addTransition(Transition trans) {
    State st = states.get(trans.startState);
    if (st == null) {
      throw new NoSuchElementException("Missing state: " + trans.startState);
    }
    st.addTransition(trans);
  }

  /**
   * Add a change listener -- this is a standard java change listener and is only used to report
   * changes that have already happened. ChangeEvents are only fired AFTER a transition's
   * doAfterTransition is called.
   */
  public void addChangeListener(ChangeListener cl) {
    if (!changeListeners.contains(cl)) {
      changeListeners.add(cl);
    }
  }

  /**
   * Feed the FSM with the named event. If the current state has a transition that responds to the
   * given event, the FSM will performed the transition using the following steps, assume start and
   * end states are A and B:
   * 
   * <ol>
   * <li>Execute the transition's "doBeforeTransition" method</li>
   * <li>Exectue the "veto" function. If it passes, continue. Else, stop.</li> 
   * <li>Run fsm.setState(B) -- see docs for that method</li>
   * <li>Execute the transition's "doAfterTransition" method</li>
   * <li>Fire a change event, notifying interested observers that the transition has completed.</li>
   * <li>Now firmly in state B, see if B has a third state C that we must automatically transition
   * to via addEvent(C).</li>
   * </ol>
   */
  public void addEvent(String evtName) {
    State state = states.get(currentState);
    if (state.transitions.containsKey(evtName)) {
      Transition trans = state.transitions.get(evtName);
      if (debug) {
        Debug.out("FSM", "Event: " + evtName + ", " + trans.startState + " --> " + trans.endState);
      }
      trans.doBeforeTransition();
      if (!trans.veto()) {
        setState(trans.endState, false);
        trans.doAfterTransition();
        fireChangeEvent();
        if (states.get(trans.endState).autoTransitionState != null) {
          if (debug) {
            Debug.out(
                "FSM",
                "Automatically transitioning from " + trans.endState + " to "
                    + states.get(trans.endState).autoTransitionState);
          }
          addEvent("(auto)");
        }
      }
    }
  }

  /**
   * Fire a change event to registered listeners.
   */
  protected void fireChangeEvent() {
    ChangeEvent changeEvent = new ChangeEvent(this);
    for (ChangeListener cl : changeListeners) {
      cl.stateChanged(changeEvent);
    }
  }

  /**
   * Represents a state with some number of associated transitions.
   */
  private static class State {
    Map<String, Transition> transitions;
    String autoTransitionState;
    Runnable entryCode;
    Runnable exitCode;
    Runnable alwaysRunCode;

    State(Runnable entryCode, Runnable exitCode, Runnable alwaysRunCode) {
      autoTransitionState = null;
      transitions = new HashMap<String, Transition>();
      this.entryCode = entryCode;
      this.exitCode = exitCode;
      this.alwaysRunCode = alwaysRunCode;
    }

    public void addTransition(Transition trans) {
      transitions.put(trans.evtName, trans);
    }

    public void runEntryCode() {
      if (entryCode != null) {
        entryCode.run();
      }
    }

    public void runExitCode() {
      if (exitCode != null) {
        exitCode.run();
      }
    }

    public void runAlwaysCode() {
      if (alwaysRunCode != null) {
        alwaysRunCode.run();
      }
    }
  }

  /**
   * Create a new transition. See the documentation for addEvent and addTransition in FSM.
   */
  public static class Transition {
    String evtName;
    String startState;
    String endState;

    /**
     * Create a transition object that responds to the given event when in the given startState, and
     * puts the FSM into the endState provided.
     */
    public Transition(String evtName, String startState, String endState) {
      this.evtName = evtName;
      this.startState = startState;
      this.endState = endState;
    }

    /**
     * Override this to have FSM execute code immediately before following a state transition.
     */
    public void doBeforeTransition() {
    }

    /**
     * Override this to have FSM execute code immediately after following a state transition.
     */
    public void doAfterTransition() {
    }

    /**
     * Override this to test if the transition shouldn't occur. By default this returns false.
     * 
     * @return true if the transition should not take place.
     */
    public boolean veto() {
      return false;
    }
  }
}
