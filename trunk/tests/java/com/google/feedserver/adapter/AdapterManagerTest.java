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

import com.google.feedserver.adapter.Adapter;
import com.google.feedserver.adapter.AdapterManager;
import com.google.feedserver.config.FeedConfiguration;

import org.apache.abdera.Abdera;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.logging.Logger;
import junit.framework.TestCase;

@RunWith(JMock.class)
public class AdapterManagerTest extends TestCase {
  protected static Logger logger =
    Logger.getLogger(AdapterManagerTest.class.getName());

  JUnit4Mockery context = new JUnit4Mockery();
  private final JUnit4Mockery forObjMock = new JUnit4Mockery() {{
    setImposteriser(ClassImposteriser.INSTANCE);
  }};

  // mock objects
  Abdera abdera = forObjMock.mock(Abdera.class);

  @Test
  public void testLoadFeedInfo() {
    // try with good feedid
    try {
      FeedConfiguration config = FeedConfiguration.getFeedConfiguration("sample");
      assertNotNull(config);
    } catch (Exception e) {
      fail("exception while trying to load info for \"sample\" feed");
    }

    // try with invalid feedid
    try {
      FeedConfiguration config = FeedConfiguration.getFeedConfiguration("invalidFeed");
      fail("should have raised exception");
    } catch (Exception e) {
      // as expected
    }
  }

  @Test
  public void testCreateAdapterInstance() {
    AdapterManager aObj = new AdapterManager(abdera);
    String feedId = "sample";
    // try with good feedid
    try {
      FeedConfiguration config = FeedConfiguration.getFeedConfiguration(feedId);
      assertNotNull(config);
      Adapter adapter = aObj.createAdapterInstance(config);
      assertNotNull(adapter);
    } catch (Exception e) {
      fail("exception while trying to create adapter instance for \"sample\" feed");
    }

    // try with invalid feedid
    try {
      FeedConfiguration config = new FeedConfiguration("invalidFeedId",
          "/invaliduri", "com.google.feedserver.adapter.InvalidAdapterClass",
          "feedLocation"); 
      Adapter adapter = aObj.createAdapterInstance(config);
      assertNull(adapter);
    } catch (Exception e) {
      // as expected
    }
  }

  @Test
  public void testGetAdapter() {
    AdapterManager aObj = new AdapterManager(abdera);
    String feedId = "sample";
    // try with good feedid
    try {
      Adapter adapter = aObj.getAdapter(feedId);
      assertNotNull(adapter);
    } catch (Exception e) {
      fail("exception while trying to create adapter instance for \"sample\" feed");
    }

    // try with invalid feedid
    try {
      Adapter adapter = aObj.getAdapter("invalidFeedId");
      fail("should have raised exception");
    } catch (Exception e) {
      // as expected
    }
  }
}
