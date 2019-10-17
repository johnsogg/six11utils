// $Id$

package org.six11.util.pen;

import java.util.Random;
import static org.six11.util.Debug.bug;
import static org.six11.util.Debug.num;


/**
 * A source of entropy. Use it before it changes!
 **/
public class Entropy {

  // --- static vars
  private static Entropy instance;
  private static boolean hasSeed = false;
  private static long seed = 42L;

  // --- instance vars
  private Random random;

  /**
   * Returns the current instance, or if one does not exist, creates
   * one first.
   */
  public static Entropy getEntropy() {
    if (instance == null) {
      instance = reset();
    }
    return instance;
  }

  /**
   * Creates a new Entropy (and replaces the old version)
   */
  public static Entropy reset() {
    instance = new Entropy();
    return instance;
  }

  /**
   * Sets the seed for this Entropy singleton. If the supplied value
   * is negative, it effectively means there is no seed, and future
   * calls to reset() (or an initial call to getEntropy()) will use
   * the current time as the seed.
   */
  public static void setSeed(long s) {
    seed = s;
    hasSeed = seed >= 0;
  }

  /**
   * Tells the factory if it should use a preset seed (see setSeed()).
   */
  public static void setHasSeed(boolean h) {
    hasSeed = h;
  }

  /**
   * Initializes a random instance.
   */
  private Entropy() {
    if (hasSeed) {
      random = new Random(seed);
    } else {
      random = new Random(System.currentTimeMillis());
    }
  }
  
  public boolean getBoolean() {
    return random.nextBoolean();
  }

  /**
   * Returns an integer between the two values. It doesn't matter if a
   * is less than or greater than b. If they are the same number
   * you'll just get that number back.
   */
  public int getIntBetween(int a, int b) {
    int spread = 1 + Math.abs(a - b);
    return Math.min(a,b) + random.nextInt(spread);
  }
  
  public double getNearbyDouble(double mean, double standardDev) {
    double d = (random.nextGaussian() * standardDev) + mean;
    bug("Value near " + num(mean) + ": " + num(d));
    return d;
  }

  public double getDouble(double d) {
    return random.nextDouble() * d;
  }

  
}
