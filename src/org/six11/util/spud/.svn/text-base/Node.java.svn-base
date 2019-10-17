package org.six11.util.spud;

import org.six11.util.Debug;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public abstract class Node {
  protected boolean solved;

  public Node() {
    this.solved = false;
  }

  public boolean isSolved() {
    return solved;
  }
  
  public abstract String getHumanReadableName();
  
  protected void bug(String what) {
    Debug.out(getClass().getSimpleName(), what);
  }
  
  protected void warn(String what) {
    Debug.out(getClass().getSimpleName(), "** WARNING ** " + what);
  }

}
