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

import org.apache.abdera.protocol.server.provider.managed.CollectionAdapterConfiguration;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Hosted version of {@link CollectionAdapterConfiguration}.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public class NamespacedAdapterConfiguration extends CollectionAdapterConfiguration {
  protected final Map<String, Object> properties;
  protected String configData;
  protected final String adapterName;



  /**
   * Creates a new {@link NamespacedAdapterConfiguration} using the properties
   * for configuration. Specific property keys include the following:
   * <ul>
   * <li>{@link FeedServerConfiguration#ID_KEY}</li>
   * <li>{@link FeedServerConfiguration#CONFIG_VALUE_KEY}</li>
   * </ul>
   * 
   * @param properties Properties to use for configuration
   * @param serverConfiguration Abdera server configuration
   */
  public NamespacedAdapterConfiguration(Map<String, Object> properties,
      PerNamespaceServerConfiguration serverConfiguration) {
    super(serverConfiguration, "");
    this.properties = new HashMap<String, Object>(properties);
    this.adapterName = getStringProperty(FeedServerConfiguration.ID_KEY);
    this.configData = getStringProperty(FeedServerConfiguration.CONFIG_VALUE_KEY);
  }

  /**
   * Gets the adapter name for this configuration
   */
  public String getAdapterName() {
    return adapterName;
  }

  /**
   * Gets the adapter config data for this configuration
   */
  public String getConfigData() {
    return configData;
  }

  /**
   * Sets the adapter config data for this configuration
   */
  public void setConfigData(String configData) {
    this.configData = configData;
    properties.put(FeedServerConfiguration.CONFIG_VALUE_KEY, configData);
  }

  /**
   * Gets a property set on the {@link NamespacedAdapterConfiguration} as a
   * string
   */
  public String getStringProperty(String key) {
    return FeedServerUtil.getStringProperty(properties, key);
  }

  /**
   * Gets a property set on the {@link NamespacedAdapterConfiguration}
   */
  public Object getProperty(String key) {
    return FeedServerUtil.getProperty(properties, key);
  }

  /**
   * Gets the collection of properties set on the
   * {@link NamespacedAdapterConfiguration}
   */
  public Map<String, Object> getProperties() {
    return properties;
  }

  /**
   * Gets the adapter config data as an {@link InputStream}
   */
  @Override
  public InputStream getConfigAsFileInputStream() {
    return FeedServerUtil.getStringAsInputStream(getConfigData());
  }

  /**
   * Get the wrapper's that are associated with this adapter.
   * 
   * @return list of wrapper configurations that can be processed using
   *         WrapperConfiguration.
   */
  public MixinConfiguration[] getMixins() {
    if (getProperties().containsKey(FeedServerConfiguration.MIXINS)) {
      return (MixinConfiguration[]) getProperties().get(FeedServerConfiguration.MIXINS);
    }
    return null;
  }

  /**
   * Get the implicit mixin's for this adapter.
   * 
   * @return Array of mixin's.
   */
  public MixinConfiguration[] getImplicitMixins() {
    if (getProperties().containsKey(FeedServerConfiguration.IMPLICIT_MIXINS)) {
      return (MixinConfiguration[]) getProperties().get(FeedServerConfiguration.IMPLICIT_MIXINS);
    }
    return null;
  }

  /**
   * Remove implicit mixins from the configuration. So that other wrappes dont
   * know about them.
   */
  public void removeImplicitMixins() {
    if (getProperties().containsKey(FeedServerConfiguration.IMPLICIT_MIXINS)) {
      getProperties().remove(FeedServerConfiguration.IMPLICIT_MIXINS);
    }
  }

  /**
   * Get list of wrappers that should be applied to this adapter.
   * 
   * @return Array of wrapper names that should be applied to this adapter.
   */
  public String[] getRequiredWrappers() {
    if (getProperties().containsKey(FeedServerConfiguration.REQUIRED_WRAPPERS)) {
      return (String[]) getProperties().get(FeedServerConfiguration.REQUIRED_WRAPPERS);
    }
    return null;
  }

  /**
   * Gets the Adapter type
   */
  public String getAdapterType() {
    return getStringProperty(FeedServerConfiguration.ADAPTER_TYPE_KEY);
  }

  /**
   * Returns the adapter class name
   * 
   * @return
   */
  public String getAdapterClassName() {
    return getStringProperty(FeedConfiguration.PROP_NAME_ADAPTER_CLASS);
  }

  /**
   * Get the server configuration
   */
  @Override
  public PerNamespaceServerConfiguration getServerConfiguration() {
    return (PerNamespaceServerConfiguration) super.getServerConfiguration();
  }
}
