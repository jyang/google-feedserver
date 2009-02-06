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

import java.util.HashMap;
import java.util.Map;

/**
 * A pool to store the backed server connectores. So that they can be re-used in
 * different adapter instances.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public class AdapterBackendPool<T> {

  private Map<String, T> clientMap;

  public AdapterBackendPool() {
    clientMap = new HashMap<String, T>();
  }

  public void addClient(String id, T client) {
    clientMap.put(id, client);
  }

  public T getClient(String id) {
    return clientMap.get(id);
  }

  protected Map<String, T> getClientMap() {
    return clientMap;
  }
}
