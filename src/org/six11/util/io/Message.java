// $Id: Message.java 178 2011-10-28 18:44:17Z gabe.johnson@gmail.com $

package org.six11.util.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * A client or server message object. It can be serialized to and from XML. A message always has a
 * root node identified as "msg", which has a single attribute, "type". The type should be enough to
 * determine how the message contents should be read.
 * 
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class Message {

  private Document xml;

  // A peer may generate a message that generates a response message. If such a response is received
  // we can use the responseHandler here as the recipient.
  // private transient MessageHandler responseHandler;
  public Message(String type) {
    Element root = new Element("msg");
    root.setAttribute("type", type);
    xml = new Document(root);
  }

  public Message(Element root) {
    xml = new Document(root);
  }
  
  public String getAttribute(String name) {
    return xml.getRootElement().getAttributeValue(name);
  }

  public String toString() {
    if (xml.getRootElement() == null) {
    return "message with no root element";
    } else {
      return xml.toString();
    }
  }

  public static Message fromXml(String encodedMessage) throws UnsupportedEncodingException,
      JDOMException, IOException {
    SAXBuilder reader = new SAXBuilder();
    Document doc = reader.build(new ByteArrayInputStream(encodedMessage.getBytes("UTF-8")));
    return new Message(doc.detachRootElement());
  }

  public Element getRoot() {
    return xml.getRootElement();
  }

  /**
   * Generate a pretty-printed, human-readable XML string. This assumes the human is versed in XML.
   * 
   * @return a nice XML string.
   */
  public String toXml() {
    Format pretty = Format.getPrettyFormat();
    XMLOutputter out = new XMLOutputter(pretty);
    return out.outputString(xml);
  }

  /**
   * Gives the type of this message, indicating which handler might be appropriate to receive it.
   * 
   * @return the value of the message's 'type' parameter
   */
  public String getType() {
    return xml.getRootElement().getAttributeValue("type");
  }

  /**
   * Adds a parameter node directly under the root node. This simply has a key/value pair.
   * 
   * @param key
   *          the parameter's name
   * @param val
   *          the parameter's value
   */
  public void addParam(String key, String val) {
    Element param = new Element("param");
    param.setAttribute(key, val);
    xml.getRootElement().addContent(param);
  }

  /**
   * Adds a named string node directly under the root node. The value of the string is provided as
   * PCDATA (parsed character data). This is in the form of:
   * 
   * [string name='strName'] strVal goes here [/string]
   * 
   * @param strName
   * @param strVal
   */
  public void addString(String strName, String strVal) {
    Element string = new Element("string");
    string.setAttribute("name", strName);
    string.addContent(strVal);
    xml.getRootElement().addContent(string);
  }

  /**
   * Returns the PCDATA contained in a [string name='strName']...foofoo...[/string] element.
   * 
   * @param strName
   *          the name of the string, to distinguish between multiple string elements.
   * @return the String contained in the named element ("...foofoo..." in this example) or the empty
   *         string if no such string is found.
   */
  @SuppressWarnings("rawtypes")
  public String getString(String strName) {
    List strElements = xml.getRootElement().getChildren("string");
    String ret = "";
    for (Object obj : strElements) {
      Element e = (Element) obj;
      if (e.getAttributeValue("name").equals(strName)) {
        ret = e.getText();
        break;
      }
    }
    return ret;
  }

  /**
   * @param key
   *          the parameter's key
   * @return
   */
  @SuppressWarnings("rawtypes")
  public String getParam(String key) {
    List list = xml.getRootElement().getChildren("param");
    String ret = null;
    for (Object elmObj : list) {
      Element elm = (Element) elmObj;
      if (elm.getAttributeValue(key) != null) {
        ret = elm.getAttributeValue(key);
        break;
      }
    }
    return ret;
  }

  /**
   * Adds the given element to the root element. This assumes the given element does not have a
   * parent already, so call 'detach' if necessary.
   * 
   * @param elm the parentless element to add.
   */
  public void addElement(Element elm) {
    xml.getRootElement().addContent(elm);
  }

  /**
   * @return
   */
  public Message copy() {
    return new Message(((Document) xml.clone()).detachRootElement());
  }
}
