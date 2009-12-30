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
package com.google.feedserver.client;

import com.google.feedserver.util.ContentUtil;
import com.google.feedserver.util.FeedServerClientException;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.OtherContent;
import com.google.gdata.util.XmlBlob;

import org.xml.sax.SAXException;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Represents a "payload-in-content" feed entry.  Simplifies interaction with feeds originating
 * from FeedServer and FeedStore.
 * 
 * @author r@kuci.org (Ray Colline)
 */
public class FeedServerEntry extends BaseEntry<FeedServerEntry> {

  protected ContentUtil contentUtil = new ContentUtil();

  /**
   * Creates empty entry.
   */
  public FeedServerEntry() {}
  
  /**
   * Creates entry from provided entity bean.
   * 
   * @param entity a populated bean.
   */
  public FeedServerEntry(Object entity) {
    setContentFromEntity(entity);
  }
  
  /**
   * Creates entry from provided populated content.
   * 
   * @param content populated content.
   */
  public FeedServerEntry(OtherContent content) {
    this.setContent(content);
  }
  
  /**
   * Creates entry from "entity" XML.  
   * 
   * Example:
   * <pre>
   *   <entity>
   *     <name>vehicle0</name>
   *     <owner>Joe</owner>
   *     <price>23000</price>
   *     <propertyName repeatable='true'>make</propertyName>
   *     <propertyName>model</propertyName>
   *     <propertyName>year</propertyName>
   *     <propertyValue repeatable='true'>Honda</propertyValue>
   *     <propertyValue>Civic Hybrid</propertyValue>
   *     <propertyValue>2007</propertyValue>
   *   </entity>
   * </pre>
   * 
   * @param xmlText XML string representing an entity.
   */
  public FeedServerEntry(String xmlText) {
    XmlBlob xmlBlob = new XmlBlob();
    xmlBlob.setBlob(xmlText);
    setXmlBlob(xmlBlob);
    setContent(contentUtil.createXmlContent(xmlText));
  }
  
  /**
   * Sets the content of this entry from the supplied entity bean.
   * 
   * @param entity a bean representing the content.
   */
  public void setContentFromEntity(Object entity) {
    try {
      OtherContent content = contentUtil.createXmlContent(entity);
      this.setXmlBlob(content.getXml());
      this.setContent(content);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid entity bean " + entity.getClass().getName(), e);
    } catch (IntrospectionException e) {
      throw new RuntimeException("Invalid entity bean " + entity.getClass().getName(), e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Invalid entity bean " + entity.getClass().getName(), e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException("Invalid entity bean " + entity.getClass().getName(), e);
    }
  }
  
  /**
   * Utility method that given XML source for a feed entry, populates the content of this entry.
   * 
   * @param xmlText XML source for the associated feed entry.
   * @throws FeedServerClientException if any conversion errors occur parsing the XML.
   */
  public void setContentFromXmlString(String xmlText) throws FeedServerClientException {
    XmlBlob xmlBlob = new XmlBlob();
    xmlBlob.setBlob(xmlText);
    setXmlBlob(xmlBlob);
    setContent(contentUtil.createXmlContent(xmlText));
  }
  
  /**
   * Returns a populated entity bean from the provided entity class template.  The bean
   * should a field for each part of the entity you wish to retrieve. 
   * 
   * @param <T> the entity bean.
   * @param entityClass the bean template class. 
   * @return a populated entity ean.
   * @throws FeedServerClientException if the content in the entry is invalid
   * @throws RuntimeException if the bean is invalid.
   */
  public <T> T getEntity(Class<T> entityClass) throws FeedServerClientException {
    try {
      T entity = entityClass.newInstance();

      // last word is URL is id
      String id = getId();
      if (id != null) {
        int slash = id.lastIndexOf('/');
        id = slash < 0 ? id : id.substring(slash + 1);
      }

      contentUtil.fillBean((OtherContent) this.getContent(), entity, id);
      return entity;
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid bean " + entityClass.getName(), e);
    } catch (SAXException e) {
      throw new FeedServerClientException(e);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException("Invalid XML handler", e);
    } catch (IntrospectionException e) {
      throw new RuntimeException("Invalid bean " + entityClass.getName(), e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Invalid bean " + entityClass.getName(), e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException("Invalid bean " + entityClass.getName(), e);
    } catch (InstantiationException e) {
      throw new RuntimeException("Could not instantiate bean class" + entityClass.getName());
    } catch (IOException e) {
      throw new FeedServerClientException(e);
    } catch (ParseException e) {
      throw new FeedServerClientException(e);
    }
  }
}
