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

package com.google.feedserver.filters;

import com.google.feedserver.authentication.FakeFilterChainImpl;
import com.google.feedserver.authentication.FakeHttpServletRequest;
import com.google.feedserver.authentication.FakeHttpServletResponse;
import com.google.feedserver.authentication.SampleTokenManager;
import com.google.feedserver.server.servlet.GetAuthTokenServlet;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;

/**
 * Tests the servlet {@link GetAuthTokenServlet} and filter
 * {@link SignedRequestFilter} for authenticating and authorizing requests using
 * Google Client Login mechanism
 * 
 * @author rakeshs101981@gmail.com (Rakesh Shete)
 */
public class GoogleClientLoginTest extends TestCase {

  private SampleTokenManager tokenManager;


  @Override
  protected void setUp() throws Exception {
    tokenManager = new SampleTokenManager();
  }

  @Override
  protected void tearDown() throws Exception {
    tokenManager = null;
  }

  public void testGoogleClientLogin() {

    try {
      SignedRequestFilter filter = new SignedRequestFilter();
      filter.setTokenManager(tokenManager);

      FakeHttpServletRequest request = new FakeHttpServletRequest();
      request.addRequestParameter("Email", "testuser");
      request.addRequestParameter("service", "testservice");
      request.setRequestURI("http://localhost:8080/accounts/ClientLogin");
      request.setMethod(FakeHttpServletRequest.POST_METHOD);
      File file = new File("out");
      file.deleteOnExit();
      PrintWriter pw = new PrintWriter(file);
      GetAuthTokenServlet authTokenServlet = new GetAuthTokenServlet();
      authTokenServlet.setTokenManager(tokenManager);
      FakeHttpServletResponse response = new FakeHttpServletResponse();
      response.setWriter(pw);
      authTokenServlet.service(request, response);
      response.getWriter().flush();
      InputStream is = new FileInputStream(file);
      byte[] buf = new byte[1024];
      is.read(buf, 0, (int) file.length());
      String authToken = new String(buf).trim().split("=")[1];
      request = new FakeHttpServletRequest();
      request.setRequestURI("");
      response = new FakeHttpServletResponse();
      request.setHeader("Authorization", new String("auth=" + authToken));
      filter.doFilter(request, response, new FakeFilterChainImpl());
    } catch (FileNotFoundException e) {
      fail(e.getMessage());
      e.printStackTrace();
    } catch (ServletException e) {
      fail(e.getMessage());
      e.printStackTrace();
    } catch (IOException e) {
      fail(e.getMessage());
      e.printStackTrace();
    }

  }


}
