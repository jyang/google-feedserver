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
import com.google.feedserver.resource.Acl;
import com.google.feedserver.resource.AuthorizedEntity;
import com.google.feedserver.resource.ResourceInfo;
import com.google.feedserver.util.CommonsCliHelper;
import com.google.feedserver.util.FeedServerClientException;
import com.google.gdata.util.AuthenticationException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
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

  public static final void main(String[] args) {
    new FeedServerClientAclTool().run(args);
  }

  @Override
  protected void processRequest(CommonsCliHelper cliHelper) throws FeedServerClientException,
      MalformedURLException, IOException, AuthenticationException {
    if (resource_FLAG == null) {
      printError("resource missing (please use flag '-resource')");
    } else if (OPERATION_SET_ACL.equals(op_FLAG)) {
      getUserCredentials();
      List<Acl> existingAcls = getAcl(resource_FLAG);
      if (existingAcls.size() > 1) {
        printError("More than 1 ACL per resource not supported by this client tool");
      } else {
        setAcl(url_FLAG, resource_FLAG, existingAcls, acl_FLAG);
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
   * @param resource Resource to set ACLs for
   * @param acls Existing ACLs to change
   * @param aclDef ACL change definitions (<crud><+|-><principal:...>,)*
   * @throws MalformedURLException
   * @throws FeedServerClientException
   * @throws UnsupportedEncodingException 
   */
  protected void setAcl(String url, String resource, List<Acl> acls, String aclDef)
      throws MalformedURLException, FeedServerClientException, UnsupportedEncodingException {
    String[] authorizedEntitiesDefs = aclDef.split(",");
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
        delete(url + "/" + URLEncoder.encode(resource, "UTF-8"));
      } else {
        if (acl.getName() == null) {
          acl.setName(resource);
          acl.setResourceInfo(new ResourceInfo(resource, ResourceInfo.RESOURCE_TYPE_FEED));
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
