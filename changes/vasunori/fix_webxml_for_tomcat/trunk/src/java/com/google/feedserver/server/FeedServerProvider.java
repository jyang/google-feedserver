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

package com.google.feedserver.server;

import com.google.feedserver.adapter.Adapter;
import com.google.feedserver.adapter.AdapterManager;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.server.impl.AbstractProvider;
import org.apache.abdera.protocol.server.impl.EmptyResponseContext;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.util.MimeTypeHelper;

import java.util.logging.Logger;

import javax.activation.MimeType;

public class FeedServerProvider extends AbstractProvider {
  public static final String PARAM_ENTRY = "entry";
  public static final String PARAM_FEED = "feed";

  private static final boolean CREATE_FLAG = true;
  private static final boolean UPDATE_FLAG = false;

  public static Logger logger = Logger.getLogger(
      FeedServerProvider.class.getName());

  public static AdapterManager adapterManager;

  public ResponseContext createEntry(RequestContext request) {
    return createOrUpdateEntry(request, CREATE_FLAG);
  }

  public ResponseContext createOrUpdateEntry(RequestContext request,
      boolean createFlag) {
    try {
      MimeType mimeType = request.getContentType();
      String contentType = mimeType == null ? null : mimeType.toString();
      if (contentType != null && !MimeTypeHelper.isAtom(contentType) &&
          !MimeTypeHelper.isXml(contentType)) {
        // unsupported media type
        return new EmptyResponseContext(415);
      }

      Abdera abdera = request.getServiceContext().getAbdera();
      Parser parser = abdera.getParser();
      Entry inputEntry = (Entry) request.getDocument(parser).getRoot();

      Target target = request.getTarget();
      String feedId = target.getParameter(PARAM_FEED);
      String entryId = createFlag == UPDATE_FLAG
          ? target.getParameter(PARAM_ENTRY) : null;

      Adapter adapter = getAdapterManagerInstance(request).getAdapter(feedId);
      Entry newEntry = createFlag == CREATE_FLAG
          ? adapter.createEntry(inputEntry)
          : adapter.updateEntry(entryId, inputEntry);
      if (newEntry != null) {
        Document<Entry> newEntryDoc = newEntry.getDocument();
        ResponseContext response =
          new FeedServerResponseContext<Document<Entry>>(newEntryDoc);
        return response;
      } else {
        return new EmptyResponseContext(404);
      }
    } catch (Exception e) {
      EmptyResponseContext response = new EmptyResponseContext(500);
      response.setStatusText(e.getMessage());
      return response;
    }
  }

  private AdapterManager getAdapterManagerInstance(RequestContext request) {
    if (adapterManager == null) {
      adapterManager =
        new AdapterManager(request.getServiceContext().getAbdera());
    }
    return adapterManager;
  }

  public ResponseContext deleteEntry(RequestContext request) {
    Target target = request.getTarget();
    String feedId = target.getParameter(PARAM_FEED);
    String entryId = target.getParameter(PARAM_ENTRY);

    try {
      Adapter adapter = getAdapterManagerInstance(request).getAdapter(feedId);
      if (adapter.deleteEntry(entryId)) {
        return new EmptyResponseContext(200);
      } else { // entry not found
        return new EmptyResponseContext(404);
      }
    } catch (Exception e) {
      EmptyResponseContext response = new EmptyResponseContext(500);
      response.setStatusText(e.getMessage());
      return response;
    }
  }

  @Override
  public ResponseContext deleteMedia(RequestContext request) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResponseContext entryPost(RequestContext request) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResponseContext getCategories(RequestContext request) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResponseContext getMedia(RequestContext request) {
    // TODO Auto-generated method stub
    return null;
  }

  public ResponseContext getService(RequestContext request) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResponseContext mediaPost(RequestContext request) {
    // TODO Auto-generated method stub
    return null;
  }

  public ResponseContext updateEntry(RequestContext request) {
    return createOrUpdateEntry(request, UPDATE_FLAG);
  }

  @Override
  public ResponseContext updateMedia(RequestContext request) {
    // TODO Auto-generated method stub
    return null;
  }

  public ResponseContext getEntry(RequestContext request) {
    Target target = request.getTarget();
    String feedId = target.getParameter(PARAM_FEED);
    String entryId = target.getParameter(PARAM_ENTRY);

    try {
      Adapter adapter = getAdapterManagerInstance(request).getAdapter(feedId);
      Entry entry = adapter.getEntry(entryId);
      if (entry != null) {
        Document<Entry> entryDoc = entry.getDocument();
        return new FeedServerResponseContext<Document<Entry>>(entryDoc);
      } else {
        return new EmptyResponseContext(404);
      }
    } catch (Exception e) {
      EmptyResponseContext response = new EmptyResponseContext(500);
      response.setStatusText(e.getMessage());
      return response;
    }
  }

  public ResponseContext getFeed(RequestContext request) {
    Target target = request.getTarget();
    String feedId = target.getParameter(PARAM_FEED);
    try {
      Adapter adapter = getAdapterManagerInstance(request).getAdapter(feedId);
      Feed feed = adapter.getFeed();
      if (feed != null) {
        Document<Feed> feedDoc = feed.getDocument();
        return new FeedServerResponseContext<Document<Feed>>(feedDoc);
      } else {
        return new EmptyResponseContext(404);
      }
    } catch (Exception e) {
      EmptyResponseContext response = new EmptyResponseContext(500);
      response.setStatusText(e.getMessage());
      return response;
    }
  }
}
