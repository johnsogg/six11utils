package org.six11.util.io;

import java.io.File;
import java.io.FileFilter;

/**
 * 
 *
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class SuffixFileFilter implements FileFilter {

  protected String suffix;
  
  public SuffixFileFilter(String suffix) {
    this.suffix = suffix;
  }
  
  public boolean accept(File pathname) {
    return pathname.getName().endsWith(suffix);
  }

}
