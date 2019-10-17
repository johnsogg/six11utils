// $Id$

package org.six11.util.thread;

import java.util.List;
import java.util.ArrayList;

/**
 * 
 **/
public class WorkerThread extends Thread {

  private List<Runnable> todo;

  public WorkerThread(String s) {
    super(s);
    todo = new ArrayList<Runnable>();
  }

  public void run() {
    while (true) {
      synchronized (todo) {
        if (todo.size() == 0) {
          try {
            todo.wait();
          } catch (InterruptedException ex) {
            // ignore
          } catch (Exception ex) {
            System.out.println("Caught exception in worker thread " + getName() + ". Continuing.");
          }
        } else {
          Runnable runme = todo.remove(0);
          runme.run();
        }
      }
    }
  }

  public void add(Runnable runme) {
    synchronized (todo) {
      todo.add(runme);
      todo.notifyAll();
    }
  }
}
