package org.six11.util.math;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static java.lang.Math.atan2;
import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.abs;
import static java.lang.Math.signum;
import static java.lang.Math.sqrt;

import org.six11.util.Debug;
import org.six11.util.args.Arguments;
import org.six11.util.gui.BoundingBox;
import org.six11.util.io.FileUtil;
import org.six11.util.pen.Functions;
import org.six11.util.pen.Pt;
import org.six11.util.pen.RotatedEllipse;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * Use this to create an ellipse based on rough data. This implements the approach described in:
 * 
 * Andrew Fitzgibbon, Maurizio Pilu, Robert B. Fisher (1998). ``Direct least Square Fitting of
 * Ellipses''. IEEE Transactions on Pattern Analysis and Machine Intelligence
 */

public class EllipseFit {

  public static void main(String[] in) throws FileNotFoundException, IOException {
    Arguments args = new Arguments();
    args.parseArguments(in);
    Debug.useColor = args.hasFlag("debug-color");

    List<Pt> points = new ArrayList<Pt>();
    String dataString = "1 1 2 3 3 5 5 4";
    if (args.getPositionCount() == 1) {
      dataString = FileUtil.loadStringFromFile(args.getPosition(0));
    } else if (args.getPositionCount() > 1) {
      dataString = "";
      for (int i = 0; i < args.getPositionCount(); i++) {
        String s = args.getPosition(i);
        dataString = dataString + " " + s;
      }
    }

    StringTokenizer tok = new StringTokenizer(dataString, " \n");
    while (tok.hasMoreTokens()) {
      double x = Double.parseDouble(tok.nextToken());
      double y = Double.parseDouble(tok.nextToken());
      points.add(new Pt(x, y));
    }

    ellipseFit(points);
  }

  public static RotatedEllipse ellipseFit(List<Pt> points) {
    Pt mean = Functions.getMean(points);
    BoundingBox bb = new BoundingBox(points);
    double sx = (bb.getMaxX() - bb.getMinX()) / 2.0;
    double sy = (bb.getMaxY() - bb.getMinY()) / 2.0;
    double mx = mean.getX();
    double my = mean.getY();
    double[] x = new double[points.size()];
    double[] y = new double[points.size()];

    for (int i = 0; i < points.size(); i++) {
      x[i] = (points.get(i).getX() - mean.getX()) / sx;
      y[i] = (points.get(i).getY() - mean.getY()) / sy;
    }
    double[][] designData = new double[points.size()][6];
    for (int i = 0; i < points.size(); i++) {// @formatter:off
      designData[i] = new double[] {
        x[i] * x[i],
        x[i] * y[i],
        y[i] * y[i],
        x[i],
        y[i],
        1.0
      }; // @formatter:on
    }
    Matrix design = new Matrix(designData);
    Matrix scatter = design.transpose().times(design);
    // @formatter:off
    double[][] cData = new double[][] {
      new double[] { 0.0, 0.0, -2.0, 0.0, 0.0, 0.0 },
      new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, 0.0 },
      new double[] { -2.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
      new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
      new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 } ,
      new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }
    };
    // @formatter:on
    Matrix c = new Matrix(cData);
    // The next part isn't in the original paper but is by the               tmpA = S(1:3,1:3); 
    // paper authors and they claim it is more numerically stable.           tmpB = S(1:3,4:6); 
    // That's cool but I use it only because the orignal version             tmpC = S(4:6,4:6); 
    // called for generalized eigenvalues, which I haven't found a           tmpD = C(1:3,1:3);
    // Java library for, and I'm sure as hell not going to make one          tmpE = inv(tmpC)*tmpB';
    // myself. So I'm glad they have this other way here.
    Matrix tmpA = scatter.getMatrix(0, 2, 0, 2);
    Matrix tmpB = scatter.getMatrix(0, 2, 3, 5);
    Matrix tmpC = scatter.getMatrix(3, 5, 3, 5);
    Matrix tmpD = c.getMatrix(0, 2, 0, 2);
    Matrix tmpE = tmpC.inverse().times(tmpB.transpose());
    Matrix dInv = tmpD.inverse();
    Matrix be = tmpB.times(tmpE);
    Matrix aMinusBE = tmpA.minus(be);
    Matrix eigMeBaby = dInv.times(aMinusBE);
    EigenvalueDecomposition eigenstuff = eigMeBaby.eig();
    double[] evals = eigenstuff.getRealEigenvalues();
    int idxPos = -1;
    double SMALL_NUMBER = 0.00000001; 
    for (int i = 0; i < evals.length; i++) {
      if (evals[i] <= SMALL_NUMBER && !Double.isInfinite(evals[i])) {
        idxPos = i;
      }
    }

    if (idxPos < 0) {
      System.out.println("EllipseFit: idxPos negative. this will ruin your day");
      System.out.println("Eigenvalues are as follows:");
      for (int i=0; i < evals.length; i++) {
        System.out.println("  " + i + ": " + evals[i]);
      }
    }
    Matrix evecX = eigenstuff.getV().getMatrix(0, 2, idxPos, idxPos);
    Matrix evecY = tmpE.uminus().times(evecX);
    double[] d1 = evecX.getColumnPackedCopy();
    double[] d2 = evecY.getColumnPackedCopy();
    double[] a = new double[d1.length + d2.length];
    System.arraycopy(d1, 0, a, 0, d1.length);
    System.arraycopy(d2, 0, a, d1.length, d2.length);

    double[] par = {
        a[0] * sy * sy,
        a[1] * sx * sy,
        a[2] * sx * sx,
        -2 * a[0] * sy * sy * mx - a[1] * sx * sy * my + a[3] * sx * sy * sy,
        -a[1] * sx * sy * mx - 2 * a[2] * sx * sx * my + a[4] * sx * sx * sy,
        a[0] * sy * sy * mx * mx + a[1] * sx * sy * mx * my + a[2] * sx * sx * my * my - a[3] * sx
            * sy * sy * mx - a[4] * sx * sx * sy * my + a[5] * sx * sx * sy * sy
    };
    double thetaRadians = 0.5 * atan2(par[1], par[0] - par[2]);
    double cosineT = cos(thetaRadians);
    double sineT = sin(thetaRadians);
    double sineSquared = sineT * sineT;
    double cosineSquared = cosineT * cosineT;
    double cosineSine = sineT * cosineT;
    double ao = par[5];
    double au = par[3] * cosineT + par[4] * sineT;
    double av = -par[3] * sineT + par[4] * cosineT;
    double auu = par[0] * cosineSquared + par[2] * sineSquared + par[1] * cosineSine;
    double avv = par[0] * sineSquared + par[2] * cosineSquared - par[1] * cosineSine;

    double tuCenter = -au / (2 * auu);
    double tvCenter = -av / (2 * avv);
    double wCenter = ao - auu * tuCenter * tuCenter - avv * tvCenter * tvCenter;
    double uCenter = tuCenter * cosineT - tvCenter * sineT;
    double vCenter = tuCenter * sineT + tvCenter * cosineT;
    double ru = -wCenter / auu;
    double rv = -wCenter / avv;
    ru = sqrt(abs(ru)) * signum(ru);
    rv = sqrt(abs(rv)) * signum(rv);

    // IMPORTANT NOTE: I introduced a unary minus to the thetaRadians variable that was not in the
    // original Matlab code. I suspect that Matlab and Java have opposite notions of up and down
    // and which way is 'positive' for angles. Negating the angle works in Java.
    RotatedEllipse ellipse = new RotatedEllipse(new Pt(uCenter, vCenter), ru, rv, -thetaRadians);
    
    return ellipse;
  }

}
