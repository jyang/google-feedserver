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

package com.google.feedserver.manager;

import com.google.feedserver.adapters.AbstractManagedCollectionAdapter;
import com.google.feedserver.adapters.FeedServerAdapterException;
import com.google.feedserver.config.AclValidator;
import com.google.feedserver.config.MixinConfiguration;
import com.google.feedserver.config.NamespacedAdapterConfiguration;
import com.google.feedserver.config.AclValidator.AclResult;
import com.google.feedserver.configstore.FeedConfigStoreException;
import com.google.feedserver.wrappers.ManagedCollectionAdapterWrapper;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.provider.managed.CollectionAdapterConfiguration;

import java.lang.reflect.InvocationTargetException;

/**
 * Wrapper manager helps to manage wrappers over adapters.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public abstract class AbstractWrapperManager extends ManagedCollectionAdapterWrapper {

  public AbstractWrapperManager(AbstractManagedCollectionAdapter targetAdapter)
      throws FeedServerAdapterException {
    super(targetAdapter, "");
    configureWrappers();
  }

  protected void configureWrappers() throws FeedServerAdapterException {
    if (getTargetAdapter() instanceof ManagedCollectionAdapterWrapper) {
      configureWrapperAdapter();
    } else {
      configureManagedAdapter();
    }
  }

  protected void configureWrapperAdapter() throws FeedServerAdapterException {
    String configData = getWrapperAdapterConfiguration();
    MixinConfiguration config = getMixinConfiguration(configData);
    AbstractManagedCollectionAdapter adapter = createTargetAdapter(config);
    ManagedCollectionAdapterWrapper wrapper = (ManagedCollectionAdapterWrapper) targetAdapter;
    wrapper.setTargetAdapter(adapter);
    wrapper.setWrapperConfig(config.getWrapperConfig());
  }

  protected abstract MixinConfiguration getMixinConfiguration(String configData)
      throws FeedServerAdapterException;

  protected void configureManagedAdapter() throws FeedServerAdapterException {
    MixinConfiguration[] wrappers = getImplicitAdapterWrappers();
    if (null != wrappers) {
      for (MixinConfiguration wrapper : wrappers) {
        applyWrapperToAdapter(wrapper);
      }
    }
    removeImplicitWrappersFromConfig();

    wrappers = getAdapterWrappers();
    if (null == wrappers) {
      return;
    }
    for (MixinConfiguration wrapper : wrappers) {
      applyWrapperToAdapter(wrapper);
    }
  }

  /**
   * Apply the wrapper to the adapter.
   * 
   * @param config Mixin configuration for the wrapper.
   * @throws FeedServerAdapterException
   */
  protected void applyWrapperToAdapter(MixinConfiguration config) throws FeedServerAdapterException {
    try {
      targetAdapter =
          FeedServerAdapterManager.createWrapper(config.getWrapperName(), targetAdapter, config
              .getWrapperConfig());
    } catch (SecurityException e) {
      throwApplyingWrapperError(config);
    } catch (IllegalArgumentException e) {
      throwApplyingWrapperError(config);
    } catch (ClassNotFoundException e) {
      throwApplyingWrapperError(config);
    } catch (NoSuchMethodException e) {
      throwApplyingWrapperError(config);
    } catch (InstantiationException e) {
      throwApplyingWrapperError(config);
    } catch (IllegalAccessException e) {
      throwApplyingWrapperError(config);
    } catch (InvocationTargetException e) {
      throwApplyingWrapperError(config);
    }
  }

  protected MixinConfiguration[] getImplicitAdapterWrappers() {
    NamespacedAdapterConfiguration adapterConfig =
        targetAdapter.getConfiguration().getAdapterConfiguration();
    return adapterConfig.getImplicitMixins();
  }

  protected void removeImplicitWrappersFromConfig() {
    NamespacedAdapterConfiguration adapterConfig =
        targetAdapter.getConfiguration().getAdapterConfiguration();
    adapterConfig.removeImplicitMixins();
  }

  protected MixinConfiguration[] getAdapterWrappers() {
    NamespacedAdapterConfiguration adapterConfig =
        targetAdapter.getConfiguration().getAdapterConfiguration();
    return adapterConfig.getMixins();
  }

  protected String getWrapperAdapterConfiguration() {
    NamespacedAdapterConfiguration adapterConfiguration =
        targetAdapter.getConfiguration().getAdapterConfiguration();
    return adapterConfiguration.getConfigData();
  }

  protected AbstractManagedCollectionAdapter createTargetAdapter(MixinConfiguration config)
      throws FeedServerAdapterException {
    CollectionAdapterConfiguration targetAdapterConfig = null;
    try {
      targetAdapterConfig =
          getConfigStore().getAdapterConfiguration(getNameSpace(), config.getTargetAdapterName());
    } catch (FeedConfigStoreException e) {
      throwApplyingWrapperError(config);
    }
    getConfiguration().setAdapterConfig((NamespacedAdapterConfiguration) targetAdapterConfig);
    CollectionAdapter adapter = null;
    try {
      adapter = FeedServerAdapterManager.createWrappedAdapter(getConfiguration(), getAbdera());
    } catch (SecurityException e) {
      throwApplyingWrapperError(config);
    } catch (IllegalArgumentException e) {
      throwApplyingWrapperError(config);
    } catch (ClassNotFoundException e) {
      throwApplyingWrapperError(config);
    } catch (NoSuchMethodException e) {
      throwApplyingWrapperError(config);
    } catch (InstantiationException e) {
      throwApplyingWrapperError(config);
    } catch (IllegalAccessException e) {
      throwApplyingWrapperError(config);
    } catch (InvocationTargetException e) {
      throwApplyingWrapperError(config);
    }
    return (AbstractManagedCollectionAdapter) adapter;
  }

  private void throwApplyingWrapperError(MixinConfiguration config)
      throws FeedServerAdapterException {
    throw new FeedServerAdapterException(
        FeedServerAdapterException.Reason.MIXIN_ERROR_APPLYING_WRAPPER,
        "Coud not apply wrapper over adapter " + config.getTargetAdapterName());
  }

  @Override
  public Entry retrieveEntry(RequestContext request, Object entryId)
      throws FeedServerAdapterException {
    if (AclResult.ACCESS_GRANTED != getAclValidator().canRetrieveEntry(request, entryId)) {
      throw new FeedServerAdapterException(FeedServerAdapterException.Reason.NOT_AUTHORIZED,
          "Access Denied");
    }
    return super.retrieveEntry(request, entryId);
  }

  @Override
  public Feed retrieveFeed(RequestContext request) throws FeedServerAdapterException {
    if (AclResult.ACCESS_GRANTED != getAclValidator().canRetrieveFeed(request)) {
      throw new FeedServerAdapterException(FeedServerAdapterException.Reason.NOT_AUTHORIZED,
          "Access Denied");
    }
    return super.retrieveFeed(request);
  }

  @Override
  public Entry updateEntry(RequestContext request, Object entryId, Entry entry)
      throws FeedServerAdapterException {
    if (AclResult.ACCESS_GRANTED != getAclValidator().canUpdateEntry(request, entryId)) {
      throw new FeedServerAdapterException(FeedServerAdapterException.Reason.NOT_AUTHORIZED,
          "Access Denied");
    }
    return super.updateEntry(request, entryId, entry);
  }

  @Override
  public Entry createEntry(RequestContext request, Entry entry) throws FeedServerAdapterException {
    if (AclResult.ACCESS_GRANTED != getAclValidator().canCreateEntry(request)) {
      throw new FeedServerAdapterException(FeedServerAdapterException.Reason.NOT_AUTHORIZED,
          "Access Denied");
    }
    return super.createEntry(request, entry);
  }

  @Override
  public void deleteEntry(RequestContext request, Object entryId) throws FeedServerAdapterException {
    if (AclResult.ACCESS_GRANTED != getAclValidator().canDeleteEntry(request, entryId)) {
      throw new FeedServerAdapterException(FeedServerAdapterException.Reason.NOT_AUTHORIZED,
          "Access Denied");
    }
    super.deleteEntry(request, entryId);
  }

  private AclValidator getAclValidator() {
    return getConfiguration().getServerConfiguration().getGolbalServerConfiguration()
        .getAclValidator();
  }
}
