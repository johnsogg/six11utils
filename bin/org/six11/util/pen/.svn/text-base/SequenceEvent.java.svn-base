package org.six11.util.pen;

import java.util.EventObject;

/**
 * Sequence event indicating that a sequence has begin, or more points have been added to one, or
 * that a sequence has been completed.
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class SequenceEvent extends EventObject {

  public static enum Type {
    BEGIN, PROGRESS, END
  }

  private Type type;
  private Sequence seq;

  /**
   * Create a new sequence event indicating that a sequence has begin, or more points have been
   * added to one, or that a sequence has been completed.
   * 
   * @param source
   *          the class where the event was generated.
   * @param seq
   *          the sequence involved
   * @param type
   *          one of the three Type values (begin/progress/end)
   */
  public SequenceEvent(Object source, Sequence seq, Type type) {
    super(source);
    this.seq = seq;
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public Sequence getSeq() {
    return seq;
  }

}
