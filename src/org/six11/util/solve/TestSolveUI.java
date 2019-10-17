package org.six11.util.solve;

import static java.awt.event.InputEvent.CTRL_MASK;
//import static java.awt.event.InputEvent.META_MASK;
import static java.awt.event.InputEvent.SHIFT_MASK;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.six11.util.gui.ApplicationFrame;
import org.six11.util.gui.Components;
import org.six11.util.io.Preferences;
import org.six11.util.lev.NamedAction;
import org.six11.util.pen.DrawingBuffer;
import org.six11.util.pen.DrawingBufferRoutines;
import org.six11.util.pen.MouseThing;
import org.six11.util.pen.Pt;
import org.six11.util.pen.Vec;
import org.six11.util.solve.Manipulator.Param;

import static org.six11.util.solve.Constraint.setPinned;
import static org.six11.util.solve.Constraint.isPinned;
import static org.six11.util.Debug.bug;
import static org.six11.util.Debug.num;

public class TestSolveUI {

  private static final String ACTION_SAVE_AS = "save as";
  private static final String ACTION_SAVE = "save";
  private static final String ACTION_OPEN = "open";
  private static final String PREF_LAST_DIRECTORY = "lastDirectory";

  Map<String, Action> actions;
  File currentFile;
  ApplicationFrame af;
  DrawingBuffer buf;
  JComponent canvas;
  JDialog toolBox;
  JPanel editPane;
  ConstraintSolver main;
  Pt nearPt;
  Pt dragPt;
  Pt mousePt;
  Manipulator currentManipulator;
  ActionListener saveManipulatorAction;
  JDialog showAddPointsDialog;
  JTable table;
  MyTableModel tableModel;
  Preferences prefs;

  @SuppressWarnings("serial")
  public TestSolveUI(ConstraintSolver m) {
    try {
      prefs = Preferences.makePrefs("testSolver");
      bug("Loaded prefs: " + prefs.getPropertiesFile().getCanonicalPath());
    } catch (IOException e) {
      bug("Could not create prefs file.");
    }
    this.main = m;
    af = new ApplicationFrame("Test Solve UI");
    af.setSize(800, 600);
    buf = new DrawingBuffer();
    toolBox = buildToolBox();
    canvas = new JComponent() {
      protected void paintComponent(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        g.setColor(Color.WHITE);
        g.fill(getVisibleRect());
        drawBuffer();
        buf.paste(g);
      }
    };
    canvas.addMouseMotionListener(new MouseThing() {
      public void mouseMoved(MouseEvent ev) {
        Pt who = findPoint(new Pt(ev));
        mousePt = new Pt(ev);
        if (nearPt != who) {
          nearPt = who;
        }
        canvas.repaint();
      }

      public void mouseDragged(MouseEvent ev) {
        if (dragPt != null) {
          dragPt.setLocation(ev.getX(), ev.getY());
          canvas.repaint();
        }
      }

    });
    canvas.addMouseListener(new MouseThing() {
      public void mousePressed(MouseEvent ev) {
        Pt who = findPoint(new Pt(ev));
        if (mousePt == null) {
          mousePt = new Pt();
        }
        mousePt.setLocation(who);
        dragPt = who;
        canvas.repaint();
      }

      public void mouseClicked(MouseEvent ev) {
        Pt who = findPoint(new Pt(ev));
        setPinned(who, !isPinned(who)); // toggle
        canvas.repaint();
      }

      public void mouseReleased(MouseEvent ev) {
        dragPt = null;
        mousePt = null;
        main.wakeUp();
        tableModel.fireTableDataChanged();
        canvas.repaint();
      }

      public void mouseExited(MouseEvent ev) {
        mousePt = null;
        canvas.repaint();
      }
    });

    makeActions();
    Components.attachKeyboardAccelerators(af.getRootPane(), actions);
    Components.attachKeyboardAccelerators(toolBox.getRootPane(), actions);
    af.setLayout(new BorderLayout());
    af.add(canvas, BorderLayout.CENTER);
    Dimension dim = new Dimension(toolBox.getWidth() + af.getWidth(), toolBox.getHeight()
        + af.getHeight());
    Point2D boxPt = Components.centerRectangle(dim);
    toolBox.setLocation((int) boxPt.getX(), (int) boxPt.getY());
    boxPt.setLocation(boxPt.getX() + toolBox.getWidth(), boxPt.getY());
    af.setLocation((int) boxPt.getX(), (int) boxPt.getY());
    //    af.center();
    af.setVisible(true);
    toolBox.setVisible(true);
  }

