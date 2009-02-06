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

package com.google.feedserver.testing;

import com.google.feedserver.adapters.AbstractManagedCollectionAdapter;
import com.google.feedserver.adapters.FeedServerAdapterException;
import com.google.feedserver.metadata.FeedInfo;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Sample Adapter for testing. This code is also copied in google feedserver and
 * abdera code base.
 * 
 */
public class SampleBasicAdapter extends AbstractManagedCollectionAdapter {
  private static final String ERROR_DUP_ENTRY = "Entry Already Exists";
  private static final String ERROR_INVALID_ENTRY = "No Such Entry in the Feed";

  protected Map<String, byte[]> entries = new HashMap<String, byte[]>();

  public SampleBasicAdapter(Abdera abdera, FeedConfiguration config) {
    super(abdera, config);
  }

  @Override
  public Feed retrieveFeed(RequestContext request) {
    Feed feed = createFeed();
    Set<String> keys = entries.keySet();
    for (String s : keys) {
      Entry entry = retrieveEntry(request, s);
      feed.addEntry((Entry) entry.clone());
    }
    return feed;
  }

  @Override
  public Entry retrieveEntry(RequestContext request, Object entryId) {
    return retrieveEntry(getEntryIdFromUri((String) entryId));
  }

  @Override
  public Entry createEntry(RequestContext request, Entry entry) throws FeedServerAdapterException {
    // entryId may be null. if it is, assign one
    setEntryIdIfNull(entry);
    String entryId = getEntryIdFromUri(entry.getId().toString());

    if (entries.containsKey(entryId)) {
      throw new FeedServerAdapterException(FeedServerAdapterException.Reason.ENTRY_ALREADY_EXISTS,
          ERROR_DUP_ENTRY);
    }
    // add an "updated" element if one was not provided
    if (entry.getUpdated() == null) {
      entry.setUpdated(new Date());
    }
    addEditLinkToEntry(entry);
    storeEntry(entryId, entry);
    return entry;
  }

  @Override
  public Entry updateEntry(RequestContext request, Object entryId, Entry entry)
      throws FeedServerAdapterException {
    String id = getEntryIdFromUri((String) entryId);
    if (!entries.containsKey(id)) {
      throw new FeedServerAdapterException(FeedServerAdapterException.Reason.ENTRY_DOES_NOT_EXIST,
          ERROR_INVALID_ENTRY);
    }
    entries.remove(entryId);
    // add an "updated" element if one was not provided
    if (entry.getUpdated() == null) {
      entry.setUpdated(new Date());
    }
    addEditLinkToEntry(entry);
    storeEntry(id, entry);
    return entry;
  }

  @Override
  public void deleteEntry(RequestContext request, Object entryId) {
    String id = getEntryIdFromUri((String) entryId);
    if (!entries.containsKey(id)) {
      return;
    }
    entries.remove(id);
  }

  protected String getEntryIdFromUri(String uri) {
    String[] segments = uri.split("/");
    return segments[segments.length - 1];
  }

  protected void storeEntry(String entryId, Entry entry) throws FeedServerAdapterException {
    Document<Element> entryDoc = entry.getDocument();
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try {
      entryDoc.writeTo(bos);
    } catch (IOException e) {
      throw new FeedServerAdapterException(FeedServerAdapterException.Reason.REMOTE_SERVER_ERROR,
          "error");
    }

    // Get the bytes of the serialized object and store in hashmap
    byte[] buf = bos.toByteArray();
    try {
      bos.close();
    } catch (IOException e) {
      throw new FeedServerAdapterException(FeedServerAdapterException.Reason.REMOTE_SERVER_ERROR,
          "error");
    }
    entries.put(entryId, buf);
  }

  protected Entry retrieveEntry(String entryId) {
    // Deserialize from a byte array
    byte[] bytes = entries.get(entryId);
    if (bytes == null) {
      // entry not found
      return null;
    }
    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    Document<Entry> entryDoc = abdera.getParser().parse(in);
    Entry entry = entryDoc.getRoot();
    return entry;
  }

  @Override
  public FeedInfo getFeedInfo(RequestContext request) throws FeedServerAdapterException {
    return null;
  }
}
