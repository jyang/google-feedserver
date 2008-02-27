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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

public class LdapSearchResultsConfig {
  private HashMap<String, String> attrMap = new HashMap<String, String>();
  private String entryId;
  
  static Logger logger = Logger.getLogger(LdapSearchResultsConfig.class.getName());
  
  LdapSearchResultsConfig(Element el) {
    NodeList maps = el.getElementsByTagName(
        LdapConfigConstants.ATTRIBUTE_MAP_NODE);
    processAttributeMaps(maps);
    entryId = el.getAttribute(LdapConfigConstants.ID);
  }
  
  protected void processAttributeMaps(NodeList list) {
    for(int index = 0; index < list.getLength(); index++) {
      Element el = (Element) list.item(index);
      String ldapName = el.getAttribute(LdapConfigConstants.LDAP_NAME);
      String resultName = el.getAttribute(LdapConfigConstants.RESULT_NAME);
      attrMap.put(ldapName, resultName);
      attrMap.put(resultName, ldapName);
    }
  }
  
  @Override
  public String toString() {
    String returnMaps = "(";
    Set<String> keys = attrMap.keySet();
    Iterator<String> keyIterator = keys.iterator();
    while(keyIterator.hasNext()) {
      String key = keyIterator.next();
      returnMaps = key + ",";
    }
    returnMaps = "Results:" + returnMaps + ")";
    return returnMaps;
  }
  
  public String getMapString(String name) {
    return attrMap.get(name);
  }
}

