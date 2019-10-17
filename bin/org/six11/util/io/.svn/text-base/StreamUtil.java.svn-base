// $Id$

package org.six11.util.io;

import java.io.*;

// import org.six11.slippy.SlippyMachine;

/**
 * Static functions for making life easier (at least, life as it pertains to reading and writing
 * Java streams like InputStream and OutputStream).
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public abstract class StreamUtil {

  public static String inputStreamToString(InputStream in) {
    StringBuilder out = new StringBuilder();
    try {
      byte[] b = new byte[4096];
      for (int n; (n = in.read(b)) != -1;) {
        out.append(new String(b, 0, n));
      }
    } catch (IOException ex) {
      out.append("exception: " + ex.getMessage());
    }
    return out.toString();
  }

  public static void writeFileToOutputStream(File f, OutputStream out) throws IOException {
    FileInputStream fin = new FileInputStream(f);
    writeInputStreamToOutputStream(fin, out);
  }

  public static void writeInputStreamToOutputStream(InputStream in, OutputStream out)
      throws IOException {
    byte[] buffer = new byte[256];
    int byteCount = 0;
    while ((byteCount = in.read(buffer)) >= 0) {
      out.write(buffer, 0, byteCount);
    }
  }

  public static void writeStringToOutputStream(String s, OutputStream out) throws IOException {
    ByteArrayInputStream bs = new ByteArrayInputStream(s.getBytes());
    writeInputStreamToOutputStream(bs, out);
  }
}
