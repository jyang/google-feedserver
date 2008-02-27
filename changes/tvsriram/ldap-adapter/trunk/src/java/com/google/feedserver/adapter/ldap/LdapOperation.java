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

import com.google.feedserver.adapter.ldap.LdapConfigConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.logging.Logger;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class LdapOperation {
  private LdapSearchQueryConfig query;
  private LdapSearchResultsConfig result;
  private String name;

  static Logger logger = Logger.getLogger(LdapOperation.class.getName());

  public LdapOperation(Element el, Document config) {
    name = el.getAttribute(LdapConfigConstants.ATTR_NAME);
    NodeList queryReferences = el.getElementsByTagName(
        LdapConfigConstants.QUERY_REF_NODE);
    if (queryReferences.getLength() > 0) {
      processQueryReferences((Element) queryReferences.item(0), config);
    }

    NodeList resultReferences = el.getElementsByTagName(
        LdapConfigConstants.RESULT_REF_NODE);
    if (resultReferences.getLength() > 0) {
      processResultReferences((Element) resultReferences.item(0), config);
    }
  }
  
  protected void processQueryReferences(Element el, Document config) {
    String queryId = el.getAttribute(LdapConfigConstants.ID);
    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
    try {
      XPathExpression xpression = xpath.compile(
          "//" + LdapConfigConstants.SEARCH_NODE + "[@" + 
          LdapConfigConstants.ID + "=\"" + queryId + "\"]");
      NodeList queries = (NodeList) xpression.evaluate(config,
          XPathConstants.NODESET);
      if (queries.getLength() > 0) {
        query = new LdapSearchQueryConfig(queries.item(0));
      }
    } catch (XPathExpressionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  protected void processResultReferences(Element el, Document config) {
    String resultMapId = el.getAttribute(LdapConfigConstants.ID);
    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
    try {
      logger.fine("Result map id " + resultMapId);
      XPathExpression xpression = xpath.compile(
          "//" + LdapConfigConstants.RESULT_NODE + "[@" +
          LdapConfigConstants.ID + "=\"" + resultMapId + "\"]");
      NodeList results = (NodeList) xpression.evaluate(config,
          XPathConstants.NODESET);
      if (results.getLength() > 0) {
        result = new LdapSearchResultsConfig((Element)results.item(0));
      }
    } catch (XPathExpressionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public LdapSearchQueryConfig getSearchQueryConfig() 
      throws LdapConfigurationException {
    if (query == null) {
      throw new LdapConfigurationException("No query configured");
    }
    return query;
  }
  
  public LdapSearchResultsConfig getSearchResultsConfig()
      throws LdapConfigurationException {
    if (result == null) {
      throw new LdapConfigurationException("No results configured");
    }
    return result;
  }
  @Override
  public String toString() {
    return "Operation Details:" + query + result;
  }
  
  public String getName() {
    return name;
  }
}
