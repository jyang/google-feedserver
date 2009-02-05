/**
 * Copyright 2007 Google Inc.
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

package com.google.feedserver.adapter;

import com.google.feedserver.config.FeedConfiguration;

import org.apache.abdera.Abdera;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.io.IOException;
import java.lang.reflect.Constructor;

public class AdapterManager {
  public static Logger logger =
    Logger.getLogger(AdapterManager.class.getName());

  protected static AdapterManager adapterManager = null;

  // maps a feed id to an adapter instance
  protected Map<String, Adapter> adapterInstanceMap =
      new HashMap<String, Adapter>();

  protected Abdera abdera;

  public AdapterManager(Abdera abdera) {
    this.abdera = abdera;
  }

  public Adapter getAdapter(String feedId) throws IOException {
    if (containsAdapter(feedId)){
      return adapterInstanceMap.get(feedId);      
    }
    FeedConfiguration feedConfiguration
        = FeedConfiguration.getFeedConfiguration(feedId);
    if (null == feedConfiguration) {
      // Configuration for this feed is missing.
      return null;
    }
    return createAdapterInstance(feedConfiguration);
  }

  public boolean containsAdapter(String feedId) {
    return adapterInstanceMap.containsKey(feedId);
  }

  @SuppressWarnings("unchecked")
  protected synchronized Adapter createAdapterInstance(
       FeedConfiguration feedConfiguration) {
    Adapter adapter = adapterInstanceMap.get(feedConfiguration.getFeedId());
    if (adapter != null) {
      return adapter;
    }

    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Class<?> adapterClass;
    try {
      adapterClass = cl.loadClass(
           feedConfiguration.getAdapterClassName());
    } catch (ClassNotFoundException e) {
      // The adapter was not found
      return null;
    }
     
    Constructor[] ctors = adapterClass.getConstructors();
    for (Constructor element : ctors) {
      logger.finest("Public constructor found: " +
           element.toString());
    }
    
    Constructor<?> c;
    try {
      c = adapterClass.getConstructor(new Class[] {Abdera.class,
           FeedConfiguration.class});
    } catch (SecurityException e) {
      return null;
    } catch (NoSuchMethodException e) {
      // The adapter does not have a valid constructor
      return null;
    }
    
    c.setAccessible(true);
     try {
      adapter = (Adapter) c.newInstance(abdera, feedConfiguration);
    } catch (Exception e) {
      // The adapter does not have a valid constructor
      return null;
    }
     // put this adapter instance in adapterInstanceMap
     adapterInstanceMap.put(feedConfiguration.getFeedId(), adapter);
     return adapter;
   }
}
