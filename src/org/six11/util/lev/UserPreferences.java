// $Id: UserPreferences.java 12 2009-11-09 22:58:47Z gabe.johnson $

package org.six11.util.lev;

/**
 * An abstract class for dealing with user preferences and storing them
 * persistently. The storage may be on the user's local disk, on a database,
 * on a web server, or some combination.
 */
public abstract class UserPreferences {
  /**
   * Returns the preference with the given name. If no such item is found, null
   * may be returned.
   */
  public abstract Object getPreference(String key);

  /**
   * Stores the given value associated with the provided key.
   */
  public abstract void setPreference(String key, Object value);
}
