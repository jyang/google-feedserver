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

import com.google.gdata.client.GoogleService;
import com.google.gdata.data.Entry;
import com.google.gdata.data.Feed;
import com.google.gdata.util.ServiceException;
import com.google.inject.Inject;
import com.google.feedserver.util.ContentUtil;
import com.google.feedserver.util.FeedServerClientException;

import org.apache.commons.beanutils.BeanMap;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Implements a Gdata feed client that represents feeds as generic maps of String->String pairs.
 * 
 * @author rayc@google.com (Ray Colline)
 */
public class FeedServerClient<T> {
  
  // Logging instance
  private static final Logger LOG = Logger.getLogger(FeedServerClient.class);
  
  // Dependencies
  private GoogleService service; 
  private ContentUtil contentUtil;
  private Class<T> beanClass;
  
  /**
   * Creates the client using provided dependencies.
   * 
   * @param service the configured Gdata service.
   * @param contentUtil a created ContentUtil.
   */
  @Inject
  public FeedServerClient(GoogleService service, ContentUtil contentUtil, Class<T> beanClass) {
    this.service = service;
    this.contentUtil = contentUtil;
    this.beanClass = beanClass;
  }
  
  /**
   * Creates client using supplied service in a non dependency-injection way.
   * 
   * @param service the configured Gdata service.
   */
  public FeedServerClient(GoogleService service, Class<T> beanClass) {
    this(service, new ContentUtil(), beanClass);
  }
  
