// $Id: AnonModule.java 23 2009-11-24 21:09:08Z gabe.johnson $

package org.six11.util.lev;

/**
 * An anonymous module, suitable for scoping a module at a narrower
 * level than at the application. This is useful for 'editors', when
 * there may be an arbitrary number of editors at any given point in
 * time, and where none of them are the 'main' one. For example, a
 * program that involves address book entries may have an arbitrary
 * number of address book editors at any time.
 *
 * <p>It may be a good idea for subclasses to override the
 * setEnabled() method, and for application code to use it when you're
 * no longer going to use an anonymous module, in case it is taking up
 * resources.
 **/
public abstract class AnonModule extends Module  {
    
  private static int anonCounter = 1;
  
  public AnonModule(Application app_) {
    super(app_, "");
    String s = getClass().getName();
    if (s.lastIndexOf('.') > 0) {
      s = s.substring(s.lastIndexOf('.') + 1);
    }
    name = "(Anon) " + s + " " + anonCounter++;
  }

}
