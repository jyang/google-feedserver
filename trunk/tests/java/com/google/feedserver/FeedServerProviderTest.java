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

import com.google.feedserver.adapter.Adapter;
import com.google.feedserver.adapter.AdapterManager;
import com.google.feedserver.server.FeedServerProvider;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.ServiceContext;
import org.apache.abdera.protocol.server.Target;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.runner.RunWith;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;
import junit.framework.TestCase;

@RunWith(JMock.class)
public class FeedServerProviderTest extends TestCase {
  protected static Logger logger =
    Logger.getLogger(FeedServerProviderTest.class.getName());

  JUnit4Mockery context = new JUnit4Mockery();
  private final JUnit4Mockery forObjMock = new JUnit4Mockery() {{
    setImposteriser(ClassImposteriser.INSTANCE);
  }};

  // setup a FeedServerProvider object
  FeedServerProvider feedServerProvider = new FeedServerProvider();

  // mock objects
  RequestContext request = context.mock(RequestContext.class);
  Target target = context.mock(Target.class);
  AdapterManager adapterManager = forObjMock.mock(AdapterManager.class);
  Adapter adapter = context.mock(Adapter.class);
  Feed feed = context.mock(Feed.class);
  Document<Feed> doc = context.mock(Document.class);
  Entry entry = context.mock(Entry.class);
  ServiceContext svcContext = context.mock(ServiceContext.class);
  Abdera abdera = forObjMock.mock(Abdera.class);
  Parser parser = context.mock(Parser.class);

  static final String FEEDID = "feedId";
  static final String ENTRYID = "entryId";

  @Override
  @Before
  public void setUp() throws Exception {
    // setup feedServerProvider object
    feedServerProvider.adapterManager = adapterManager;

    context.checking(new Expectations() {{
      allowing (request).getTarget(); will(returnValue(target));
      allowing (request).getContentType(); will(returnValue(null));
      allowing (request).getServiceContext(); will(returnValue(svcContext));
      allowing (svcContext).getAbdera(); will(returnValue(abdera));
      allowing (request).getDocument(parser); will(returnValue(doc));

      allowing (feed).getDocument(); will(returnValue(doc));
      allowing (doc).getContentType(); will(returnValue("text"));
      allowing (entry).getDocument(); will(returnValue(doc));
      allowing (doc).getRoot(); will(returnValue(entry));

      allowing (target).getParameter(FeedServerProvider.PARAM_FEED);
          will(returnValue(FEEDID));
      allowing (target).getParameter(FeedServerProvider.PARAM_ENTRY);
          will(returnValue(ENTRYID));
    }});

    forObjMock.checking(new Expectations() {{
      allowing (adapterManager).getAdapter(FEEDID);
          will(returnValue(adapter));
      allowing (abdera).getParser(); will(returnValue(parser));

    }});
  }

  @Override
  @After
  public void tearDown() throws Exception {
    feedServerProvider.adapterManager = null;
  }

  @Test
  public void testGetFeed() throws Exception {

    // test with good feedid
    context.checking(new Expectations() {{
      one (adapter).getFeed(); will(returnValue(feed));
    }});
    ResponseContext response = feedServerProvider.getFeed(request);
    assertEquals(response.getStatus(), 200);

    // test with incorrect feedid
    context.checking(new Expectations() {{
      one (adapter).getFeed(); will(returnValue(null));
    }});
    ResponseContext badResponse = feedServerProvider.getFeed(request);
    assertEquals(badResponse.getStatus(), 404);
  }

  @Test
  public void testGetEntry() throws Exception {
    // test with good entry id
    context.checking(new Expectations() {{
      one (adapter).getEntry(ENTRYID); will(returnValue(entry));
    }});
    ResponseContext response = feedServerProvider.getEntry(request);
    assertEquals(response.getStatus(), 200);

    // test with incorrect entry id
    context.checking(new Expectations() {{
      one (adapter).getEntry(ENTRYID); will(returnValue(null));
    }});
    ResponseContext badResponse = feedServerProvider.getEntry(request);
    assertEquals(badResponse.getStatus(), 404);
  }

  @Test
  public void testCreateEntry() throws Exception {
    // create a valid entry
    context.checking(new Expectations() {{
      one (adapter).createEntry(entry); will(returnValue(entry));
    }});
    ResponseContext response = feedServerProvider.createEntry(request);
    assertEquals(response.getStatus(), 200);

    // create entry with entryid =  an already existent entryid
    context.checking(new Expectations() {{
      one (adapter).createEntry(entry); will(returnValue(null));
    }});
    ResponseContext badResponse = feedServerProvider.createEntry(request);
    assertEquals(badResponse.getStatus(), 404);
  }

  @Test
  public void testUpdateEntry() throws Exception {
    // update a valid entry
    context.checking(new Expectations() {{
      one (adapter).updateEntry(ENTRYID, entry); will(returnValue(entry));
    }});
    ResponseContext response = feedServerProvider.updateEntry(request);
    assertEquals(response.getStatus(), 200);

    // update a non-existent entry
    context.checking(new Expectations() {{
      one (adapter).updateEntry(ENTRYID, entry); will(returnValue(null));
    }});
    ResponseContext badResponse = feedServerProvider.updateEntry(request);
    assertEquals(badResponse.getStatus(), 404);
  }

  @Test
  public void testDeleteEntry() throws Exception {
    // delete a valid entry
    context.checking(new Expectations() {{
      one (adapter).deleteEntry(ENTRYID); will(returnValue(true));
    }});
    ResponseContext response = feedServerProvider.deleteEntry(request);
    assertEquals(response.getStatus(), 200);

    // delete a non-existent entry
    context.checking(new Expectations() {{
      one (adapter).deleteEntry(ENTRYID); will(returnValue(false));
    }});
    ResponseContext badResponse = feedServerProvider.deleteEntry(request);
    assertEquals(badResponse.getStatus(), 404);
  }

  @Test
  public void testDeleteMedia() {
    ResponseContext response = feedServerProvider.deleteMedia(request);
    assertNull(response);
  }

  @Test
  public void testEntryPost() {
    ResponseContext response = feedServerProvider.entryPost(request);
    assertNull(response);
  }

  @Test
  public void testGetCategories() {
    ResponseContext response = feedServerProvider.getCategories(request);
    assertNull(response);
  }

  @Test
  public void testGetMedia() {
    ResponseContext response = feedServerProvider.getMedia(request);
    assertNull(response);
  }

  @Test
  public void testGetService() {
    ResponseContext response = feedServerProvider.getService(request);
    assertNull(response);
  }

  @Test
  public void testMediaPost() {
    ResponseContext response = feedServerProvider.mediaPost(request);
    assertNull(response);
  }

  @Test
  public void testUpdateMedia() {
    ResponseContext response = feedServerProvider.updateMedia(request);
    assertNull(response);
  }
}
