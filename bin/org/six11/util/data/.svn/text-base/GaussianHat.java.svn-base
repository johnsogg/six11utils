// $Id$

package org.six11.util.data;

import java.util.Random;

/**
 * A GaussianHat is a random number generator that will give you
 * numbers based on a population mean and standard deviation. You can
 * ask it for integers or probabilities. You may also specify a floor
 * and ceiling (so in that case it's not truely Gaussian).
 */
public class GaussianHat {

    // this is necessary because if you create a few GaussianHats
    // during the same millisecond and then use them in the exact
    // manner, they will yield the same 'random' numbers if you ask
    // for the same kinds of random numbers in the same order, which
    // is absolutely no good. So we'll create a single Random that
    // will then generate better Randoms from it.
    static Random staticRand;
    static {
        staticRand = new Random(System.currentTimeMillis());
    }

    double mean;
    double sd;
    Random rand;
    double floor;
    double ceiling;

    /**
     * Creates a copy of the input GaussianHat.
     */
    public GaussianHat(GaussianHat other) {
        this(other.mean, other.sd, other.floor, other.ceiling);
    }
    
    public GaussianHat(double mean, double sd, double floor, 
		       double ceiling) {
        this.mean = mean;
        this.sd = sd;
        this.floor = floor;
        this.ceiling = ceiling;
        this.rand = new Random(staticRand.nextLong());
    }

    public GaussianHat(double mean, double sd) {
        this(mean, sd, 0, Double.MAX_VALUE);
    }

    public GaussianHat(int mean, double sd) {
        this((double) mean, sd);
    }

    public GaussianHat(int mean, int sd) {
        this((double) mean, (double) sd);
    }
    
    public GaussianHat(int mean, int sd, int floor, int ceiling) {
        this ((double) mean, (double) sd, (double) floor, (double) ceiling);
    }

    public GaussianHat(double mean) {
        this(mean, 1.0);
    }

  public void setMean(double mean) {
    this.mean = mean;
  }

  public void setStdDev(double sd) {
    this.sd = sd;
  }

    public int getInt() {
        // simply return getDouble() as an int.
        return (int) getDouble();
    }
    
    public double getDouble() {
        // based on the mean and standard deviation, pick a number from a normal
        // distribution, cast it to an integer
        double d = (rand.nextGaussian() * sd) + mean;
        if (d < floor) d = floor;
        if (d > ceiling) d = ceiling;
        return d;
    }

    public double getUniformDouble() {
        double d = rand.nextDouble();
        d = floor + (d * (ceiling - floor));
        return d;
    }
    
    public boolean getYesNo() {
        // get a random number from uniform distribution 0..1 and return true if
        // that number is less than or equal to the provided 'chance' parameter
        return (rand.nextDouble() <= mean);
    }

    public String toString() {
        return mean + " (" + sd + ")";
    }
    
    public static void main(String[] args) {
        // I have convinced myself that my randomness is OK using JMP
        GaussianHat[] intHats = new GaussianHat[] { new GaussianHat(10, 2),
                new GaussianHat(10, 4), new GaussianHat(20, 2),
                new GaussianHat(20, 4) };

        GaussianHat[] chanceHats = new GaussianHat[] {
                new GaussianHat(0.60, 0.05), new GaussianHat(0.60, 0.15),
                new GaussianHat(0.30, 0.05), new GaussianHat(0.30, 0.15) };
        for (int trial = 1; trial <= 100; trial++) {
            for (int i = 0; i < intHats.length; i++) {
                System.out.print(intHats[i].getInt() + "\t");
                if (i == (intHats.length - 1))
                    System.out.println();
            }
        }
        for (int trial = 1; trial <= 100; trial++) {
            for (int i = 0; i < chanceHats.length; i++) {
                System.out.print(chanceHats[i].getYesNo() + "\t");
                if (i == (chanceHats.length - 1))
                    System.out.println();
            }
        }
    }
}
