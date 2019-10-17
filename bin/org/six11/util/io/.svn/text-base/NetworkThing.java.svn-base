// $Id$

package org.six11.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.six11.util.Debug;
import org.six11.util.adt.MultiState;
import org.six11.util.adt.SynchronizedQueue;

/**
 * The network thing has several threads dedicated to sending, recieving, and routing Message
 * objects between the client and server. All communication is asynchronous. Each message has a
 * 'type', which is a good indicator of which components might be interested in hearing about them.
 * Register listeners to be notified about messages of various types.
 * 
 * Here's a toy example: a message might ask "what is 2+2"? To send the message, simply add it to
 * the SynchronizedQueue<Message> found in multiState.getValue(ClientStateNames.K_TRANSMIT_QUEUE).
 * The message goes up (in another thread), but there is no immediate reply. At some point in the
 * near future, another message will come down the pipe with an answer, and listeners that are
 * registered with the response type will hear about it.
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class NetworkThing {

  private MultiState multiState;
  private URL url;
  private String pingString;
  private Filter msgFilter;
  private Map<String, List<MessageHandler>> messageHandlers;
  private boolean running = false;

  private SynchronizedQueue<Message> downloadedMessages;
  private List<Thread> threads;

  public NetworkThing(MultiState multiState, URL url) {
    this.multiState = multiState;
    this.url = url;
    this.msgFilter = new ElementFilter("msg");
    this.messageHandlers = new HashMap<String, List<MessageHandler>>();
    this.downloadedMessages = new SynchronizedQueue<Message>();
    this.threads = new ArrayList<Thread>();
    this.running = false;

    // initialize 'pingString'. This is sent to the server on every download request, which can be
    // quite frequent, so it is important to be efficient.
    Format pretty = Format.getPrettyFormat();
    XMLOutputter xmlout = new XMLOutputter(pretty);
    Element messages = new Element("messages");
    Element msg = new Element("msg");
    msg.setAttribute("type", "ping");
    messages.addContent(msg);
    pingString = xmlout.outputString(messages);

    Thread uploadThread = new Thread(new Runnable() {
      public void run() {
        upload();
      }
    });
    Thread downloadThread = new Thread(new Runnable() {
      public void run() {
        download();
      }
    });
    Thread penDataThread = new Thread(new Runnable() {
      public void run() {
        makePenMessages();
      }
    });
    Thread redistributeThread = new Thread(new Runnable() {
      public void run() {
        redistributeMessages();
      }
    });
    threads.add(uploadThread);
    threads.add(downloadThread);
    threads.add(penDataThread);
    threads.add(redistributeThread);
    setRunning(true);
  }

  public void setRunning(boolean v) {
    if (v == running)
      return;
    running = v;
    if (v) {
      for (Thread t : threads) {
        t.start();
      }
    }
  }

  /**
   * 
   */
  @SuppressWarnings("unchecked")
  private void makePenMessages() {
    bug("Pen message thread is working.");
    while (running) {
      // look in the pen data queue, and when there's something there, slurp them out, make a new
      // Message object and add it to the main message queue.
      SynchronizedQueue<Element> penQueue = (SynchronizedQueue<Element>) multiState
          .getValue("pen queue");
      Collection<Element> commands = null;
      try {
        synchronized (penQueue) {
          while (penQueue.isEmpty()) {
            penQueue.wait((long) 5000);
          }
          if (!penQueue.isEmpty()) {
            commands = penQueue.getAll(true);
          }
        }
      } catch (InterruptedException ex) {
        // I was interrupted. How rude!
      }
      if (commands != null && commands.size() > 0) {
        Message penMessage = new Message("pen");
        penMessage.addParam("room", multiState.getString("room"));
        for (Element elm : commands) {
          penMessage.addElement(elm);
        }

        SynchronizedQueue<Message> msgQueue = (SynchronizedQueue<Message>) multiState
            .getValue("transmit queue");
        msgQueue.add(penMessage);
      } else {
        bug("Somehow I got out here but commands is " + commands);
      }
    }
    bug("Pen message thread finished.");
  }

  private HttpURLConnection initConnection() throws IOException {
    // connect, set up relevant HTTP parameters, return.
    // bug("Hitting" + url.toExternalForm());
    HttpURLConnection ht = (HttpURLConnection) url.openConnection();
    ht.setRequestMethod("POST");
    ht.setUseCaches(false);
    ht.setDoOutput(true);
    ht.setDoInput(true);
    ht.setRequestProperty("Connection", "Keep-Alive");
    return ht;
  }

  @SuppressWarnings("unchecked")
  private void upload() {
    bug("Upload thread is working.");
    while (running) {
      try {
        SynchronizedQueue<Message> messageQueue = (SynchronizedQueue<Message>) multiState
            .getValue("transmit queue");
        Collection<Message> outbound = null;
        synchronized (messageQueue) {
          while (messageQueue.isEmpty()) {
            messageQueue.wait((long) 5000);
          }
          if (!messageQueue.isEmpty()) {
            outbound = messageQueue.getAll(true);
          }
        }
        if (outbound != null && outbound.size() > 0) {
          upload(outbound);
        } else {
          bug("No outbound messages. This should not happen.");
        }
      } catch (InterruptedException ex) {

      }
    }
    bug("Upload thread finished.");
  }

  private void upload(Collection<Message> outbound) {
    try {
      HttpURLConnection ht = initConnection();
      Element root = new Element("messages");
      Document messages = new Document(root);
      OutputStreamWriter outWriter = new OutputStreamWriter(ht.getOutputStream());

      for (Message m : outbound) {
        root.addContent(m.getRoot().detach());
      }
      Format pretty = Format.getPrettyFormat();
      XMLOutputter out = new XMLOutputter(pretty);
      outWriter.write(out.outputString(messages));
      outWriter.flush();
      outWriter.close();
      ht.getInputStream(); // Accessing the input stream is necessary for some reason.
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @SuppressWarnings("rawtypes")
  private void download() {
    bug("Download thread is working.");
    while (running) {
      try {
        HttpURLConnection ht = initConnection();
        OutputStreamWriter outWriter = new OutputStreamWriter(ht.getOutputStream());
        outWriter.write(pingString);
        outWriter.close();
        InputStream in = ht.getInputStream();
        if (in.available() > 0) {
          try {
            Document msgDoc = new SAXBuilder().build(in);
            List msgList = msgDoc.getRootElement().getContent(msgFilter);
            if (msgList.isEmpty()) {
              bug("User connected but did not send any messages.");
            }
            for (Object msgObj : msgList) {
              Element elm = (Element) ((Element) msgObj).clone();
              elm.detach();
              Message msg = new Message(elm);
              downloadedMessages.add(msg);
            }
          } catch (JDOMException ex) {
            ex.printStackTrace();
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }

      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    bug("Download thread finished.");
  }

  private void bug(String what) {
    Debug.out("NetworkThing", what);
  }

  /**
   * @param type
   * @param messageHandler
   */
  public void addMessageHandler(String type, MessageHandler messageHandler) {
    if (!messageHandlers.containsKey(type)) {
      messageHandlers.put(type, new ArrayList<MessageHandler>());
    }
    messageHandlers.get(type).add(messageHandler);
  }

  /**
   * This method distributes Message objects obtained from the download thread. This runs in its own
   * thread so the download thread is not affected by the potentially laggy process of doling
   * messages out to the handlers.
   */
  private void redistributeMessages() {
    bug("Redistribution thread is working.");
    while (running) {
      Collection<Message> messages = null;
      synchronized (downloadedMessages) {
        if (downloadedMessages.isEmpty()) {
          try {
            downloadedMessages.wait(4000);
          } catch (InterruptedException ex) {
          }
        }
        if (!downloadedMessages.isEmpty()) {
          messages = downloadedMessages.getAll(true);
        }
      }
      if (messages != null) {
        for (Message msg : messages) {
          List<MessageHandler> handlers = messageHandlers.get(msg.getType());
          if (handlers != null) {
            for (MessageHandler handler : handlers) {
              handler.handle(msg);
            }
          } else {
            bug("Received message '" + msg.getType()
                + "' but there are no message handler registered.");
          }
        }
      }
    }
    bug("Redistribution thread finished.");
  }
}
