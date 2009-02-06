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

import com.google.feedserver.authentication.FakeFilterChainImpl;
import com.google.feedserver.authentication.FakeHttpServletRequest;
import com.google.feedserver.authentication.FakeHttpServletResponse;
import com.google.feedserver.server.servlet.MethodOverridableRequest;
import com.google.feedserver.server.servlet.MethodOverrideServletFilter;

import junit.framework.TestCase;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Unit tests for {@link MethodOverrideServletFilter}.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public class MethodOverrideServletFilterTest extends TestCase {

  private static final String METHOD_NAME = "METHOD_NAME";
  private static final String POST_METHOD = "POST";
  private static final String GET_METHOD = "GET";

  public static class FakeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
      resp.setHeader(METHOD_NAME, GET_METHOD);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
      resp.setHeader(METHOD_NAME, POST_METHOD);
    }
  }

  private FakeHttpServletResponse getResponse(FakeHttpServletRequest request)
      throws ServletException, IOException {
    Filter[] filter = {new MethodOverrideServletFilter()};
    FakeHttpServletResponse resp = new FakeHttpServletResponse();
    HttpServlet serv = new FakeServlet();
    filter[0].doFilter(request, resp, new FakeFilterChainImpl(serv));
    return resp;
  }

  public void testNoHeaderPresent() throws Exception {
    FakeHttpServletRequest request = new FakeHttpServletRequest();
    request.setMethod(GET_METHOD);
    FakeHttpServletResponse resp = getResponse(request);
    assertEquals(GET_METHOD, resp.getHeader(METHOD_NAME));
  }

  public void testHeaderPresent() throws Exception {
    FakeHttpServletRequest request = new FakeHttpServletRequest();
    request.setMethod(GET_METHOD);
    request.setHeader(MethodOverridableRequest.X_HTTP_METHOD_OVERRIDE, POST_METHOD);
    FakeHttpServletResponse resp = getResponse(request);
    assertEquals(POST_METHOD, resp.getHeader(METHOD_NAME));
  }
}
