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

import com.google.feedserver.config.Configuration;
import com.google.feedserver.config.ServerConfiguration;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class LdapServerConfigParser extends Configuration {

  private LdapServerConfiguration serverConfig = new LdapServerConfiguration();
  private LdapFeedConfiguration feedConfig;
  
  public LdapServerConfigParser(String fileName) 
      throws LdapConfigurationException, FileNotFoundException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    InputSource is = new InputSource( new InputStreamReader(
        new FileInputStream(fileName)));
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();      
      try {
        Document document = builder.parse(is);
        processDocument(document);
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
  
  public LdapServerConfigParser(Reader reader) 
      throws LdapConfigurationException {
    BufferedReader bufReader = new BufferedReader(reader);
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    InputSource is = new InputSource(bufReader);
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();      
      try {
        Document document = builder.parse(is);
        processDocument(document);
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
  
  private void processDocument(Document document) 
      throws LdapConfigurationException {
    
    // we should expect atleast one Role
    NodeList roles = document.getElementsByTagName(
        LdapConfigConstants.ROLE_NODE);
    if (roles.getLength() > 0) {
      processRoleInformation((Element) roles.item(0));
    } else {
      throw new LdapConfigurationException("Role missing in config file");
    }
    
    // handle all the server configuration
    NodeList servers = document.getElementsByTagName(
        LdapConfigConstants.SERVER_NODE);
    processServerInformation(servers);
    
    // handle the configuration information about the queries for the file
    NodeList config = document.getElementsByTagName(
        LdapConfigConstants.CONFIG_NODE);
    if (config.getLength() <= 0)
      throw new LdapConfigurationException("Config file missing");
    processConfigFile((Element)config.item(0));
  }
  
  private void processRoleInformation(Element role) {
    serverConfig.setRoleDn(role.getAttribute(LdapConfigConstants.DN));
    serverConfig.setRolePassword(role.getAttribute(LdapConfigConstants.SECRET));
  }
  
  private void processServerInformation(NodeList servers) 
      throws LdapConfigurationException {
    if (servers.getLength() <= 0) 
      throw new LdapConfigurationException("No ldap server specified");
    for(int index = 0; index < servers.getLength(); index++) {
      Element server = (Element) servers.item(index);
      String address = server.getAttribute(LdapConfigConstants.IP);
      String port = server.getAttribute(LdapConfigConstants.PORT);
      String ssl = server.getAttribute(LdapConfigConstants.SSL);
      String maxConnections = server.getAttribute(LdapConfigConstants.MAX_CONN);
      short portValue = (short)Integer.parseInt(port);
      boolean useSsl = ("true".equals(ssl)) ? true : false;
      int maxConnectionValue = Integer.parseInt(maxConnections);
      try {
        LdapServer ldapServer = new LdapServer(
            address, portValue, useSsl, maxConnectionValue);
        serverConfig.addServer(ldapServer);
      } catch (UnknownHostException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        throw new LdapConfigurationException("Unknown host defined for" +  
            "ldap Server");
      }
    }
  }
  
  private void processConfigFile(Element config) 
      throws LdapConfigurationException {
    String fileName = config.getAttribute(LdapConfigConstants.FILE_NAME);
    try {
      ServerConfiguration serverConfig = ServerConfiguration.getInstance();
      String filePath = serverConfig.getAdapterConfigLocation() + fileName;
      System.out.println("Ldap Configuration from: " + filePath);
      LdapConfigParser parser = new LdapConfigParser(
          loadConfigFileAsReader(filePath));
      feedConfig = parser.getConfiguration();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new LdapConfigurationException("Invalid filename");
    }
  }
  
  public String toString() {
    return "Role: " + serverConfig.getRoleDn();
  }
  
  public LdapFeedConfiguration getFeedConfiguration() 
     throws LdapConfigurationException {
    if (feedConfig == null) {
      throw new LdapConfigurationException("Invalid feed configuration");
    }
    return feedConfig;
  }
  
  public LdapServerConfiguration getServerConfiguration()
      throws LdapConfigurationException {
    if (serverConfig == null)
      throw new LdapConfigurationException("Invalid server configuration");
    return serverConfig;
  }
}

