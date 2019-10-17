// $Id: FileChooserFilter.java 12 2009-11-09 22:58:47Z gabe.johnson $

package org.six11.util.gui;

import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import java.util.List;
import java.util.ArrayList;
import java.io.File;

public class FileChooserFilter extends FileFilter {

  protected List<String> extensions;
  protected String desc;

  public FileChooserFilter(String desc, String... ext) {
    this.desc = desc;
    this.extensions = new ArrayList<String>();
    for (String s : ext) {
      extensions.add(s);
    }
  }

  public boolean accept(File f) {
    String n = f.getName();
    boolean ok = false;
    if (f.isDirectory()) {
      ok = true;
    } else {
      for (String ex : extensions) {
	if (n.endsWith(ex)) {
	  ok = true;
	  break;
	}
      }
    }
    return ok;
  }

  public String getDescription() {
    return desc;
  }

  public static JFileChooser getChooser(String dir, String desc, String... ext) {
    FileChooserFilter filt = new FileChooserFilter(desc, ext);
    Chooser chooser = new Chooser(dir, filt);
    return chooser;
  }

  public static class Chooser extends JFileChooser {
    public Chooser(String dir, FileChooserFilter filt) {
      super(dir);
      setFileFilter(filt);
    }
  }

}
