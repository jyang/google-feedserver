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


import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class LdapQueryResponse {
  private HashMap<String, String> attributeMap = new HashMap<String, String>();
  private String dn;
  
  public LdapQueryResponse(String dn) {
    this.dn = dn;
  }
  
  public String getDn() {
    return dn;
  }
  
  public String getAttribute(String name) {
    return attributeMap.get(name);    
  }
  
  public void addAttribute(String name, String value) {
    attributeMap.put(name, value);
  }
  
  public Iterator<String> getAttributeIterator() {
    Set<String> set = attributeMap.keySet();
    return set.iterator();
  }
  
}
