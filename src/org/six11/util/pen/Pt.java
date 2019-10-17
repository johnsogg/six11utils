// $Id: Pt.java 274 2012-03-06 01:41:36Z gabe.johnson@gmail.com $

package org.six11.util.pen;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

/**
 * My own special point object that does magic tricks, especially when paired with other Pt objects
 * in a Sequence. It helps me do calculations and provides a cleaner syntax than it's parent class.
 * I mean, seriously, I got sick of doing Point2D pt = new Point2D.Double(x, y) -- it's so ugly next
 * to Pt pt = new Pt(x, y);
 * 
 * I can also write on points because of the attributes map.
 **/
public class Pt extends Point2D.Double implements Comparable<Pt> {

  public static int ID_COUNTER = 0;

  protected long time;
  protected Map<String, Object> attribs;
  protected final int id;

  public Pt() {
    this(0, 0);
  }

  public Pt(int x, int y) {
    this((double) x, (double) y);
  }

  public Pt(double x, double y) {
    this(x, y, 0);
  }

  public Pt(Vec direction) {
    this(direction.getX(), direction.getY());
  }

  public Pt(Point2D pt) {
    this(pt.getX(), pt.getY());
  }

  public Pt(MouseEvent ev) {
    this(ev.getPoint().getX(), ev.getPoint().getY(), ev.getWhen());
  }

  public Pt(double x, double y, long time) {
    this(++ID_COUNTER, x, y, time);
    this.time = time;
  }

  public Pt(int id, double x, double y, long time) {
    super(x, y);
    this.id = id;
    this.time = time;
    attribs = new HashMap<String, Object>();
    ID_COUNTER = Math.max(id, ID_COUNTER);
  }

  public Pt(Point2D source, long time) {
    this(source.getX(), source.getY(), time);
  }

  public Pt(Point src) {
    this(src.x, src.y);
  }

  public int getID() {
    return id;
  }

  public Map<String, Object> getAttribs() {
    if (attribs == null) {
      attribs = new HashMap<String, Object>();
    }
    return attribs;
  }

  /**
   * Scale this point by the given amount.
   */
  public void scale(double amt) {
    setLocation(getX() * amt, getY() * amt);
  }

  public Pt getScaled(double amt) {
    return new Pt(getX() * amt, getY() * amt);
  }

  public Pt getTranslated(double x, double y) {
    return new Pt(getX() + x, getY() + y);
  }
  
  public Pt getTranslated(Vec v) {
    return new Pt(getX() + v.getX(), getY() + v.getY());
  }

  public int compareTo(Pt other) {
    if (getTime() < other.getTime())
      return -1;
    if (getTime() > other.getTime())
      return 1;
    return 0;
  }

  public boolean isSameLocation(Pt other) {
    return ((Math.abs(getX() - other.getX()) < Functions.EQ_TOL) && (Math
        .abs(getY() - other.getY()) < Functions.EQ_TOL));
  }

  public static Comparator<Pt> sortByX = new Comparator<Pt>() {
    public int compare(Pt a, Pt b) {
      int ret = 0;
      if (a.getX() < b.getX()) {
        ret = -1;
      } else if (a.getX() > b.getX()) {
        ret = 1;
      } else {
        // x values are the same. to ensure consistent ordering defer to the y value.
        if (a.getY() > b.getY()) {
          ret = 1;
        } else {
          ret = -1;
        }
      }
      return ret;
    }

    // public boolean equals(Object obj) {
    // return false;
    // }
  };

  public static Comparator<Pt> sortByY = new Comparator<Pt>() {
    public int compare(Pt a, Pt b) {
      int ret = 0;
      if (a.getY() < b.getY()) {
        ret = -1;
      } else if (a.getY() > b.getY()) {
        ret = 1;
      } else {
        // y values are the same. to ensure consistent ordering defer to the x value.
        if (a.getX() > b.getX()) {
          ret = 1;
        } else {
          ret = -1;
        }
      }
      return ret;
    }
  };

  public static Comparator<Pt> sortById = new Comparator<Pt>() {
    public int compare(Pt a, Pt b) {
      return (((Integer) a.getID()).compareTo(b.getID()));
    }
  };

