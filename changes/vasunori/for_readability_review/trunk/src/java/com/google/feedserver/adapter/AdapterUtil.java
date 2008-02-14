/**
 * Copyright 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.feedserver.adapter;

import com.google.feedserver.config.FeedConfiguration;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.io.IOException;
import java.lang.reflect.Constructor;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

/**
 * Contains various util methods used by adapters.
 */
public class AdapterUtil {

  public static Logger logger =
    Logger.getLogger(AdapterUtil.class.getName());

  // The adapter instance map - maintains a list of currently loaded adapters
  protected static Map<String, Adapter> adapterInstanceMap =
      new HashMap<String, Adapter>();

  /**
   * Creates the feed object and fills in the mandatory elements such as
   * id, title, author, updated.
   * id, title, author are pulled from the config file
   *    (loaded when the adapter is instantiated) and command line input args.
   * TODO: why do we have them in 2 different places. not clean at all
   *
   * @param abdera
   * @param feedConfiguration
   *
   * @return Feed object created
   */
  protected static Feed createFeed(Abdera abdera,
      FeedConfiguration feedConfiguration) {
    Feed feed = abdera.newFeed();
    feed.setId(feedConfiguration.getFeedUri());
    feed.setTitle(feedConfiguration.getFeedTitle());
    feed.setUpdated(new Date());
    feed.addAuthor(feedConfiguration.getFeedAuthor());
    return feed;
  }

  /**
   * Adds the edit link to entry.
   *
   * @param entry
   *
   * @throws Exception raised by abdera during addition of link.
   */
  protected static void addEditLinkToEntry(Entry entry) throws Exception {
    if (getEditUriFromEntry(entry) == null) {
      entry.addLink(entry.getId().toString(), "edit");
    }
  }

  /**
   * Gets the uri from edit link of the specified entry.
   *
   * @param entry the entry
   *
   * @return the editUri from entry
   *
   * @throws Exception raised by abdera
   */
  public static String getEditUriFromEntry(Entry entry) throws Exception {
    String editUri = null;
    List<Link> editLinks = entry.getLinks("edit");
    if (editLinks != null) {
      for (Link link : editLinks) {
        /* if there is more than one edit link, we should not automatically
         * assume that it's always going to point to an Atom document
         * representation.
         */
        if (link.getMimeType() != null) {
          if (link.getMimeType().match("application/atom+xml")) {
            editUri = link.getResolvedHref().toString();
            break;
          }
        } else {
          // edit link with no type attribute is the right one to use
          editUri = link.getResolvedHref().toString();
          break;
        }
      }
     }
   return editUri;
  }

  /**
   * If entry doesn't have its "id" set, an id is assigned to it.
   *
   * @param abdera
   * @param feedConfiguration
   * @param entry
   */
  protected static void setEntryIdIfNull(Abdera abdera,
      FeedConfiguration feedConfiguration, Entry entry)  {
    // if there is no id in Entry, assign one.
    if (entry.getId() != null) {
      return;
    }
    String uuidUri = abdera.getFactory().newUuidUri();
    String[] segments = uuidUri.split(":");
    String entryId = segments[segments.length - 1];
    entry.setId(createEntryIdUri(feedConfiguration, entryId));
  }

  /**
   * Creates the entry id in uri format, given the entryid
   *     (the last part of uri) and the config info object
   *
   * @param feedConfiguration
   * @param entryId the entry id
   *
   * @return the uri string created
   */
  protected static String createEntryIdUri(FeedConfiguration feedConfiguration,
      String entryId) {
    return feedConfiguration.getFeedUri() + "/" + entryId;
  }

