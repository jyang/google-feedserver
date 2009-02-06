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

package com.google.feedserver.wrappers;

import com.google.feedserver.adapters.AbstractManagedCollectionAdapter;
import com.google.feedserver.adapters.FeedServerAdapterException;
import com.google.feedserver.config.PerNamespaceServerConfiguration;
import com.google.feedserver.configstore.FeedConfigStore;
import com.google.feedserver.metadata.FeedInfo;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;
import org.apache.abdera.protocol.server.provider.managed.ServerConfiguration;

/**
 * A wrapper framework around {@link AbstractManagedCollectionAdapter}'s.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 */
public class ManagedCollectionAdapterWrapper extends AbstractManagedCollectionAdapter {
  protected AbstractManagedCollectionAdapter targetAdapter;
  protected String wrapperConfig;

  /**
   * A wrapper constructor to be used when a wrapper is declared as a part of
   * AdapterConfiguration of another adapter in AdapterConfig feed.
   * 
   * The config of target adapter defines all the wrappers to be used for this
   * adapter and also their configurations.
   * 
   * @param targetAdapter the target adapter for this wrapper.
   * @param wrapperConfig the configuration data for this wrapper.
   */
  public ManagedCollectionAdapterWrapper(AbstractManagedCollectionAdapter targetAdapter,
      String wrapperConfig) {
    super(targetAdapter.getAbdera(), targetAdapter.getConfiguration());
    this.targetAdapter = targetAdapter;
    this.wrapperConfig = wrapperConfig;
  }

  /**
   * A wrapper constructor to be used when this wrapper is defined and used as
   * an adapter in AdapterConfig feed.
   * 
   * The config in AdapterConfig defines the target adapter for this wrapper and
   * the configuration data for this wrapper.
   * 
   */
  public ManagedCollectionAdapterWrapper(Abdera abdera, FeedConfiguration config) {
    super(abdera, config);
  }

  public AbstractManagedCollectionAdapter getTargetAdapter() {
    return targetAdapter;
  }

  public void setTargetAdapter(AbstractManagedCollectionAdapter targetAdapter) {
    this.targetAdapter = targetAdapter;
  }

  public String getWrapperConfig() {
    return wrapperConfig;
  }

  public void setWrapperConfig(String wrapperConfig) {
    this.wrapperConfig = wrapperConfig;
  }

  public FeedConfigStore getConfigStore() {
    ServerConfiguration serverConfig =
        getConfiguration().getAdapterConfiguration().getServerConfiguration();
    if (serverConfig instanceof PerNamespaceServerConfiguration) {
      PerNamespaceServerConfiguration namespaceServerConfiguration =
          (PerNamespaceServerConfiguration) serverConfig;
      return namespaceServerConfiguration.getGolbalServerConfiguration().getFeedConfigStore();
    }
    return null;
  }

  @Override
  public Feed retrieveFeed(RequestContext request) throws FeedServerAdapterException {
    return targetAdapter.retrieveFeed(request);
  }

  @Override
  public Entry retrieveEntry(RequestContext request, Object entryId)
      throws FeedServerAdapterException {
    return targetAdapter.retrieveEntry(request, entryId);
  }

  @Override
  public Entry createEntry(RequestContext request, Entry entry) throws FeedServerAdapterException {
    return targetAdapter.createEntry(request, entry);
  }

  @Override
  public Entry updateEntry(RequestContext request, Object entryId, Entry entry)
      throws FeedServerAdapterException {
    return targetAdapter.updateEntry(request, entryId, entry);
  }

  @Override
  public void deleteEntry(RequestContext request, Object entryId) throws FeedServerAdapterException {
    targetAdapter.deleteEntry(request, entryId);
  }

  @Override
  public FeedInfo getFeedInfo(RequestContext request) throws FeedServerAdapterException {
    return targetAdapter.getFeedInfo(request);
  }
}