  public static Comparator<Pt> sortByT = new Comparator<Pt>() {
    public int compare(Pt a, Pt b) {
      int ret = 0;
      if (a.getTime() < b.getTime()) {
        ret = -1;
      } else if (a.getTime() > b.getTime()) {
        ret = 1;
      }
      return ret;
    }
  };
  
  public float fx() {
    return (float) getX();
  }
  
  public float fy() {
    return (float) getY();
  }

  public int ix() {
    return (int) getX();
  }

  public int iy() {
    return (int) getY();
  }

  public boolean equals(Pt other) {
    // boolean basic = (other.compareTo(this) == 0 &&
    // /*
    // * it used to be this: other.getX() == getX() && other.getY() == getY()
    // */
    // Functions.eq(this, other, Functions.EQ_TOL));
    // boolean advanced = basic ? getAttribs().equals(other.getAttribs()) : false;
    //
    // return basic && advanced;
    return this.id == other.id;
  }

  public int hashCode() {
//    // this is totally a guess
//    int hash = super.hashCode() ^ ((Long) time).hashCode() ^ getAttribs().hashCode();
//    return hash;
    return ((Integer) id).hashCode();
  }

  public Pt copyXYT() {
    return new Pt(getX(), getY(), getTime());
  }

  @SuppressWarnings("unchecked")
  public Pt copy() {
    Pt twin = copyXYT();
    if (attribs == null) {
      twin.attribs = null;
    } else {
      twin.attribs = (HashMap<String, Object>) ((HashMap<String, Object>) attribs).clone();
    }
    return twin;
  }

  public final void setTime(long time) {
    this.time = time;
  }

  public long getTime() {
    return time;
  }

  public void setAttribute(String name, Object value) {
    getAttribs().put(name, value);
  }

  public void setBoolean(String name, boolean value) {
    getAttribs().put(name, value);
  }

  public boolean getBoolean(String name) {
    return (getAttribs().containsKey(name) && (Boolean) getAttribute(name));
  }

  public boolean getBoolean(String name, boolean defaultValue) {
    boolean ret = defaultValue;
    if (getAttribs().containsKey(name)) {
      ret = (Boolean) getAttribute(name);
    }
    return ret;
  }

  public void setDouble(String name, double value) {
    // if (name.equals("selection strength")) {
    // Debug.out("Pt", "setting selection strength to " + value);
    // }
    getAttribs().put(name, value);
  }

  public void setString(String name, String value) {
    getAttribs().put(name, value);
  }

  public boolean hasAttribute(String name) {
    return getAttribs().containsKey(name);
  }

  public Object getAttribute(String name) {
    return getAttribs().get(name);
  }

  public void removeAttribute(String name) {
    getAttribs().remove(name);
  }

  public Vec getVec(String name) {
    return (Vec) getAttribute(name);
  }

  public void setVec(String name, Vec value) {
    setAttribute(name, value);
  }

  public double getDouble(String name) {
    Object shouldBeDouble = getAttribute(name);
    return ((java.lang.Double) shouldBeDouble).doubleValue();
  }

  public double getDouble(String name, double defaultValue) {
    double ret = defaultValue;
    Object shouldBeDouble = getAttribute(name);
    if (shouldBeDouble != null) {
      ret = ((java.lang.Double) shouldBeDouble).doubleValue();
    }
    return ret;
  }

  public String getString(String name) {
    return (String) getAttribute(name);
  }

  public void setMap(String name, Map<?, ?> value) {
    getAttribs().put(name, value);
  }

  public Map<?, ?> getMap(String name) {
    return (Map<?, ?>) getAttribute(name);
  }

  public void setList(String name, List<?> value) {
    getAttribs().put(name, value);
  }

  public List<?> getList(String name) {
    return (List<?>) getAttribute(name);
  }

  public void setSequence(String name, Sequence seq) {
    getAttribs().put(name, seq);
  }

  public Sequence getSequence(String name) {
    return (Sequence) getAttribute(name);
  }

  /**
   * Sets this points location by translating it the given amount: this.x + v.x, this.y + v.y.
   */
  public void move(Vec v) {
    setLocation(x + v.getX(), y + v.getY());
  }

  public Pt getTranslated(Vec vec, double len) {
    Vec resized = vec.getVectorOfMagnitude(len);
    return getTranslated(resized.getX(), resized.getY());
  }

  public void move(double dx, double dy) {
    setLocation(x + dx, y + dy);
  }
}
