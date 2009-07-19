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
package com.google.feedserver.resource;

/**
 * ACL bean / resource
 */
public class Acl {
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