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
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.Entry;
import com.google.gdata.data.Feed;
import com.google.gdata.data.OtherContent;
import com.google.gdata.util.ServiceException;
import com.google.inject.Inject;
import com.google.feedserver.util.FeedServerClientException;
import com.google.feedserver.util.ContentUtil;
import com.google.feedserver.util.XmlUtil;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Implements a Gdata feed client that represents feeds as "typeless" maps of String->String pairs.
 * 
 * "typeless" maps are String -> List<String> representations of a FeedServer entry.  Each
 * key represents an element in the entry XML while the List<String> are the possible values.
 * For non-repeated elements, the list will only ever have one entry.  For "repeated=true" 
 * elements, the list will have N number of elements depending on how many entries exist for
 * that element.
 * 
 * This client is good for consumers of feeds who need quick access to a limited set of data
 * in the feed.  If you are using most of the data, using the "typed" java bean 
 * {@link FeedServerClient} is probably a better choice.
 * 
 * @author rayc@google.com (Ray Colline)
 */
public class TypelessFeedServerClient {
  
  // Logging instance
  private static final Logger log = Logger.getLogger(TypelessFeedServerClient.class);
  
  // Dependencies
  private GoogleService service;
  private ContentUtil contentUtil;
  private XmlUtil xmlUtil; 
  
  /**
   * Creates the client using provided dependencies.
   * 
   * @param service the configured Gdata service.
   */
  @Inject
  public TypelessFeedServerClient(GoogleService service, ContentUtil contentUtil, XmlUtil xmlUtil) {
    this.service = service;
    this.contentUtil = contentUtil;
    this.xmlUtil = xmlUtil;
  }
  
  /**
   * Creates the client by creating the dependencies.
   * 
   * @param service the configured Gdata service.
   */
  public TypelessFeedServerClient(GoogleService service) {
    this(service, new ContentUtil(), new XmlUtil());
  }
  
  /**
   * Fetches generic "payload-in-content" feed to a Map.  The returned map is a 
   * "map of lists of strings" where the lists or strings represent values.  
   * For non-repeatable elements, the list will only have one value.  The keys of this map 
   * are the element names.
   * 
   * @param feedUrl the feed URL which can contain any valid ATOM "query"
   * @return a map of lists of strings representing the "payload-in-content" entry.
   * @throws FeedServerClientException if we cannot contact the feedserver, fetch the URL, or 
   * parse the XML.
   */
  public Map<String, List<String>> getEntry(URL feedUrl) throws FeedServerClientException {
    try {
      Entry entry = service.getEntry(feedUrl, Entry.class);
      return getEntryMap(entry);
    } catch (IOException e) {
      throw new FeedServerClientException("Error while fetching " + feedUrl, e);
    } catch (ServiceException e) {
      throw new FeedServerClientException(e);
    }
  }
  
