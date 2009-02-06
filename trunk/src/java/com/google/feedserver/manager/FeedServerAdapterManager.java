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
import com.google.feedserver.config.PerNamespaceServerConfiguration;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.provider.managed.CollectionAdapterManager;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;
import org.apache.abdera.protocol.server.provider.managed.ServerConfiguration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Hosted version of {@link CollectionAdapterManager}
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 */
public class FeedServerAdapterManager extends CollectionAdapterManager {

  public FeedServerAdapterManager(Abdera abdera, ServerConfiguration config) {
    super(abdera, config);
  }

  /**
   * Gets a {@link CollectionAdapter} for the specified feed based on the
   * {@link FeedConfiguration}
   * 
   * @param feedId The feed to get a collection adapter for
   */
  @Override
  public AbstractManagedCollectionAdapter getAdapter(String feedId) throws Exception {
    FeedConfiguration feedConfiguration = config.loadFeedConfiguration(feedId);
    return createWrappedAdapter(feedConfiguration, abdera);
  }

  /**
   * Creates a wrapper around {@code targetAdapter}.
   * 
   * @param wrapperClassName fully qualified class name of wrapper.
   * @param targetAdapter the adapter to be wrapped.
   * @param wrapperConfig config data for wrapper.
   * 
   * @return targetAdapter wrapped with {@code wrapperClassName}.
   * @throws ClassNotFoundException
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws IllegalArgumentException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  public static AbstractManagedCollectionAdapter createWrapper(String wrapperClassName,
      AbstractManagedCollectionAdapter targetAdapter, String wrapperConfig)
      throws ClassNotFoundException, SecurityException, NoSuchMethodException,
      IllegalArgumentException, InstantiationException, IllegalAccessException,
      InvocationTargetException {
    return (AbstractManagedCollectionAdapter) createAdapter(wrapperClassName, new Class[] {
        AbstractManagedCollectionAdapter.class, String.class}, targetAdapter, wrapperConfig);
  }

  /**
   * Creates {@link CollectionAdapter} for feed with given
   * {@link FeedConfiguration}. This also wraps the adapter with all the needed
   * wrappers.
   * 
   * @param config {@link FeedConfiguration} of the feed.
   * @param abdera {@link Abdera} instance used in the server.
   * 
   * @return Wrapped {@link CollectionAdapter} for the feed.
   * 
   * @throws ClassNotFoundException
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws IllegalArgumentException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  public static AbstractManagedCollectionAdapter createWrappedAdapter(FeedConfiguration config,
      Abdera abdera) throws ClassNotFoundException, SecurityException, NoSuchMethodException,
      IllegalArgumentException, InstantiationException, IllegalAccessException,
      InvocationTargetException {
    AbstractManagedCollectionAdapter adapter = createManagedAdapter(config, abdera);

    if (config.getServerConfiguration() instanceof PerNamespaceServerConfiguration) {
      String wrapperManagerClass =
          ((PerNamespaceServerConfiguration) config.getServerConfiguration())
              .getWrapperManagerClassName();
      return (AbstractManagedCollectionAdapter) createAdapter(wrapperManagerClass,
          new Class[] {AbstractManagedCollectionAdapter.class}, adapter);
    } else {
      return adapter;
    }
  }

  /**
   * Creates {@link AbstractManagedCollectionAdapter} for the feed with given
   * {@link FeedConfiguration}.
   * 
   * @param config {@link FeedConfiguration} of the feed.
   * @param abdera {@link Abdera} instance used in the server.
   * 
   * @return {@link CollectionAdapter} for the feed.
   * 
   * @throws ClassNotFoundException
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws IllegalArgumentException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  public static AbstractManagedCollectionAdapter createManagedAdapter(FeedConfiguration config,
      Abdera abdera) throws ClassNotFoundException, SecurityException, NoSuchMethodException,
      IllegalArgumentException, InstantiationException, IllegalAccessException,
      InvocationTargetException {
    return (AbstractManagedCollectionAdapter) createAdapter(config.getAdapterClassName(),
        new Class[] {Abdera.class, FeedConfiguration.class}, abdera, config);
  }

/**
   * Creates {@link CollectionAdapter}.
   *
   * @param adapterType fully qualified class name for the adapter.
   * @param paramClasses the class's for parameters in the constructor
   *                     of the adapter (in order).
   * @param objects the objects that need to be passed to the constructor
   *                (in order).
   * @return an instance of type {@link adapterType)
   * @throws ClassNotFoundException
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws IllegalArgumentException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  @SuppressWarnings("unchecked")
  public static synchronized CollectionAdapter createAdapter(String adapterType,
      Class[] paramClasses, Object... objects) throws ClassNotFoundException, SecurityException,
      NoSuchMethodException, IllegalArgumentException, InstantiationException,
      IllegalAccessException, InvocationTargetException {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Class<?> adapterClass = cl.loadClass(adapterType);
    Constructor<?>[] ctors = adapterClass.getConstructors();
    for (Constructor<?> element : ctors) {
      logger.finest("Public constructor found: " + element);
    }
    Constructor<?> c = adapterClass.getConstructor(paramClasses);
    c.setAccessible(true);
    CollectionAdapter adapterInstance = (CollectionAdapter) c.newInstance(objects);
    return adapterInstance;
  }
}
