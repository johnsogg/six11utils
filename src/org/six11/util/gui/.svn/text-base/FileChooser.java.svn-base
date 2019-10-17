package org.six11.util.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * 
 *
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public abstract class FileChooser {

  public static File chooseFile(String baseDir, String... extensions) {
    File ret = null;
    JFileChooser chooser = new JFileChooser(baseDir);
    FileFilter filter = new FileChooserFilter("Olive Files", extensions);
    chooser.setFileFilter(filter);
    int returnVal = chooser.showOpenDialog(null);
    if(returnVal == JFileChooser.APPROVE_OPTION) {
      ret = chooser.getSelectedFile();
    }
    return ret;
  }
}
