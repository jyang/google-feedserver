/*
 * Copyright 2009 Google Inc.
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

package com.google.feedserver.util;

import com.google.feedserver.config.FeedServerConfiguration;
import com.google.feedserver.configstore.FeedConfigStore;

import java.util.Map;
import java.util.Properties;

/**
 * The utiltiy class for {@link FeedConfigStore} implementations
 * 
 * @author rakeshs101981@gmail.com (Rakesh Shete)
 */
public class ConfigStoreUtil {

  protected ConfigStoreUtil() {

  }

  /**
   * Is the Adapter these properties belong to has implicit wrappers or not
   * 
   * @param properties belonging to Adapter.
   * @return <code>true</code> if the adapter has implcit mixins,
   *         <code>false</code> otherwise.
   */
  public static boolean hasImplicitMixins(Properties properties) {
    return containsKeyWithNonNullValue(properties, FeedServerConfiguration.IMPLICIT_MIXINS);
  }

  /**
   * Is the Adapter these properties belong to is a wrapper or not.
   * 
   * @param properties belonging to Adapter.
   * @return <code>true</code> if the adapter is a wrapper, <code>false</code>
   *         otherwise.
   */
  public static boolean isWrapper(Properties properties) {
    if (containsKeyWithNonNullValue(properties, FeedServerConfiguration.IS_WRAPPER)) {
      return new Boolean(properties.get(FeedServerConfiguration.IS_WRAPPER).toString())
          .booleanValue();
    }
    return false;
  }

  /**
   * Checks if the specified property is in the map with a non-null value
   * 
   * @param configProperties The containing map
   * @param key The key to be checked
   * @return True if the key is in the map with a non-null value and false
   *         otherwise
   */
  public static boolean containsKeyWithNonNullValue(Map<?, ?> configProperties, String key) {
    return configProperties.containsKey(key) && null != configProperties.get(key);
  }


}
