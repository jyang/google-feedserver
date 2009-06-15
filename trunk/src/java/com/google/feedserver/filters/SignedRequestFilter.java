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
package com.google.feedserver.filters;

import com.google.feedserver.authentication.TokenManager;
import com.google.feedserver.authentication.TokenManagerModule;
import com.google.feedserver.authentication.TokenManagerException;
import com.google.feedserver.server.servlet.GuiceServletContextListener;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This filter intercepts the requests and validates the authorization token
 * using the {@link TokenManager}. For configuring a concrete instance of
 * {@link TokenManager} please refer to {@link TokenManagerModule}
 * 
 * @author rakeshs101981@gmail.com (Rakesh Shete)
 */
public class SignedRequestFilter implements Filter {

  private TokenManager tokenManager;

  private static Logger logger = Logger.getLogger(SignedRequestFilter.class.getName());

  @Override
  public void destroy() {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;

    // By pass the check for authenticating and getting the authentication token
    if (!req.getRequestURI().contains("/accounts/ClientLogin")) {
      try {
        if (!tokenManager.validateAuthzToken(req)) {
          ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
          return;
        }
      } catch (TokenManagerException e) {
        logger.log(Level.WARNING,
            "Unexpected errors were encountered while trying to generate the token", e);
        ((HttpServletResponse) response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            "Unexpected errors were encountered while validating the authorization token");
      }
    }

    // Forward the request to other filters
    chain.doFilter(request, response);
  }

  @Inject
  public void setTokenManager(TokenManager tokenManager) {
    this.tokenManager = tokenManager;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // Get the inject instance from the servlet context and inject the members
    ServletContext context = filterConfig.getServletContext();
    Injector injector = (Injector) context.getAttribute(GuiceServletContextListener.KEY);
    if (injector == null) {
      logger
          .log(
              Level.SEVERE,
              "No injector found. Ensure that the GuiceServletContextListener has been configured correctly. The token generation & validation will not work!!!");
    } else {
      injector.injectMembers(this);
    }
  }
}
