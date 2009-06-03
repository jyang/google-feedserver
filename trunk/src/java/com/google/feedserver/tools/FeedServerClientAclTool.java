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

package com.google.feedserver.tools;

import com.google.feedserver.client.FeedServerClient;
import com.google.feedserver.util.CommonsCliHelper;
import com.google.feedserver.util.FeedServerClientException;
import com.google.gdata.util.AuthenticationException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * FeedServer client tool for setting ACLs.
 *
 * -url <aclFeedUrl> -resource <feedUrl|feedUrl/entryName> -op setAcl
 * -acl (<crud><+|-><principal:...>,)*
 *
 * Special principals are:
 *   For per domain resources:
 *     DOMAIN_USERS: any user in the domain
 *     DOMAIN_ADMIN: any administrator in the domain
 *     *: anyone
 *   For per user resources:
 *     DOMAIN_INDIVIDUAL: any user in the domain
 *     ANY_INDIVIDUAL: any user him/herself
 *     *: anyone
 *
 * Example:
 *   -url http://feedserver-enterprise.googleusercontent.com/a/example.com/g/acl - resource Contact
 *   -op setAcl -acl crud+admin@example.com:gadget-dev@example.com,r+DOMAIN_USERS,
 *   crud-untrusted@example.com:untrusted-group@example.com
 */
public class FeedServerClientAclTool extends FeedServerClientTool {

  public static final String OPERATION_SET_ACL = "setAcl";

  public static String resource_FLAG = null;
  public static String resource_HELP = "resource name whose ACL to change " +
      "(e.g. Contact or Contact/john.doe";

  public static String acl_FLAG = null;
  public static String acl_HELP = "Access control list";

  // ACL data model
  public static class ResourceInfo {
    public static final String FEED = "feed";
    public static final String ENTRY = "entry";

    protected String resourceRule;
    protected String resourceType;

    public ResourceInfo() {
    }

    public ResourceInfo(String resourceRule, String resourceType) {
      setResourceRule(resourceRule);
      setResourceType(resourceType);
    }

    public String getResourceRule() {
      return resourceRule;
    }

    public void setResourceRule(String resourceRule) {
      this.resourceRule = resourceRule;
    }

    public String getResourceType() {
      return resourceType;
    }

    public void setResourceType(String resourceType) {
      this.resourceType = resourceType;
    }
  }

  public static class AuthorizedEntity {
    public static final String CREATE = "create";
    public static final String RETRIEVE = "retrieve";
    public static final String UPDATE = "update";
    public static final String DELETE = "delete";

    protected String operation;
    protected String[] entities;

    public AuthorizedEntity() {
      entities = new String[0];
    }

    public AuthorizedEntity(String operation, String[] entities) {
      setOperation(operation);
      setEntities(entities);
    }

    public String getOperation() {
      return operation;
    }

    public void setOperation(String operation) {
      this.operation = operation;
    }

    public String[] getEntities() {
      return entities;
    }

    public void setEntities(String[] entities) {
      this.entities = entities == null ? new String[0] : entities;
    }

    public String lookupOperation(char op) {
      switch(op) {
        case 'c': return CREATE;
        case 'r': return RETRIEVE;
        case 'u': return UPDATE;
        case 'd': return DELETE;
        default: return null;
      }
    }
  }

  public static class Acl {
    protected String name;
    protected ResourceInfo resourceInfo;
    protected AuthorizedEntity[] authorizedEntities;

    public Acl() {
      authorizedEntities = new AuthorizedEntity[0];
    }

    public Acl(String name, ResourceInfo resourceInfo, AuthorizedEntity[] authorizedEntities) {
      setName(name);
      setResourceInfo(resourceInfo);
      setAuthorizedEntities(authorizedEntities);
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public ResourceInfo getResourceInfo() {
      return resourceInfo;
    }

    public void setResourceInfo(ResourceInfo resourceInfo) {
      this.resourceInfo = resourceInfo;
    }

    public AuthorizedEntity[] getAuthorizedEntities() {
      return authorizedEntities;
    }

    public void setAuthorizedEntities(AuthorizedEntity[] authorizedEntities) {
      this.authorizedEntities =
          authorizedEntities == null ? new AuthorizedEntity[0] : authorizedEntities;
    }
  }
  // ACL data model

  public static final void main(String[] args) throws MalformedURLException,
      AuthenticationException, FeedServerClientException, IOException {
    new FeedServerClientAclTool().run(args);
  }

  @Override
  protected void processRequest(CommonsCliHelper cliHelper) throws FeedServerClientException,
      MalformedURLException, IOException, AuthenticationException {
    if (OPERATION_SET_ACL.equals(op_FLAG)) {
      getUserCredentials();
      List<Acl> existingAcls = getAcl(resource_FLAG);
      if (existingAcls.size() > 1) {
        printError("More than 1 ACL per resource not supported by this client tool");
      } else {
        setAcl(url_FLAG, existingAcls, acl_FLAG);
      }
    } else {
      super.processRequest(cliHelper);
    }
  }

  /**
   * Gets all the ACLs of a resource.
   * @param resourceName Name of resource
   * @return List of ACLs for the resource
   * @throws MalformedURLException
   * @throws FeedServerClientException
   */
  protected List<Acl> getAcl(String resourceName) throws MalformedURLException,
      FeedServerClientException {
    FeedServerClient<Acl> feedServerClient = new FeedServerClient<Acl>(
        this.feedServerClient.getService(), Acl.class);
    String aclFeedUrl = url_FLAG + "?bq=[resourceRule=" + resourceName + "]";
    List<Acl> aclIds = feedServerClient.getEntities(new URL(aclFeedUrl));

    List<Acl> acls = new ArrayList<Acl>(aclIds.size());
    for (Acl aclId: aclIds) {
      URL aclEntryUrl = new URL(url_FLAG + '/' + aclId.getName());
      acls.add(feedServerClient.getEntity(aclEntryUrl));
    }
    return acls;
  }

