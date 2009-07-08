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
import com.google.gdata.util.ServiceException;
import com.google.inject.Inject;
import com.google.feedserver.util.FeedServerClientException;

import org.apache.commons.beanutils.BeanMap;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements a parameterized Gdata feed client for FeedServer "payload-in-content" feeds employing
 * java beans to represent the data.  Create a java bean that represents the entity data and
 * use it as the parameterized type for the feed client.  
 * 
 * @author r@kuci.org (Ray Colline)
 */
public class FeedServerClient<T> {
  
  private static final String NAME_ELEMENT = "name";

  // Logging instance
  private static final Logger LOG = Logger.getLogger(FeedServerClient.class);
  
  // Dependencies
  private GoogleService service; 
  private Class<T> entityClass; // Java bean
  
  
  /**
   * Creates client using supplied service and entityClass
   * 
   * @param service the configured Gdata service.
   */
  @Inject
  public FeedServerClient(GoogleService service, Class<T> entityClass) {
    this.service = service;
    this.entityClass = entityClass;
  }
  

  /**
   * Fetches generic "payload-in-content" entry into a {@link FeedServerEntry}.   The
   * FeedServerEntry allows you to return the content of the entry as a java bean.
   * 
   * @param entryUrl the entry URL which can contain any valid ATOM "query"
   * @return the populated entry.
   * @throws FeedServerClientException if we cannot contact the feedserver, fetch the URL, or 
   * parse the XML.
   * @throws RuntimeException if the bean is not constructed properly and is missing fields.
   */
  public FeedServerEntry getEntry(URL entryUrl) throws FeedServerClientException {
    try {
      return service.getEntry(entryUrl, FeedServerEntry.class);
    } catch (IOException e) {
      throw new FeedServerClientException("Error while fetching " + entryUrl, e);
    } catch (ServiceException e) {
      throw new FeedServerClientException(e);
    }
  }
    
