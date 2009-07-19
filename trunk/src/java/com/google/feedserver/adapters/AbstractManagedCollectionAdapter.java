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

package com.google.feedserver.adapters;

import com.google.feedserver.config.FeedServerConfiguration;
import com.google.feedserver.config.GlobalServerConfiguration;
import com.google.feedserver.config.NamespacedAdapterConfiguration;
import com.google.feedserver.config.NamespacedFeedConfiguration;
import com.google.feedserver.config.PerNamespaceServerConfiguration;
import com.google.feedserver.config.UserInfo;
import com.google.feedserver.metadata.FeedInfo;
import com.google.feedserver.metadata.SimpleFeedInfo;
import com.google.feedserver.util.FeedServerUtil;
import com.google.feedserver.util.XmlUtil;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.provider.managed.CollectionAdapterConfiguration;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;
import org.apache.abdera.protocol.server.provider.managed.ManagedCollectionAdapter;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * This class tries to aggregate common functions that will be used in most of
 * the adapters and tries to remove the duplication that would happen. It also
 * provides utility functions for adapters.
 * 
 * Ideally all the code here should get pulled up into
 * {@link ManagedCollectionAdapter}.
 * 
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 * 
 */
public abstract class AbstractManagedCollectionAdapter extends ManagedCollectionAdapter {

  public static final String PARAM_ENTRY = "entry";
  public static final String PARAM_FEED = "feed";
  public static final String PARAM_NAMESPACE = "namespace";
  public static final String PARAM_FEED_TYPE = "feedType";
  public static final String USER_INFO = "__user_info__";
  public static final String ENCODING_UTF_8 = "UTF-8";
  public static final String CATEGORY_PARAMETER = "category";

  protected final XmlUtil xmlUtil;

  protected AbstractManagedCollectionAdapter(Abdera abdera, FeedConfiguration config) {
    super(abdera, config);
    xmlUtil = new XmlUtil();
  }

  public ResponseContext deleteEntry(RequestContext request) {
    String entryId = getEntryId(request);
    try {
      deleteEntry(request, entryId);
      return sendSuccessfulDeleteResponse();
    } catch (FeedServerAdapterException e) {
      return sendErrorResponse(request, e);
    }
  }

  public ResponseContext getEntry(RequestContext request) {
    String entryId = getEntryId(request);

    try {
      Entry entry = retrieveEntry(request, entryId);
      if (null == entry) {
        return sendNotFoundResponse(request);
      } else {
        return sendResponse(request, entry.getDocument(), 200);
      }
    } catch (FeedServerAdapterException e) {
      return sendErrorResponse(request, e);
    }
  }

  public ResponseContext getFeed(RequestContext request) {
    try {
      Feed feed = retrieveFeed(request);
      return feed != null ? sendResponse(request, feed.getDocument(), 200)
          : sendNotFoundResponse(request);
    } catch (FeedServerAdapterException e) {
      return sendErrorResponse(request, e);
    }
  }

  public ResponseContext postEntry(RequestContext request) {
    Entry inputEntry;
    try {
      inputEntry = getInputEntry(request);
    } catch (IOException e) {
      return sendErrorResponse(request, e);
    }

    try {
      Entry newEntry = createEntry(request, inputEntry);
      return sendResponseWithEditLink(request, newEntry, 201);
    } catch (FeedServerAdapterException e) {
      return sendErrorResponse(request, e);
    }
  }

  public ResponseContext putEntry(RequestContext request) {
    Entry inputEntry;
    try {
      inputEntry = getInputEntry(request);
    } catch (IOException e) {
      return sendErrorResponse(request, e);
    }

    String entryId = getEntryId(request);
    try {
      Entry newEntry = updateEntry(request, entryId, inputEntry);
      return sendResponseWithEditLink(request, newEntry, 200);
    } catch (FeedServerAdapterException e) {
      return sendErrorResponse(request, e);
    }
  }

  protected ResponseContext sendErrorResponse(RequestContext request, IOException e) {
    return ProviderHelper.servererror(request, e.getMessage(), e);
  }

  protected ResponseContext sendErrorResponse(RequestContext request, FeedServerAdapterException e) {
    return ProviderHelper.servererror(request, e.getMessage(), e);
  }

  protected ResponseContext sendSuccessfulDeleteResponse() {
    return ProviderHelper.nocontent();
  }

  private ResponseContext sendNotFoundResponse(RequestContext request) {
    return ProviderHelper.notfound(request);
  }

  private ResponseContext sendResponseWithEditLink(RequestContext request, Entry newEntry,
      int responseCode) {
    if (newEntry != null) {
      Document<Entry> newEntryDoc = newEntry.getDocument();
      String loc = newEntry.getEditLinkResolvedHref().toString();
      return sendResponse(request, newEntryDoc, responseCode).setLocation(loc);
    } else {
      return sendNotFoundResponse(request);
    }
  }

