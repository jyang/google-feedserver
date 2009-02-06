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

package com.google.feedserver.abdera.config;

import com.google.feedserver.testing.SampleBasicAdapter;

import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;

/**
 * Sample {@link FeedConfiguration} to be used for testing.
 * 
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 */
public class SampleFeedConfig extends FeedConfiguration {
  public static final String FEED_ID = "sample";
  public static final String FEED_SUB_URI = "sample";
  public static final String ADAPTER_CLASS_NAME = SampleBasicAdapter.class.getCanonicalName();
  public static final String FEED_CONFIG_LOCATION = "noConfig";

  public SampleFeedConfig() {
    super(FEED_ID, FEED_SUB_URI, ADAPTER_CLASS_NAME, FEED_CONFIG_LOCATION,
        SampleServerConfiguration.getInstance());
  }
}
