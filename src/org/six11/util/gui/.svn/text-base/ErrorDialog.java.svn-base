// $Id$

package org.six11.util.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.*;

import org.six11.util.layout.FrontEnd;

/**
 * 
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class ErrorDialog extends JDialog {

  private static Random entropy = new Random(System.currentTimeMillis());
  private static String[] SMARMY_OK_MESSAGES = {
    "Cool",
    "OK",
    "Very well, then",
    "Alright",
    "Whatever",
    "If that's how it's going to be then OK",
    "Dang",
    "Sure",
    ">.<",
    "Close",
    "This makes me cry",
    "Oh Snap!"
  };
  
  private static String getSmarmyMessage() {
    return SMARMY_OK_MESSAGES[entropy.nextInt(SMARMY_OK_MESSAGES.length)];    
  }
  
  public static void showError(String title, String message) {
    new ErrorDialog(title, message).setVisible(true);
  }

  
  private ErrorDialog(String title, String message) {
    super();
    this.setTitle(title);
    FrontEnd fe = new FrontEnd();
    String MSG = "msg";
    String OK = "ok";
    
    JTextArea text = new JTextArea(message);
    text.setLineWrap(true);
    text.setWrapStyleWord(true);
    text.setEditable(false);
    JScrollPane textScroller = new JScrollPane(text,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    JButton done = new JButton(getSmarmyMessage());
    done.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        ErrorDialog.this.setVisible(false);
      }
    });
    fe.add(textScroller, MSG);
    fe.add(done, OK);
    fe.addRule(FrontEnd.ROOT, FrontEnd.S, OK, FrontEnd.S);
    fe.addRule(FrontEnd.ROOT, FrontEnd.E, OK, FrontEnd.E);
    fe.addRule(FrontEnd.ROOT, FrontEnd.N, MSG, FrontEnd.N);
    fe.addRule(FrontEnd.ROOT, FrontEnd.W, MSG, FrontEnd.W);
    fe.addRule(FrontEnd.ROOT, FrontEnd.E, MSG, FrontEnd.E);
    fe.addRule(OK, FrontEnd.N, MSG, FrontEnd.S);
    
    add(fe);
    
    this.setSize(400, 300);
    center();
  }

  public void center() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = getSize();
    int x = (screenSize.width - frameSize.width) / 2;
    int y = (screenSize.height - frameSize.height) / 2;
    setLocation(x, y);
  }
}
