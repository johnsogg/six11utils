// $Id: TestFSM.java 37 2010-01-18 16:55:13Z gabe.johnson@gmail.com $

package org.six11.util.pen;

import junit.framework.TestCase;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import org.six11.util.data.FSM;

/**
 * 
 **/
public class TestFSM extends TestCase {

  protected FSM fsm;
  protected int val1;
  protected int val2;
  protected int val3;
  protected int val4;

  protected void setUp() {
    fsm = new FSM("JUnit Testing FSM");
    /*
     * A: 1 (to B) B: 2 (to A), 3 (to C) C: 4 (to C), 5 (to D), 6 (to B) D: 7 (to A)
     */
    fsm.addState("A");
    fsm.addState("B");
    fsm.addState("C");
    fsm.addState("D");
    fsm.addTransition(mkTrans("1", "A", "B"));
    fsm.addTransition(mkTrans("2", "B", "A"));
    fsm.addTransition(mkTrans("3", "B", "C"));
    fsm.addTransition(mkTrans("4", "C", "C"));
    fsm.addTransition(mkTrans("5", "C", "D"));
    fsm.addTransition(mkTrans("6", "C", "B"));
    fsm.addTransition(mkTrans("7", "D", "A"));
    val1 = 0;
    val2 = 0;
    val3 = 0;
    val4 = 0;
  }

  protected void tearDown() {
    fsm = null;
  }

  public void testStartState() {
    assertEquals("A", fsm.getState());
  }

  public void testSameNameEvents() {
    // augment fsm with a new state 'Held' and three new transitions:
    // B: hold (to Held)
    // C: hold (to Held)
    // Held: done (to A)
    fsm.addState("Held");
    fsm.addTransition(mkTrans("hold", "B", "Held"));
    fsm.addTransition(mkTrans("hold", "C", "Held"));
    fsm.addTransition(mkTrans("done", "Held", "A"));

    assertEquals("A", fsm.getState());
    fsm.addEvent("hold"); // no effect
    assertEquals("A", fsm.getState());
    fsm.addEvent("1"); // move to B
    assertEquals("B", fsm.getState());
    fsm.addEvent("hold"); // move to Held
    assertEquals("Held", fsm.getState());
    fsm.addEvent("done");
    assertEquals("A", fsm.getState());
    fsm.addEvent("1"); // to B
    fsm.addEvent("3"); // to C
    fsm.addEvent("hold"); // move to Held
    assertEquals("Held", fsm.getState());
    fsm.addEvent("done");
    assertEquals("A", fsm.getState());

  }

  public void testTransitions() {
    fsm.addEvent("1"); // A-->B
    assertEquals("B", fsm.getState());
    fsm.addEvent("3"); // B-->C
    assertEquals("C", fsm.getState());
    fsm.addEvent("2"); // should have no effect
    assertEquals("C", fsm.getState());
    for (int i = 0; i < 4; i++) {
      fsm.addEvent("4"); // transition from C back to itself
      assertEquals("C", fsm.getState());
    }
    fsm.addEvent("5");
    assertEquals("D", fsm.getState());
    fsm.addEvent("non-existant event name");
    assertEquals("D", fsm.getState());
    fsm.addEvent("7");
    assertEquals("A", fsm.getState());
  }

  public void testProgrammableCode() {
    // this tests both the doBefore/doAfter as well as the change
    // listeners
    fsm.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent ev) {
        val4++;
      }
    });
    assertEquals(0, val1);
    assertEquals(0, val2);
    assertEquals(0, val3);
    fsm.addEvent("1");
    assertEquals(1, val1);
    assertEquals(0, val2);
    assertEquals(0, val3);
    fsm.addEvent("3");
    assertEquals(1, val1);
    assertEquals(1, val2);
    assertEquals(1, val3);
    fsm.addEvent("6");
    fsm.addEvent("3");
    fsm.addEvent("6");
    fsm.addEvent("3");
    assertEquals(1, val1);
    assertEquals(3, val2);
    assertEquals(3, val3);
    fsm.addEvent("5");
    assertEquals(1, val1);
    assertEquals(0, val2);
    assertEquals(3, val3);
    assertEquals(7, val4);

  }

  private FSM.Transition mkTrans(String evtName, final String startState, final String endState) {
    return new FSM.Transition(evtName, startState, endState) {
      public void doBeforeTransition() {
        if (startState.equals("A"))
          val1++;
        if (startState.equals("B"))
          val2++;
        if (endState.equals("D"))
          val2 = 0;
      }

      public void doAfterTransition() {
        if (endState.equals("C"))
          val3 = val1 * val2;
      }
    };
  }
}
