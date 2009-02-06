/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.feedserver.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Utility functions for feedserver.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 */
public class FeedServerUtil {
  private FeedServerUtil() {
  }

  private static final Logger logger = Logger.getLogger(FeedServerUtil.class.getName());

  /**
   * Get a set of properties as an XML Configuration
   * 
   * @param properties The properties to convert to an XML Configuration
   * @return The XML description of the Configuration
   * @throws TransformerException
   * @throws ParserConfigurationException
   */
  public static String getConfigurationAsXML(Map<String, Object> properties)
      throws TransformerException, ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.newDocument();
    Element entity = doc.createElement("entity");
    doc.appendChild(entity);

    for (Map.Entry<String, Object> element : properties.entrySet()) {
      String key = element.getKey();
      Object value = element.getValue();
      if (null == value) {
        continue;
      }

      if (value.getClass().isArray()) {
        Object[] valueObjects = (Object[]) value;
        for (int i = 0; i < valueObjects.length; i++) {
          Element node = doc.createElement(key);
          node.appendChild(doc.createTextNode(valueObjects[i].toString()));
          if (0 == i) {
            node.setAttribute("repeatable", "true");
          }
          entity.appendChild(node);
        }
      } else {
        Element node = doc.createElement(key);
        node.appendChild(doc.createTextNode(value.toString()));
        entity.appendChild(node);
      }
    }
    return getDocumentAsXml(doc);
  }

  /**
   * Get a DOM Document as XML
   * 
   * @param doc The DOM document to convert
   * @return The XML description of the DOM
   * @throws TransformerException
   */
  public static String getDocumentAsXml(Document doc) throws TransformerException {
    DOMSource domSource = new DOMSource(doc);
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    StringWriter sw = new StringWriter();
    StreamResult sr = new StreamResult(sw);
    transformer.transform(domSource, sr);
    String str = sw.toString();
    return str;
  }

  /**
   * Gets a property from the collection as a string
   * 
   * @param properties The collection to get the property from
   * @param key The property to get
   * @return The value of the property converted to a string
   * @throws RuntimeException if the property does not exist
   */
  public static String getStringProperty(Map<String, Object> properties, String key) {
    Object value = getProperty(properties, key);
    if (value instanceof String) {
      return (String) value;
    }

    String message = "Property " + key + " Not found";
    RuntimeException e = new RuntimeException(message);
    logger.log(Level.SEVERE, message, e);
    throw e;
  }

  /**
   * Converts a string to an InputStream
   * 
   * @param data The string to convert to a stream
   * @return The string converted to a stream
   */
  public static InputStream getStringAsInputStream(String data) {
    ByteArrayInputStream byteArrayStream = null;
    byteArrayStream = new ByteArrayInputStream(data.getBytes());

    return byteArrayStream;
  }

  /**
   * Convert an XML string into a DOM Document
   * 
   * @param docString The XML string to convert
   * @return The DOM Document for the XML
   */
  public static Document parseDocument(String docString) {
    return parseDocument(docString, null);
  }

  /**
   * Convert an XML string into a DOM Document
   * 
   * @param docString The XML string to convert
   * @param entityResolver {@link EntityResolver} to be used while parsing the
   *        document.
   * @return The DOM Document for the XML
   */
  public static Document parseDocument(String docString, EntityResolver entityResolver) {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    try {
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      logger.log(Level.SEVERE, e.getMessage(), e);
      return null;
    }
    Document doc;
    try {
      if (null != entityResolver) {
        builder.setEntityResolver(entityResolver);
      }
      doc = builder.parse(getStringAsInputStream(docString));
      doc.getDocumentElement().normalize();
      return doc;
    } catch (SAXException e) {
      logger.log(Level.SEVERE, e.getMessage(), e);
      return null;
    } catch (IOException e) {
      logger.log(Level.SEVERE, e.getMessage(), e);
      return null;
    }
  }

  /**
   * Gets a property from the collection
   * 
   * @param properties The collection to get the property from
   * @param key The property key to get the value for
   * @return The value of the property
   */
  public static Object getProperty(Map<String, Object> properties, String key) {
    if (properties.containsKey(key)) {
      return properties.get(key);
    }

    String message = "Property " + key + " Not found";
    RuntimeException e = new RuntimeException(message);
    logger.log(Level.SEVERE, message, e);
    throw e;
  }

  static Map<String, Object> parseContentWithDtd(String str, EntityResolver entityResolver) {
    return parseContentWithDtd(str, entityResolver, "entity");
  }

  static Map<String, Object> parseContentWithDtd(String str, EntityResolver entityResolver,
      String parentNodeName) {
    Map<String, Object> columns = new HashMap<String, Object>();
    Document doc = FeedServerUtil.parseDocument(str, entityResolver);
    Node parentNode = doc.getFirstChild();
    if (parentNode == null || !parentNode.getNodeName().equals(parentNodeName)) {
      throw new UnsupportedOperationException("Not supported");
    }
    NodeList childNodeList = parentNode.getChildNodes();
    for (int i = 0; i < childNodeList.getLength(); i++) {
      Node childNode = childNodeList.item(i);
      columns.put(childNode.getNodeName(), childNode.getTextContent());
    }
    return columns;
  }
}
