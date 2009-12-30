/* Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.feedserver.util;

import com.google.gdata.data.Content;
import com.google.gdata.data.Entry;
import com.google.gdata.data.OtherContent;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.XmlBlob;
import org.xml.sax.SAXException;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Utility to handle content payload
 */
public class ContentUtil {

  /**
   * Name of id property
   */
  public static final String ID = "id";

  /**
   * Name of the unique name property
   */
  public static final String NAME = "name";

  protected BeanUtil beanUtil = new BeanUtil();
  protected XmlUtil xmlUtil = new XmlUtil();

  /**
   * MIME type for content payload
   */
  public static final ContentType APPLICATION_XML =
      new ContentType("application/xml");

  /**
   * Converts XML text to a GData {@code  OtherContent} description of the
   * payload
   * @param xmlSource XML source of the payload
   * @return A GData {@code OtherContent} representation of the payload
   */
  public OtherContent createXmlContent(String xmlSource) {
    OtherContent xmlContent = new OtherContent();
    XmlBlob xmlBlob = new XmlBlob();
    xmlBlob.setBlob(xmlSource);
    xmlContent.setXml(xmlBlob);
    xmlContent.setMimeType(APPLICATION_XML);
    return xmlContent;
  }

  /**
   * Converts a JavaBean to a GData {@code  OtherContent} description of the
   * payload
   * @param bean JavaBean description of the payload
   * @return A GData {@code OtherContent} representation of the payload
   */
  public OtherContent createXmlContent(Object bean)
      throws IllegalArgumentException, IntrospectionException,
          IllegalAccessException, InvocationTargetException {
    Map<String, Object> properties = beanUtil.convertBeanToProperties(bean);
    String xmlSource = xmlUtil.convertPropertiesToXml(properties);
    return createXmlContent(xmlSource);
  }

  /**
   * Gets an XML description of the payload from a GData {@code  OtherContent}
   * @param content a GData {@code  OtherContent} representation of the payload
   * @return An XML string describing the payload
   */
  public String getXmlFromContent(OtherContent content) {
    XmlBlob xmlBlob = content.getXml();
    return xmlBlob.getBlob();
  }

  /**
   * Sets properties on the JavaBean by extracting them from a GData
   * {@code OtherContent} description of the payload
   * @param content a GData {@code  OtherContent} representation of the payload
   * @param bean a JavaBean to set the properties on
   * @throws InvocationTargetException 
   * @throws IllegalAccessException 
   * @throws IntrospectionException 
   * @throws ParserConfigurationException 
   * @throws IOException 
   * @throws SAXException 
   * @throws IllegalArgumentException 
   * @throws ParseException 
   */
  public void fillBean(OtherContent content, Object bean) throws IllegalArgumentException,
      SAXException, IOException, ParserConfigurationException, IntrospectionException,
      IllegalAccessException, InvocationTargetException, ParseException {
    fillBean(content, bean, null);
  }

  /**
   * Sets properties on the JavaBean by extracting them from a GData
   * {@code OtherContent} description of the payload
   * @param content a GData {@code  OtherContent} representation of the payload
   * @param bean a JavaBean to set the properties on
   * @param id optional id of entity
   */
  public void fillBean(OtherContent content, Object bean, String id)
      throws SAXException, IOException, ParserConfigurationException,
          IllegalArgumentException, IntrospectionException,
          IllegalAccessException, InvocationTargetException, ParseException {
    String xmlText = getXmlFromContent(content);
    xmlText = fixEscaping(xmlText);
    Map<String, Object> properties = xmlUtil.convertXmlToProperties(xmlText);
    if (properties == null) {
      properties = new HashMap<String, Object>();
    }
    if (id != null) {
      properties.put(ID, id);
      properties.put(NAME, id);
    }
    beanUtil.convertPropertiesToBean(properties, bean);
  }

  /**
   * Temp fix of XML escaping.  Changes "]]>" to "]]&gt;".
   * @param xmlText Input XML text
   * @return Escaped XML text
   */
  protected String fixEscaping(String xmlText) {
    return xmlText.replaceAll("]]>", "]]&gt;");
  }

  /**
   * Sets properties on the JavaBean by extracting them from a GData entry
   * @param entry a GData entry
   * @param bean a JavaBean to set the properties on
   */
  public void fillBean(Entry entry, Object bean)
      throws IllegalArgumentException, SAXException, IOException,
          ParserConfigurationException, IntrospectionException,
          IllegalAccessException, InvocationTargetException, ParseException {
    Content content = entry.getContent();
    if (content instanceof OtherContent) {
      fillBean((OtherContent) content, bean);
    }
  }
}
