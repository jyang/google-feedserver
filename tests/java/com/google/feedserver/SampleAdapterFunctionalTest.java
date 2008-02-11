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
import java.util.List;
import java.util.logging.Logger;
import junit.framework.TestCase;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

public class SampleAdapterFunctionalTest  extends TestCase {

  protected Server server;
  protected static Logger logger =
    Logger.getLogger(SampleAdapterFunctionalTest.class.getName());

  protected static final int JETTY_PORT = 9090;
  protected static int port = JETTY_PORT;

  protected static final String BASE_URL = "http://localhost:" + JETTY_PORT;
  protected static final String FEED_URL = BASE_URL + "/sample";

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

  protected Entry postEntry(String id, String title, String author,
      String content) throws Exception {
    Abdera abdera = new Abdera();
    AbderaClient abderaClient = new AbderaClient(abdera);
    Factory factory = abdera.getFactory();
    Entry entry = factory.newEntry();
    entry.setBaseUri(FEED_URL);
    if (id != null) {
      entry.setId(FEED_URL + "/" + id);
    }
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
      Assert.assertEquals(feed0.getEntries().size(), 0);

      /*
       * create an entry in this feed
       */
      logger.info("************** REQUEST: Post entry");
      String title = "title for entry";
      String id = "4";
      Entry postedEntry = postEntry(id, title,
          "author for entry", "content for this entry");
      Assert.assertEquals(200, status);
      String entryUri = postedEntry.getId().toString();
      String editUri = AbstractAdapter.getEditUriFromEntry(postedEntry);
      Assert.assertEquals(entryUri, editUri);
      String[] segments = entryUri.split("/");
      Assert.assertEquals(segments[segments.length - 1], id);

      // get feed to make sure posted entry is in the feed
      logger.info("************** REQUEST: get feed AFTER posting entry");
      Feed feed = getFeed();
      Assert.assertEquals(200, status);
      // count # of entries in the feed - should be 1
      Assert.assertEquals(feed.getEntries().size(), 1);
      List<Entry> listOfEntries = feed.getEntries();
      Entry entryInFeed = listOfEntries.get(0);
      Assert.assertEquals(entryInFeed.getId().toString(), entryUri);
      Assert.assertEquals(entryInFeed.getId().toString(), editUri);

      /*
       * post the same entry again and see if error is returned by the server
       */
      logger.info("***** REQUEST: Post entry which already exists in the feed");
      postEntry("4", "dup title for entry",
          "dup author for entry", "content for this entry");
      Assert.assertEquals(500, status);
      // get feed to make sure there is only one entry in the feed
      logger.info("************** REQUEST: get feed AFTER posting entry");
      Feed feed1 = getFeed();
      Assert.assertEquals(200, status);
      // count # of entries in the feed - should be 1
      Assert.assertEquals(feed1.getEntries().size(), 1);

      /*
       * create an entry with no id - let server assign id
       */
      logger.info("************** REQUEST: Post entry with null id");
      Entry nullIdEntry = postEntry(null, "title for NULL entry",
          "author for NULL entry", "NULL content for this entry");
      Assert.assertEquals(200, status);
      logger.info("After Posting null id entry, entryUri = " +
          nullIdEntry.getId().toString());
      logger.info("After Posting null id entry, editUri = " +
          AbstractAdapter.getEditUriFromEntry(nullIdEntry));
      Assert.assertNotNull(nullIdEntry.getBaseUri());
      Assert.assertNotNull(nullIdEntry.getId());
      Assert.assertNotNull(AbstractAdapter.getEditUriFromEntry(nullIdEntry));
      Assert.assertEquals(nullIdEntry.getId().toString(),
          AbstractAdapter.getEditUriFromEntry(nullIdEntry));

      // should have 2 entries in feed
      Feed feed2 = getFeed();
      Assert.assertEquals(200, status);
      Assert.assertEquals(feed2.getEntries().size(), 2);

      /*
       * Update the entry
       */
      // If there is an Edit Link, we can edit the entry
      if (editUri != null) {
        // Before we can edit, we need to grab an "editable" representation
        logger.info("********** REQUEST: GET entry using editUri: " + editUri);
        Entry editedEntry = getEntry(editUri, true);
        Assert.assertEquals(200, status);

        // Change whatever you want in the retrieved entry
        String changedTitle = "changed title";
        editedEntry.getTitleElement().setValue(changedTitle);

        // Put it back to the server
        logger.info("**************  REQUEST: Put entry (editing entry)");
        Entry modifiedEntry = putEntry(editUri, editedEntry);
        Assert.assertEquals(200, status);
        Assert.assertEquals(changedTitle, modifiedEntry.getTitle());
        Assert.assertEquals(editUri,
            AbstractAdapter.getEditUriFromEntry(modifiedEntry));
        Assert.assertEquals(editUri, modifiedEntry.getId().toString());

        // Grab the Edit URI from the entry.  The entry MAY have more than one
        // edit link.  We need to make sure we grab the right one.
        editUri = AbstractAdapter.getEditUriFromEntry(modifiedEntry);

        // This is just to show that the entry has been modified
        logger.info("**** REQUEST: get entry AFTER editing it. entry id = " +
            editUri);
        Entry changedTitleEntry = getEntry(editUri, true);
        Assert.assertEquals(200, status);
        Assert.assertEquals(changedTitle, changedTitleEntry.getTitle());
        Assert.assertEquals(editUri, changedTitleEntry.getId().toString());
      } else {
        logger.info("Entry can't be modified because it doesn't have editUri");
      }

      // Delete the entry.
      logger.info("************** REQUEST: delete entry: " + entryUri);
      deleteEntry("4");
      Assert.assertEquals(200, status);

      // try to get the entry
      logger.info("******** REQUEST: get entry AFTER deleting it: " + entryUri);
      getEntry(entryUri, true);
      Assert.assertEquals(404, status);

      // is the feed updated?
      logger.info("************** REQUEST: get feed AFTER delting the entry");
      // should have 1 entry in feed
      Feed feedAfterDelete = getFeed();
      Assert.assertEquals(200, status);
      Assert.assertEquals(feedAfterDelete.getEntries().size(), 1);

      // done
      logger.info("done");

    } catch (Exception e) {
      logger.info(e.getMessage());
    }
  }


  @Override
  @Before
  public void setUp() throws Exception {
    // set up server
    // set up server
    String[] args = new String[] {"--port=" + JETTY_PORT,
        "--uri=http://localhost:" + JETTY_PORT};
    server = com.google.feedserver.server.jetty.Main.runJetty(args);
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
