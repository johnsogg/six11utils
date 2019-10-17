package org.six11.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A simple tool for storing and retrieving non-critical information about how somebody uses an
 * application. For example, if the user stores an applications files in some directory, you can
 * remember that location by setting a value here, and retrieving it later on.
 * 
 * This is an alternative to the garbage in java.util.prefs, which is incomprehensible.
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class Preferences {

  private String appName;
  private File propsFile;
  private Properties props;
  private long lastRead;

  private Preferences(File where, String appName) {
    propsFile = where;
    this.appName = appName;
  }

  public File getPropertiesFile() {
    return propsFile;
  }
  
  /**
   * Uses the operating system's conventions to find or create a file for persisting properties.
   * 
   * On OS X, this file is ~/Library/Preferences/appName.properties
   * 
   * On Linux, this file is ~/.appName
   * 
   * On Windows, who knows? I'll just use ~/.appName and see what happens.
   * 
   * @param appName
   */
  public static Preferences makePrefs(String appName) throws IOException {
    if (isWindows() || isUnix()) {
      return makePrefs(new File(System.getProperty("user.home"), "." + appName), appName);
    } else if (isMac()) {
      return makePrefs(new File(System.getProperty("user.home") + File.separator + "Library"
          + File.separator + "Preferences", appName + ".properties"), appName);
    } else {
      System.out.println("Platform unknown using ~/." + appName + " for prefs.");
      return makePrefs(new File(System.getProperty("user.home"), "." + appName), appName);
    }
  }

  public static Preferences makePrefs(File propsFile, String appName) throws IOException {
    FileUtil.createIfNecessary(propsFile);
    Preferences prefs = new Preferences(propsFile, appName);
    return prefs;
  }

  public String getProperty(String key) {
    try {
      if (props == null || (lastRead < propsFile.lastModified())) {
        reload();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return props.getProperty(key);
  }

  public void setPropertyDefault(String key, String defaultValue) {
    String currentVal = getProperty(key);
    if (currentVal == null) {
      setProperty(key, defaultValue);
    }
  }
  
  public void setProperty(String key, String value) {
    try {
      if (props == null || (lastRead < propsFile.lastModified())) {
        reload();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    props.setProperty(key, value);
  }

  public void save() throws FileNotFoundException, IOException {
    props.store(new FileOutputStream(propsFile), "Properties for " + appName);
    lastRead = propsFile.lastModified();
  }

  private void reload() throws FileNotFoundException, IOException {
    props = new Properties();
    props.load(new FileInputStream(propsFile));
    lastRead = propsFile.lastModified();
  }

  public static boolean isWindows() {
    String os = System.getProperty("os.name").toLowerCase();
    return (os.indexOf("win") >= 0);
  }

  public static boolean isMac() {
    String os = System.getProperty("os.name").toLowerCase();
    return (os.indexOf("mac") >= 0);
  }

  public static boolean isUnix() {
    String os = System.getProperty("os.name").toLowerCase();
    return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
  }

}
