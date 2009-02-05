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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
public abstract class AbstractAdapter implements Adapter {
  public static Logger logger =
    Logger.getLogger(AbstractAdapter.class.getName());

  protected FeedConfiguration feedConfiguration;
  protected final Abdera abdera;

  protected AbstractAdapter(Abdera abdera, FeedConfiguration feedConfiguration) {
    this.abdera = abdera;
    this.feedConfiguration = feedConfiguration;
  }

  protected Feed createFeed() throws Exception {
    Feed feed = abdera.newFeed();
    feed.setId(feedConfiguration.getFeedUri());
    feed.setTitle(feedConfiguration.getFeedTitle());
    feed.setUpdated(new Date());
    feed.addAuthor(feedConfiguration.getFeedAuthor());
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
    if (editLinks != null) {
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
    return feedConfiguration.getFeedUri() + "/" + entryId;
  }

  protected Map<String, Object> collectColumns(Entry entry)
      throws Exception {
    Map<String, Object> columns = new HashMap<String, Object>();

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

    Content content = entry.getContentElement();
    if (content != null) {
      String contentStr = content.getValue();
      parseContent(contentStr, columns);
    }

    return columns;
  }

  static void parseContent(String str, Map<String, Object> columns)
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

}
