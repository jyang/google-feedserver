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

import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;
import org.apache.abdera.protocol.server.provider.managed.ServerConfiguration;

/**
 * Sample server configuration for unit tests
 * 
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 * 
 */
public class SampleServerConfiguration extends ServerConfiguration {

  protected SampleServerConfiguration() {
  }

  private static final SampleServerConfiguration INSTANCE = new SampleServerConfiguration();

  public static SampleServerConfiguration getInstance() {
    return INSTANCE;
  }

  @Override
  public String getAdapterConfigLocation() {
    return "/tmp/adapters";
  }

  @Override
  public String getFeedConfigLocation() {
    return "/tmp/feeds";
  }

  @Override
  public String getFeedConfigSuffix() {
    return ".properties";
  }

  @Override
  public String getFeedNamespace() {
    return "http://feed-test.example.com/1.0/";
  }

  @Override
  public String getFeedNamespacePrefix() {
    return "f";
  }

  @Override
  public int getPort() {
    return 8080;
  }

  @Override
  public String getServerUri() {
    return "http://localhost:8080";
  }

  @Override
  public FeedConfiguration loadFeedConfiguration(String feedId) throws Exception {
    if (feedId == SampleFeedConfig.FEED_ID) {
      return new SampleFeedConfig();
    }
    return null;
  }
}
