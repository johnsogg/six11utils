package org.six11.util.pen;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import org.six11.util.Debug;
import org.six11.util.gui.ApplicationFrame;
import org.six11.util.gui.BoundingBox;
import org.six11.util.io.FileUtil;
import org.six11.util.io.Preferences;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class SketchReader extends JPanel implements DropTargetListener {
  private static final String PROP_PDF_DIR = "pdfDir";
  private static final String MAKE_PNG_IMG = "makePngImg";
  private static final String MAKE_PDF_IMG = "makePdfImg";

  public static void main(String[] args) throws IOException {
    Debug.useColor = false;
    Debug.useTime = false;

    ApplicationFrame af = new ApplicationFrame("Sketch Reader: Drag sketch files here");
    SketchReader sr = new SketchReader();
    af.add(sr);
    af.setSize(600, 100);
    af.center();
    af.setVisible(true);
  }

  private OliveDrawingSurface ds;
  private Preferences prefs;
  private JCheckBox pngCb;
  private JCheckBox pdfCb;
  private JLabel dirLabel;
  private JButton dirButton;
  private File outDir;

  public SketchReader() throws IOException {
    try {
      prefs = Preferences.makePrefs("skrui");
    } catch (IOException ex) {
      bug("Got IOException when making prefs object. This is going to ruin your day.");
      ex.printStackTrace();
    }
    ds = new OliveDrawingSurface();
    outDir = maybeGetInitialDir(PROP_PDF_DIR);
    setLayout(new GridLayout(0, 1));
    new DropTarget(this, this);
    pngCb = new JCheckBox("Make PNG Images", getInitialBool(MAKE_PNG_IMG));
    pngCb.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setProperty(MAKE_PNG_IMG, "" + pngCb.isSelected());
      }
    });
    pdfCb = new JCheckBox("Make PDF Images", getInitialBool(MAKE_PDF_IMG));
    pdfCb.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setProperty(MAKE_PDF_IMG, "" + pdfCb.isSelected());
      }
    });
    add(pngCb);
    add(pdfCb);
    JPanel chooser = new JPanel();
    chooser.setLayout(new BorderLayout());
    dirLabel = new JLabel();
    if (outDir == null) {
      dirLabel.setText("(nothing set yet)");
    } else {
      dirLabel.setText(outDir.getCanonicalPath());
    }
    dirButton = new JButton("Choose output directory...");
    dirButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        chooseDir();
      }
    });
    chooser.add(dirButton, BorderLayout.WEST);
    chooser.add(dirLabel, BorderLayout.CENTER);
    add(chooser);
  }

  public void setProperty(String key, String value) {
    prefs.setProperty(key, value);
    try {
      prefs.save();
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  protected void chooseDir() {
    File initialDir = maybeGetInitialDir(PROP_PDF_DIR);
    JFileChooser chooser = new JFileChooser();
    if (initialDir != null) {
      chooser.setCurrentDirectory(initialDir);
    }
    chooser.setDialogTitle("Pick a Directory for PNG and PDF files to go");
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    int result = chooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      File inFile = chooser.getSelectedFile();
      dirLabel.setText(inFile.getAbsolutePath());
      setProperty(PROP_PDF_DIR, inFile.getAbsolutePath());
      outDir = inFile;
    }
  }

  private File maybeGetInitialDir(String propKey) {
    File ret = null;
    String path = prefs.getProperty(propKey);
    if (path != null) {
      ret = new File(path);
    }
    bug("Initial dir for '" + propKey + "' is " + (ret == null ? "''" : ret.getAbsolutePath()));
    return ret;
  }

  private boolean getInitialBool(String propKey) {
    boolean ret = false;
    String str = prefs.getProperty(propKey);
    if (str != null) {
      ret = Boolean.parseBoolean(str);
    }
    return ret;
  }

  private void writeSketch(File inFile) throws IOException {
    String fileName = inFile.getName();
    FileUtil.complainIfNotReadable(inFile);
    BufferedReader in = new BufferedReader(new FileReader(inFile));
    List<Sequence> sequences = SequenceIO.readAll(in);
    bug("Read " + sequences.size() + " sequences from " + inFile.getAbsolutePath());
    ds.getSoup().clearDrawing();
    double x = 0;
    double y = 0;
    for (Sequence seq : sequences) {
      ds.getSoup().setCurrentSequenceShapeVisible(true);
      ds.getSoup().addFinishedSequence(seq);
      for (Pt pt : seq) {
        x = Math.max(x, pt.getX());
        y = Math.max(y, pt.getY());
      }
    }
    String fileNamePrefix = fileName;
    if (fileName.indexOf(".sketch") > 0) {
      fileNamePrefix = fileName.substring(0, fileName.indexOf(".sketch"));
    } else if (fileName.indexOf(".log") > 0) {
      fileNamePrefix = fileName.substring(0, fileName.indexOf(".log"));
    }
    if (pdfCb.isSelected()) {
      File pdfFile = new File(outDir, fileNamePrefix + ".pdf");
      bug("Making a pdf: " + pdfFile.getAbsolutePath());
      savePdf(pdfFile);
    }
    if (pngCb.isSelected()) {
      File pngFile = new File(outDir, fileNamePrefix + ".png");
      bug("Making a png: " + pngFile.getAbsolutePath());
      savePng(pngFile, (int) (x + 10), (int) (y + 10));
    }
  }

  private void savePng(File pngFile, int w, int h) {
    BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = img.createGraphics();
    ds.paintContent(g, false);
    try {
      ImageIO.write(img, "PNG", pngFile);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  private void savePdf(File outFile) {
    outFile = outFile.getAbsoluteFile();
    FileOutputStream out;
    try {
      out = new FileOutputStream(outFile);
      List<DrawingBuffer> layers = ds.getSoup().getDrawingBuffers();
      bug("There are " + layers.size() + " layers");
      BoundingBox bb = new BoundingBox();
      for (DrawingBuffer layer : layers) {
        layer.update();
        bb.add(layer.getBoundingBox());
      }

      int w = bb.getWidthInt();
      int h = bb.getHeightInt();
      Rectangle size = new Rectangle(w, h);
      Document document = new Document(size, 0, 0, 0, 0);
      try {
        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();
        DefaultFontMapper mapper = new DefaultFontMapper();
        PdfContentByte cb = writer.getDirectContent();
        PdfTemplate tp = cb.createTemplate(w, h);
        Graphics2D g2 = tp.createGraphics(w, h, mapper);
        tp.setWidth(w);
        tp.setHeight(h);
        g2.translate(-bb.getX(), -bb.getY());
        ds.paintContent(g2, false);
        g2.dispose();
        cb.addTemplate(tp, 0, 0);
      } catch (DocumentException ex) {
        bug(ex.getMessage());
      }
      document.close();
      System.out.println("Wrote " + outFile.getAbsolutePath());
    } catch (FileNotFoundException ex1) {
      ex1.printStackTrace();
    }

  }

  private static void bug(String what) {
    Debug.out("SketchReader", what);
  }

  public void dragEnter(DropTargetDragEvent dtde) {
    
  }

  public void dragExit(DropTargetEvent dte) {
    
  }

  public void dragOver(DropTargetDragEvent dtde) {
    
  }

  @SuppressWarnings("unchecked")
  public void drop(DropTargetDropEvent dtde) {
    
    try {
      // Ok, get the dropped object and try to figure out what it is
      Transferable tr = dtde.getTransferable();
      DataFlavor[] flavors = tr.getTransferDataFlavors();
      for (int i = 0; i < flavors.length; i++) {
        if (flavors[i].isFlavorJavaFileListType()) {

          dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
          List<File> list = (List<File>) tr.getTransferData(flavors[i]);
          for (int j = 0; j < list.size(); j++) {
            bug(list.get(j).getAbsolutePath());
            writeSketch(list.get(j));
          }

          Desktop.getDesktop().open(outDir);
          // If we made it this far, everything worked.
          dtde.dropComplete(true);
          return;
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      dtde.rejectDrop();
    }
  }

  public void dropActionChanged(DropTargetDragEvent dtde) {
    
  }
}
