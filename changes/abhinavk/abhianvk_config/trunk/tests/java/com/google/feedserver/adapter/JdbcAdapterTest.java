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

import com.ibatis.sqlmap.client.SqlMapClient;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import junit.framework.TestCase;

@RunWith(JMock.class)
public class JdbcAdapterTest extends TestCase {
  protected static Logger logger =
    Logger.getLogger(JdbcAdapterTest.class.getName());

  JUnit4Mockery context = new JUnit4Mockery();
  private final JUnit4Mockery forObjMock = new JUnit4Mockery() {{
    setImposteriser(ClassImposteriser.INSTANCE);
  }};

  // mock objects
  Abdera abdera = forObjMock.mock(Abdera.class);
  Feed feed = context.mock(Feed.class);
  Entry entry = context.mock(Entry.class);

  SqlMapClient client = context.mock(SqlMapClient.class);

  // data
  FeedConfiguration feedConfig;
  JdbcAdapter jdbcAdapter;
  Map<String, Object> row1;
  List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

  @Override
  @Before
  public void setUp() throws Exception {
    feedConfig = new FeedConfiguration("contact", "", "jdbcAdapter", "sqlmap");
    feedConfig.setFeedAuthor("author");
    feedConfig.setFeedTitle("title");
    
    jdbcAdapter = new JdbcAdapter(abdera, feedConfig);
    jdbcAdapter.sqlMapClients.put("sqlmap", client);

    rows.add(createOneRow(0));
    rows.add(createOneRow(1));
    row1 = createOneRow(1);

    forObjMock.checking(new Expectations() {{
      allowing (abdera).newFeed(); will(returnValue(feed));
      allowing (abdera).newEntry(); will(returnValue(entry));
    }});

    context.checking(new Expectations() {{
      allowing (feed).setId(with(any(String.class)));
      allowing (feed).setTitle(with(any(String.class)));
      allowing (feed).setUpdated(with(any(Date.class)));
      allowing (feed).addAuthor(with(any(String.class)));
      allowing (feed).addEntry(with(any(Entry.class)));
      allowing (feed).declareNS(with(any(String.class)),with(any(String.class)));
      allowing (entry).addExtension(with(any(String.class)),
          with(any(String.class)), with(any(String.class)));
      allowing (entry).setContentAsHtml(with(any(String.class)));
      allowing (entry).setId(with(any(String.class)));
      allowing (entry).setTitle(with(any(String.class)));
      allowing (entry).setUpdated(with(any(Date.class)));
      allowing (entry).addAuthor(with(any(String.class)));
      allowing (entry).addLink(with(any(String.class)));
      allowing (entry).setText(with(any(String.class)));
      allowing (entry).getTitle(); will(returnValue(null));
      allowing (entry).getUpdated(); will(returnValue(null));
      allowing (entry).getAuthor(); will(returnValue(null));
      allowing (entry).getId(); will(returnValue(null));
      allowing (entry).getExtensions(with(any(String.class)));
          will(returnValue(null));

    // for getFeed
      allowing (client).queryForList("contact-get-feed");
          will(returnValue(rows));

    // for getEntry
      allowing (client).queryForObject("contact-get-entry", "1");
          will(returnValue(row1));

    // for deleteentry
      allowing (client).delete("contact-delete-entry", "1");
          will(returnValue(1));

    // for createEntry
      allowing (client).insert(with(any(String.class)), with(any(Entry.class)));
          will(returnValue("1"));

    // for updateEntry
      allowing (client).update(with(any(String.class)), with(any(Entry.class)));
          will(returnValue(1));
    }});
  }

  @Test
  public void testGetFeed() throws Exception {
    try {
      Feed feed1 = jdbcAdapter.getFeed();
      assertNotNull(feed1);
    } catch (Exception e) {
      fail("Exception: " + e.getMessage());
    }
  }

  @Test
  public void testGetEntry() throws Exception {
    try {
      Entry entry1 = jdbcAdapter.getEntry("1");
      assertNotNull(entry1);
    } catch (Exception e) {
      fail("Exception: " + e.getMessage());
    }
  }

  @Test
  public void testDeleteEntry() throws Exception {
    try {
      boolean status = jdbcAdapter.deleteEntry("1");
      assertTrue(status);
    } catch (Exception e) {
      fail("Exception: " + e.getMessage());
    }
  }

  @Test
  public void testCreateEntry() throws Exception {
    try {
      Entry entry1 = jdbcAdapter.createEntry(entry);
      assertNotNull(entry1);
    } catch (Exception e) {
      fail("Exception: " + e.getMessage());
    }
  }

  @Test
  public void testUpdateEntry() throws Exception {
    try {
      Entry entry1 = jdbcAdapter.updateEntry("1", entry);
      assertNotNull(entry1);
    } catch (Exception e) {
      fail("Exception: " + e.getMessage());
    }
  }

  static Map<String, Object> createOneRow(int indx) {
    Map<String, Object> row = new HashMap<String, Object>();
    row.put("id", "id_" + indx);
    row.put("content", "content_" + indx);
    row.put("firstName", "firstname_foo_" + indx);
    row.put("lastName", "lastname_bar_" + indx);
    row.put("address", "address_" + indx);
    return row;
  }
}