  private ResponseContext sendResponse(RequestContext request, Base document, int responseCode) {
    if (null == document) {
      return sendNotFoundResponse(request);
    } else {
      return ProviderHelper.returnBase(document, responseCode, null);
    }
  }

  protected String getEntryId(RequestContext request) {
    Target target = request.getTarget();
    String entryId = target.getParameter(PARAM_ENTRY);
    return entryId;
  }

  protected String getProperty(String key) throws Exception {
    Object val = config.getProperty(key);
    if (val == null) {
      throw new RuntimeException("Configuration property not found");
    }
    if (val instanceof String) {
      return (String) val;
    }
    throw new RuntimeException("Configuration property is not string");
  }

  protected Feed createFeed() {
    String feedTitle = config.getFeedTitle();
    String feedAuthor = config.getFeedAuthor();
    
    Feed feed = abdera.newFeed();
    feed.setId(config.getFeedUri());
    feed.setTitle(feedTitle == null ? "feed" : feedTitle);
    feed.setUpdated(new Date());
    feed.addAuthor(feedAuthor == null ? getServerName() : feedAuthor);

    return feed;
  }

  protected void addEditLinkToEntry(Entry entry) {
    if (ProviderHelper.getEditUriFromEntry(entry) == null) {
      entry.addLink(entry.getId().toString(), "edit");
    }
  }

  protected void setEntryIdIfNull(Entry entry) {
    // if there is no id in Entry, assign one.
    if (entry.getId() != null) {
      return;
    }
    String uuidUri = abdera.getFactory().newUuidUri();
    String[] segments = uuidUri.split(":");
    String entryId = segments[segments.length - 1];
    entry.setId(createEntryIdUri(entryId));
  }

  protected String createEntryIdUri(String entryId) {
    return config.getFeedUri() + "/" + entryId;
  }

  protected Entry getInputEntry(RequestContext request) throws IOException {
    Abdera abdera = request.getAbdera();
    Parser parser = abdera.getParser();
    Entry inputEntry = (Entry) request.getDocument(parser).getRoot();
    return inputEntry;
  }

  @Override
  public ResponseContext extensionRequest(RequestContext request) {
    return ProviderHelper.notallowed(request, ProviderHelper.getDefaultMethods(request));
  }

  @Override
  public ResponseContext getCategories(RequestContext request) {
    return ProviderHelper.notfound(request);
  }

  public abstract Feed retrieveFeed(RequestContext request) throws FeedServerAdapterException;

  public abstract Entry retrieveEntry(RequestContext request, Object entryId)
      throws FeedServerAdapterException;

  public abstract Entry createEntry(RequestContext request, Entry entry)
      throws FeedServerAdapterException;

  public abstract Entry updateEntry(RequestContext request, Object entryId, Entry entry)
      throws FeedServerAdapterException;

  public abstract void deleteEntry(RequestContext request, Object entryId)
      throws FeedServerAdapterException;

  public abstract FeedInfo getFeedInfo(RequestContext request) throws FeedServerAdapterException;

  @Override
  public NamespacedFeedConfiguration getConfiguration() {
    return (NamespacedFeedConfiguration) super.getConfiguration();
  }

  public NamespacedAdapterConfiguration getAdapterConfiguration() {
    return getConfiguration().getAdapterConfiguration();
  }

  public PerNamespaceServerConfiguration getServerConfiguration() {
    return getConfiguration().getServerConfiguration();
  }

  public GlobalServerConfiguration getGolbalServerConfiguration() {
    return getServerConfiguration().getGolbalServerConfiguration();
  }

  protected Entry createEntryFromProperties(Feed feed, Map<String, Object> properties)
      throws FeedServerAdapterException {
    Map<String, Object> contentData = new HashMap<String, Object>();
    Entry entry = feed != null ? feed.addEntry() : abdera.newEntry();
    for (String key : properties.keySet()) {
      if (properties.get(key) == null) {
        continue;
      }
      Object value = properties.get(key);
      if (FeedConfiguration.ENTRY_ELEM_NAME_ID.equals(key)) {
        entry.setId(createEntryIdUri(value.toString()));
      } else if (FeedConfiguration.ENTRY_ELEM_NAME_TITLE.equals(key)) {
        entry.setTitle(value.toString());
      } else if (FeedConfiguration.ENTRY_ELEM_NAME_AUTHOR.equals(key)) {
        entry.addAuthor(value.toString());
      } else if (FeedConfiguration.ENTRY_ELEM_NAME_UPDATED.equals(key)
          && value instanceof java.util.Date) {
        entry.setUpdated((Date) value);
      } else if (FeedConfiguration.ENTRY_ELEM_NAME_LINK.equals(key)) {
        entry.addLink(value.toString());
      }
      contentData.put(key, value);
    }
    if (entry.getUpdated() == null) {
      entry.setUpdated(new Date());
    }
    if (entry.getAuthor() == null) {
      entry.addAuthor(config.getFeedAuthor());
    }
    if (entry.getTitle() == null) {
      entry.setTitle("");
    }
    try {
      entry.setContent(FeedServerUtil.getConfigurationAsXML(contentData), Content.Type.XML);
    } catch (TransformerException e) {
      throw new FeedServerAdapterException(
          FeedServerAdapterException.Reason.BAD_RESPONSE_FROM_REMOTE_SERVER, e.getMessage());
    } catch (ParserConfigurationException e) {
      throw new FeedServerAdapterException(
          FeedServerAdapterException.Reason.BAD_RESPONSE_FROM_REMOTE_SERVER, e.getMessage());
    }
    addEditLinkToEntry(entry);
    return entry;
  }

