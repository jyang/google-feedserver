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

package com.google.feedserver;

import com.google.feedserver.adapter.AbstractAdapter;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.mortbay.jetty.Server;
import java.util.Date;
import java.util.logging.Logger;
import junit.framework.TestCase;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

public class JdbcAdapterFunctionalTest  extends TestCase {

  protected Server server;
  protected static Logger logger =
    Logger.getLogger(JdbcAdapterFunctionalTest.class.getName());

  protected static final int JETTY_PORT = 9090;
  protected static int port = JETTY_PORT;

  protected static final String BASE_URL = "http://localhost:" + JETTY_PORT;
  protected static final String TEST_TABLE = "perfinfo";
  protected static final String FEED_URL = BASE_URL + "/" + TEST_TABLE;

  protected int status = 0;

  protected Feed getFeed() throws Exception {
    logger.info("in get feed");
    Abdera abdera = new Abdera();
    AbderaClient abderaClient = new AbderaClient(abdera);
    ClientResponse response = abderaClient.get(FEED_URL);
    status = response.getStatus();
    logger.info("response code = " + response.getStatus() +
        ", status text = " + response.getStatusText());
    if (response.getStatusText().equals("OK")) {
      Document<Feed> feedDoc = response.getDocument();
      Feed feed = feedDoc.getRoot();
      logger.info("RESPONSE: in getFeed: received this Feed =  "  +
          feed.toString());
      return feed;
    } else {
      return null;
    }
  }

  protected  Entry getEntry(String id, boolean inUriFormat) throws Exception {
   logger.info("in get entry");
   Abdera abdera = new Abdera();
   AbderaClient abderaClient = new AbderaClient(abdera);
   String entryId = inUriFormat ? id : FEED_URL + "/" + id;
   ClientResponse response = abderaClient.get(entryId);
   status = response.getStatus();
   logger.info("response code = " + response.getStatus() +
       ", status text = " + response.getStatusText());
   if (response.getStatusText().equals("OK")) {
     Document<Entry> doc = response.getDocument();
     Entry entry = doc.getRoot();
     logger.info("RESPONSE: in getEntry: received this Entry =  "  +
         entry.toString());
     return entry;
   } else {
    return null;
  }
  }

  protected  void deleteEntry(String id) throws Exception {
   logger.info("in delete entry");
   Abdera abdera = new Abdera();
   AbderaClient abderaClient = new AbderaClient(abdera);
   ClientResponse response = abderaClient.delete(FEED_URL + "/" + id);
   status = response.getStatus();
   logger.info("response code = " + response.getStatus() +
       ", status text = " + response.getStatusText());
  }

  protected Entry postEntry(String title, String author,
      String content) throws Exception {
    Abdera abdera = new Abdera();
    AbderaClient abderaClient = new AbderaClient(abdera);
    Factory factory = abdera.getFactory();
    Entry entry = factory.newEntry();
    entry.setBaseUri(FEED_URL);
    entry.setTitle(title);
    entry.setUpdated(new Date());
    entry.addAuthor(author);
    entry.setContent(content);
    ClientResponse response = abderaClient.post(FEED_URL, entry);
    status = response.getStatus();
    logger.info("response code = " + response.getStatus() + ", " +
        "status text = " + response.getStatusText());

    if (response.getStatusText().equals("OK")) {
      Document<Entry> doc = response.getDocument();
      Entry newEntry = doc.getRoot();
      logger.info("RESPONSE: in postEntry: received this Entry =  "  +
          newEntry.toString());
      return newEntry;
    } else {
      return null;
    }
  }


  protected Entry putEntry(String url, Entry entry) throws Exception {
    Abdera abdera = new Abdera();
    AbderaClient abderaClient = new AbderaClient(abdera);
    entry.setUpdated(new Date());
    logger.info("Edited Entry: " + entry.toString());
    logger.info("Put entry to this feed: "  + url);

    ClientResponse response = abderaClient.put(url, entry);
    status = response.getStatus();
    logger.info("response code = " + response.getStatus() +
        ", status text = " + response.getStatusText());

    if (response.getStatusText().equals("OK")) {
      Document<Entry> doc = response.getDocument();
      Entry newEntry = doc.getRoot();
      logger.info("RESPONSE: in putEntry: received this Entry =  "  +
          newEntry.toString());
      return newEntry;
    } else {
      return null;
    }
  }


  @Test
  public  void testAdapter() {
    try {
      // get the feed
      logger.info("************** REQUEST: get feed");
      Feed feed0 = getFeed();
      Assert.assertEquals(200, status);
      // count # of entries in the feed - should be 0
      Assert.assertEquals(feed0.getEntries().size(), 9);

      /*
       * create an entry in this feed
       */
      logger.info("************** Post entry");
      String content =
          "<content xmlns=''>" +
          "<entity>" +
          "<JobTitle> new job title</JobTitle>"+
          "<JobGrade> new job grade</JobGrade>"+
          "<JobCode> new job JobCode</JobCode>"+
          "<EmployeeId>34</EmployeeId>"+
          "</entity>" +
          "</content>";

      Entry postedEntry = postEntry("new title", "author for entry", content);
      Assert.assertEquals(200, status);
      String entryUri = postedEntry.getId().toString();
      String editUri = AbstractAdapter.getEditUriFromEntry(postedEntry);
      Assert.assertNotNull(editUri);
      Assert.assertEquals(entryUri, editUri);

      /*
       * get entry
       */
      Entry newEntry = getEntry(editUri, true);
      Assert.assertEquals(200, status);
      String newEntryUri = newEntry.getId().toString();
      Assert.assertNotNull(newEntryUri);
      Assert.assertEquals(newEntryUri, editUri);


      /*
       * delete entry
       */
      logger.info("************** REQUEST: delete entry: " + entryUri);
      String[] segments = entryUri.split("/");
      deleteEntry(segments[segments.length - 1]);
      Assert.assertEquals(200, status);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


  @Override
  @Before
  public void setUp() throws Exception {
    // is the database connection available? if not, don't run this test at all

    // set up server
    String[] args = new String[] {"--port=" + JETTY_PORT,
        "--uri=http://localhost:" + JETTY_PORT};
    com.google.feedserver.server.jetty.Main.runJetty(args);
  }

  @Override
  @After
  public void tearDown() throws Exception {
    if (server != null) {
      logger.info("stopping server");
      server.stop();
    }
  }
}