  /**
   * Fetches generic "payload-in-content" entry into a predefined java bean.  This bean should
   * have fields necessary to receive all the elements of the feed.  Using a bean, requires you
   * know the schema of your feed ahead of time, but gives you the convenience of having first
   * class object access. 
   * 
   * @param feedUrl the feed URL which can contain any valid ATOM "query"
   * @return the populated bean.
   * @throws FeedServerClientException if we cannot contact the feedserver, fetch the URL, or 
   * parse the XML.
   * @throws RuntimeException if the bean is not constructed properly and is missing fields.
   */
  public T getEntry(URL feedUrl) throws FeedServerClientException {
    try {
      Entry entry = service.getEntry(feedUrl, Entry.class);
      T bean = beanClass.newInstance();
      contentUtil.fillBean(entry, bean);
      return bean;
    } catch (IOException e) { // holy exception list batman!
      throw new FeedServerClientException("Error while fetching " + feedUrl, e);
    } catch (ServiceException e) {
      throw new FeedServerClientException(e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid bean " + beanClass.getName(), e);
    } catch (SAXException e) {
      throw new FeedServerClientException(e);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException("Invalid XML handler", e);
    } catch (IntrospectionException e) {
      throw new RuntimeException("Invalid bean " + beanClass.getName(), e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Invalid bean " + beanClass.getName(), e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException("Invalid bean " + beanClass.getName(), e);
    } catch (InstantiationException e) {
      throw new RuntimeException("Could not instantiate bean class" + beanClass.getName());
    }
  }

  /**
   * Fetches generic "payload-in-content" feed into a list of the supplied predefined java bean.  
   * This bean should have fields necessary to receive all the elements of the feed.  Using a bean
   * requires you know the schema of your feed ahead of time, but gives you the convenience of 
   * having first-class object representation. 
   * 
   * @param feedUrl the feed URL which can contain any valid ATOM "query"
   * @return the list of populated bean.
   * @throws FeedServerClientException if we cannot contact the feedserver, fetch the URL, or 
   * parse the XML.
   * @throws RuntimeException if the bean is not constructed properly and is missing fields.
   */
  @SuppressWarnings("cast")
  public List<T> getFeed(URL feedUrl) throws FeedServerClientException {
    try {
      Feed feed = service.getFeed(feedUrl, Feed.class);
      ArrayList<T> beanEntries = new ArrayList<T>();
      for (Entry entry : feed.getEntries()) {
        T beanEntry = (T) beanClass.newInstance();
        contentUtil.fillBean(entry, beanEntry);
        beanEntries.add(beanEntry);
      }
    return beanEntries;
    } catch (IOException e) { // holy exception list batman!
      throw new FeedServerClientException("Error while fetching " + feedUrl, e);
    } catch (ServiceException e) {
      throw new FeedServerClientException(e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid bean " + beanClass.getName(), e);
    } catch (SAXException e) {
      throw new FeedServerClientException(e);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException("Invalid XML handler", e);
    } catch (IntrospectionException e) {
      throw new RuntimeException("Invalid bean " + beanClass.getName(), e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Invalid bean " + beanClass.getName(), e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException("Invalid bean " + beanClass.getName(), e);
    } catch (InstantiationException e) {
      throw new RuntimeException("Invalid bean " + beanClass.getName(), e);
    }
  }
  
  /**
   * Deletes entry specified by supplied URL.  This URL must include the full path.
   * 
   * @param feedUrl the full URL to the entry in this feed.
   * @throws FeedServerClientException if any communication issues occur with the feed or the
   * feed ID is invalid or malformed..
   */
  public void deleteEntry(URL feedUrl) throws FeedServerClientException {
    try {
      service.delete(feedUrl);
    } catch (IOException e) {
      throw new FeedServerClientException("Error while deleting " + feedUrl, e);
    } catch (ServiceException e) {
      throw new FeedServerClientException(e);
    }
  }
  
  /**
   * Deletes specified entry using "name" property contained in the bean.
   * 
   * @param baseUrl Feed url not including ID
   * @param entry valid entry bean.
   * @throws FeedServerClientException if any communication issues occur with the feed or the
   * feed ID is invalid or malformed.
   */
  public void deleteEntry(URL baseUrl, T entry) throws FeedServerClientException {
    String name = (String) getBeanProperty("name", entry, new String());
    try {
      URL feedUrl = new URL(baseUrl.toString() + "/" + name);
      LOG.info("deleting entry at feed " + feedUrl);
      deleteEntry(feedUrl);
    } catch (MalformedURLException e) {
      throw new FeedServerClientException("invalid base URL", e);
    }
  }
  
  /**
   * Deletes specified entries using "name" property contained in the bean.  This makes one
   * request per entry.
   * 
   * @param baseUrl Feed url not including ID
   * @param entries list of valid entry beans.
   * @throws FeedServerClientException if any communication issues occur with the feed or the
   * feed ID is invalid or malformed.
   */
  public void deleteEntries(URL baseUrl, List<T> entries) throws FeedServerClientException {
    for (T entry : entries) {
      deleteEntry(baseUrl, entry);
    }
  }
  
  
  /**
   * Updates the entry using the baseUrl plus the ID contained in the entry.
   * 
   * @param baseUrl feed URL without an ID.
   * @param beanEntry a bean representing a feed entry.
   * @throws FeedServerClientException if any feed communication issues occur or the URL is 
   * malformed.
   */
  public void updateEntry(URL baseUrl, T beanEntry) throws FeedServerClientException {
    String name = (String) getBeanProperty("name", beanEntry, new String());
    FeedServerEntry feedServerEntry = createEntryFromBean(beanEntry);
    try {
      LOG.info("updating entry at feed " + baseUrl + "/" + name);
      updateEntry(new URL(baseUrl.toString() + "/" + name), feedServerEntry);
    } catch (MalformedURLException e) {
      throw new FeedServerClientException("invalid base URL", e);
    }
  }

  /**
   * Updates the entries using the baseUrl plus the ID contained in each entry.
   * 
   * @param baseUrl feed URL without an ID.
   * @param beanEntries a list of beans representing feed entries.
   * @throws FeedServerClientException if any feed communication issues occur or the URL is 
   * malformed.
   */
  public void updateEntries(URL baseUrl, List<T> beanEntries) throws FeedServerClientException {
    for (T entry : beanEntries) {
      updateEntry(baseUrl, entry);
    }
  }

  /**
   * Inserts the entry using the baseUrl provided.
   * 
   * @param baseUrl feed URL without an ID.
   * @param beanEntry a bean representing a feed entry.
   * @throws FeedServerClientException if any feed communication issues occur or the URL is 
   * malformed.
   */
  public void insertEntry(URL baseUrl, T beanEntry) throws FeedServerClientException {
    String name = (String) getBeanProperty("name", beanEntry, new String());
    FeedServerEntry feedServerEntry = createEntryFromBean(beanEntry);
    try {
      LOG.info("inserting entry at feed " + baseUrl);
      service.insert(baseUrl, feedServerEntry);
    } catch (IOException e) {
      throw new FeedServerClientException(e);
    } catch (ServiceException e) {
      throw new FeedServerClientException(e);
    }
  }
  
  /**
   * Inserts the entries using the baseUrl provided.
   * 
   * @param baseUrl feed URL without an ID.
   * @param beanEntries a list of beans each representing a feed entry.
   * @throws FeedServerClientException if any feed communication issues occur or the URL is 
   * malformed.
   */
  public void insertEntries(URL baseUrl, List<T> beanEntries) throws FeedServerClientException {
    for (T entry : beanEntries) {
      insertEntry(baseUrl, entry);
    }
  }
  
  /**
   * Utility method that given XML source for a feed entry, creates a bean.
   * 
   * @param xmlText XML source for the associated feed entry.
   * @return a populated bean.
   * @throws FeedServerClientException if any conversion errors occur parsing the XML.
   */
  public T fillBeanFromXml(String xmlText) throws FeedServerClientException {
    
    try {
      T bean = beanClass.newInstance();
      contentUtil.fillBean(contentUtil.createXmlContent(xmlText), bean);
      return bean;
    } catch (InstantiationException e) {
      throw new RuntimeException("Could not create new bean " + beanClass.getName());
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Could not construct new bean " + beanClass.getName());
    } catch (IOException e) { // holy exception list batman!
      throw new FeedServerClientException("Error while converting XML to bean.", e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid bean " + beanClass.getName(), e);
    } catch (SAXException e) {
      throw new FeedServerClientException(e);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException("Invalid XML handler", e);
    } catch (IntrospectionException e) {
      throw new RuntimeException("Invalid bean " + beanClass.getName(), e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException("Invalid bean " + beanClass.getName(), e);
    }
  }

  /**
   * Helper function to update gdata feed using native {@link Entry}.
   * 
   * @param feedUrl fully qualified feed URL.
   * @param entry populated entry object.
   * @throws FeedServerClientException if any feed communication errors occur.
   */
  private void updateEntry(URL feedUrl, FeedServerEntry entry) throws FeedServerClientException {
    try {
      service.update(feedUrl, entry);
    } catch (IOException e) {
      throw new FeedServerClientException(e);
    } catch (ServiceException e) {
      throw new FeedServerClientException(e);
    }
  }
  
  /**
   * Helper method to retrieve a property from the provided bean.
   * 
   * @param propertyName the property to read from the bean.
   * @param bean the bean to read from.
   * @param container the place to put the read value.
   * @return the container supplied.
   * @throws FeedServerClientException if any problems exist with the bean.
   */
  private Object getBeanProperty(String propertyName, T bean, Object container) throws 
      FeedServerClientException {
    try {
      BeanMap beanMap = new BeanMap(bean);
      return beanMap.get(propertyName);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid bean " + bean.getClass().getName(), e);
    } catch (SecurityException e) {
      throw new RuntimeException("Invalid bean " + bean.getClass().getName(), e);
    }
  }
  
  /**
   * Helper function that creates an Gdata Entry object from the supplied bean.
   * 
   * @param bean representing an entry's content.
   * @return FeedServerEntry with populated content.
   */
  private FeedServerEntry createEntryFromBean(T bean) {
    try {
      FeedServerEntry feedServerEntry = new FeedServerEntry();
      feedServerEntry.setContent(contentUtil.createXmlContent(bean));
      return feedServerEntry;
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid bean " + bean.getClass().getName(), e);
    } catch (IntrospectionException e) {
      throw new RuntimeException("Invalid bean " + bean.getClass().getName(), e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Invalid bean " + bean.getClass().getName(), e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException("Invalid bean " + bean.getClass().getName(), e);
    }
  } 
}
