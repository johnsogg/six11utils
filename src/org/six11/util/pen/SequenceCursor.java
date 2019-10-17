// $Id: SequenceCursor.java 23 2009-11-24 21:09:08Z gabe.johnson $

package org.six11.util.pen;

import java.util.Iterator;

/**
 * 
 **/
public class SequenceCursor implements Iterator<Pt> {

  protected Sequence seq;
  protected int currentIdx;
  protected int boundaryA;
  protected int boundaryB;
  protected boolean forward;

  public SequenceCursor(Sequence seq, int boundaryA, int boundaryB) {
    this.seq = seq;
    this.boundaryA = boundaryA;
    this.boundaryB = boundaryB;
    forward = (boundaryA < boundaryB);
    currentIdx = boundaryA;
  }

  public SequenceCursor(Sequence seq) {
    this(seq, 0, seq.size() - 1);
  }

  public boolean hasNext() {
    boolean ret = false;
    if (forward) {
      ret = (currentIdx <= boundaryB && seq.size() > 0);
    } else {
      ret = (currentIdx >= boundaryB && seq.size() > 0);
    }
    return ret;
  }
  
  public Pt next() {
    Pt ret = seq.get(currentIdx);
    if (forward) currentIdx++;
    else currentIdx--;
    return ret;
  }

  public Pt getCurrent() {
    return seq.get(currentIdx);
  }
  
  public int getCurrentIdx() {
    return currentIdx;
  }

  public void remove() {
    // nichts zu tun.
  }
}
