/**
 * Copyright 2008 Google Inc.
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

package com.google.feedserver.adapter.ldap;

import com.google.feedserver.adapter.ldap.LdapConfigurationException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

public class LdapFeedConfiguration {
  private HashMap<String, LdapOperation> operations = 
    new HashMap<String, LdapOperation>();
  
  public LdapFeedConfiguration(HashMap<String, LdapOperation> map) {
    operations = map;
  }
  
  public LdapOperation getOperation(String name) 
      throws LdapConfigurationException {
    if (operations.containsKey(name)) {
      return operations.get(name);
    } else {
      throw new LdapConfigurationException("Invalid operation name");
    }
  }
  
  public LdapOperation getOperationEndsWith(String endsWith) 
      throws LdapConfigurationException {
    Set<String> keySet = operations.keySet();
    Iterator<String> iterator = keySet.iterator();
    while (iterator.hasNext()) {
      String name = iterator.next();
      if (name.endsWith(endsWith)) {
        return getOperation(name);
      }
    }
    throw new LdapConfigurationException("Invalid operation wildcard");
  }
  
  public Iterator<Entry<String, LdapOperation>> getOperations() {
    Set<Entry<String, LdapOperation>> set =  operations.entrySet();
    return set.iterator();
  }
}
