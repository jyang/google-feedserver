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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class LdapSearchQueryConfig {
  private String baseDn;
  private String scope;
  private String filter;
  private String attributes[] = null;
  
  LdapSearchQueryConfig(Node node) {
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      Element search = (Element) node;
      baseDn = search.getAttribute(LdapConfigConstants.SEARCH_BASE);
      scope = search.getAttribute(LdapConfigConstants.SEARCH_SCOPE);
      NodeList filters = search.getElementsByTagName(
          LdapConfigConstants.FILTER_NODE);
      if (filters.getLength() > 0)
        processFilter((Element)filters.item(0));
      NodeList attrs = search.getElementsByTagName(
          LdapConfigConstants.ATTRIBUTE_NODE);
      processAttributes(attrs);
    }
  }
  
  protected void processFilter(Element el) {
    Text node = (Text) el.getFirstChild();
    filter = node.getData();
  }
  
  protected void processAttributes(NodeList nodes) {
    attributes = new String[nodes.getLength()];
    for(int index = 0; index < nodes.getLength(); index++) {
      attributes[index] = ((Element)nodes.item(index)).getAttribute(
          LdapConfigConstants.ATTR_NAME);
    }
  }
  
  @Override
  public String toString() {
    String returnAttributes = "(";
    for(int i = 0; i < attributes.length; i++) {
      returnAttributes += attributes[i] + ",";
    }
    returnAttributes += ")";
    return ("Base: " + baseDn + ", scope:" + scope + ",filter:" + filter +
            "Attributes:" + returnAttributes);
  }
  
  public String getBaseDn() {
    return baseDn;
  }
  
  public String getScope() {
    return scope;
  }
  
  public String getFilter() {
    return filter;
  }
  
  public String[] getRequiredAttributes() {
    return attributes;
  }
}