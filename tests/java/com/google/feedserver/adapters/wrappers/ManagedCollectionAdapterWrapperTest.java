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
import com.google.feedserver.testing.TestUtil;
import com.google.feedserver.wrappers.ManagedCollectionAdapterWrapper;

import junit.framework.TestCase;

/**
 * Unit tests for {@link ManagedCollectionAdapterWrapper}.
 * 
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 * 
 */

public class ManagedCollectionAdapterWrapperTest extends TestCase {

  private TestUtil testUtil;
  private AbstractManagedCollectionAdapter targetAdapter;


  @Override
  protected void setUp() throws Exception {
    super.setUp();
    testUtil = new TestUtil();
    testUtil.setup();
    targetAdapter = testUtil.getBasicAdapterWithFeedConfigWithNoWrappers();
  }

  @Override
  protected void tearDown() throws Exception {
    testUtil.tearDown();
    super.tearDown();
  }

  public void testTargetAdapterConstructor() {
    ManagedCollectionAdapterWrapper wrappedAdapter =
        new ManagedCollectionAdapterWrapper(targetAdapter, TestUtil.TEST_WRAPPER1_CONFIG);
    assertEquals(targetAdapter.getConfiguration(), wrappedAdapter.getConfiguration());
    assertEquals(TestUtil.TEST_WRAPPER1_CONFIG, wrappedAdapter.getWrapperConfig());
  }


}