  /**
   * Parses the input atom:entry and
   * returns a map of all element_names and their values.
   *
   * It first looks for the reserved elements: id, author, title, updated.
   * and then it looks for the <content> element,
   *  which is in the following format
   *       <content>
   *          <entity>
   *              <col_name_1> col value </col_name_1>
   *              <col_name_2> col value </col_name_2>
   *                ... so on ..
   *          </entity>
   *       </content>
   *  From this "content" element, it collects all col_names and col_values
   *  and creates data in the map with key=col_name & value = col_value.
   *
   * @param entry to be parsed
   *
   * @return map<String, Object> = map<col_name, col_value>
   *
   * @throws Exception
   */
  protected static Map<String, Object> collectColumns(Entry entry)
      throws Exception {
    Map<String, Object> columns = new HashMap<String, Object>();

    // look for reserved elements: id, author, title
    if (entry.getId() != null) {
      columns.put(FeedConfiguration.ENTRY_ELEM_NAME_ID,
          entry.getId().toString());
    }
    if (entry.getAuthor() != null) {
      columns.put(FeedConfiguration.ENTRY_ELEM_NAME_AUTHOR,
          entry.getAuthor().getText());
    }
    if (entry.getTitle() != null) {
      columns.put(FeedConfiguration.ENTRY_ELEM_NAME_TITLE, entry.getTitle());
    }
    if (entry.getUpdated() != null) {
      columns.put(FeedConfiguration.ENTRY_ELEM_NAME_UPDATED,
          entry.getUpdated());
    }

    // look for content element
    Content content = entry.getContentElement();
    if (content != null) {
      String contentStr = content.getValue();
      parseContent(contentStr, columns);
    }

    return columns;
  }

  /**
   * Parses the content element in the entry
   *
   * @param str The content element
   * @param columns map to collect all col_names and col_values
   *
   * @throws Exception
   */
  private static void parseContent(String str, Map<String, Object> columns)
      throws Exception {
    ByteArrayInputStream inStr = new ByteArrayInputStream(str.getBytes());
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader parser = factory.createXMLStreamReader(inStr);

    while (true) {
      int event = parser.next();
      if (event == XMLStreamConstants.END_DOCUMENT) {
         parser.close();
         break;
      }
      if (event == XMLStreamConstants.START_ELEMENT) {
        String name = parser.getLocalName();
        int eventType =  parser.next();
        if (eventType == XMLStreamConstants.CHARACTERS) {
          String value = parser.getText();
          columns.put(name, value);
        }
      }
    }
  }

  /**
   * returns the adapter instance for the given feedid.
   *
   * @param abdera
   * @param feedId
   *
   * @return the adapter instance object
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static Adapter getAdapter(Abdera abdera, String feedId)
      throws IOException {

    // is an adapter already created for this feedid?
    // if so, return the existing instance.
    if (adapterInstanceMap.containsKey(feedId)){
      return adapterInstanceMap.get(feedId);
    }

    // no adapter exists for the given feedid. create it.
    FeedConfiguration feedConfiguration
        = FeedConfiguration.getFeedConfiguration(feedId);
    if (null == feedConfiguration) {
      // Configuration for this feed is missing.
      return null;
    }
    return createAdapterInstance(abdera, feedConfiguration);
  }

  /**
   * Creates the adapter instance by loading the class and passing in
   * the config info for the adapter.
   *
   * @param abdera
   * @param feedConfiguration config object
   *
   * @return the adapter instance object created/found
   */
  protected static synchronized Adapter createAdapterInstance(
      Abdera abdera, FeedConfiguration feedConfiguration) {

    // due to a small window, it is possible that an adapter for this
    // feedid is already created. if so, check and return
    Adapter adapter = adapterInstanceMap.get(feedConfiguration.getFeedId());
    if (adapter != null) {
      return adapter;
    }

    // load the adapter class in preparation for instantiation.
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Class<?> adapterClass;
    try {
      adapterClass = cl.loadClass(
           feedConfiguration.getAdapterClassName());
    } catch (ClassNotFoundException e) {
      // The adapter was not found
      return null;
    }

    // get its constructor
    Constructor[] ctors = adapterClass.getConstructors();
    for (Constructor element : ctors) {
      logger.finest("Public constructor found: " +
           element.toString());
    }

    // instantiate the adapter, by calling its constructor with
    // config object
    Constructor<?> c;
    try {
      c = adapterClass.getConstructor(new Class[] {Abdera.class,
           FeedConfiguration.class});
    } catch (SecurityException e) {
      return null;
    } catch (NoSuchMethodException e) {
      // The adapter does not have a valid constructor
      return null;
    }
    c.setAccessible(true);
     try {
      adapter = (Adapter) c.newInstance(abdera, feedConfiguration);
    } catch (Exception e) {
      // The adapter does not have a valid constructor
      return null;
    }
     // put this adapter instance in adapterInstanceMap
     adapterInstanceMap.put(feedConfiguration.getFeedId(), adapter);
     return adapter;
   }
}
