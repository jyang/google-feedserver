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

import com.google.feedserver.config.MixinConfiguration;
import com.google.feedserver.samples.config.MapMixinConfiguration;
import com.google.feedserver.samples.config.XmlMixinConfiguration;
import com.google.feedserver.testing.TestUtil;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for {@link XmlMixinConfiguration}.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public class WrapperConfigurationTest extends TestCase {

  public void testCorrectWrapperConfig() {
    MixinConfiguration config = new XmlMixinConfiguration(TestUtil.WRAPPER_ADAPTER1_CONFIG);
    assertEquals(TestUtil.TEST_ADAPTER_WITH_NO_WRAPPER, config.getTargetAdapterName());
    assertEquals(TestUtil.TEST_WRAPPER3_CONFIG, config.getWrapperConfig());
    assertEquals(null, config.getWrapperName());

    config = new MapMixinConfiguration(getWrapper());
    assertEquals(TestUtil.WRAPPER_CLASS_NAME, config.getWrapperName());
    assertEquals(TestUtil.TEST_WRAPPER3_CONFIG, config.getWrapperConfig());
    assertEquals(null, config.getTargetAdapterName());
  }

  public void testIncorrectWrapperConfig() {
    try {
      MixinConfiguration config =
          new XmlMixinConfiguration(TestUtil.XML_PROLOG + "<asdf>asdf</asdf>");
      fail();
    } catch (UnsupportedOperationException ex) {
      // expected.
    }
  }

  public static Map<String, Object> getWrapper() {
    Map<String, Object> TEST_WRAPPER3 = new HashMap<String, Object>();
    TEST_WRAPPER3.put(MixinConfiguration.WRAPPER_CLASS_NAME, TestUtil.WRAPPER_CLASS_NAME);
    TEST_WRAPPER3.put(MixinConfiguration.WRAPPER_CONFIG, TestUtil.TEST_WRAPPER3_CONFIG);

    return TEST_WRAPPER3;
  }
}
