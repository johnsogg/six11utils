// $Id: Example.java 12 2009-11-09 22:58:47Z gabe.johnson $

package org.six11.util.layout.example;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;

import org.six11.util.gui.ApplicationFrame;
import org.six11.util.gui.Placeholder;
import org.six11.util.layout.FrontEnd;
import org.six11.util.layout.Holder;

/**
 * An example application that shows off the FrontEnd container's relative layout system as well as
 * the Holders that allow you to maintain consistency even when you want to remove things.
 * 
 * Notice how regular the syntax is wrt Holders and the constraint rules--it really lends itself to
 * being put into an XML file.
 **/
public class Example {

  ApplicationFrame af;

  public static void main(String[] args) {
    Example example = new Example();
    example.go();
  }

  public Example() {
    af = new ApplicationFrame("FrontEnd and Holder Example");

    // The FrontEnd is the container into which the rest of the
    // components will be put
    FrontEnd fe = new FrontEnd();

    // Some constants
    String a = "a";
    String b = "b";
    String c = "c";
    String d = "d";
    String e = "e";
    String tb = "tb";
    String tc = "tc";
    String td = "td";

    // Below here I create 'Placeholders' which are simple
    // JComponents. In a real application these would be text boxes,
    // buttons, and the like.
    Placeholder blockA = new Placeholder(new Dimension(200, 200), "A");

    // Add the component to the layout with a constraint object (the
    // string a)
    fe.add(blockA, a);

    // This is the part where the constraints are established. The way
    // to read this is pretty simple: Root's northern edge defines a's
    // northern edge.
    fe.addRule(FrontEnd.ROOT, FrontEnd.N, a, FrontEnd.N);
    // Root's western edge defines a's western edge
    fe.addRule(FrontEnd.ROOT, FrontEnd.W, a, FrontEnd.W);

    Placeholder blockB = new Placeholder(new Dimension(300, 200), "B");
    // a 'Holder' is a container that is geared to hold a single child
    // component. The holder itself may be of any size. If a holder is
    // told to stretch it's subcomponent in a given direction
    // (vertical or horizontal), then the holder will set the bounds
    // of the held component to match the holder's bounds (minus
    // padding, if present). Also, a special property of the Holder
    // class is that you can make it's child component invisible,
    // optionally without changing the size of the holder itself. Say
    // I have components A, B, and C. C depends on B, which depends on
    // A. But what if B for whatever reason needs to disappear? C
    // still depends on it. The answer in this case is to throw B into
    // a Holder, and toggle the visibility of B, while the holder that
    // contains it remains present, so C can get it's constraint.

    // I'll go down each parameter this first time
    final Holder blockBHolder = new Holder(blockB, // our child is blockB
        false, // don't stretch blockB in x dir when I have extra space
        false, // or in y direction--blockB is centered w/pref. size.
        0, // no horizontal padding
        0, // no vertical padding
        false, // when invisible, do not give blockB width
        false); // when invisible, do not give blockB height
    fe.add(blockBHolder, b);
    fe.addRule(FrontEnd.ROOT, FrontEnd.N, b, FrontEnd.N, 10);
    fe.addRule(FrontEnd.ROOT, FrontEnd.E, b, FrontEnd.E, 10);

    Placeholder blockC = new Placeholder(new Dimension(200, 200), "C");
    // the Holder below stretches it's component's width but not
    // height when the component is visible. When the component is
    // invisible, both the x and y directions are present--so the
    // component still takes up vertical and horizontal space, it just
    // isn't visible.
    final Holder blockCHolder = new Holder(blockC, true, false, 0, 0, true, true);
    fe.add(blockCHolder, c);
    fe.addRule(b, FrontEnd.S, c, FrontEnd.N, 20);
    fe.addRule(d, FrontEnd.E, c, FrontEnd.W, 30);
    fe.addRule(FrontEnd.ROOT, FrontEnd.E, c, FrontEnd.E);

    Placeholder blockD = new Placeholder(new Dimension(200, 200), "D");
    final Holder blockDHolder = new Holder(blockD, false, false, 0, 0, true, true);
    fe.add(blockDHolder, d);
    fe.addRule(FrontEnd.ROOT, FrontEnd.W, d, FrontEnd.W, 10);
    fe.addRule(FrontEnd.ROOT, FrontEnd.S, d, FrontEnd.S, 10);

    Placeholder blockE = new Placeholder(new Dimension(280, 80), "E");
    fe.add(blockE, e);
    fe.addRule(FrontEnd.ROOT, FrontEnd.E, e, FrontEnd.E, 15);
    fe.addRule(FrontEnd.ROOT, FrontEnd.S, e, FrontEnd.S, 5);
    fe.addRule(c, FrontEnd.S, e, FrontEnd.N);

    // Three buttons that dangle off to the right of component A that
    // let you toggle the visibility of components B, C, and D
    final JButton toggleB = new JButton("Toggle B");
    final JButton toggleC = new JButton("Toggle C");
    final JButton toggleD = new JButton("Toggle D");

    fe.add(toggleB, tb);
    fe.addRule(a, FrontEnd.S, tb, FrontEnd.N, 10);
    fe.addRule(FrontEnd.ROOT, FrontEnd.W, tb, FrontEnd.W, 10);

    fe.add(toggleC, tc);
    fe.addRule(tb, FrontEnd.S, tc, FrontEnd.N);
    fe.addRule(FrontEnd.ROOT, FrontEnd.W, tc, FrontEnd.W, 10);

    fe.add(toggleD, td);
    fe.addRule(tc, FrontEnd.S, td, FrontEnd.N);
    fe.addRule(FrontEnd.ROOT, FrontEnd.W, td, FrontEnd.W, 10);

    af.add(fe);

    af.setSize(800, 700);
    af.center();

    ActionListener al = new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        Holder h = null;
        if (ev.getSource() == toggleB) {
          h = blockBHolder;
        } else if (ev.getSource() == toggleC) {
          h = blockCHolder;
        } else if (ev.getSource() == toggleD) {
          h = blockDHolder;
        }

        if (h != null) {
          h.setComponentVisible(!h.isComponentVisible());
        }
      }
    };

    toggleB.addActionListener(al);
    toggleC.addActionListener(al);
    toggleD.addActionListener(al);
  }

  public void go() {
    af.setVisible(true);
  }
}
