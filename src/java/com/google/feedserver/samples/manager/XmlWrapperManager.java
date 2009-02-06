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

package com.google.feedserver.samples.manager;

import com.google.feedserver.adapters.AbstractManagedCollectionAdapter;
import com.google.feedserver.adapters.FeedServerAdapterException;
import com.google.feedserver.config.MixinConfiguration;
import com.google.feedserver.manager.AbstractWrapperManager;
import com.google.feedserver.samples.config.XmlMixinConfiguration;

/**
 * A manager that handles and wraps adapters. Whenever an adapter is created we
 * apply {@link WrapperManger} as a wrapper over the adapter. The
 * {@link XmlWrapperManager} looks at configuration of the adapter and applies
 * all the wrappers that needed by this adapter. If the adapter itself is a
 * wrapper then Wrapper manager finds its target adapter and applies this
 * wrapper over the target adapter.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public class XmlWrapperManager extends AbstractWrapperManager {
  public XmlWrapperManager(AbstractManagedCollectionAdapter targetAdapter)
      throws FeedServerAdapterException {
    super(targetAdapter);
  }

  @Override
  protected MixinConfiguration getMixinConfiguration(String configData) {
    return new XmlMixinConfiguration(configData);
  }
}
