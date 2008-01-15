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

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public abstract class AbstractAdapter implements Adapter {
  public static Logger logger =
    Logger.getLogger(AbstractAdapter.class.getName());

  protected Properties feedProperties;
  protected final String feedId;
  protected final Abdera abdera;

  public static final String GBASE_NS = "http://base.google.com/ns/1.0";
  public static final String GBASE_NS_PREFIX = "g";

  protected static final String PROP_NAME_FEED_URI = "feedUri";
  protected static final String PROP_NAME_TITLE = "title";
  protected static final String PROP_NAME_AUTHOR = "author";

  public static final String ENTRY_ELEM_NAME_ID = "id";
  public static final String ENTRY_ELEM_NAME_TITLE = "title";
  public static final String ENTRY_ELEM_NAME_AUTHOR = "author";
  public static final String ENTRY_ELEM_NAME_UPDATED = "updated";

  protected AbstractAdapter(Abdera abdera, Properties feedProperties,
      String feedId) {
    this.abdera = abdera;
    this.feedProperties = feedProperties;
    this.feedId = feedId;
  }

  public String getProperty(String key) throws Exception {
    String val = feedProperties.getProperty(key);
    if (val == null) {
      logger.warning("Cannot find property " + key +
          "in Adapter properties file for feed " + feedId);
      throw new RuntimeException();
    }
    return val;
  }

  protected Feed createFeed() throws Exception {
    Feed feed = abdera.newFeed();
    feed.setId(getProperty(PROP_NAME_FEED_URI));
    feed.setTitle(getProperty(PROP_NAME_TITLE));
    feed.setUpdated(new Date());
    feed.addAuthor(getProperty(PROP_NAME_AUTHOR));
    return feed;
  }

  protected void addEditLinkToEntry(Entry entry) throws Exception {
    if (getEditUriFromEntry(entry) == null) {
      entry.addLink(entry.getId().toString(), "edit");
    }
  }

  //TODO: this should be moved to a Util class
  public static String getEditUriFromEntry(Entry entry) throws Exception {
    String editUri = null;
    List<Link> editLinks = entry.getLinks("edit");
    for (Link link : editLinks) {
      // if there is more than one edit link, we should not automatically
      // assume that it's always going to point to an Atom document
      // representation.
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
   return editUri;
  }

  protected void setEntryIdIfNull(Entry entry) throws Exception{
    // if there is no id in Entry, assign one.
    if (entry.getId() != null) {
      return;
    }
    String uuidUri = abdera.getFactory().newUuidUri();
    String[] segments = uuidUri.split(":");
    String entryId = segments[segments.length - 1];
    entry.setId(createEntryIdUri(entryId));
  }

  protected String createEntryIdUri(String entryId) throws Exception{
    return getProperty(PROP_NAME_FEED_URI) + "/" + entryId;
  }

  protected Map<String, Object> collectColumns(Entry entry) {
    Map<String, Object> columns = new HashMap<String, Object>();

    if (entry.getId() != null) {
      columns.put(ENTRY_ELEM_NAME_ID, entry.getId().toString());
    }
    if (entry.getAuthor() != null) {
      columns.put(ENTRY_ELEM_NAME_AUTHOR, entry.getAuthor().getText());
    }
    if (entry.getTitle() != null) {
      columns.put(ENTRY_ELEM_NAME_TITLE, entry.getTitle());
    }
    if (entry.getUpdated() != null) {
      columns.put(ENTRY_ELEM_NAME_UPDATED, entry.getUpdated());
    }

    // get all our namespace extension elements
    List<Element> extElements = entry.getExtensions(GBASE_NS);
    if (extElements != null) {
      for (Element element : extElements) {
        String name = element.getQName().getLocalPart();
        String value = element.getText();
        columns.put(name, value);
      }
    }
    return columns;
  }
}
