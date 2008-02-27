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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class LdapConfigParser {
  private Document configDocument;
  private HashMap<String, LdapOperation> operationMap = new HashMap();
  
  static Logger logger = Logger.getLogger(LdapConfigParser.class.getName());
  
  public LdapConfigParser(Reader input) {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      try {
        InputSource is = new InputSource(input);
        configDocument = builder.parse(is);
        NodeList operationList = configDocument.getElementsByTagName(
            LdapConfigConstants.OPERATION_NODE);
        for(int i = 0; i < operationList.getLength(); i++) {
          LdapOperation operation = new LdapOperation(
              (Element) operationList.item(i), configDocument);
          logger.info("Operation " + i + " " + operation);
          operationMap.put(operation.getName(), operation);
        }
      } catch (SAXException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } catch (ParserConfigurationException e) {
      // TODO throw a config parser exceptions
      e.printStackTrace();
    }
  }
  
  public LdapFeedConfiguration getConfiguration() {
    return new LdapFeedConfiguration(operationMap);
  }
}