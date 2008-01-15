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

import org.apache.abdera.Abdera;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;

public class AdapterManager {
  public static Logger logger =
    Logger.getLogger(AdapterManager.class.getName());

  protected static final String PROP_NAME_ADAPTER_CLASS = "adapterClassName";
  protected static final String PROPERTIES_PATH = "/feedserver/adapter/";
  protected static final String PROPERTIES_FILE_SUFFIX = ".properties";

  protected static AdapterManager adapterManager = null;

  // maps a feed id to an adapter instance
  protected Map<String, Adapter> adapterInstanceMap =
      new HashMap<String, Adapter>();

  protected Abdera abdera;

  public AdapterManager(Abdera abdera) {
    this.abdera = abdera;
  }

  public Adapter getAdapter(String feedId) throws Exception {
    Adapter adapter = adapterInstanceMap.get(feedId);
    if (adapter != null) {
      return adapter;
    }

    // load the feed properties file
    Properties properties = loadFeedInfo(feedId);
    String className = properties.getProperty(PROP_NAME_ADAPTER_CLASS);
    if (className == null) {
      logger.warning("property '" + PROP_NAME_ADAPTER_CLASS +
          "' not found for feed '" + feedId + "'");
      throw new RuntimeException();
    }

    return createAdapterInstance(feedId, className, properties);
  }

   protected Properties loadFeedInfo(String feedId)
      throws Exception {
     String fileName = PROPERTIES_PATH + feedId + PROPERTIES_FILE_SUFFIX;
     InputStream in = this.getClass().getResourceAsStream(fileName);
     if (in == null) {
       throw new FileNotFoundException();
     }
     Properties props = new Properties();
     props.load(in);
     in.close();
     return props;
}

   protected synchronized Adapter createAdapterInstance(String feedId,
      String className, Properties properties) throws Exception {

     Adapter adapter = adapterInstanceMap.get(feedId);
     if (adapter != null) {
       return adapter;
     }

     ClassLoader cl = Thread.currentThread().getContextClassLoader();
     Class<?> adapterClass = cl.loadClass(className);
     Constructor[] ctors = adapterClass.getConstructors();
     for (Constructor element : ctors) {
         logger.finest("Public constructor found: " +
             element.toString());
     }
     Constructor<?> c = adapterClass.getConstructor(new Class[] {Abdera.class,
         Properties.class, String.class});
     c.setAccessible(true);
     Adapter adapterInstance = (Adapter) c.newInstance(abdera, properties,
         feedId);

     // put this adapter instance in adapterInstanceMap
     adapterInstanceMap.put(feedId, adapterInstance);
     return adapterInstance;
   }
}
