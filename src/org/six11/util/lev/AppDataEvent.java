// $Id: AppDataEvent.java 23 2009-11-24 21:09:08Z gabe.johnson $

package org.six11.util.lev;


/**
 * This is a marker class indicating that the subclass is to be used in the lev
 * model event framework.
 */
public abstract class AppDataEvent {

  /**
   * An identifier for use when something changes but (for some
   * reason) it isn't necessary to be specific as to what it was. This
   * can be useful if a model has a number of 'transient' values that
   * (for exapmle) represent a temporary GUI state, but it isn't
   * desirable to have a different listener method for each one. If
   * subclasses enumerate a list of static integers for their types,
   * they should all be positive, as the TYPE_ANONYMOUS is -1.
   */
  public static final int TYPE_ANONYMOUS = -1;
  
  private Object source;

  /**
   * Creates a blank app data event.
   */
  public AppDataEvent(Object source_) {
    source = source_;
  }

  /**
   * Create a blank app data event, sans a source.
   **/
  public AppDataEvent() {
    this(null);
  }
  
  /**
   * Set the value of source.
   **/
  public void setSource(Object source_) {
    source = source_;
  }

  /**
   * Get the value of source.
   **/
  public Object getSource() {
    return source;
  }

}
