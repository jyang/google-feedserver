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

package com.google.feedserver.authentication;

import junit.framework.TestCase;

/**
 * Test cases for {@link SampleTokenManager}
 * 
 * @author rakeshs101981@gmail.com (Rakesh Shete)
 * 
 */
public class SampleTokenManagerTest extends TestCase {

  private SampleTokenManager tokenManager;

  @Override
  protected void setUp() throws Exception {
    tokenManager = new SampleTokenManager();
  }

  public void testGenerateAndValidateToken() {
    FakeHttpServletRequest request = new FakeHttpServletRequest();
    request.addRequestParameter("Email", "testuser");
    request.addRequestParameter("service", "testservice");


    String authZToken = null;

    try {
      authZToken = tokenManager.generateAuthzToken(request);
      assertNotNull("The authz token should not have been null", authZToken);
    } catch (TokenManagerException e) {
      fail("Exceptions encountered while generating authz token : " + e.getMessage());
      e.printStackTrace();
    }

    try {
      request = new FakeHttpServletRequest();
      request.setHeader("Authorization", "auth=" + authZToken);
      assertTrue("The authz-token should have been validated successfully ", tokenManager
          .validateAuthzToken(request));
    } catch (TokenManagerException e) {
      fail("Exceptions encountered while validating authz token : " + e.getMessage());
      e.printStackTrace();
    }


  }

}
