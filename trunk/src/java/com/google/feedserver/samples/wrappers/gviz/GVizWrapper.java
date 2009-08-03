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
package com.google.feedserver.samples.wrappers.gviz;

import com.google.feedserver.adapters.AbstractManagedCollectionAdapter;
import com.google.feedserver.adapters.FeedServerAdapterException;
import com.google.feedserver.metadata.FeedInfo;
import com.google.feedserver.wrappers.ManagedCollectionAdapterWrapper;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;

public class GVizWrapper extends ManagedCollectionAdapterWrapper {

  /**
   * Constructs a new GVIZWrapper with the given adapter and wrapper config
   * string
   * @param targetAdapter The Adapter used by this wrapper.
   * @param wrapperConfig The wrapper configuration.
   */
  public GVizWrapper(AbstractManagedCollectionAdapter targetAdapter, String wrapperConfig) {
    super(targetAdapter, wrapperConfig);
  }

  /**
   * Constructs a new GVIZWrapper with the given Abdera and feed configuration
   * This is a wrapper constructor to be used when this wrapper is defined and
   * used as an adapter in AdapterConfig feed.
   * The config in AdapterConfig defines the target adapter for this wrapper and
   * the configuration data for this wrapper.
   * @param abdera instance of Abdera
   * @param config The feed configuration
   */
  public GVizWrapper(Abdera abdera, FeedConfiguration config) {
    super(abdera, config);
  }

  @Override
  public Feed retrieveFeed(RequestContext request) throws FeedServerAdapterException {
    FeedInfo feedInfo = getFeedInfo(request);
    return super.retrieveFeed(request);
  }
}
