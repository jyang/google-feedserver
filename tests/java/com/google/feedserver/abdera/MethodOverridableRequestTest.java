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

package com.google.feedserver.abdera;

import com.google.feedserver.authentication.FakeHttpServletRequest;
import com.google.feedserver.server.servlet.MethodOverridableRequest;

import junit.framework.TestCase;

/**
 * Unit test for {@link MethodOverridableRequest}.
 * 
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 * 
 */

public class MethodOverridableRequestTest extends TestCase {

  private static final String POST_METHOD = "POST";
  private static final String GET_METHOD = "GET";

  public void testNoHeaderPresent() {
    FakeHttpServletRequest request = new FakeHttpServletRequest();
    request.setMethod(GET_METHOD);
    MethodOverridableRequest overridableRequest = new MethodOverridableRequest(request);
    assertEquals(GET_METHOD, overridableRequest.getMethod());
  }

  public void testHeaderPresent() {
    FakeHttpServletRequest request = new FakeHttpServletRequest();
    request.setMethod(GET_METHOD);
    request.setHeader(MethodOverridableRequest.X_HTTP_METHOD_OVERRIDE, POST_METHOD);
    MethodOverridableRequest overridableRequest = new MethodOverridableRequest(request);
    assertEquals(POST_METHOD, overridableRequest.getMethod());
  }
}
