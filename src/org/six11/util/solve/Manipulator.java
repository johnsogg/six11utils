package org.six11.util.solve;

import java.util.HashMap;
import java.util.Map;

import org.six11.util.pen.Pt;

import static org.six11.util.Debug.bug;

public class Manipulator {

  public static final String ADD_POINT = "Add Point";
  public static final String ADD_DISTANCE = "Add Distance Constraint";
  public static final String ADD_ANGLE = "Add Angle Constraint";
  public static final String ADD_ORIENTATION = "Add Orientation Constraint";
  public static final String ADD_POINT_AS_LINE_PARAM = "Add Point-As-Line-Param Constraint";
  public static final String ADD_POINT_ON_LINE = "Add point on line Constraint";

  Class<?> ptOrConstraint; // Pt, Constraint, NumericValue, etc... could be many things
  String label;
  Param[] params;
  boolean newThing;
  Constraint constraint;
  Pt myPoint;

  public Manipulator(Class<?> ptOrConstraint, String label, Param... params) {
    this.ptOrConstraint = ptOrConstraint;
    this.label = label;
    this.params = params;
    newThing = true;
    constraint = null;
    myPoint = null;
  }

  public Manipulator(Manipulator other, VariableBank vars) {
    this.ptOrConstraint = other.ptOrConstraint;
    this.label = other.label;
    this.params = new Param[other.params.length];
    for (int i = 0; i < params.length; i++) {
      Param o = other.params[i];
      params[i] = new Param(o.key, o.helpText, o.required);
      params[i].value = o.value; // copy value, not reference to value
    }
    try {
      if (Constraint.class.isAssignableFrom(ptOrConstraint)) {
        Constraint c = (Constraint) ptOrConstraint.newInstance();
        c.assume(this, vars);
        this.constraint = c;
        this.newThing = false;
      } else {
        bug("No I can't assign " + ptOrConstraint);
      }
    } catch (InstantiationException ex) {
      bug("Can't instantiate class: " + ptOrConstraint.getName()
          + ". Maybe you didn't implement the nullary constructor?");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void setParamValue(String key, String value) {
    for (Param p : params) {
      if (p.key.equals(key)) {
        p.value = value;
        break;
      }
    }
  }

  public String toString() {
    return label;
  }

  public static class Param {

    boolean required;
    String helpText;
    String key;
    String value;

    public Param(String key, String helpText, boolean required) {
      this.key = key;
      this.helpText = helpText;
      this.required = required;
      this.value = "";
    }

    //    private JTextField createEditBox() {
    //      final JTextField ret = new JTextField(12);
    //      ret.addFocusListener(new FocusListener() {
    //        public void focusGained(FocusEvent arg0) {
    //          ret.selectAll();
    //        }
    //
    //        public void focusLost(FocusEvent arg0) {
    //        }
    //
    //      });
    //      ret.getDocument().addDocumentListener(new DocumentListener() {
    //        public void changedUpdate(DocumentEvent ev) {
    //          whack();
    //        }
    //
    //        public void insertUpdate(DocumentEvent ev) {
    //          whack();
    //        }
    //
    //        public void removeUpdate(DocumentEvent ev) {
    //          whack();
    //        }
    //
    //        void whack() {
    //          Runnable runner = new Runnable() {
    //            public void run() {
    //              if (ret.getText().length() == 0) {
    //                ret.setText(helpText);
    //              } else if (ret.getText().length() > helpText.length()
    //                  && ret.getText().startsWith(helpText)) {
    //                ret.setText(ret.getText().substring(helpText.length()));
    //              }
    //              if (ret.getText().equals(helpText)) {
    //                ret.setForeground(Color.LIGHT_GRAY);
    //              } else {
    //                ret.setForeground(Color.BLACK);
    //              }
    //            }
    //          };
    //          SwingUtilities.invokeLater(runner);
    //        }
    //      });
    //      ret.addActionListener(new ActionListener() {
    //        public void actionPerformed(ActionEvent ev) {
    //          ret.selectAll();
    //        }        
    //      });
    //      ret.setText(helpText);
    //      ret.setForeground(Color.LIGHT_GRAY);
    //      ret.setFont(new Font("Dialog", required ? Font.BOLD : Font.PLAIN, 16));
    //      return ret;
    //    }
  }

  public String getValue(String key) {
    String ret = "Unknown";
    for (Param p : params) {
      if (p.key.equals(key)) {
        ret = p.value;
        break;
      }
    }
    return ret;
  }

  public boolean isNew() {
    return newThing;
  }

  public Manipulator makeInstance(VariableBank vars) {
    Manipulator ret = new Manipulator(this, vars);
    return ret;
  }

  /**
   * Creates and returns a map relating parameter keys to parameter values.
   */
  public Map<String, String> getParamsAsMap() {
    Map<String, String> ret = new HashMap<String, String>();
    for (Param p : params) {
      ret.put(p.key, p.value);
    }
    return ret;
  }

  public boolean isConstraint() {
    return constraint != null;
  }

  public boolean isPoint() {
    return myPoint != null;
  }

  public Constraint getConstraint() {
    return constraint;
  }

  public Pt getPoint() {
    return myPoint;
  }

}