  /**
   * Sets ACLs of a resource.
   * @param url URL of resource to set ACLs
   * @param acls Existing ACLs to change
   * @param aclDef ACL change definitions (<crud><+|-><principal:...>,)*
   * @throws MalformedURLException
   * @throws FeedServerClientException
   */
  protected void setAcl(String url, List<Acl> acls, String aclDef) throws MalformedURLException,
      FeedServerClientException {
    String[] authorizedEntitiesDefs = aclDef.split(",");
    List<String> addAuthorizedEntities = new ArrayList<String>();
    List<String> removeAuthorizedAuthities = new ArrayList<String>();
    for (String authorizedEntitiesDef: authorizedEntitiesDefs) {
      String modifier = authorizedEntitiesDef.indexOf('+') > 0 ? "\\+" : "-";
      String[] opEntities = authorizedEntitiesDef.split(modifier);
      if (opEntities.length != 2) {
        continue;
      }
      String[] entities = opEntities[1].split(":");
      for (int i = 0; i < opEntities[0].length(); i++) {
        char op = opEntities[0].charAt(i);
        for (String entity: entities) {
          if ("\\+".equals(modifier)) {
            addAcl(acls, op, entity);
          } else {
            removeAcl(acls, op, entity);
          }
        }
      }
    }

    FeedServerClient<Acl> feedServerClient = new FeedServerClient<Acl>(
        this.feedServerClient.getService(), Acl.class);
    for (Acl acl: acls) {
      if (acl.getAuthorizedEntities().length == 0) {
        printError("Error: cannot have empty authorized entities");
      } else {
        if (acl.getName() == null) {
          feedServerClient.insertEntity(new URL(url), acl);
        } else {
          feedServerClient.updateEntity(new URL(url), acl);
        }
      }
    }
  }

  /**
   * Adds a principal for an operation to ACLs.
   * @param existingAcls ACLs to add to
   * @param op Operation of interest
   * @param principalToAdd Principal of interest
   */
  protected void addAcl(List<Acl> existingAcls, char op, String principalToAdd) {
    removeAcl(existingAcls, op, principalToAdd);

    Acl firstAcl = (existingAcls.size() > 0) ? existingAcls.get(0) : new Acl();
    if (existingAcls.size() == 0) {
      existingAcls.add(firstAcl);
    }

    boolean opFound = false;
    if (firstAcl.getAuthorizedEntities() != null) {
      for (AuthorizedEntity authorizedEntity: firstAcl.getAuthorizedEntities()) {
        if (authorizedEntity.getOperation().charAt(0) == op) {
          opFound = true;
          String[] currentPrincipals = authorizedEntity.getEntities();
          if (currentPrincipals == null) {
            authorizedEntity.setEntities(new String[]{principalToAdd});
          } else {
            String[] updatedPrincipals = new String[currentPrincipals.length + 1];
            for (int i = 0; i < currentPrincipals.length; i++) {
              updatedPrincipals[i] = currentPrincipals[i];
            }
            updatedPrincipals[currentPrincipals.length] = principalToAdd;
            authorizedEntity.setEntities(updatedPrincipals);
          }
        }
      }
    }

    if (!opFound) {
      int currentLength = firstAcl.getAuthorizedEntities().length;
      AuthorizedEntity[] updatedAuthorizedEntities =
          new AuthorizedEntity[currentLength + 1];
      for (int i = 0; i < currentLength; i++) {
        updatedAuthorizedEntities[i] = firstAcl.getAuthorizedEntities()[i];
      }
      
      AuthorizedEntity authorizedEntity = new AuthorizedEntity();
      authorizedEntity.setOperation(authorizedEntity.lookupOperation(op));
      authorizedEntity.setEntities(new String[]{principalToAdd});
      updatedAuthorizedEntities[currentLength] = authorizedEntity;
      firstAcl.setAuthorizedEntities(updatedAuthorizedEntities);
    }
  }

  /**
   * Removes a principal for an operation from ACLs.
   * @param existingAcls ACLs to remove from
   * @param op Operation of interest
   * @param principalToRemove Principal of interest
   */
  protected void removeAcl(List<Acl> existingAcls, char op, String principalToRemove) {
    for (Acl existingAcl: existingAcls) {
      if (existingAcl.getAuthorizedEntities() != null) {
        List<AuthorizedEntity> updatedAuthorizedEntities = new ArrayList<AuthorizedEntity>();
        for (AuthorizedEntity authorizedEntity: existingAcl.getAuthorizedEntities()) {
          if (authorizedEntity.getOperation().charAt(0) == op) {
            List<String> updatedPrincipals = new ArrayList<String>();
            if (authorizedEntity.getEntities() != null) {
              for (String principal: authorizedEntity.getEntities()) {
                if (!principal.equals(principalToRemove)) {
                  updatedPrincipals.add(principal);
                }
              }
              if (updatedPrincipals.size() != 0) {
                authorizedEntity.setEntities(
                    updatedPrincipals.toArray(new String[updatedPrincipals.size()]));
                updatedAuthorizedEntities.add(authorizedEntity);
              }
            }
          } else {
            // not operation of interest
            updatedAuthorizedEntities.add(authorizedEntity);
          }
        }
        existingAcl.setAuthorizedEntities(updatedAuthorizedEntities.toArray(
            new AuthorizedEntity[updatedAuthorizedEntities.size()]));
      }
    }
  }
}