  /**
   * Fetches generic "payload-in-content" entity into a predefined java bean.  This bean should
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
  public T getEntity(URL feedUrl) throws FeedServerClientException {
    return getEntry(feedUrl).getEntity(entityClass);
  }
  
  /**
   * Fetches generic "payload-in-content" entry into a list of {@link FeedServerEntry}.   The
   * FeedServerEntry allows you to return the content of the entry as a java bean.
   * 
   * @param feedUrl the feed URL which can contain any valid ATOM "query"
   * @return the list of populated entries.
   * @throws FeedServerClientException if we cannot contact the feedserver, fetch the URL, or 
   * parse the XML.
   * @throws RuntimeException if the bean is not constructed properly and is missing fields.
   */
  public List<FeedServerEntry> getEntries(URL feedUrl) throws FeedServerClientException {
    try {
      FeedServerFeed feed = service.getFeed(feedUrl, FeedServerFeed.class);
      return feed.getEntries();
    } catch (IOException e) {
      throw new FeedServerClientException("Error while fetching " + feedUrl, e);
    } catch (ServiceException e) {
      throw new FeedServerClientException(e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid bean " + entityClass.getName(), e);
    }
  }
  
  /**
   * Fetches generic "payload-in-content" feed .
   * 
   * @param feedUrl the feed URL which can contain any valid ATOM "query"
   * @return the populated feed.
   * @throws FeedServerClientException if we cannot contact the feedserver, fetch the URL, or 
   * parse the XML.
   * @throws RuntimeException if the bean is not constructed properly and is missing fields.
   */
  public FeedServerFeed getFeed(URL feedUrl) throws FeedServerClientException {
    try {
      return service.getFeed(feedUrl, FeedServerFeed.class);
    } catch (IOException e) {
      throw new FeedServerClientException("Error while fetching " + feedUrl, e);
    } catch (ServiceException e) {
      throw new FeedServerClientException(e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid bean " + entityClass.getName(), e);
    }
  }
  
  /**
   * Fetches generic "payload-in-content" entity into a list of predefined java bean.  This 
   * bean should have fields necessary to receive all the elements of the feed.  Using a bean, 
   * requires you know the schema of your feed ahead of time, but gives you the convenience of 
   * having first class object access. 
   * 
   * @param feedUrl the feed URL which can contain any valid ATOM "query"
   * @return a list of populated beans representing an entry's entity.
   * @throws FeedServerClientException if we cannot contact the feedserver, fetch the URL, or 
   * parse the XML.
   * @throws RuntimeException if the bean is not constructed properly and is missing fields.
   */
  public List<T> getEntities(URL feedUrl) throws FeedServerClientException {
    
    List<FeedServerEntry> entries = getEntries(feedUrl);
    List<T> entities = new ArrayList<T>();
    for (FeedServerEntry entry : entries) {
      entities.add(entry.getEntity(entityClass));  
    }
    return entities;
  }
  
  /**
   * Deletes entry specified by supplied URL.  This URL must include the ID.
   * 
   * @param entryUrl the full URL to the entry in this feed.
   * @throws FeedServerClientException if any communication issues occur with the feed or the
   * feed ID is invalid or malformed..
   */
  public void deleteEntry(URL entryUrl) throws FeedServerClientException {
    try {
      service.delete(entryUrl);
    } catch (IOException e) {
      throw new FeedServerClientException("Error while deleting " + entryUrl, e);
    } catch (ServiceException e) {
      throw new FeedServerClientException(e);
    }
  }
  
  /**
   * Deletes specified entry using "name" property contained in the entry's entity.
   * 
   * @param baseUrl Feed url not including ID
   * @param entry valid entry containing an entity bean.
   * @throws FeedServerClientException if any communication issues occur with the feed or the
   * feed ID is invalid or malformed.
   */
  public void deleteEntry(URL baseUrl, FeedServerEntry entry) throws FeedServerClientException {
    String name = (String) getBeanProperty(NAME_ELEMENT, entry.getEntity(entityClass), new String());
    try {
      URL feedUrl = new URL(baseUrl.toString() + "/" + name);
      LOG.info("deleting entry at feed " + feedUrl);
      deleteEntry(feedUrl);
    } catch (MalformedURLException e) {
      throw new FeedServerClientException("invalid base URL", e);
    }
  }
  
  /**
   * Deletes specified entry using "name" property contained in the supplied entity bean.
   * 
   * @param baseUrl Feed url not including ID
   * @param entity valid entry bean.
   * @throws FeedServerClientException if any communication issues occur with the feed or the
   * feed ID is invalid or malformed.
   */
  public void deleteEntity(URL baseUrl, T entity) throws FeedServerClientException {
    String name = (String) getBeanProperty(NAME_ELEMENT, entity, new String());
    try {
      URL feedUrl = new URL(baseUrl.toString() + "/" + name);
      LOG.info("deleting entry at feed " + feedUrl);
      deleteEntry(feedUrl);
    } catch (MalformedURLException e) {
      throw new FeedServerClientException("invalid base URL", e);
    }
  }
  
  /**
   * Deletes specified entries using "name" property contained in the entry's entity.  This 
   * makes one request per entry.
   * 
   * @param baseUrl Feed url not including ID
   * @param entries list of valid populated entries.
   * @throws FeedServerClientException if any communication issues occur with the feed or the
   * feed ID is invalid or malformed.
   */
  public void deleteEntries(URL baseUrl, List<FeedServerEntry> entries) 
      throws FeedServerClientException {
    for (FeedServerEntry entry : entries) {
      deleteEntry(baseUrl, entry);
    }
  }
  
  /**
   * Deletes specified entries using "name" property contained in the entity bean.  This makes one
   * request per entry.
   * 
   * @param baseUrl Feed url not including ID
   * @param entities list of valid entity beans.
   * @throws FeedServerClientException if any communication issues occur with the feed or the
   * feed ID is invalid or malformed.
   */
  public void deleteEntities(URL baseUrl, List<T> entities) throws FeedServerClientException {
    for (T entity : entities) {
      deleteEntity(baseUrl, entity);
    }
  }

  /**
   * Updates the entry using the baseUrl plus the ID contained in the entry's entity.
   * 
   * @param baseUrl fully qualified feed URL.
   * @param entry populated entry object.
   * @throws FeedServerClientException if any feed communication errors occur.
   */
  public void updateEntry(URL baseUrl, FeedServerEntry entry) throws FeedServerClientException {
    String name = (String) getBeanProperty(NAME_ELEMENT, entry.getEntity(entityClass), new String());
    try {
      LOG.info("updating entry at feed " + baseUrl + "/" + name);
      URL url = new URL(baseUrl + "/" + name);
      service.update(url, entry);
    } catch (IOException e) {
      throw new FeedServerClientException(e);
    } catch (ServiceException e) {
      throw new FeedServerClientException(e);
    }
  }
  
  /**
   * Updates the entry using the baseUrl plus the ID contained in the entity.
   * 
   * @param baseUrl feed URL without an ID.
   * @param entity a bean representing a feed entry.
   * @throws FeedServerClientException if any feed communication issues occur or the URL is 
   * malformed.
   */
  public void updateEntity(URL baseUrl, T entity) throws FeedServerClientException {
    FeedServerEntry entry = new FeedServerEntry(entity);
    updateEntry(baseUrl, entry);
  }

  /**
   * Updates the entries using the baseUrl plus the ID contained in each entry's entity.
   * 
   * @param baseUrl feed URL without an ID.
   * @param entries a list of entries
   * @throws FeedServerClientException if any feed communication issues occur or the URL is 
   * malformed.
   */
  public void updateEntries(URL baseUrl, List<FeedServerEntry> entries) throws FeedServerClientException {
    for (FeedServerEntry entry : entries) {
      updateEntry(baseUrl, entry);
    }
  }
  
  /**
   * Updates the entries using the baseUrl plus the ID contained in each entity.
   * 
   * @param baseUrl feed URL without an ID.
   * @param entities a list of beans representing feed entries.
   * @throws FeedServerClientException if any feed communication issues occur or the URL is 
   * malformed.
   */
  public void updateEntities(URL baseUrl, List<T> entities) throws FeedServerClientException {
    for (T entity : entities) {
      updateEntity(baseUrl, entity);
    }
  }
  
  /**
   * Creates a new entry from the given entity and inserts it.
   * 
   * @param baseUrl feed URL without an ID.
   * @param entity a bean representing a feed entry.
   * @throws FeedServerClientException if any feed communication issues occur or the URL is 
   * malformed.
   */
  @SuppressWarnings("unchecked")
  public T insertEntity(URL baseUrl, T entity) throws FeedServerClientException {
    FeedServerEntry entry = new FeedServerEntry(entity);
    try {
      LOG.info("inserting entry at feed " + baseUrl);
      entry = service.insert(baseUrl, entry);
      return (T) entry.getEntity(entity.getClass());
    } catch (IOException e) {
      throw new FeedServerClientException(e);
    } catch (ServiceException e) {
      throw new FeedServerClientException(e);
    }
  }
    
  /**
   * Inserts the entry using the baseUrl provided.
   * 
   * @param baseUrl feed URL without an ID.
   * @param entry a populated feed entry.
   * @throws FeedServerClientException if any feed communication issues occur or the URL is 
   * malformed.
   */
  public void insertEntry(URL baseUrl, FeedServerEntry entry) throws FeedServerClientException {
    try {
      LOG.info("inserting entry at feed " + baseUrl);
      service.insert(baseUrl, entry);
    } catch (IOException e) {
      throw new FeedServerClientException(e);
    } catch (ServiceException e) {
      throw new FeedServerClientException(e);
    }
  }
  
  /**
   * Creates an entry for each provided entity and inserts this.  This results in one request
   * for each given entity.
   * 
   * @param baseUrl feed URL without an ID.
   * @param entities a list of entity beans each representing a feed entry.
   * @throws FeedServerClientException if any feed communication issues occur or the URL is 
   * malformed.
   */
  public void insertEntities(URL baseUrl, List<T> entities) throws FeedServerClientException {
    for (T entity : entities) {
      insertEntity(baseUrl, entity);
    }
  }
  
  /**
   * Inserts the entries provided using the baseUrl provided. This results in one request for
   * each given entry.
   * 
   * @param baseUrl feed URL without an ID.
   * @param entries a list of feed entries.
   * @throws FeedServerClientException if any feed communication issues occur or the URL is 
   * malformed.
   */
  public void insertEntries(URL baseUrl, List<FeedServerEntry> entries) 
      throws FeedServerClientException {
    for (FeedServerEntry entry : entries) {
      insertEntry(baseUrl, entry);
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

  public GoogleService getService() {
    return service;
  }
}
