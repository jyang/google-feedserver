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

import com.google.feedserver.config.AdapterConfiguration;
import com.google.feedserver.config.FeedConfiguration;
import com.google.feedserver.adapter.ldap.LdapFeedConfiguration;
import com.google.feedserver.adapter.ldap.LdapOperation;
import com.google.feedserver.adapter.ldap.LdapQueryException;
import com.google.feedserver.adapter.ldap.LdapQueryFactory;
import com.google.feedserver.adapter.ldap.LdapQueryRequest;
import com.google.feedserver.adapter.ldap.LdapQueryResponse;
import com.google.feedserver.adapter.ldap.LdapSearchQueryConfig;
import com.google.feedserver.adapter.ldap.LdapSearchResultsConfig;
import com.google.feedserver.adapter.ldap.LdapServerConfigParser;
import com.google.feedserver.adapter.ldap.LdapServerConfiguration;
import com.google.feedserver.adapter.ldap.LdapConfigurationException;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class LdapAdapter extends AbstractAdapter implements Adapter {
  private static final String ENTRY_AUTHOR = "feedserver";
  private static final String ENTRY_TITLE = "ldap entry title";

  public static Logger logger = Logger.getLogger(SampleAdapter.class.getName());
  protected HashMap<String, byte[]> entries = new HashMap<String, byte[]>();
  protected LdapServerConfigParser parser;
  protected LdapFeedConfiguration ldapFeedConfiguration;
  protected LdapServerConfiguration ldapServerConfiguration;
  protected LdapOperation ldapFeedOperation;
  protected LdapQueryFactory queryFactory = LdapQueryFactory.getInstance();

  public LdapAdapter(Abdera abdera, FeedConfiguration feedConfiguration) {
    super(abdera, feedConfiguration);
    logger.info("Feed config location is " +
        feedConfiguration.getFeedConfigLocation());
    try{
      AdapterConfiguration adapterConfiguration = 
          feedConfiguration.getAdapterConfiguration();
      logger.info("Adapter configuration " + 
          adapterConfiguration.getAdapterConfigAsReader());
      parser = new LdapServerConfigParser(
          adapterConfiguration.getAdapterConfigAsReader());
      ldapFeedConfiguration = parser.getFeedConfiguration();
      ldapServerConfiguration = parser.getServerConfiguration();
      ldapFeedOperation = ldapFeedConfiguration.getOperationEndsWith("feed");
    } catch (LdapConfigurationException ex) {
      ex.printStackTrace(System.err);
    } catch (FileNotFoundException e) {
      e.printStackTrace(System.err);
    } catch (IOException ioException) {
      ioException.printStackTrace(System.err);
    }
  }

  public Feed getFeed() throws Exception {
    Feed feed = createFeed();

    logger.info("getFeed");
    LdapSearchQueryConfig queryConfig =
        ldapFeedOperation.getSearchQueryConfig();
    
    LdapSearchResultsConfig resultsConfig =
        ldapFeedOperation.getSearchResultsConfig();
    
    LdapQueryRequest request = queryFactory.newLdapQueryRequest(
        queryConfig.getBaseDn(), queryConfig.getScope(),
        queryConfig.getFilter(), queryConfig.getRequiredAttributes(),
        ldapServerConfiguration);
    
    List<LdapQueryResponse> responses = null;
    try {
      responses = request.processRequest();
      for (LdapQueryResponse response : responses) {
        Entry entry = createEntryFromResponse(response, resultsConfig);
        //addEditLinkToEntry(entry);
        feed.addEntry(entry);
      }
      logger.info("THe entries added are " + feed);
    } catch (LdapQueryException ex) {
      ex.printStackTrace(System.err);
      return null;
    }
    return feed;
  }

  public Entry getEntry(Object entryId)  throws Exception {
    return null;
  }

  public Entry createEntry(Entry entry) throws Exception {
    return entry;
  }

  public Entry updateEntry(Object entryId, Entry entry) throws Exception {
    return entry;
  }

  public boolean deleteEntry(Object entryId) throws Exception {
    return true;
  }



  protected Entry retrieveEntry(String entryId) throws Exception {
    return null;
  }
  
  protected Entry createEntryFromResponse(LdapQueryResponse response,
      LdapSearchResultsConfig results) throws Exception {
    Entry entry = abdera.newEntry();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.newDocument();
    Element entity = doc.createElement("entity");
    doc.appendChild(entity);
    
    Iterator<String> iterator = response.getAttributeIterator();
    while (iterator.hasNext()) {
      String attribute = iterator.next();
      String result = results.getMapString(attribute);
      logger.info("Attribute " + attribute + ":" + result + ":" +
          response.getAttribute(attribute));
      if (result != null) {
        if ("id".equals(result)) {
          entry.setId(response.getAttribute(attribute));
        } else if ("title".equals(result)) {
          entry.setTitle(response.getAttribute(attribute));
        } else if ("updated".equals(result)) {
          //entry.setUpdated((Date) value); 
        } else if ("link".equals(result)) {
          entry.addLink(response.getAttribute(attribute));
        } else {
          logger.info("Attribute " + attribute + ":" + result + ":" +
              response.getAttribute(attribute));
          Element node = doc.createElement(result);
          node.appendChild(doc.createTextNode(
              response.getAttribute(attribute)));
          entity.appendChild(node);
          logger.info("Attribute after " + attribute + ":" + result + ":" +
              response.getAttribute(attribute));
        }
      } else { // result != null
        Element node = doc.createElement(attribute);
        node.appendChild(doc.createTextNode(
            response.getAttribute(attribute)));
        entity.appendChild(node);
      }
      logger.info("Attribute " + attribute + ":" + result + ":" +
          response.getAttribute(attribute));
    }
    if (entry.getUpdated() == null) {
      entry.setUpdated(new Date());
    }
    if (entry.getAuthor() == null) {
      entry.addAuthor(ENTRY_AUTHOR);
    }
    if (entry.getTitle() == null) {
      entry.setTitle(ENTRY_TITLE);
    }
    entry.setContent(getDocumentAsXml(doc),"text/xml");
    logger.info("Entry on completion " + entry);
    return entry;
  }

  public static String getDocumentAsXml(Document doc)
      throws TransformerConfigurationException, TransformerException {
    DOMSource domSource = new DOMSource(doc);
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    java.io.StringWriter sw = new java.io.StringWriter();
    StreamResult sr = new StreamResult(sw);
    transformer.transform(domSource, sr);
    String str = sw.toString();
    logger.finest(str);
    return str;
  }

}
