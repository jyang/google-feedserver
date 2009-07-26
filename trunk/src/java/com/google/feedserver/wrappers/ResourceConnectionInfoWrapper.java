/*
 * Copyright 2009 Google Inc.
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
package com.google.feedserver.wrappers;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.abdera.protocol.server.RequestContext;
import org.xml.sax.SAXException;

import com.google.feedserver.adapters.AbstractManagedCollectionAdapter;
import com.google.feedserver.adapters.FeedServerAdapterException;
import com.google.feedserver.resource.Acl;
import com.google.feedserver.resource.AuthorizedEntity;
import com.google.feedserver.util.FileSystemConfigStoreUtil;
import com.google.feedserver.util.FileUtil;
import com.google.feedserver.util.XmlUtil;

/**
 * An ACL wrapper using the ResourceConnectionInfo configuration format
 * Wrapper config:
 * <entity>
 *   <acl repeatable="true">
 *     <name>don't care</name>
 *     <resourceInfo>
 *       <resourceRule>"/" for feed|"/{entryId}" for entry</resourceRule>
 *       <resourceType>don't care</resourceType>
 *     </resourceInfo>
 *     <authorizedEntities repeatable="true">
 *       <operation>create|retrieve|update|delete</operation>
 *       <entities repeatable="true">john.doe@example.com</entities>
 *       <entities repeatable="true">jane.doe@example.com</entities>
 *     </authorizedEntities>
 *   </acl>
 * </entity>
 */
public class ResourceConnectionInfoWrapper extends AccessControlWrapper {

  private static Logger logger = Logger.getLogger(ResourceConnectionInfoWrapper.class.getName());

  public static final String DOMAIN_USERS = "DOMAIN_USERS";
  public static final String ANYONE = "*";

  public static class Config {
    protected Acl[] acl;
    public Acl[] getAcl() {
      return acl;
    }
    public void setAcl(Acl[] acl) {
      this.acl = acl;
    }
  }

  protected XmlUtil xmlUtil;

  /**
   * resourcePath (/, /entryId) -> operation -> principals
   */
  protected Map<String, Map<String, Set<String>>> resourceAclMap;

  public ResourceConnectionInfoWrapper(AbstractManagedCollectionAdapter target,
      String wrapperConfig) throws IllegalArgumentException, IntrospectionException,
      IllegalAccessException, InvocationTargetException, SAXException, IOException,
      ParserConfigurationException {
    super(target, wrapperConfig);

    xmlUtil = new XmlUtil();
    Config config = new Config();
    if (wrapperConfig.startsWith(FileSystemConfigStoreUtil.FILE_INDICATOR)) {
      wrapperConfig = loadWrapperConfig(wrapperConfig.substring(1));
    }
    xmlUtil.convertXmlToBean(wrapperConfig, config);
    
    resourceAclMap = new HashMap<String, Map<String, Set<String>>>();
    for (Acl acl: config.getAcl()) {
      Map<String, Set<String>> operationPrincipalsMap = new HashMap<String, Set<String>>();
      for (AuthorizedEntity ae: acl.getAuthorizedEntities()) {
        Set<String> principals = new HashSet<String>();
        for (String principal: ae.getEntities()) {
          principals.add(principal);
        }
        operationPrincipalsMap.put(ae.getOperation(), principals);
      }
      String path = acl.getResourceInfo().getResourceRule();
      resourceAclMap.put(path, operationPrincipalsMap);
    }

    logger.info("ResourceConnectionInfoWrapper config=" + resourceAclMap);
  }

  protected String loadWrapperConfig(String fileName) throws IOException {
	String filePath = "conf/feedserver/" + this.getNameSpace() + "/AdapterConfig/acl/" + fileName;
    return new FileUtil().readFileContents(filePath);
  }

  @Override
  protected void doCheckAccess(String operation, RequestContext request, Object entryId)
      throws FeedServerAdapterException {
    String resourcePath = entryId == null ? "/" : "/" + entryId;
    Map<String, Set<String>> operationPrincipalsMap = resourceAclMap.get(resourcePath);
    if (operationPrincipalsMap == null) {
      // if we checked entry level ACL, check feed level now
      try {
        checkAccess(operation, request, null);
        return;
      } catch(FeedServerAdapterException e) {
        logger.info("checkAccess: access denied");
        throw new FeedServerAdapterException(
            FeedServerAdapterException.Reason.NOT_AUTHORIZED, "No ACL defined for '" +
                operation + "," + resourcePath + "'; " + e.getMessage());
      }
    }

    Set<String> principals = operationPrincipalsMap.get(operation);
    logger.info("checkAccess: principals=" + principals);
    if (principals == null) {
      logger.info("checkAccess: access denied");
      throw new FeedServerAdapterException(
          FeedServerAdapterException.Reason.NOT_AUTHORIZED, "No ACL defined for '" +
              operation + "," + resourcePath + "'");
    }

    String userEmail = getUserEmailForRequest(request);
    logger.info("checkAccess: userEmail=" + userEmail);

    if (userEmail == null) {
      // user unauthenticated
      if (!principals.contains(ANYONE)) {
        throw new FeedServerAdapterException(
            FeedServerAdapterException.Reason.NOT_AUTHORIZED, "viewer unauthenticated and " +
                ANYONE + " not allowed for '" + operation + "," + resourcePath + "'");
      }
    } else {
      // user authenticated
      if (!principals.contains(userEmail) &&
          !(principals.contains(DOMAIN_USERS) && userEmail.endsWith(getNameSpace()))) {
        logger.info("checkAccess: access denied");
        throw new FeedServerAdapterException(
            FeedServerAdapterException.Reason.NOT_AUTHORIZED, "viewer '" + userEmail +
                "' not on list of principals for '" + operation + "," + resourcePath + "'");
      }
    }

    logger.info("checkAccess: access granted");
  }
}
