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

import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Utils for handling XML entities.
 */
public class XmlUtil {

  private SAXParserFactory parserFactory;

  protected BeanUtil beanUtil = new BeanUtil();

  public XmlUtil() {
    parserFactory = SAXParserFactory.newInstance();
    parserFactory.setValidating(false);
  }

  /**
   * Converts a a map of properties into an entity XML representation as used
   * in the content section of FeedStore entries:
   *<pre>
   * &lt;entity&gt;
   *   &lt;name0&gt;value0&lt;/name0&gt;
   *   &lt;name1&gt;value1&lt;/name1&gt;
   *   ...
   * &lt;/entity&gt;
   *</pre>
   */
  public String convertPropertiesToXml(Map<String, Object> properties) {
    return convertPropertiesToXml(properties, XmlHandler.ENTITY);
  }

  /**
   * Converts a a map of properties into an entity XML representation as used
   * in the content section of FeedStore entries:
   *<pre>
   * &lt;topLevelElement&gt;
   *   &lt;name0&gt;value0&lt;/name0&gt;
   *   &lt;name1&gt;value1&lt;/name1&gt;
   *   ...
   * &lt;/topLevelElement&gt;
   *</pre>
   */
  public String convertPropertiesToXml(Map<String, Object> properties,
      String topLevelElement) {
    StringBuilder builder = new StringBuilder();

    builder.append("<");
    builder.append(topLevelElement);
    builder.append(">");

    addMapValue(properties, builder);

    builder.append("</");
    builder.append(topLevelElement);
    builder.append(">");

    return builder.toString();
  }

  private void addMapValue(Map<String, Object> properties, StringBuilder builder) {
    for (String name : properties.keySet()) {
      Object value = properties.get(name);
      if (value instanceof Object[]) {
        Object[] values = (Object[]) value;
        if (values.length == 0) {
          addPropertyXml(builder, name, null, true);
        } else {
          for (int i = 0; i < values.length; i++) {
            addPropertyXml(builder, name, values[i], i == 0);
          }
        }
      } else {
        addPropertyXml(builder, name, value, false);
      }
    }
  }

  private void addPropertyXml(StringBuilder builder, String propertyName,
      Object value, boolean isRepeatable) {
    if (value == null) {
      value = "";
    }
    builder.append("<");
    builder.append(propertyName);
    if (isRepeatable) {
      builder.append(" ");
      builder.append(XmlHandler.REPEATABLE);
      builder.append("=\"true\"");
    }
    builder.append(">");
    addValue(builder, value);
    builder.append("</");
    builder.append(propertyName);
    builder.append(">");
  }

  /**
   * @param builder
   * @param value
   */
  @SuppressWarnings("unchecked")
  private void addValue(StringBuilder builder, Object value) {
    if (value instanceof Map) {
      addMapValue((Map) value, builder);
    } else {
      builder.append(StringEscapeUtils.escapeXml(value.toString()));
    }
  }

  /**
   * Converts XML representation of an entity into properties.
   */
  public Map<String, Object> convertXmlToProperties(String xmlText)
      throws SAXException, IOException, ParserConfigurationException {
    return convertXmlToProperties(xmlText, XmlHandler.ENTITY);
  }

  /**
   * Converts XML representation of an entity into properties.
   */
  public Map<String, Object> convertXmlToProperties(String xmlText,
      String topLevelELement)
      throws SAXException, IOException, ParserConfigurationException {
    InputSource input = new InputSource(new StringReader(xmlText));
    XmlHandler xmlHandler = new XmlHandler(topLevelELement);
    getParser().parse(input, xmlHandler);
    return xmlHandler.getValueMap();
  }

  /**
   * Creates a new SAXParser. Note that SAXParser.parse() is not thread-safe, so
   * we need to create a new one. Alternatively, we could synchronize parsing
   * which is most likely less efficient though.
   */
  protected SAXParser getParser()
      throws SAXException, ParserConfigurationException {
    return parserFactory.newSAXParser();
  }

  /**
   * Converts XML representation of an entity into a JavaBean.
   * @param xmlText XML text of entity
   * @param bean JavaBean to fill
   * @throws IllegalArgumentException
   * @throws IntrospectionException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws SAXException
   * @throws IOException
   * @throws ParserConfigurationException
   */
  public void convertXmlToBean(String xmlText, Object bean)
      throws IllegalArgumentException, IntrospectionException, IllegalAccessException,
          InvocationTargetException, SAXException, IOException, ParserConfigurationException {
    beanUtil.convertPropertiesToBean(convertXmlToProperties(xmlText), bean);
  }
}
