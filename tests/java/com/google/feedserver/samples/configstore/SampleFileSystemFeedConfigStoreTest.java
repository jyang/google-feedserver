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

package com.google.feedserver.samples.configstore;

import com.google.feedserver.config.MixinConfiguration;
import com.google.feedserver.config.NamespacedAdapterConfiguration;
import com.google.feedserver.config.NamespacedFeedConfiguration;
import com.google.feedserver.configstore.FeedConfigStoreException;
import com.google.feedserver.testing.TestUtil;

import junit.framework.TestCase;

/**
 * Test case for testing {@link SampleFileSystemFeedConfigStore}
 * 
 * @author rakeshs@google.com (Rakesh Shete)
 * 
 */
public class SampleFileSystemFeedConfigStoreTest extends TestCase {

  TestUtil testUtil;
  protected SampleFileSystemFeedConfigStore feedStoreBasedFeedConfigStore;

  public NamespacedFeedConfiguration feedConfiguration;
  public NamespacedAdapterConfiguration adapterConfiguration;

  public static final String NAMESPACE = TestUtil.DOMAIN_NAME;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    testUtil = new TestUtil();
    testUtil.setup(false);

    feedStoreBasedFeedConfigStore = (SampleFileSystemFeedConfigStore) testUtil.getFeedConfigStore();
    adapterConfiguration = testUtil.getAdapterWithNoWrappers();
    feedConfiguration = testUtil.getFeedWithAdaptersWithNoWrappers();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  protected void createConfigurations() {
  }

  public void testAddFeed() throws Exception {
    feedStoreBasedFeedConfigStore.addFeed(NAMESPACE, feedConfiguration);
    assertTrue(feedStoreBasedFeedConfigStore.getFeedIds(NAMESPACE).contains(
        feedConfiguration.getFeedId()));
    assertEquals(1, feedStoreBasedFeedConfigStore.getFeedIds(NAMESPACE).size());
    try {
      feedStoreBasedFeedConfigStore.addFeed(NAMESPACE, feedConfiguration);
      fail();
    } catch (FeedConfigStoreException e) {
      assertEquals(e.getReason(), FeedConfigStoreException.Reason.INTERNAL_ERROR);
    }

    try {
      feedStoreBasedFeedConfigStore.deleteFeed(NAMESPACE, feedConfiguration.getFeedId());
    } catch (FeedConfigStoreException e) {
      fail(e.getLocalizedMessage());
      e.printStackTrace();
    }
  }

  public void testDeleteFeed() throws Exception {
    feedStoreBasedFeedConfigStore.addFeed(NAMESPACE, feedConfiguration);
    assertTrue(feedStoreBasedFeedConfigStore.getFeedIds(NAMESPACE).contains(
        feedConfiguration.getFeedId()));
    assertEquals(1, feedStoreBasedFeedConfigStore.getFeedIds(NAMESPACE).size());
    feedStoreBasedFeedConfigStore.deleteFeed(NAMESPACE, feedConfiguration.getFeedId());
    assertEquals(0, feedStoreBasedFeedConfigStore.getFeedIds(NAMESPACE).size());
  }

  public void testHasFeed() throws Exception {
    assertFalse(feedStoreBasedFeedConfigStore.hasFeed(NAMESPACE, feedConfiguration.getFeedId()));
    feedStoreBasedFeedConfigStore.addFeed(NAMESPACE, feedConfiguration);
    assertTrue(feedStoreBasedFeedConfigStore.hasFeed(NAMESPACE, feedConfiguration.getFeedId()));
    feedStoreBasedFeedConfigStore.deleteFeed(NAMESPACE, feedConfiguration.getFeedId());
    assertFalse(feedStoreBasedFeedConfigStore.hasFeed(NAMESPACE, feedConfiguration.getFeedId()));
  }

  public void testGetAdatperConfigWithoutImpliciMixins() throws Exception {

    NamespacedAdapterConfiguration adapterConfig =
        (NamespacedAdapterConfiguration) feedStoreBasedFeedConfigStore.getAdapterConfiguration(
            NAMESPACE, TestUtil.TEST_ADAPTER_WITH_WRAPPER);
    assertEquals(TestUtil.SAMPLE_ADAPTER_CLASS, adapterConfig.getAdapterType());
    assertEquals(TestUtil.TEST_ADAPTER_WITH_WRAPPER, adapterConfig.getAdapterName());
    assertEquals(TestUtil.ADAPTER_CONFIGURATION, adapterConfig.getConfigData());

  }

  public void testGetAdatperConfigWithImpliciMixins() throws Exception {

    NamespacedAdapterConfiguration adapterConfig =
        (NamespacedAdapterConfiguration) feedStoreBasedFeedConfigStore.getAdapterConfiguration(
            NAMESPACE, TestUtil.SAMPLE_ADAPTER_WITH_IMPLICIT_MIXINS);
    assertEquals(TestUtil.SAMPLE_ADAPTER_CLASS, adapterConfig.getAdapterType());
    assertEquals(TestUtil.SAMPLE_ADAPTER_WITH_IMPLICIT_MIXINS, adapterConfig.getAdapterName());
    assertEquals(TestUtil.ADAPTER_CONFIGURATION, adapterConfig.getConfigData());

    MixinConfiguration[] implicitMixins = adapterConfig.getImplicitMixins();
    assertNotNull(implicitMixins);
    assertEquals(3, implicitMixins.length);
    assertEquals(TestUtil.WRAPPER_CLASS, implicitMixins[0].getWrapperName());
    assertEquals(TestUtil.WRAPPER_CLASS, implicitMixins[1].getWrapperName());
    assertEquals(TestUtil.WRAPPER_CLASS, implicitMixins[2].getWrapperName());
    assertEquals(TestUtil.TEST_WRAPPER1_CONFIG, implicitMixins[0].getWrapperConfig());
    assertEquals(TestUtil.TEST_WRAPPER2_CONFIG, implicitMixins[1].getWrapperConfig());
    assertEquals(TestUtil.TEST_WRAPPER3_CONFIG, implicitMixins[2].getWrapperConfig());
  }


}
