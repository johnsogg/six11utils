package org.six11.util.solve;

import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.six11.util.Debug;
import org.six11.util.args.Arguments;
import org.six11.util.data.Statistics;
import org.six11.util.pen.Entropy;
import org.six11.util.pen.Pt;
import org.six11.util.pen.Vec;

import static org.six11.util.Debug.bug;
import static java.lang.Math.max;

public class ConstraintSolver {

  private boolean debugOutput = false;
  private BufferedWriter debugOutWriter = null;
  private String f = "%+#.10f";
  private StringBuilder buf;

  public static interface Listener {
    public void constraintStepDone(State state, int numIterations, double err, int numPoints,
        int numConstraints);
  }

  public static enum State {
    Solved, Unsatisfied, Working;
  }

  public static final String ACCUM_CORRECTION = "accumulated correction";
  public static final String LAST_SOLVER_ADJUSTMENT_VEC = "last solver adjustment";
  private static final double MIN_ACCPETABLE_ERROR = 0.0001;
  private static final String LAST_SOLVER_DESIRED_ADJUSTMENT_VEC = "last desired solver adjustment";

  /**
   * When the heat value is above this threshold, each step() will move ALL points. Below this
   * threshold, step() moves only the points associated with the most out-of-whack constraint.
   */
  private static final double HEAT_SINGLE_TARGET_THRESHOLD = 0.3;

  private List<Listener> stepListeners;
  private TestSolveUI ui = null;
  protected String msg = null;
  private boolean finished = false;
  protected int fps;
  protected VariableBank vars;
  private Object monitor;
  private State currentState;
  private double residual;
  private int numIterations;
  private boolean paused;
  private boolean shouldPrintToFile;
  private File debuggingFile;
  private FileWriter debuggingFileWriter;
  private Entropy entropy;

  public static void main(String[] in) throws Exception {
    new ConstraintSolver(in);
  }

  public ConstraintSolver() {
    init();
  }

  public ConstraintSolver(String[] in) throws SecurityException, NoSuchMethodException {
    init();
    Arguments args = new Arguments();
    args.parseArguments(in);
    Debug.useColor = args.hasFlag("use-color");
    if (args.hasValue("fps")) {
      this.fps = Integer.parseInt(args.getValue("fps"));
    }
    if (args.hasFlag("ui")) {
      createUI();
    }
    run();
  }

  private final void init() {
    this.monitor = new Object();
    this.vars = new VariableBank();
    this.stepListeners = new ArrayList<Listener>();
    this.buf = new StringBuilder();
    this.entropy = Entropy.getEntropy();
  }

  public void setFrameRate(int frameRate) {
    this.fps = frameRate;
  }

  public int getFrameRate() {
    return fps;
  }

  public void setDebugOut(boolean v) {
    this.debugOutput = v;
  }

  public void setDebugOutWriter(BufferedWriter bw) {
    this.debugOutWriter = bw;
  }

  public void createUI() {
    this.ui = new TestSolveUI(this);
  }

  public void addListener(Listener lis) {
    if (!stepListeners.contains(lis)) {
      stepListeners.add(lis);
    }
  }

  public void removeListener(Listener lis) {
    stepListeners.remove(lis);
  }

  protected void fire() {
    for (Listener lis : stepListeners) {
      lis.constraintStepDone(currentState, numIterations, residual, vars.getPoints().size(), vars
          .getConstraints().size());
    }
  }

  public Pt mkRandomPoint(Component comp) {
    return mkRandomPoint(comp.getWidth(), comp.getHeight());
  }

  public Pt mkRandomPoint(int i, int j) {
    Entropy rand = Entropy.getEntropy();
    return new Pt(rand.getIntBetween(0, i), rand.getIntBetween(0, j));
  }

  public void runInBackground() {
    Runnable runner = new Runnable() {
      public void run() {
        ConstraintSolver.this.run();
      }
    };
    new Thread(runner, "Constraint Solver").start();
  }

