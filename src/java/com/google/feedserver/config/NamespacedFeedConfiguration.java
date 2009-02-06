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

package com.google.feedserver.config;

import com.google.feedserver.util.FeedServerUtil;

import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Hosted version of {@link FeedConfiguration}
 * 
 * @author abhinavk@gmail.com (Abhinav kHandelwal)
 * 
 */
public class NamespacedFeedConfiguration extends FeedConfiguration {
  protected final Map<String, Object> properties;
  protected NamespacedAdapterConfiguration adapterConfiguration;

  /**
   * Creates a new {@link NamespacedFeedConfiguration} using the properties for
   * configuration. Specific property keys used include the following:
   * <ul>
   * <li>{@link FeedServerConfiguration#ID_KEY}</li>
   * <li>{@link FeedServerConfiguration#FEED_ADAPTER_NAME_KEY}</li>
   * <li>{@link FeedConfiguration#PROP_AUTHOR_NAME}</li>
   * <li>{@link FeedConfiguration#PROP_TITLE_NAME}</li>
   * </ul>
   * 
   * @param properties Properties to use for configuration
   * @param serverConfiguration FeedServer per namespace configuration
   */
  public NamespacedFeedConfiguration(Map<String, Object> properties,
      NamespacedAdapterConfiguration adapterConfiguration,
      PerNamespaceServerConfiguration serverConfiguration) {
    super(
        FeedServerUtil.getStringProperty(properties, FeedServerConfiguration.ID_KEY),
        FeedServerUtil.getStringProperty(properties, FeedServerConfiguration.ID_KEY),
        FeedServerUtil.getStringProperty(properties, FeedServerConfiguration.ID_KEY),
        FeedServerUtil.getStringProperty(properties, FeedServerConfiguration.FEED_ADAPTER_NAME_KEY),
        serverConfiguration);
    this.properties = new HashMap<String, Object>(properties);
    setAdapterConfig(adapterConfiguration);
  }

  /**
   * Gets the Feed author for the {@link FeedConfiguration}
   */
  @Override
  public String getFeedAuthor() {
    return getStringProperty(FeedConfiguration.PROP_AUTHOR_NAME);
  }

  /**
   * Gets the Feed title for the {@link FeedConfiguration}
   */
  @Override
  public String getFeedTitle() {
    return getStringProperty(FeedConfiguration.PROP_TITLE_NAME);
  }

  /**
   * Sets the Feed author for the {@link FeedConfiguration}
   * 
   * @param feedAuthor The new feed author
   */
  @Override
  public void setFeedAuthor(String feedAuthor) {
    super.setFeedAuthor(feedAuthor);
    properties.put(FeedConfiguration.PROP_AUTHOR_NAME, feedAuthor);
  }

  /**
   * Set the Feed title for the {@link FeedConfiguration}
   * 
   * @param feedTitle The new feed title
   */
  @Override
  public void setFeedTitle(String feedTitle) {
    super.setFeedTitle(feedTitle);
    properties.put(FeedConfiguration.PROP_TITLE_NAME, feedTitle);
  }

  /**
   * Gets the adaptor name for the {@link FeedConfiguration}
   */
  public String getAdapterName() {
    return getFeedConfigLocation();
  }

  public Object getTypeMetadataConfig() {
    return properties.get(FeedServerConfiguration.FEED_TYPE_CONFIG_KEY);
  }

  /**
   * Gets the properties for the {@link NamespacedFeedConfiguration}
   */
  public Map<String, Object> getProprties() {
    return properties;
  }

  /**
   * Gets the Adapter configuration for the {@link FeedConfiguration}
   */
  @Override
  public NamespacedAdapterConfiguration getAdapterConfiguration() {
    return adapterConfiguration;
  }

  /**
   * Sets the Adapter configuration for the {@link FeedConfiguration}
   */
  public void setAdapterConfig(NamespacedAdapterConfiguration adapterConfig) {
    this.adapterConfiguration = adapterConfig;
  }

  /**
   * Gets the named string property for the {@link NamespacedFeedConfiguration}
   * as a string
   */
  public String getStringProperty(String key) {
    return FeedServerUtil.getStringProperty(properties, key);
  }

  /**
   * Gets the named string property for the {@link NamespacedFeedConfiguration}
   */
  @Override
  public Object getProperty(String key) {
    return FeedServerUtil.getProperty(properties, key);
  }

  /**
   * Gets the Java class name for the Adapter configuration
   */
  @Override
  public String getAdapterClassName() {
    return getAdapterConfiguration().getAdapterType();
  }

  /**
   * Gets the per namespace server configuration
   */
  @Override
  public PerNamespaceServerConfiguration getServerConfiguration() {
    return adapterConfiguration.getServerConfiguration();
  }

  public String getConfigData() {
    return getStringProperty(FeedServerConfiguration.CONFIG_VALUE_KEY);
  }
}
