// $Id: FileUtil.java 285 2012-03-16 21:25:18Z gabe.johnson@gmail.com $

package org.six11.util.io;

import org.six11.util.Debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.io.*;

import javax.swing.JFileChooser;
// import java.io.*;
import javax.swing.filechooser.FileFilter;

/**
 * 
 **/
public abstract class FileUtil {

  public static String loadStringFromFile(String fileName) throws FileNotFoundException,
      IOException {
    return loadStringFromFile(new File(fileName));
  }

  public static void writeStringToFile(File file, String contents, boolean append) {
    BufferedWriter out;
    try {
      file.getParentFile().mkdirs();
      file.createNewFile();
      out = new BufferedWriter(new FileWriter(file, append));
      out.write(contents);
      out.close();
    } catch (IOException ex) {
      ex.printStackTrace();
      bug("Continuing without re-throwing above exception.");
    }
  }

  public static void writeStringToFile(String fileName, String contents, boolean append) {
    writeStringToFile(new File(fileName), contents, append);
  }

  private static void bug(String what) {
    Debug.out("FileUtil", what);
  }

  /**
   * The following function was taken from
   * http://www.java-tips.org/java-se-tips/java.io/reading-a-file-into-a-byte-array.html.
   */
  public static byte[] getBytesFromFile(File file) throws IOException {
    InputStream is = new FileInputStream(file);
    long length = file.length();

    if (length > Integer.MAX_VALUE) {
      return new byte[0];
    }
    byte[] bytes = new byte[(int) length];
    int offset = 0;
    int numRead = 0;
    while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
      offset += numRead;
    }
    if (offset < bytes.length) {
      throw new IOException("Could not completely read file " + file.getName());
    }
    is.close();
    return bytes;
  }

  public static String loadStringFromFile(File f) {
    try {
      BufferedReader in = new BufferedReader(new FileReader(f));
      String line;
      StringBuffer allText = new StringBuffer();
      while (in.ready()) {
        line = in.readLine();
        allText.append(line + "\n");
      }
      return allText.toString();
    } catch (Exception ex) {
      if (!f.exists()) {
        // Debug.out("FileUtil", f.getName() + ": no such file");
        throw new RuntimeException(f.getName() + ": no such file");
      }
      if (!f.canRead()) {
        // Debug.out("FileUtil", f.getName() + ": can not read");
        throw new RuntimeException(f.getName() + ": can not read");
      }
      ex.printStackTrace();
      return "";
    }
  }

  /**
   * Create a JFileChooser in a given directory that accepts files of a certain type. 'dir' is a
   * File object, but it could be null (in which case we just open the default dir). If it is
   * non-null, it is either a file or a directory. In either case we will use the deepest directory
   * as the place to look. In other words, /home/billybob/mystuff/mything.txt and
   * /home/billybob/mystuff will both resolve the the mystuff directory.
   */
  public static JFileChooser makeFileChooser(File dir, String suffix, final String description) {
    JFileChooser ret = new JFileChooser(dir);
    final String suffixWithDot = suffix.startsWith(".") ? suffix : "." + suffix;
    FileFilter f = new FileFilter() {
      public boolean accept(File f) {
        return (f != null && (f.getName().endsWith(suffixWithDot) || f.isDirectory()));
      }

      public String getDescription() {
        return description;
      }
    };
    ret.setFileFilter(f);
    return ret;
  }

  public static List<File> searchForSuffix(String suffix, File baseDir) {
    List<File> ret = new ArrayList<File>();
    SuffixFileFilter filter = new SuffixFileFilter(suffix);
    searchForSuffix(filter, baseDir, ret);
    return ret;
  }

  /**
   * Make a file whose name looks something my "myThing-3.txt". It searches for the first available
   * filename, where the number increases until it finds something.
   * 
   * @param baseDir
   *          the directory where you want the file to be made.
   * @param prefix
   *          the first part of the filename, such as "myThing". Do not include a dash.
   * @param suffix
   *          the end of the filename, such as ".txt". you must include a dot if your filename has
   *          it. e
   * @param startNum
   *          The number to start searching.
   * @return the first file of the above format it finds.
   */
  public static File makeIncrementalFile(File baseDir, String prefix, String suffix, int startNum) {
    File ret = null;
    int num = startNum;
    while (true) {
      ret = new File(baseDir, prefix + "-" + num + suffix);
      if (!ret.exists()) {
        break;
      }
      num = num + 1;
    }
    return ret;
  }

  private static void searchForSuffix(SuffixFileFilter suffix, File dir, List<File> inOut) {
    for (File child : dir.listFiles()) {
      if (suffix.accept(child)) {
        inOut.add(child);
      }
      if (child.isDirectory()) {
        searchForSuffix(suffix, child, inOut);
      }
    }
  }

  public static void copyTree(File src, File dest, FileFilter whitelist, FileFilter blacklist)
      throws IOException {
    Stack<File> sources = new Stack<File>();
    sources.push(src);
    while (!sources.empty()) {
      File copyMe = sources.pop();
      File[] children = copyMe.listFiles();
      for (File child : children) {
        boolean ok = true;
        if (whitelist != null) {
          ok = whitelist.accept(child);
        }
        if (blacklist != null) {
          ok = !blacklist.accept(child);
        }
        if (ok && child.isDirectory()) {
          sources.push(child);
        } else if (ok) {
          copyTree(src, dest, child);
        }
      }
    }
  }

  public static void copyTree(File srcBase, File destBase, File copyMe) throws IOException {
    String path = copyMe.getAbsolutePath().replace(srcBase.getAbsolutePath(),
        destBase.getAbsolutePath());
    File destFile = new File(path);
    destFile.getParentFile().mkdirs();
    destFile.createNewFile();
    FileOutputStream destFileOutput = new FileOutputStream(destFile);
    StreamUtil.writeFileToOutputStream(copyMe, destFileOutput);
    destFileOutput.close();
    System.out.println(destFile.getAbsolutePath() + " (" + destFile.length() + " bytes)");
  }

  public static boolean copy(File source, File dest) {
    boolean ret = true;
    try {
      FileInputStream in = new FileInputStream(source);
      FileOutputStream out = new FileOutputStream(dest);
      StreamUtil.writeInputStreamToOutputStream(in, out);
      out.close();
    } catch (FileNotFoundException ex) {
      ret = false;
    } catch (IOException ex) {
      ret = false;
    }
    return ret;
  }

  public static void complainIfNotExist(File f) throws IOException {
    if (!f.exists()) {
      throw new IOException(f.getAbsolutePath() + " does not exist");
    }
  }

  public static void complainIfNotReadable(File f) throws IOException {
    complainIfNotExist(f);
    if (!f.canRead()) {
      throw new IOException(f.getAbsolutePath() + " not readable");
    }
  }

  public static void complainIfNotWriteable(File f) throws IOException {
    complainIfNotReadable(f);
    if (!f.canWrite()) {
      throw new IOException(f.getAbsolutePath() + " not writeable");
    }
  }

  public static boolean deleteTree(File f) {
    boolean ret = true;
    if (f.isDirectory()) {
      for (File child : f.listFiles()) {
        ret = ret && deleteTree(child);
      }
    }
    ret = ret && f.delete();
    if (!f.exists()) {
      System.out.println("Deleted  " + f.getAbsolutePath());
    }
    return ret;
  }

  public static class FileFinder {
    List<File> dirs;

    public FileFinder(List<File> dirs) {
      this.dirs = dirs;
      for (File d : dirs) {
        if (!d.exists() || !d.canRead()) {
          throw new RuntimeException(d.getAbsolutePath() + " not readable");
        }
      }
    }

    public File search(String fileName) {
      File ret = null;
      File f;
      for (File d : dirs) {
        f = new File(d, fileName);
        if (f.exists() && f.canRead()) {
          ret = f;
          break;
        }
      }
      return ret;
    }

  }

  public static String getPath(String file) {
    String p = new File(file).getParentFile().getPath();
    return p;
  }

  public static void createIfNecessary(File f) throws IOException {
    if (!f.exists()) {
      if (!f.getParentFile().exists()) {
        boolean result = f.getParentFile().mkdirs();
        if (!result) {
          throw new IOException("Can't create parent directory: "
              + f.getParentFile().getAbsolutePath());
        }
      }
      boolean result = f.createNewFile();
      if (!result) {
        throw new IOException("Can't create new file: " + f.getAbsolutePath());
      }
    }
  }

}