  void run() {
    finished = false;
    long naptime = 0;
    if (fps > 0) {
      naptime = (long) (1000.0 / (double) fps);
    } else {
      naptime = 0;
    }
    double prevError = Double.MAX_VALUE;
    double heat = 1.0;
    double heatStep = -0.001;
    numIterations = 0;
    Statistics errorStats = new Statistics();
    int sampleStatsN = 10;
    errorStats.setMaximumN(sampleStatsN);
    double prevRunningErrorMean = 0;
    while (true) {
      synchronized (monitor) {
        try {
          if (paused || finished) {
            prevError = Double.MAX_VALUE;
            heat = 1.0;
            residual = Double.MAX_VALUE;
            monitor.wait();
            numIterations = 0;
          }
          double e = step(prevError, heat);
          numIterations = numIterations + 1;
          if (debugOutput && debugOutWriter != null) {
            try {
              debugOutWriter.flush();
            } catch (IOException ex) {
              ex.printStackTrace();
            }
          }
          errorStats.addData(e);
          if (errorStats.getN() == sampleStatsN && (numIterations % sampleStatsN == 0)) {
            double thisRunningErrorMean = errorStats.getMean();
            if (prevRunningErrorMean > 0) {
              double improvementRatio = (thisRunningErrorMean / prevRunningErrorMean);
              if (improvementRatio > 0.95) { // if we not improving,
                heat = heat + heatStep; // cool down a little bit.
              }
            }
            prevRunningErrorMean = thisRunningErrorMean;
          }
          prevError = e;
          //          heat = heat + heatStep;
          if (heat < HEAT_SINGLE_TARGET_THRESHOLD && heat - heatStep > HEAT_SINGLE_TARGET_THRESHOLD) {
            bug("Cold :(");
          }
          heat = max(0.1, heat);
          printDebug(heat);
          if (!finished) {
            currentState = State.Working;
          }
          if (ui != null) {
            ui.modelChanged();
          }
          if (fps > 0) { // the framerate can change from the UI or from the user program.
            naptime = (long) (1000.0 / (double) fps);
          } else {
            naptime = 0;
          }
        } catch (InterruptedException ex) {
          System.out.println("Interrupted in main constraint solver loop");
        }
      }
      try {
        Thread.sleep(naptime);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void printDebug(double heat) {
    if (shouldPrintToFile && debuggingFileWriter != null) {
      //      if (false) {
      //        // numIterations totalError heat c1name c1numPoints c1err ...
      //        StringBuilder buf = new StringBuilder();
      //        buf.append(String.format("%d\t%.6f\t%1.6f\t", numIterations, residual, heat));
      //        for (int i = 0; i < vars.getConstraints().size(); i++) {
      //          Constraint c = vars.getConstraints().get(i);
      //          String dashedType = c.getType().replaceAll(" ", "-");
      //          buf.append(String.format("%s\t%d\t%2.6f\t", dashedType, c.getRelatedPoints().length,
      //              Math.abs(c.measureError())));
      //        }
      //        buf.append("\n");
      //        try {
      //          debuggingFileWriter.append(buf.toString());
      //          debuggingFileWriter.flush();
      //        } catch (IOException ex) {
      //          ex.printStackTrace();
      //          bug("Got exception when writing debug file. I will stop debugging now.");
      //          shouldPrintToFile = false;
      //        }
      //      }
      if (true) {
        // numIterations totalError p1LastMove p2LastMove ...
        StringBuilder buf = new StringBuilder();
        if (numIterations == 1) {
          buf.append("# col = " + vars.getPoints().size() + "\n");
          buf.append("# plot for [i=2:col] 'file.dat' using 1:i with lines title column(i)\n");
          buf.append("Step\tTotalError");
          for (Pt pt : vars.getPoints()) {
            buf.append("\t" + pt.getString("name"));
          }
          buf.append("\n");
        }
        buf.append(numIterations + "\t" + residual + "\t");
        for (int i = 0; i < vars.getPoints().size(); i++) { //
          Pt pt = vars.getPoints().get(i);
          Vec delta = (Vec) pt.getAttribute(LAST_SOLVER_ADJUSTMENT_VEC);
          if (delta != null) {
            buf.append("\t" + delta.mag());
          } else {
            buf.append("0");
          }
        }
        buf.append("\n");
        try {
          debuggingFileWriter.append(buf.toString());
          debuggingFileWriter.flush();
        } catch (IOException ex) {
          ex.printStackTrace();
          bug("Got exception when writing debug file. I will stop debugging now.");
          shouldPrintToFile = false;
        }
      }
    }
  }

  public void setFileDebug(File outfile) {
    try {
      if (debuggingFileWriter != null) { // close old one, if it exists
        debuggingFileWriter.close();
      }
      debuggingFile = outfile;
      debuggingFileWriter = new FileWriter(debuggingFile);
      shouldPrintToFile = outfile != null;
      bug("Constraint solver is writing massive amounts of debugging information to "
          + outfile.getAbsolutePath());
    } catch (IOException e) {
      bug("Will be unable to debug to file: " + outfile);
      bug("Make sure it exists and is writeable and stuff.");
    }

  }

  private double calcTotalConstraintError() {
    double sum = 0;
    for (Constraint c : vars.getConstraints()) {
      sum += Math.abs(c.measureError());
    }
    return sum;
  }

  @SuppressWarnings("unchecked")
  private double step(double prevError, double heat) {
    if (debugOutput) {
      buf.setLength(0);
    }
    double totalError = 0;
    try {
      // 1: clear any current correction values
      for (Pt pt : vars.getPoints()) {
        List<Vec> corrections = (List<Vec>) pt.getAttribute(ACCUM_CORRECTION);
        if (corrections == null) {
          pt.setAttribute(ACCUM_CORRECTION, new ArrayList<Vec>());
          corrections = (List<Vec>) pt.getAttribute(ACCUM_CORRECTION);
        }
        corrections.clear();
      }

      // 2: poll all constraints and have them add correction vectors to each point
      Constraint worst = null;
      double worstError = 0;
      for (Constraint c : vars.getConstraints()) {
        c.clearMessages();
        if (heat > HEAT_SINGLE_TARGET_THRESHOLD) {
          c.accumulateCorrection(heat);
        } else {
          bug("moving just one");
          double e = c.measureError();
          if (Math.abs(e) > Math.abs(worstError)) {
            worst = c;
            worstError = e;
          }
        }
        c.pushLastError();
      }
      for (Constraint c : vars.getConstraints()) {
        if (debugOutput) {
          if (c == worst) {
            buf.append("[" + String.format(f + "] ", c.measureError()));
          } else {
            buf.append(String.format(f + " ", c.measureError()));
          }
        }
      }
      if (worst != null) {
        //        bug("Worst offender: " + worst);
        worst.accumulateCorrection(heat);
      }

      // 3: now all points have some accumulated correction. sum them and update the point's location.
      int numFinished = 0;
      double biggestMove = 0;
      for (Pt pt : vars.getPoints()) {
        List<Vec> corrections = (List<Vec>) pt.getAttribute(ACCUM_CORRECTION);
        pt.setBoolean("stable", corrections.size() == 0); // used by the UI
        if (corrections.size() == 0) {
          numFinished = numFinished + 1;
        }
        Vec delta = Vec.sum(corrections.toArray(new Vec[0]));
        double mag = delta.mag();
        biggestMove = max(biggestMove, mag);
        totalError = totalError + mag;
        pt.setAttribute(LAST_SOLVER_DESIRED_ADJUSTMENT_VEC, delta);
      }
      double biggestActualMove = 0;
      for (Pt pt : vars.getPoints()) {
        // respects the shape of root function:
        Vec desired = (Vec) pt.getAttribute(LAST_SOLVER_DESIRED_ADJUSTMENT_VEC);
        Vec delta = desired;
        if (biggestMove > 1) {
          double targetMag = desired.mag() / biggestMove;
          delta = desired.getVectorOfMagnitude(targetMag);
        }
        double mag = delta.mag();
        if (mag > 0.0) {
          delta = delta.getScaled(entropy.getDouble(heat)); // shorten delta by a random amount in range [0..heat]
          pt.move(delta);
          biggestActualMove = max(biggestActualMove, delta.mag());
          pt.setAttribute(LAST_SOLVER_ADJUSTMENT_VEC, delta);
        }
      }
      residual = totalError;
      if (totalError < MIN_ACCPETABLE_ERROR || numFinished == vars.getPoints().size()) {
        finished = true;
        currentState = State.Solved;
      }
      fire();
    } catch (Exception ex) {
      // and they say I have a software engineering background.
    }
    if (debugOutput) {
      buf.insert(0, String.format(f + " ", totalError));
      buf.insert(0, (prevError < totalError ? "*WORSE* " : "better! "));
      buf.insert(0, String.format("%#.3f ", heat));
      if (finished) {
        buf.insert(0, "done ");
      }
      if (debugOutWriter == null) {
        System.out.println(buf.toString());
      } else {
        try {
          debugOutWriter.write(buf.toString() + "\n");
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return totalError;
  }

  public boolean hasPoints(Pt... pts) {
    boolean ret = true;
    for (Pt pt : pts) {
      if (!vars.getPoints().contains(pt)) {
        ret = false;
        break;
      }
    }
    return ret;
  }

  public List<Pt> getPoints() {
    return vars.getPoints();
  }

  public synchronized void addPoint(String name, Pt pt) {
    if (hasName(pt) && !getName(pt).equals(name)) {
      Debug.stacktrace("warning: do you really want to change the name of this point from "
          + getName(pt) + " to " + name + "?", 10);
    }
    setName(pt, name);
    addPoint(pt);
  }

  public synchronized void addPoint(Pt pt) {
    if (!vars.getPoints().contains(pt)) {
      if (!hasName(pt)) {
        bug("warning: adding a point with no name");
      }
      //      Debug.stacktrace("made point " + pt.getString("name"), 8);
      vars.getPoints().add(pt);
    }
    if (ui != null) {
      ui.modelChanged();
    }
  }

  public List<Constraint> getConstraints() {
    return vars.getConstraints();
  }

  public VariableBank getVars() {
    return vars;
  }

  public void addConstraint(Constraint c) {
    if (!vars.getConstraints().contains(c)) {
      vars.getConstraints().add(c);
      if (ui != null) {
        ui.modelChanged();
      }
    }
  }

  public void removeConstraint(Constraint c) {
    Debug.stacktrace("removing basic constraint: " + c, 8);
    vars.getConstraints().remove(c);
    if (ui != null) {
      ui.modelChanged();
    }
  }

  public void wakeUp() {
    synchronized (monitor) {
      double err = calcTotalConstraintError();
      if (err > MIN_ACCPETABLE_ERROR) {
        finished = false;
        currentState = State.Working;
        monitor.notify();
      }
    }
  }

  public State getSolutionState() {
    return currentState;
  }

  public Set<Constraint> removePoint(Pt doomed) {
    vars.getPoints().remove(doomed);
    Set<Constraint> doomedConstraints = new HashSet<Constraint>();
    for (Constraint c : vars.getConstraints()) {
      c.remove(doomed);
      if (!c.isValid(vars)) {
        doomedConstraints.add(c);
      }
    }
    vars.getConstraints().removeAll(doomedConstraints);
    wakeUp();
    return doomedConstraints;
  }

  public void replacePoint(Pt oldPt, Pt newPt) {
    vars.getPoints().remove(oldPt);
    addPoint(newPt);
    if (!hasName(newPt)) {
      Debug.stacktrace("point has no name", 6);
    }
    for (Constraint c : vars.getConstraints()) {
      if (c.involves(oldPt)) {
        c.replace(oldPt, newPt);
      }
    }
    wakeUp();
  }

  public void replacePoint(Pt oldPt, String name, Pt newPt) {
    setName(newPt, name);
    replacePoint(oldPt, newPt);
  }

  public void clearConstraints() {
    vars.clear();
  }

  /**
   * Sets the name of the point, even if it is not involved in the solver's variables or
   * constraints.
   */
  public static void setName(Pt pt, String name) {
    pt.setString("name", name);
  }

  public static String getName(Pt pt) {
    return pt.getString("name");
  }

  public static boolean hasName(Pt pt) {
    return pt.hasAttribute("name");
  }

  public void setPaused(boolean v) {
    paused = v;
  }

  public boolean isPaused() {
    return paused;
  }

}
