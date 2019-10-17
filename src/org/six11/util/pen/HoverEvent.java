package org.six11.util.pen;

import java.util.EventObject;

/**
 * 
 *
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class HoverEvent extends EventObject {

  public static enum Type {
    In, Out, Hover
  };

  
  Pt pt;
  Type type;
  
  /**
   * @param source
   */
  public HoverEvent(Object source, Pt pt, Type type) {
    super(source);
    this.pt = pt;
    this.type = type;
  }

  public Pt getPt() {
    return pt;
  }
  
  public void setPt(Pt pt) {
    this.pt = pt;
  }

  public Type getType() {
    return type;
  }
  
  

}
