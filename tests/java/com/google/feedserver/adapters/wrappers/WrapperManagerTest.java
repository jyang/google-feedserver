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

package com.google.feedserver.adapters.wrappers;

import com.google.feedserver.adapters.AbstractManagedCollectionAdapter;
import com.google.feedserver.adapters.FeedServerAdapterException;
import com.google.feedserver.samples.manager.XmlWrapperManager;
import com.google.feedserver.testing.TestUtil;
import com.google.feedserver.wrappers.ManagedCollectionAdapterWrapper;

import junit.framework.TestCase;

/**
 * Unit tests for {@link XmlWrapperManager}
 * 
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 * 
 */

public class WrapperManagerTest extends TestCase {

  private TestUtil testUtil;

  @Override
  protected void setUp() throws Exception {
    testUtil = new TestUtil();
    testUtil.setup();
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    testUtil.tearDown();
    super.tearDown();
  }

  public void testWrapperManagerForAdapterWithWrappers() throws FeedServerAdapterException {
    AbstractManagedCollectionAdapter targetAdapter =
        testUtil.getBasicAdapterWithFeedConfigWithWrappers();
    XmlWrapperManager manager = new XmlWrapperManager(targetAdapter);
    assertEquals(manager.getConfiguration(), targetAdapter.getConfiguration());

    AbstractManagedCollectionAdapter targetAdapter1 = manager.getTargetAdapter();
    assertEquals(TestUtil.WRAPPER_CLASS, targetAdapter1.getClass().getName());
    ManagedCollectionAdapterWrapper wrapper = ((ManagedCollectionAdapterWrapper) targetAdapter1);
    assertEquals(TestUtil.TEST_WRAPPER3_CONFIG, wrapper.getWrapperConfig());

    AbstractManagedCollectionAdapter targetAdapter2 = wrapper.getTargetAdapter();
    assertEquals(TestUtil.WRAPPER_CLASS, targetAdapter2.getClass().getName());
    wrapper = ((ManagedCollectionAdapterWrapper) targetAdapter2);
    assertEquals(TestUtil.TEST_WRAPPER2_CONFIG, wrapper.getWrapperConfig());

    AbstractManagedCollectionAdapter targetAdapter3 = wrapper.getTargetAdapter();
    assertEquals(TestUtil.WRAPPER_CLASS, targetAdapter3.getClass().getName());
    wrapper = ((ManagedCollectionAdapterWrapper) targetAdapter3);
    assertEquals(TestUtil.TEST_WRAPPER1_CONFIG, wrapper.getWrapperConfig());

    AbstractManagedCollectionAdapter targetAdapter4 = wrapper.getTargetAdapter();
    assertEquals(TestUtil.SAMPLE_ADAPTER_CLASS, targetAdapter4.getClass().getName());
    assertEquals(targetAdapter, targetAdapter4);
  }

  public void testWrapperManagerForAdapterWithNoWrappers() throws FeedServerAdapterException {
    AbstractManagedCollectionAdapter targetAdapter =
        testUtil.getBasicAdapterWithFeedConfigWithNoWrappers();
    XmlWrapperManager manager = new XmlWrapperManager(targetAdapter);
    assertEquals(manager.getConfiguration(), targetAdapter.getConfiguration());

    AbstractManagedCollectionAdapter targetAdapter1 = manager.getTargetAdapter();
    assertEquals(TestUtil.SAMPLE_ADAPTER_CLASS, targetAdapter1.getClass().getName());
    assertEquals(targetAdapter, targetAdapter1);
  }

  public void testWrapperManagerForWrapperAdapterWithWrappers() throws FeedServerAdapterException {
    AbstractManagedCollectionAdapter targetAdapter =
        testUtil.getBasicWrapperAdapterWithFeedConfigWithWrappers();
    XmlWrapperManager manager = new XmlWrapperManager(targetAdapter);
    assertEquals(manager.getConfiguration(), targetAdapter.getConfiguration());

    AbstractManagedCollectionAdapter targetAdapter1 = manager.getTargetAdapter();
    assertEquals(TestUtil.WRAPPER_CLASS, targetAdapter1.getClass().getName());
    ManagedCollectionAdapterWrapper wrapper = ((ManagedCollectionAdapterWrapper) targetAdapter1);
    assertEquals(TestUtil.TEST_WRAPPER3_CONFIG, wrapper.getWrapperConfig());
    assertEquals(targetAdapter, targetAdapter1);

    AbstractManagedCollectionAdapter targetAdapter2 = wrapper.getTargetAdapter();
    assertEquals(XmlWrapperManager.class.getName(), targetAdapter2.getClass().getName());
    wrapper = ((ManagedCollectionAdapterWrapper) targetAdapter2);

    AbstractManagedCollectionAdapter targetAdapter3 = wrapper.getTargetAdapter();
    assertEquals(TestUtil.WRAPPER_CLASS, targetAdapter3.getClass().getName());
    wrapper = ((ManagedCollectionAdapterWrapper) targetAdapter3);
    assertEquals(TestUtil.TEST_WRAPPER3_CONFIG, wrapper.getWrapperConfig());

    AbstractManagedCollectionAdapter targetAdapter4 = wrapper.getTargetAdapter();
    assertEquals(TestUtil.WRAPPER_CLASS, targetAdapter4.getClass().getName());
    wrapper = ((ManagedCollectionAdapterWrapper) targetAdapter4);
    assertEquals(TestUtil.TEST_WRAPPER2_CONFIG, wrapper.getWrapperConfig());

    AbstractManagedCollectionAdapter targetAdapter5 = wrapper.getTargetAdapter();
    assertEquals(TestUtil.WRAPPER_CLASS, targetAdapter5.getClass().getName());
    wrapper = ((ManagedCollectionAdapterWrapper) targetAdapter5);
    assertEquals(TestUtil.TEST_WRAPPER1_CONFIG, wrapper.getWrapperConfig());

    AbstractManagedCollectionAdapter targetAdapter6 = wrapper.getTargetAdapter();
    assertEquals(TestUtil.SAMPLE_ADAPTER_CLASS, targetAdapter6.getClass().getName());
  }

  public void testWrapperManagerForWrapperAdapterWithNoWrappers() throws FeedServerAdapterException {
    AbstractManagedCollectionAdapter targetAdapter =
        testUtil.getBasicWrapperAdapterWithFeedConfigWithNoWrappers();
    XmlWrapperManager manager = new XmlWrapperManager(targetAdapter);
    assertEquals(manager.getConfiguration(), targetAdapter.getConfiguration());

    AbstractManagedCollectionAdapter targetAdapter1 = manager.getTargetAdapter();
    assertEquals(TestUtil.WRAPPER_CLASS, targetAdapter1.getClass().getName());
    ManagedCollectionAdapterWrapper wrapper = ((ManagedCollectionAdapterWrapper) targetAdapter1);
    assertEquals(TestUtil.TEST_WRAPPER3_CONFIG, wrapper.getWrapperConfig());
    assertEquals(targetAdapter, targetAdapter1);

    AbstractManagedCollectionAdapter targetAdapter2 = wrapper.getTargetAdapter();
    assertEquals(XmlWrapperManager.class.getName(), targetAdapter2.getClass().getName());
    wrapper = ((ManagedCollectionAdapterWrapper) targetAdapter2);

    AbstractManagedCollectionAdapter targetAdapter3 = wrapper.getTargetAdapter();
    assertEquals(TestUtil.SAMPLE_ADAPTER_CLASS, targetAdapter3.getClass().getName());
  }

}
