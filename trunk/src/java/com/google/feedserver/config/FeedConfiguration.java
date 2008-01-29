/**
 * Copyright 2008 Google Inc.
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

package com.google.feedserver.config;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration for feeds
 *
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 *
 */
public class FeedConfiguration extends Configuration {
  public static final String PROP_NAME_ADAPTER_CLASS = "adapterClassName";
  public static final String PROP_SUB_URI_NAME = "subUri";
  public static final String PROP_AUTHOR_NAME = "author";
  public static final String PROP_TITLE_NAME = "title";
  public static final String PROP_FEED_CONFIG_LOCATION_NAME = "configFile";

  public static final String ENTRY_ELEM_NAME_ID = "id";
  public static final String ENTRY_ELEM_NAME_TITLE = "title";
  public static final String ENTRY_ELEM_NAME_AUTHOR = "author";
  public static final String ENTRY_ELEM_NAME_UPDATED = "updated";

  private final String feedId;
  private final String subUri;
  private final String adapterClassName;
  private final String feedConfigLocation;
  private String feedTitle = "unknown";
  private String feedAuthor = "unknown";
  private Map<Object, Object> optionalProperties;
  private final AdapterConfiguration adapterConfiguration;

  public FeedConfiguration(String feedId, String subUri,
      String adapterClassName, String feedConfigLocation) {
    this.feedId = feedId;
    this.subUri = subUri;
    this.adapterClassName = adapterClassName;
    this.feedConfigLocation = feedConfigLocation;
    this.adapterConfiguration = new AdapterConfiguration(feedConfigLocation);
  }
  
  public static FeedConfiguration getFeedConfiguration(String feedId,
      Properties properties) {
    FeedConfiguration feedConfiguration = new FeedConfiguration(feedId,
        Configuration.getProperty(properties, PROP_SUB_URI_NAME),
        Configuration.getProperty(properties, PROP_NAME_ADAPTER_CLASS),
        Configuration.getProperty(properties, PROP_FEED_CONFIG_LOCATION_NAME));
    if (properties.containsKey(PROP_AUTHOR_NAME)) {
      feedConfiguration.setFeedAuthor(
          Configuration.getProperty(properties, PROP_AUTHOR_NAME));      
    }

    if (properties.containsKey(PROP_TITLE_NAME)) {
      feedConfiguration.setFeedTitle(
          Configuration.getProperty(properties, PROP_TITLE_NAME));      
    }
    feedConfiguration.optionalProperties = properties;
    return feedConfiguration;
  }

  public static FeedConfiguration getFeedConfiguration(
      String feedId) throws IOException {
    ServerConfiguration config = ServerConfiguration.getInstance();
    Properties prop = loadFileAsProperties(
        config.getFeedConfigLocation() + feedId +
        ServerConfiguration.PROPERTIES_FILE_SUFFIX);
    return getFeedConfiguration(feedId, prop);
  }

  public String getAdapterClassName() {
    return adapterClassName;
  }

  public String getFeedAuthor() {
    return feedAuthor;
  }

  public String getFeedConfigLocation() {
    return feedConfigLocation;
  }

  public String getFeedId() {
    return feedId;
  }
  
  public String getFeedTitle() {
    return feedTitle;
  }
  
  public String getSubUri() {
    return subUri;
  }
  
  public void setFeedAuthor(String feedAuthor) {
    this.feedAuthor = feedAuthor;
  }

  public void setFeedTitle(String feedTitle) {
    this.feedTitle = feedTitle;
  }
  
  public String getFeedUri() {
    return ServerConfiguration.getInstance().getServerUri() + "/" + getSubUri();
  }

  public Object hasProperty(String key) {
    return optionalProperties.containsKey(key);
  }

  public Object getProperty(String key) {
    return optionalProperties.get(key);
  }
  
  public AdapterConfiguration getAdapterConfiguration() {
    return adapterConfiguration;
  }
}