  public List<Map<String, Object>> getPropertyMapForFeed(Feed feed)
      throws FeedServerAdapterException {
    List<Entry> entries = feed.getEntries();
    List<Map<String, Object>> propertiesList = new ArrayList<Map<String, Object>>(entries.size());
    for (Entry entry : entries) {
      propertiesList.add(getPropertyMapForEntry(entry));
    }
    return propertiesList;
  }

  protected Map<String, Object> getPropertyMapForEntry(Entry entry)
      throws FeedServerAdapterException {
    try {
      Map<String, Object> columns;

      Content content = entry.getContentElement();
      if (content != null) {
        String contentStr = content.getValue();
        columns = parseContent(contentStr);
      } else {
        columns = new HashMap<String, Object>();
      }

      if (entry.getId() != null) {
        columns.put(FeedConfiguration.ENTRY_ELEM_NAME_ID, getEntryId(entry));
      }
      return columns;
    } catch (SAXException e) {
      throw new FeedServerAdapterException(FeedServerAdapterException.Reason.INVALID_INPUT, e
          .getMessage());
    } catch (IOException e) {
      throw new FeedServerAdapterException(FeedServerAdapterException.Reason.INVALID_INPUT, e
          .getMessage());
    } catch (ParserConfigurationException e) {
      throw new FeedServerAdapterException(FeedServerAdapterException.Reason.INVALID_INPUT, e
          .getMessage());
    }
  }

  /**
   * Parse the content data of an Entry into a property map.
   * 
   * @param str the contentData
   * @return Property map.
   */
  protected Map<String, Object> parseContent(String str) throws SAXException, IOException,
      ParserConfigurationException {
    return xmlUtil.convertXmlToProperties(str);
  }

  /**
   * Get entry id for an entry.
   * 
   * @param entry the entry.
   * @return id of the entry
   */
  public String getEntryId(Entry entry) {
    String uri = entry.getId().toString();
    String[] uriParts = uri.split("/");
    String id = uriParts[uriParts.length - 1];
    return id;
  }

  @SuppressWarnings({"unchecked"})
  public FeedInfo getFeedInfoFromConfig(RequestContext request) throws FeedServerAdapterException {
    return getFeedInfoFromMap((Map<String, Object>) getConfiguration().getTypeMetadataConfig());
  }

  public FeedInfo getFeedInfoFromMap(Map<String, Object> typeMetadataConfig) {
    return new SimpleFeedInfo(typeMetadataConfig);
  }

  public UserInfo getUserInfoForRequest(RequestContext request) {
    return (UserInfo) request.getAttribute(RequestContext.Scope.REQUEST, USER_INFO);
  }

  public String getUserEmailForRequest(RequestContext request) {
    UserInfo userInfo = getUserInfoForRequest(request);
    return userInfo == null ? null : userInfo.getEmail();
  }

  public FeedInfo getFeedInfoFromXml(String feedInfoXml) throws FeedServerAdapterException {
    try {
      return new SimpleFeedInfo(feedInfoXml);
    } catch (SAXException e) {
      throw new FeedServerAdapterException(FeedServerAdapterException.Reason.BAD_FEED_TYPE_CONFIG,
          e.getMessage());
    } catch (IOException e) {
      throw new FeedServerAdapterException(FeedServerAdapterException.Reason.BAD_FEED_TYPE_CONFIG,
          e.getMessage());
    } catch (ParserConfigurationException e) {
      throw new FeedServerAdapterException(FeedServerAdapterException.Reason.BAD_FEED_TYPE_CONFIG,
          e.getMessage());
    }
  }

  public String getNameSpace() {
    CollectionAdapterConfiguration config = getConfiguration().getAdapterConfiguration();
    if (config instanceof NamespacedAdapterConfiguration) {
      return ((NamespacedAdapterConfiguration) config).getServerConfiguration().getNameSpace();
    }
    return null;
  }

  protected String getHostName() {
    try {
      return InetAddress.getLocalHost().getHostName().toLowerCase();
    } catch (UnknownHostException e) {
      return "localhost";
    }
  }

  protected String getServerName() {
    return "feedserver@" + getHostName() + ":" + FeedServerConfiguration.getIntance().getPort();
  }
}
