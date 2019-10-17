// $Id: ProcessWatcher.java 23 2009-11-24 21:09:08Z gabe.johnson $

package org.six11.util.thread;

import java.io.IOException;

import org.six11.util.io.StreamUtil;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class ProcessWatcher {

  private static WorkerThread workerThread;

  public ProcessWatcher(final Process proc, final long timeout, final Runnable onFail,
      final Runnable onSuccess, final Runnable onTimeout) {
    Runnable runner = new Runnable() {
      public void run() {
        int result;
        try {
          result = proc.waitFor();
          if (result == 0 && onSuccess != null) {
            onSuccess.run();
          } else if (result != 0 && onFail != null) {
            onFail.run();
          }
        } catch (InterruptedException ex) {
          if (onFail != null) {
            onFail.run();
          }
        }
      }
    };
    getWorkerThread().add(runner);
    if (timeout > 0) {
      Runnable timeoutRunner = new Runnable() {
        public void run() {
          long endTime = System.currentTimeMillis() + timeout;
          while (System.currentTimeMillis() < endTime) {
            try {
              Thread.sleep(50);
            } catch (InterruptedException ignore) {
            }
          }
          boolean stillRunning = true;
          try {
            proc.exitValue();
            stillRunning = false;
          } catch (IllegalThreadStateException ex) {
          }
          if (stillRunning) {
            proc.destroy();
            if (onTimeout != null) {
              onTimeout.run();
            }
          }
        }
      };
      new Thread(timeoutRunner).start();
    }
  }

  private static WorkerThread getWorkerThread() {
    if (workerThread == null) {
      workerThread = new WorkerThread("ProcessWatcher worker thread");
    }
    return workerThread;
  }

  public static Runnable getDiagnostic(final Process proc) {
    return new Runnable() {
      public void run() {
        try {
          StreamUtil.writeInputStreamToOutputStream(proc.getInputStream(), System.out);
          StreamUtil.writeInputStreamToOutputStream(proc.getErrorStream(), System.out);
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    };
  }
}
