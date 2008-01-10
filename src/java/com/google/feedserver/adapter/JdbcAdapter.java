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

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class JdbcAdapter extends AbstractAdapter implements Adapter {
  public static final String CONFIG_FILE_NAME = "sqlMapConfigFile";
  public static final String ADAPTER_TYPE = "jdbc";
  private static final String ENTRY_AUTHOR = "feedserver";
  private static final String ENTRY_TITLE = "jdbc entry title";


  // this class needs to be public - so that Adapter Manager can invoke it
  // to create an instance of this adapter
  public JdbcAdapter(Abdera abdera, Properties feedProperties,
      String feedId) {
    super(abdera, feedProperties, feedId);
  }

  protected Map<String, SqlMapClient> sqlMapClients =
      new HashMap<String, SqlMapClient>();

  protected SqlMapClient getSqlMapClient() throws Exception {
    String dataSourceId = getProperty(CONFIG_FILE_NAME);
    SqlMapClient client = sqlMapClients.get(dataSourceId);
    if (client == null) {
      client = SqlMapClientBuilder.buildSqlMapClient(
          Resources.getResourceAsReader("feedserver/" + dataSourceId + ".xml"));
      sqlMapClients.put(dataSourceId, client);
    }
    return client;
  }

  public Feed getFeed() throws Exception {
    SqlMapClient client = getSqlMapClient();
    String queryId = feedId + "-get-feed";
    List<Map<String, Object>> rows = client.queryForList(queryId);
    Feed feed = createFeed();
    feed.declareNS(GBASE_NS_PREFIX, GBASE_NS);
    for (Map<String, Object> row : rows) {
      Entry entry = createEntryFromRow(row);
      feed.addEntry(entry);
    }
    return feed;
  }

  public Entry getEntry(Object entryId) throws Exception {
    String queryId = feedId + "-get-entry";
    SqlMapClient client = getSqlMapClient();
    Map<String, Object> row = (Map<String, Object>)
        client.queryForObject(queryId, entryId);
    if (row == null) {
      // didn't find the entry.
      return null;
    }
    return createEntryFromRow(row);
  }

  public Entry createEntry(Entry entry) throws Exception {
    SqlMapClient client = getSqlMapClient();
    String queryId = feedId + "-insert-entry";
    Object newEntryId = client.insert(queryId, collectColumns(entry));

    return getEntry(newEntryId);
  }

  public Entry updateEntry(Object entryId, Entry entry) throws Exception {
    SqlMapClient client = getSqlMapClient();
    String queryId = feedId + "-update-entry";
    return client.update(queryId, collectColumns(entry)) > 0
        ? getEntry(entryId) : null;
  }

  public boolean deleteEntry(Object entryId) throws Exception {
    String queryId = feedId + "-delete-entry";
    SqlMapClient client = getSqlMapClient();
    return client.delete(queryId, entryId) > 0;
  }

  protected Entry createEntryFromRow(Map<String, Object> row)
      throws Exception {
    Entry entry = abdera.newEntry();
    for (String columnName : row.keySet()) {
      if (row.get(columnName) == null) {
        continue;
      }
      Object value = row.get(columnName);
      if ("id".equals(columnName)) {
        entry.setId(createEntryIdUri(value.toString()));
      } else if ("title".equals(columnName)) {
        entry.setTitle(value.toString());
      } else if ("author".equals(columnName)) {
        entry.addAuthor(value.toString());
      } else if ("updated".equals(columnName) &&
          value instanceof java.util.Date) {
        entry.setUpdated((Date) value);
      } else if ("link".equals(columnName)) {
        entry.addLink(value.toString());
      } else if ("content".equals(columnName)) {
        entry.setContentAsHtml(value.toString());
      } else {
        Element ext = entry.addExtension(
            GBASE_NS, columnName, GBASE_NS_PREFIX);
        ext.setText(value.toString());
      }
    }
    if (entry.getUpdated() == null) {
      entry.setUpdated(new Date());
    }
    if (entry.getAuthor() == null) {
      entry.addAuthor(ENTRY_AUTHOR);
    }
    if (entry.getTitle() == null) {
      entry.setTitle(ENTRY_TITLE);
    }
    return entry;
  }
}
