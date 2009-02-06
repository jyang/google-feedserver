/* Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.feedserver.tools;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;

import com.google.feedserver.client.TypelessFeedServerClient;
import com.google.feedserver.testing.FeedServerClientTestUtil;

/**
 * Unit test for FeedServerClientTool
 */

public class FeedServerClientToolTest extends TestCase {

  protected String TEST_FEED_URL = "http://www.domain.com/feed";
  protected String TEST_ENTRY_URL = "http://www.domain.com/feed/entry";
	  
  protected FeedServerClientTestUtil testUtil;
  protected TypelessFeedServerClient mockFeedServerClient;
  protected FeedServerClientTool feedServerClientTool;
	  
  @Override
  protected void setUp() throws Exception {

	super.setUp();

	testUtil = new FeedServerClientTestUtil();
    mockFeedServerClient = EasyMock.createMock(TypelessFeedServerClient.class);
    feedServerClientTool = new FeedServerClientTool(mockFeedServerClient);
  }

  public void testGetFeed() throws Exception {

    Map<String, Object> expectedEntity = testUtil.getSampleVehicleMap();
    List<Map<String, Object>> expectedEntities = new ArrayList<Map<String, Object>>();
    expectedEntities.add(expectedEntity);

    EasyMock.expect(mockFeedServerClient.getEntries(new URL(TEST_FEED_URL))).andReturn(
        expectedEntities);
    EasyMock.replay(mockFeedServerClient);

    List<Map<String, Object>> entities = feedServerClientTool.getFeed(TEST_FEED_URL);

    assertEquals(expectedEntities, entities);
    EasyMock.verify(mockFeedServerClient);
  }

  public void testGetEntry() throws Exception {

    Map<String, Object> expectedEntity = testUtil.getSampleVehicleMap();

    EasyMock.expect(mockFeedServerClient.getEntry(new URL(TEST_ENTRY_URL))).andReturn(
        expectedEntity);
    EasyMock.replay(mockFeedServerClient);

    Map<String, Object> entity = feedServerClientTool.getEntry(TEST_ENTRY_URL);

    assertEquals(expectedEntity, entity);
    EasyMock.verify(mockFeedServerClient);
  }

  public void testInsertEntry() throws Exception {

    Map<String, Object> expectedEntity = testUtil.getSampleVehicleMap();

    EasyMock.expect(
        mockFeedServerClient.insertEntry(new URL(TEST_FEED_URL), expectedEntity))
            .andReturn(expectedEntity);
    EasyMock.replay(mockFeedServerClient);

    Map<String, Object> entity = feedServerClientTool.insert(TEST_FEED_URL, expectedEntity);

    assertEquals(expectedEntity, entity);
    EasyMock.verify(mockFeedServerClient);
  }

  public void testUpdateEntry() throws Exception {

    Map<String, Object> expectedEntity = testUtil.getSampleVehicleMap();

    EasyMock.expect(
        mockFeedServerClient.updateEntry(new URL(TEST_ENTRY_URL), expectedEntity))
            .andReturn(expectedEntity);
    EasyMock.replay(mockFeedServerClient);

    Map<String, Object> entity = feedServerClientTool.update(TEST_ENTRY_URL, expectedEntity);

    assertEquals(expectedEntity, entity);
    EasyMock.verify(mockFeedServerClient);
  }

  public void testDeleteEntry() throws Exception {

    mockFeedServerClient.deleteEntry(new URL(TEST_ENTRY_URL));
    EasyMock.replay(mockFeedServerClient);

    feedServerClientTool.delete(TEST_ENTRY_URL);

    EasyMock.verify(mockFeedServerClient);
  }
}
