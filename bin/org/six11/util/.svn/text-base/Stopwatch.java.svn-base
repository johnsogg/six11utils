package org.six11.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Stopwatch {

  Map<String, Long> times;
  BufferedWriter log;

  public Stopwatch() {
    times = new HashMap<String, Long>();
  }

  public void start(String name) {
    long t = System.currentTimeMillis();
    times.put(name, t);
  }

  public long split(String name) {
    long t = 0;
    if (times.containsKey(name)) {
      long now = System.currentTimeMillis();
      t = now - times.get(name);
    }
    return t;
  }

  public long stop(String name) {
    long ret = split(name);
    times.remove(name);
    return ret;
  }

  public void setLogFile(String logfile) {
    try {
      log = new BufferedWriter(new FileWriter(logfile));
      System.out.println("Logging timing to " + logfile);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void log(long[] diffs) {
    if (log != null) {
      try {
        for (int i = 0; i < diffs.length; i++) {
          log.append(diffs[i] + "\t");
          log.flush();
        }

        log.append("\n");
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public void logHeaders(String[] headz) {
    if (log != null) {
      try {
        for (int i = 0; i < headz.length; i++) {
          log.append(headz[i] + "\t");
          log.flush();
        }
        log.append("\n");
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }

}
