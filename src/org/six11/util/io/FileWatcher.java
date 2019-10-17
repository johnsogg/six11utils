package org.six11.util.io;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.six11.util.Debug;
import org.six11.util.args.Arguments;
import org.six11.util.data.Lists;

public class FileWatcher {
  public static void main(String[] in) throws Exception {
    if (in.length == 0) {
      System.out
          .print("watch [--handler=FileWatcherHandlerImplementation] [filename1 filename2 ...]\n");
      System.exit(0);
    }
    Arguments args = new Arguments(in);
    Handler handler = new Handler() {
      public void init(WatchedFile wf) {
        System.out.println("Watching file: " + wf.fileName);
      }

      public void noticeChange(WatchedFile wf) {
        System.out.println("File changed: " + wf.fileName);
        ;
      }
    };
    if (args.hasValue("handler")) {
      System.out.println("Attempting to load handler: " + args.getValue("handler"));
      Class handlerClass = Class.forName(args.getValue("handler"));
      Constructor constr = handlerClass.getConstructor();
      handler = (Handler) constr.newInstance();
      System.out.println("Using handler defined by " + handlerClass.getName());
    }
    List<WatchedFile> files = new ArrayList<WatchedFile>();
    for (int i = 0; i < args.getPositionCount(); i++) {
      files.add(new WatchedFile(args.getPosition(i)));
      handler.init(Lists.getLast(files));
    }
    new FileWatcher().watch(handler, files);
  }

  public void watch(Handler handler, List<WatchedFile> files) {
    int errorCount = 0;
    while (true) {
      try {
        synchronized (files) {
          for (WatchedFile wf : files) {
            File file = new File(wf.fileName);
            if (wf.modTime == 0) {
              wf.modTime = file.lastModified();
            }
            if (wf.modTime < file.lastModified()) {
              wf.modTime = file.lastModified();
              handler.noticeChange(wf);
            }
            errorCount = 0;
          }
        }
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ex) {

        }
      } catch (Exception ex) {
        errorCount++;
        if (errorCount > 10) {
          System.out.println("Maximum error count reached.");
          System.exit(0);
        }
      }
    }
  }

  public static interface Handler {
    public void init(WatchedFile wf);

    public void noticeChange(WatchedFile wf);
  }

  public static class WatchedFile {
    public String fileName;
    public long modTime;

    public WatchedFile(String fn) {
      this.fileName = fn;
      this.modTime = 0;
    }

    public String toString() {
      return fileName;
    }
  }
}