  private void makeActions() {
    int mod = CTRL_MASK;
    int shiftMod = CTRL_MASK | SHIFT_MASK;
    actions = new HashMap<String, Action>();

    // Save Action
    actions.put(ACTION_SAVE, new NamedAction("Save", KeyStroke.getKeyStroke(KeyEvent.VK_S, mod)) {
      public void activate() {
        save();
      }
    });

    // Save As Action
    actions.put(ACTION_SAVE_AS,
        new NamedAction("Save As...", KeyStroke.getKeyStroke(KeyEvent.VK_S, shiftMod)) {
          public void activate() {
            saveAs();
          }
        });

    // Open Action
    actions.put(ACTION_OPEN, new NamedAction("Open", KeyStroke.getKeyStroke(KeyEvent.VK_O, mod)) {
      public void activate() {
        open();
      }
    });
  }

  private JDialog buildToolBox() {
    JDialog ret = new JDialog(af, ModalityType.MODELESS);
    JPanel buttonPane = new JPanel();
    JButton addPointsButton = new JButton("Add Points...");
    addPointsButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        showAddPointsDialog();
      }
    });
    buttonPane.add(addPointsButton);
    final JComboBox addConstraintBox = new JComboBox(createManipulators());
    addConstraintBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        setManipulator((Manipulator) addConstraintBox.getSelectedItem());
      }
    });

    buttonPane.add(addConstraintBox);
    table = new JTable(2, 0);
    tableModel = new MyTableModel();
    table.setModel(tableModel);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.getColumnModel().getColumn(0).setHeaderValue("Object");
    table.getColumnModel().getColumn(1).setMaxWidth(60);
    table.getColumnModel().getColumn(1).setHeaderValue("Error");
    table.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          int row = table.rowAtPoint(e.getPoint());
          Constraint constraint = tableModel.getConstraint(row);
          Manipulator useMe = constraint.getManipulator(main.vars);
          setManipulator(useMe);
        }
      }
    });
    KeyStroke delKey = KeyStroke.getKeyStroke((char) KeyEvent.VK_BACK_SPACE);

    Action delAction = new AbstractAction() {
      public void actionPerformed(ActionEvent ev) {
        int row = table.getSelectionModel().getLeadSelectionIndex();
        Constraint constraint = tableModel.getConstraint(row);
        main.vars.getConstraints().remove(constraint);
        modelChanged();
      }
    };
    table.getInputMap().put(delKey, delAction);
    table.getActionMap().put(delAction, delAction);

    JScrollPane tablePane = new JScrollPane(table);
    saveManipulatorAction = new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        bug("saveManipulatorAction...");
        if (currentManipulator != null) {
          if (currentManipulator.isNew()) {
            bug("Manipulator is new. Trying to save it.");
            Manipulator prev = currentManipulator;
            currentManipulator = currentManipulator.makeInstance(main.vars);
            if (currentManipulator.isConstraint()) {
              main.addConstraint(currentManipulator.getConstraint());
            }
            setManipulator(prev);
          } else {
            bug("Manipulator is not new. SO i should save to the existing constraint");
            currentManipulator.getConstraint().assume(currentManipulator, main.vars);
          }
        } else {
          bug("current manipulator is null!");
        }
        modelChanged();
        main.wakeUp();
      }
    };

    editPane = new JPanel();
    editPane.setLayout(new BorderLayout());
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editPane, tablePane);
    splitPane.setDividerLocation(160);
    ret.setLayout(new BorderLayout());
    ret.add(buttonPane, BorderLayout.NORTH);
    ret.add(splitPane, BorderLayout.CENTER);
    JPanel fpsPane = new JPanel();
    fpsPane.add(new JLabel("Frames per second:"));
    final JTextField fpsText = new JTextField("" + main.fps, 6);
    fpsText.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        bug("actionPerformed");
        String frameString = fpsText.getText();
        bug("frameString: " + frameString);
        try {
          int frameRate = Integer.parseInt(frameString.trim());
          main.fps = frameRate;
          bug("frameRate: " + frameRate);
        } catch (NumberFormatException ex) { }
      }
    });
    fpsPane.add(fpsText);
    ret.add(fpsPane, BorderLayout.SOUTH);
    ret.setPreferredSize(new Dimension(400, 600));
    ret.pack();
    return ret;
  }

  protected void showAddPointsDialog() {
    if (showAddPointsDialog == null) {
      showAddPointsDialog = new JDialog(af, ModalityType.MODELESS);
      KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      JRootPane rootPane = showAddPointsDialog.getRootPane();
      ActionListener closeMe = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
          showAddPointsDialog.setVisible(false);
        }
      };
      rootPane.registerKeyboardAction(closeMe, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
      JPanel content = new JPanel();
      JLabel instructions = new JLabel("Point Name:");
      final JTextField ptName = new JTextField(6);
      ptName.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          String val = ptName.getText();
          Pt p = main.mkRandomPoint(canvas);
          main.addPoint(val, p);
          ptName.selectAll();
          canvas.repaint();
        }
      });
      content.add(instructions);
      content.add(ptName);
      showAddPointsDialog.add(content);
      showAddPointsDialog.pack();
      showAddPointsDialog.setLocation(toolBox.getX() + 40, toolBox.getY() + 40);
    }
    showAddPointsDialog.setVisible(true);
  }

  private void setManipulator(Manipulator manip) {
    this.currentManipulator = manip;
    editPane.removeAll();
    editPane.add(new JLabel("Editor for " + manip.label), BorderLayout.NORTH);
    JPanel paramBox = new JPanel();
    paramBox.setLayout(new GridLayout(0, 2));
    JTextField first = null;
    for (Manipulator.Param p : manip.params) {
      paramBox.add(new JLabel(p.helpText));
      JTextField textbox = makeAutosaveTextbox(p);
      if (first == null) {
        first = textbox;
      }
      textbox.addActionListener(saveManipulatorAction);
      paramBox.add(textbox);
    }
    JScrollPane paramScroller = new JScrollPane(paramBox,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    editPane.add(paramScroller, BorderLayout.CENTER);
    editPane.revalidate();
    if (first != null) {
      bug("first text box is requesting focus.");
      first.requestFocus();
    }
  }

  private JTextField makeAutosaveTextbox(final Param p) {
    final JTextField ret = new JTextField(6);
    ret.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent ev) {
        whack();
      }

      public void insertUpdate(DocumentEvent ev) {
        whack();
      }

      public void removeUpdate(DocumentEvent ev) {
        whack();
      }

      void whack() {
        p.value = ret.getText();
      }
    });
    if (p.value != null) {
      ret.setText(p.value);
    }
    return ret;
  }

  private Manipulator[] createManipulators() {
    List<Manipulator> men = new ArrayList<Manipulator>();
    men.add(AngleConstraint.getManipulator());
    men.add(DistanceConstraint.getManipulator());
    men.add(LocationConstraint.getManipulator());
    men.add(OrientationConstraint.getManipulator());
    men.add(PointAsLineParamConstraint.getManipulator());
    men.add(PointOnLineConstraint.getManipulator());
    Manipulator[] ret = men.toArray(new Manipulator[men.size()]);
    return ret;
  }

  private Pt findPoint(Pt cursor) {
    double best = Double.MAX_VALUE;
    Pt ret = null;
    for (Pt pt : main.getPoints()) {
      double dist = pt.distance(cursor);
      if (dist < best) {
        best = dist;
        ret = pt;
      }
    }
    return ret;
  }

  private void drawBuffer() {
    buf.clear();
    // if there is a mouse point and a near point, it means the user has moved the mouse and 
    // a nearby point is activated. Draw an arrow between them.
    if (mousePt != null && nearPt != null && mousePt.distance(nearPt) > 40) {
      Vec mToN = new Vec(mousePt, nearPt).getUnitVector();
      DrawingBufferRoutines.arrow(buf, mousePt,
          mousePt.getTranslated(mToN, mousePt.distance(nearPt) - 20), 1.4, Color.magenta);
    }

    List<Constraint> constraints = main.getConstraints();
    List<Pt> points = main.getPoints();
    Pt msgCursor = new Pt(12, 12);
    if (main.msg != null && main.msg.length() > 0) {
      DrawingBufferRoutines.text(buf, msgCursor, main.msg, Color.BLACK);
      msgCursor.setLocation(msgCursor.x, msgCursor.y + 20);
    }
    for (Constraint c : constraints) {
      String msg = c.getMessages();
      if (msg.length() > 0) {
        DrawingBufferRoutines.text(buf, msgCursor, msg, Color.BLACK);
        msgCursor.setLocation(msgCursor.x, msgCursor.y + 20);
      }
      c.draw(buf);
    }
    switch (main.getSolutionState()) {
      case Solved:
        DrawingBufferRoutines.text(buf, msgCursor, "Solved", Color.GREEN.darker().darker());
        break;
      case Unsatisfied:
        DrawingBufferRoutines.text(buf, msgCursor, "Unsatisfied", Color.YELLOW.darker().darker().darker());
        break;
      case Working:
        DrawingBufferRoutines.text(buf, msgCursor, "Working...", Color.RED.darker());
        break;
    }
//    if (main.finished) {
//      DrawingBufferRoutines.text(buf, msgCursor, "Solved", Color.GREEN.darker().darker());
//    } else {
//      DrawingBufferRoutines.text(buf, msgCursor, "Working...", Color.RED.darker());
//    }

    for (Pt pt : points) {
      Color fillColor = Color.BLUE;
      if (isPinned(pt)) {
        fillColor = Color.GREEN.darker();
      } else if (pt.hasAttribute("stable") && pt.getBoolean("stable")) {
        fillColor = Color.LIGHT_GRAY;
      }
      DrawingBufferRoutines.text(buf, pt.getTranslated(0, -10), pt.getString("name"),
          Color.GREEN.darker());
      double radius = 5;
      if (pt == nearPt) {
        radius = 10;
      }
      double borderThickness = 1.8;
      Color borderColor = Color.BLACK;
      if (isPinned(pt)) {
        borderThickness = 3.0;
        borderColor = Color.GREEN.darker().darker();
      }
      DrawingBufferRoutines.dot(buf, pt, radius, borderThickness, borderColor, fillColor);
    }
  }

  class MyTableModel extends AbstractTableModel {

    public int getColumnCount() {
      return 2;
    }

    public int getRowCount() {
      return main.vars.getConstraints().size();
    }

    public Object getValueAt(int row, int col) {
      Object ret = null;
      Constraint c = main.vars.getConstraints().get(row);
      if (col == 0) {
        ret = c.getHumanDescriptionString();
      } else if (col == 1) {
        double e = c.measureError();
        ret = num(e);
        if (ret.toString().equals("-0.0") || ret.toString().equals("0.0")) {
          ret = "0";
        }
      }
      return ret;
    }

    public Constraint getConstraint(int row) {
      return main.vars.getConstraints().get(row);
    }

  }

  public void modelChanged() {
    canvas.repaint();
    tableModel.fireTableDataChanged();
  }

  public void save() {
    bug("save");
    if (currentFile == null) {
      saveAs();
    } else {
      save(currentFile);
    }
  }

  private JFileChooser makeFileChooser() {
    JFileChooser fileChooser = new JFileChooser();
    if (currentFile != null) {
      fileChooser.setCurrentDirectory(currentFile.getParentFile());
    } else if (prefs.getProperty(PREF_LAST_DIRECTORY) != null) {
      File lastDirectory = new File(prefs.getProperty("lastDirectory"));
      fileChooser.setCurrentDirectory(lastDirectory);
    }
    return fileChooser;
  }

  public void saveAs() {
    bug("save as");
    JFileChooser fileChooser = makeFileChooser();
    int retVal = fileChooser.showSaveDialog(af);
    if (retVal == JFileChooser.APPROVE_OPTION) {
      save(fileChooser.getSelectedFile());
    }
  }

  public void save(File file) {
    try {
      if (!file.exists()) {
        file.createNewFile();
      }
      JsonIO io = new JsonIO();
      JSONArray points = io.write(main.vars.getPoints(), "name", "pinned");
      JSONArray constraints = io.write(main.vars.getConstraints());
      JSONObject top = new JSONObject();
      top.put("points", points);
      top.put("constraints", constraints);
      FileWriter writer = new FileWriter(file);
      writer.write(top.toString());
      writer.flush();
      writer.close();
      setCurrentFile(file);
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public void open() {
    JFileChooser fileChooser = makeFileChooser();
    int retVal = fileChooser.showOpenDialog(af);
    if (retVal == JFileChooser.APPROVE_OPTION) {
      setCurrentFile(fileChooser.getSelectedFile());
      open(currentFile);
    }
  }

  private void setCurrentFile(File f) {
    currentFile = f;
    prefs.setProperty(PREF_LAST_DIRECTORY, currentFile.getParent());
    try {
      prefs.save();
      bug("Saved prefs.");
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void open(File file) {
    try {
      JsonIO io = new JsonIO();
      FileReader reader = new FileReader(file);
      JSONTokener toks = new JSONTokener(reader);
      JSONObject top = new JSONObject(toks);
      JSONArray pointArray = top.getJSONArray("points");
      JSONArray constraintArray = top.getJSONArray("constraints");
      List<Pt> points = io.readPoints(pointArray, "name", "pinned");
      main.vars.getPoints().clear();
      main.vars.getConstraints().clear();
      main.vars.getPoints().addAll(points);
      List<Constraint> constraints = io.readConstraints(constraintArray, main.vars);
      main.vars.getConstraints().addAll(constraints);
      modelChanged();
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