  /**
   * Fetches generic "payload-in-content" entries for the given feed query and returns them
   * as a list of maps. Each entry in the map is one entry returned by the feed and
   * they can be consumed without any knowledge of the schema for the feed.  See 
   * {@link TypelessFeedServerClient#getEntry(URL)} for description of the maps returned.
   * 
   * @param feedUrl the feed URL which can contain any valid ATOM "query"
   * @return a list of maps representing all the "payload-in-content" entries.
   * @throws FeedServerClientException if we cannot contact the feedserver, fetch the URL, or 
   * parse the XML.
   */
  public List<Map<String, List<String>>> getFeed(URL feedUrl) throws FeedServerClientException {
    // Retrieve Feed from network
    Feed feed;
    try {
      feed = service.getFeed(feedUrl, Feed.class);
    } catch (IOException e) {
      throw new FeedServerClientException("Error while fetching " + feedUrl, e);
    } catch (ServiceException e) {
      throw new FeedServerClientException(e);
    }
    
    // Go through all entries and build the map.
    List<Map<String, List<String>>> feedMap = new ArrayList<Map<String, List<String>>>();
    for (Entry entry : feed.getEntries()) {
      feedMap.add(getEntryMap(entry));
    }
    return feedMap;
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
   * Deletes specified by "name" in supplied entry map.
   * 
   * @param baseUrl Feed url not including ID.
   * @param entry a valid entry map.
   * @throws FeedServerClientException if any communication issues occur with the feed or the
   * feed ID is invalid or malformed.
   */
  public void deleteEntry(URL baseUrl, Map<String, List<String>> entry) throws 
      FeedServerClientException {
    
    try {
      String name = entry.get("name").get(0);
      URL feedUrl = new URL(baseUrl.toString() + "/" + name);
      log.info("deleting entry in feed " + feedUrl);
      deleteEntry(feedUrl);
    } catch (NullPointerException e) {
      throw new RuntimeException("entry map does not have 'name' key", e);
    } catch (IndexOutOfBoundsException e) {
      throw new RuntimeException("'name' in entry map is invalid.", e);
    } catch (MalformedURLException e) {
      throw new FeedServerClientException("invalid base URL", e);
    }
  }
  
  /**
   * Deletes each entry in the supplied list of entries.  This makes one request per entry.
   * 
   * @param baseUrl the feed URL not including ID.
   * @param entries a list of valid entries.
   * @throws FeedServerClientException if any communication issues occur with the feed or the
   * feed ID is invalid.
   */
  public void deleteEntries(URL baseUrl, List<Map<String, List<String>>> entries) throws
      FeedServerClientException {
    for (Map<String, List<String>> entry : entries) {
      deleteEntry(baseUrl, entry);
    }
  }
  
  /**
   * Updates the entry using the baseUrl plus the ID contained in the entry.
   * 
   * @param baseUrl feed URL without an ID.
   * @param mapEntry a "typeless" map representing a feed entry.
   * @throws FeedServerClientException if any feed communication issues occur or the URL is 
   * malformed.
   */
  public void updateEntry(URL baseUrl, Map<String, List<String>> mapEntry) throws 
      FeedServerClientException {
    
    try {
      String name = mapEntry.get("name").get(0);
      URL feedUrl = new URL(baseUrl.toString() + "/" + name);
      log.info("updating entry to feed " + feedUrl);
      service.update(feedUrl, getEntryFromMap(mapEntry));
    } catch (MalformedURLException e) {
      throw new RuntimeException("Invalid URL", e);
    } catch (IOException e) {
      throw new FeedServerClientException("Error while deleting " + baseUrl, e);
    } catch (ServiceException e) {
      throw new FeedServerClientException(e);
    } catch (NullPointerException e) {
      throw new RuntimeException("Invalid Entry", e); 
    }
  }
  
  /**
   * Updates the entries using the baseUrl plus the ID contained in each entry.
   * 
   * @param baseUrl feed URL without an ID.
   * @param entries a list of "typeless" maps representing feed entries.
   * @throws FeedServerClientException if any feed communication issues occur or the URL is 
   * malformed.
   */
  public void updateEntries(URL baseUrl, List<Map<String, List<String>>> entries) 
      throws FeedServerClientException {
    for (Map<String, List<String>> mapEntry : entries) {
       updateEntry(baseUrl, mapEntry);      
    }
  }

  /**
   * Inserts the entry using the baseUrl provided.
   * 
   * @param baseUrl feed URL without an ID.
   * @param mapEntry a "typeless" map representing a feed entry.
   * @throws FeedServerClientException if any feed communication issues occur or the URL is 
   * malformed.
   */
  public void insertEntry(URL baseUrl, Map<String, List<String>> mapEntry) throws
      FeedServerClientException {
    
    try {
      String name = mapEntry.get("name").get(0);
      log.info("inserting entry to feed " + baseUrl);
      service.insert(baseUrl, getEntryFromMap(mapEntry));
    } catch (MalformedURLException e) {
      throw new RuntimeException("Invalid URL", e);
    } catch (IOException e) {
      throw new FeedServerClientException("Error while deleting " + baseUrl, e);
    } catch (ServiceException e) {
      throw new FeedServerClientException(e);
    } catch (NullPointerException e) {
      throw new RuntimeException("Invalid Entry", e); 
    }
  }
  
  /**
   * Inserts the entries using the baseUrl provided.
   * 
   * @param baseUrl feed URL without an ID.
   * @param entries a list of "typeless" maps each representing a feed entry.
   * @throws FeedServerClientException if any feed communication issues occur or the URL is 
   * malformed.
   */
  public void insertEntries(URL baseUrl, List<Map<String, List<String>>> entries) throws
      FeedServerClientException {
    for (Map<String, List<String>> mapEntry : entries) {
       insertEntry(baseUrl, mapEntry);      
    }
  }
  
  /**
   * Converts raw XML representation of a feed entry into a "typeless" map.
   * 
   * @param xmlText raw XML entry.
   * @returns a "typeless" map representing an entry.
   * @throws FeedServerClientException if the Xml cannot be parsed.
   */
  public Map<String, List<String>> getEntryMapFromXml(String xmlText) throws
      FeedServerClientException {
    try {
      Map<String,Object> rawEntryMap = xmlUtil.convertXmlToProperties(xmlText);
      OtherContent content = contentUtil.createXmlContent(
          xmlUtil.convertPropertiesToXml(rawEntryMap));
      Entry entry = new Entry();
      entry.setContent(content);
      return getEntryMap(entry);
    } catch (SAXException e) {
      throw new FeedServerClientException(e);
    } catch (IOException e) {
      throw new FeedServerClientException(e);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Returns a gdata entry object populated with contents generated from a "typeless" map.
   * 
   * @param entryMap the "typeless" map to convert.
   * @return a populated gdata entry object.
   */
  private Entry getEntryFromMap(Map<String, List<String>> entryMap) {
    // XMLutil expects entries in map form of string -> object.  For repeatable elements
    // the object is really an "object[]" but for single elements its a "String".  
    // This loop prepares this very hacky map representation from the passed in more sane
    // typed implementation.
    Map<String, Object> baseEntryMap = new HashMap<String, Object>();
    for (String key : entryMap.keySet()) {
      int size = entryMap.get(key).size();
      if (entryMap.get(key).size() > 1) {
        Object[] objects = new Object[size];
        for (int index=0; index < size; index++) {
          objects[index] = entryMap.get(key).get(index);
        }
        baseEntryMap.put(key, objects);
      } else {
        baseEntryMap.put(key, entryMap.get(key).get(0));
      }
    }
    OtherContent content = contentUtil.createXmlContent(
        xmlUtil.convertPropertiesToXml(baseEntryMap));
    Entry entry = new Entry();
    entry.setContent(content);
    entry.setXmlBlob(content.getXml());
    return entry;
  }
  
  /**
   * Helper function that parses entry into an entry map of with string keys and list of string
   * values.  
   * 
   * @param entry the entry to parse.
   * @return the populated map.
   * @throws FeedServerClientException if the XML parse fails.
   */
  private Map<String, List<String>> getEntryMap(Entry entry) throws FeedServerClientException {
    // Get XML and convert to primitive Object map. 
    OtherContent content = (OtherContent) entry.getContent();  
    log.info("Entry info " + content.getXml().getBlob());
    XmlUtil xmlUtil = new XmlUtil();
    Map<String, Object> rawEntryMap;
    try {
      rawEntryMap = xmlUtil.convertXmlToProperties(content.getXml().getBlob());
    } catch (SAXException e) {
      throw new FeedServerClientException(e);
    } catch (IOException e) {
      throw new FeedServerClientException(e);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
    
    // Convert into more consumable format.
    Map<String, List<String>> entryMap = new HashMap<String, List<String>>();
    for (String key : rawEntryMap.keySet()) {
      List<String> value = new ArrayList<String>();
      if (rawEntryMap.get(key) instanceof Object[]) {
        Object[] rawValues = (Object[]) rawEntryMap.get(key);
        for (Object rawValue : rawValues) {
          value.add((String) rawValue);
        }
      } else {
        value.add((String) rawEntryMap.get(key));
      }
      entryMap.put(key, value);
    }
    return entryMap;
  }
}
