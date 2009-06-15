/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. The ASF licenses this file to You under the
 * Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License. For additional information regarding copyright in this work,
 * please see the NOTICE file in the top level directory of this distribution.
 */
package com.google.feedserver.samples.adapters;

import com.google.feedserver.adapters.AbstractManagedCollectionAdapter;
import com.google.feedserver.adapters.FeedServerAdapterException;
import com.google.feedserver.metadata.FeedInfo;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;
import org.apache.abdera.protocol.server.provider.managed.ServerConfiguration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter that uses IBatis and a database to store Atompub collection entries.
 * As an extension to BasicAdapter, the adapter is intended to be used with
 * BasicProvider and is configured using /abdera/adapter/*.properties files.
 */
public class IBatisCollectionAdapter extends AbstractManagedCollectionAdapter {
  // this class needs to be public - so that Adapter Manager can invoke it
  // to create an instance of this adapter
  public IBatisCollectionAdapter(Abdera abdera, FeedConfiguration config) {
    super(abdera, config);
  }

  protected Map<String, SqlMapClient> sqlMapClients = new HashMap<String, SqlMapClient>();

  protected SqlMapClient getSqlMapClient() throws FeedServerAdapterException {
    String dataSourceId = config.getFeedConfigLocation();
    if (sqlMapClients.containsKey(dataSourceId)) {
      return sqlMapClients.get(dataSourceId);
    } else {
      SqlMapClient client;
      try {
        client =
            SqlMapClientBuilder.buildSqlMapClient(config.getAdapterConfiguration()
                .getAdapterConfigAsReader());
      } catch (IOException e) {
        throw new FeedServerAdapterException(
            FeedServerAdapterException.Reason.ADAPTER_CONFIGURATION_NOT_CORRECT, dataSourceId);
      }
      sqlMapClients.put(dataSourceId, client);
      return client;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public Feed retrieveFeed(RequestContext request) throws FeedServerAdapterException {
    SqlMapClient client = getSqlMapClient();
    String queryId = config.getFeedId() + "-get-feed";
    List<Map<String, Object>> rows;
    try {
      rows = client.queryForList(queryId, getRequestParams(request));
    } catch (SQLException e) {
      throw new FeedServerAdapterException(
          FeedServerAdapterException.Reason.ERROR_EXECUTING_ADAPTER_REQUEST, e.getMessage());
    }
    Feed feed = createFeed();
    ServerConfiguration serverConfig = config.getServerConfiguration();
    if (serverConfig.getFeedNamespacePrefix() != null
        && serverConfig.getFeedNamespacePrefix().length() > 0) {
      feed.declareNS(serverConfig.getFeedNamespace(), serverConfig.getFeedNamespacePrefix());
    }
    for (Map<String, Object> row : rows)
      createEntryFromProperties(feed, row);
    return feed;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Entry retrieveEntry(RequestContext request, Object entryId)
      throws FeedServerAdapterException {
    String queryId = config.getFeedId() + "-get-entry";
    SqlMapClient client = getSqlMapClient();
    Map<String, Object> row;
    try {
      row = (Map<String, Object>) client.queryForObject(queryId, entryId,  getRequestParams(request));
    } catch (SQLException e) {
      throw new FeedServerAdapterException(
          FeedServerAdapterException.Reason.ERROR_EXECUTING_ADAPTER_REQUEST, e.getMessage());
    }
    if (row == null) {
      // didn't find the entry.
      return null;
    }
    return createEntryFromProperties(null, row);
  }

  @Override
  public Entry createEntry(RequestContext request, Entry entry) throws FeedServerAdapterException {
    SqlMapClient client = getSqlMapClient();
    String queryId = config.getFeedId() + "-insert-entry";
    Object newEntryId;
    try {
      Map<String, Object> params = getRequestParams(request);
      params.putAll(getPropertyMapForEntry(entry));
      newEntryId = client.insert(queryId, params);
    } catch (SQLException e) {
      throw new FeedServerAdapterException(
          FeedServerAdapterException.Reason.ERROR_EXECUTING_ADAPTER_REQUEST, e.getMessage());
    }
    return retrieveEntry(request, newEntryId);
  }

  @Override
  public Entry updateEntry(RequestContext request, Object entryId, Entry entry)
      throws FeedServerAdapterException {
    SqlMapClient client = getSqlMapClient();
    String queryId = config.getFeedId() + "-update-entry";
    try {
        Map<String, Object> params = getRequestParams(request);
        params.putAll(getPropertyMapForEntry(entry));
      return client.update(queryId, params) > 0 ? retrieveEntry(request,
          entryId) : null;
    } catch (SQLException e) {
      throw new FeedServerAdapterException(
          FeedServerAdapterException.Reason.ERROR_EXECUTING_ADAPTER_REQUEST, e.getMessage());
    }
  }

  @Override
  public void deleteEntry(RequestContext request, Object entryId) throws FeedServerAdapterException {
    String queryId = config.getFeedId() + "-delete-entry";
    SqlMapClient client = getSqlMapClient();
    try {
      Map<String, Object> params = getRequestParams(request);
      params.put("id", entryId);
      if (!(client.delete(queryId, params) > 0)) {
        throw new FeedServerAdapterException(
            FeedServerAdapterException.Reason.ERROR_EXECUTING_ADAPTER_REQUEST, "could not delete");
      }
    } catch (SQLException e) {
      throw new FeedServerAdapterException(
          FeedServerAdapterException.Reason.ERROR_EXECUTING_ADAPTER_REQUEST, e.getMessage());
    }
  }

  @Override
  public FeedInfo getFeedInfo(RequestContext request) throws FeedServerAdapterException {
    return getFeedInfoFromConfig(request);
  }

  /**
   * Gets request parameters from request context
   * @param request Request context
   * @return All parameters on request context
   */
  protected Map<String, Object> getRequestParams(RequestContext request) {
    HashMap<String, Object> params = new HashMap<String, Object>();
    Target target = request.getTarget();
    for (String name: target.getParameterNames()) {
      params.put(name, target.getParameter(name));
    }
    return params;
  }
}
