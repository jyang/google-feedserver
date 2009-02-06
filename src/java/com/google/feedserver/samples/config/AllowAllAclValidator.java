/*
 * Copyright 2008 Google Inc.
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

package com.google.feedserver.samples.config;


import com.google.feedserver.config.AclValidator;

import org.apache.abdera.protocol.server.RequestContext;

/**
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public class AllowAllAclValidator implements AclValidator {

  @Override
  public AclResult canCreateEntry(RequestContext context) {
    return AclResult.ACCESS_GRANTED;
  }

  @Override
  public AclResult canDeleteEntry(RequestContext context, Object entryId) {
    return AclResult.ACCESS_GRANTED;
  }

  @Override
  public AclResult canPeek(RequestContext context) {
    return AclResult.ACCESS_GRANTED;
  }

  @Override
  public AclResult canRetrieveEntry(RequestContext context, Object entryId) {
    return AclResult.ACCESS_GRANTED;
  }

  @Override
  public AclResult canRetrieveFeed(RequestContext context) {
    return AclResult.ACCESS_GRANTED;
  }

  @Override
  public AclResult canUpdateEntry(RequestContext context, Object entryId) {
    return AclResult.ACCESS_GRANTED;
  }
}
