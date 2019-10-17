// $Id$

package org.six11.util.gui;

import java.awt.Image;
import java.net.URL;
import java.awt.Toolkit;
import java.awt.Component;
import java.awt.MediaTracker;
import java.awt.Graphics2D;
import java.awt.image.ColorModel;
import java.awt.image.VolatileImage;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.List;

/**
 * A utility class for working with images.
 **/
public class Images {

  protected final static Component component = new Component() {
  };
  protected final static MediaTracker tracker = new MediaTracker(component);

  private static int mediaTrackerID;

  public static Image loadImage(String name) {
    URL url = Thread.currentThread().getContextClassLoader().getResource(name);
    Toolkit kit = Toolkit.getDefaultToolkit();
    Image img = kit.getImage(url);
    loadImage(img);
    return img;
  }

  protected static void loadImage(Image image) {
    synchronized (tracker) {
      int id;
      synchronized (tracker) {
        id = ++mediaTrackerID;
      }

      tracker.addImage(image, id);
      try {
        tracker.waitForID(id, 0);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }

      tracker.removeImage(image, id);
    }
  }

  public static VolatileImage drawVolatileImage(Graphics2D g, VolatileImage vol, int x, int y,
      Image orig, boolean saveSnap, List<BufferedImage> snapList) {
    // due to the fickle nature of hardware, the volatile image might
    // get screwed up at any point in the process, so we might have to
    // try rendering it a number of times.
    for (int i = 0; i < 100; i++) {
      if (vol != null) {
        BufferedImage snap = null;
        if (saveSnap) {
          snap = vol.getSnapshot();
        }
        g.drawImage(vol, x, y, null);

        if (!vol.contentsLost()) {
          // mission accomplished
          if (saveSnap && snapList != null) {
            snapList.add(snap);
          }
          return vol;
        }
      } else {
        vol = g.getDeviceConfiguration().createCompatibleVolatileImage(orig.getWidth(null),
            orig.getHeight(null));
      }

      // if we haven't returned yet, the image was either null to
      // start with or some contents have been lost. So, try and
      // (re)constitute it.
      switch (vol.validate(g.getDeviceConfiguration())) {
        case VolatileImage.IMAGE_INCOMPATIBLE:
          // you moved the thing to another display device?
          vol.flush();
          vol = g.getDeviceConfiguration().createCompatibleVolatileImage(orig.getWidth(null),
              orig.getHeight(null));
          // don't break
        case VolatileImage.IMAGE_OK:
        case VolatileImage.IMAGE_RESTORED:
          // copy the original image to accelerated image memory
          Graphics2D gcopy = (Graphics2D) vol.createGraphics();
          gcopy.drawImage(orig, 0, 0, null);
          gcopy.dispose();
          break;
      }
    }

    // If we get here, it means that 'mission accomplished' didn't
    // really happen. Punt. Copy the lame non-accelerated image.
    g.drawImage(orig, x, y, null);
    return vol;

  }

  public static BufferedImage deepCopy(BufferedImage bi) {
    ColorModel cm = bi.getColorModel();
    boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
    WritableRaster raster = bi.copyData(null);
    return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
   }
}
