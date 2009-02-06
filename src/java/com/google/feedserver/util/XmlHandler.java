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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * XML parser handler for parsing input data. Only handles one level of elements
 * (does not handle nested elements).
 * 
 * An empty element is parsed as a null value for that property (e.g.,
 * <string></string> will set the property "string" to null).
 * 
 * @author jyang825@gmail.com (Jun Yang)
 */
public class XmlHandler extends DefaultHandler {

  public static final String ENTITY = "entity";

  /**
   * XML attributre name indicating the entity can be repeated.
   */
  public static final String REPEATABLE = "repeatable";

  protected Map<String, Object> valueMap;
  protected Stack<String> currentElementStack;
  protected Stack<Boolean> elementRepetableStack;
  protected String lastElement;
  protected boolean isRepeatable;
  protected Object value;
  protected Stack<Map<String, Object>> valueMapStack;
  protected final String topLevelElement;

  /**
   * Creates a new XMLHandler with top element as "entity"
   */
  public XmlHandler() {
    this(ENTITY);
  }

  /**
   * Creates a new FeedStoreXMLHandler
   */
  public XmlHandler(String topLevelElement) {
    currentElementStack = new Stack<String>();
    elementRepetableStack = new Stack<Boolean>();
    valueMapStack = new Stack<Map<String, Object>>();
    this.topLevelElement = topLevelElement;
  }

  /**
   * Gets the collection of properties parsed from XML.
   */
  public Map<String, Object> getValueMap() {
    return valueMap;
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes)
      throws SAXException {
    super.startElement(uri, localName, name, attributes);

    if (!(topLevelElement.equals(name) && currentElementStack.size() == 0)) {
      if (!name.equals(lastElement)) {
        isRepeatable = "true".equals(attributes.getValue(REPEATABLE));
        lastElement = null;
      }
      currentElementStack.push(name);
      elementRepetableStack.push(isRepeatable);
      updateValueMapStack();
    }
  }

  protected void updateValueMapStack() {
    if (valueMapStack.size() != currentElementStack.size()) {
      valueMap = new HashMap<String, Object>();
      valueMapStack.push(valueMap);
    }
  }

  protected String getLastElement() {
    return lastElement;
  }

  protected String getCurrenttElement() {
    return currentElementStack.peek();
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    super.endElement(uri, localName, name);

    if (currentElementStack.size() > 0) {
      removeLastElementFromStack();
      collectValues();
      value = null;
    }
  }

  /**
   *
   */
  private void removeLastElementFromStack() {
    lastElement = currentElementStack.pop();
    isRepeatable = elementRepetableStack.pop();
    if (valueMapStack.size() > currentElementStack.size() + 1) {
      value = valueMapStack.pop();
      valueMap = valueMapStack.peek();
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    super.characters(ch, start, length);

    if (currentElementStack.size() > 0) {
      StringBuilder builder;
      if (value == null) {
        builder = new StringBuilder();
      } else if (value instanceof StringBuilder) {
        builder = (StringBuilder) value;
      } else {
        throw new SAXException("Unexpected text content");
      }
      builder.append(new String(ch, start, length));
      value = builder;
    }
  }

  /**
   * Coalesces the most recently processed XML element into the property
   * collection
   * 
   * @throws SAXException If an element is repeated without the {@code
   *         repeatable} attribute being present
   */
  protected void collectValues() throws SAXException {
    if (value instanceof StringBuilder) {
      String stringValue = value.toString().trim();
      value = stringValue.isEmpty() ? null : stringValue;
    }

    String elementToBeUpdated = getLastElement();
    if (isRepeatable) {
      if (value == null) {
        value = "";
      }

      if (value instanceof Map) {
        updateValueInArray(new Map[0], elementToBeUpdated);
      } else {
        updateValueInArray(new String[0], elementToBeUpdated);
      }
    } else if (getValueMap().containsKey(elementToBeUpdated)) {
      throw new SAXException("Repeated element " + currentElementStack);
    } else {
      getValueMap().put(elementToBeUpdated, value);
    }
  }

  @SuppressWarnings( {"unchecked"})
  private <T> void updateValueInArray(T[] dummy, String elementToBeUpdated) {
    T[] array = (T[]) getValueMap().get(elementToBeUpdated);
    if (array == null) {
      array = (T[]) Array.newInstance(value.getClass(), 1);
      Array.set(array, 0, value);
    } else {
      T[] newArray = (T[]) Array.newInstance(value.getClass(), array.length + 1);
      for (int i = 0; i < array.length; i++) {
        newArray[i] = array[i];
      }
      newArray[array.length] = (T) value;
      array = newArray;
    }
    getValueMap().put(elementToBeUpdated, array);
  }
}
