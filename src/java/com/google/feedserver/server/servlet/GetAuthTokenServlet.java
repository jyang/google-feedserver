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

package com.google.feedserver.server.servlet;

import com.google.feedserver.authentication.TokenManager;
import com.google.feedserver.authentication.TokenManagerDIModule;
import com.google.feedserver.authentication.TokenManagerException;
import com.google.feedserver.authentication.TokenManagerException.Reason;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The servlet that will generate the tokens to be exchanged as authorization
 * tokens for every request
 * <p>
 * It does not do any authentication before generating the tokens. <br/> If
 * authentication is to be handled, it can be done by the {@link TokenManager}
 * implementation before issuing the token
 *</p>
 * For configuring a concrete instance of {@link TokenManager} please refer to
 * {@link TokenManagerDIModule}
 * 
 * @author rakeshs101981@gmail.com (Rakesh Shete)
 */
public class GetAuthTokenServlet extends HttpServlet {
  /**
   * The tokenmanager instance to be used
   */
  private TokenManager tokenManager;

  private static Logger logger = Logger.getLogger(GetAuthTokenServlet.class.getName());

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    doPost(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    PrintWriter pw = resp.getWriter();
    try {
      // Generate the token and send it back as response
      pw.write("Auth=" + tokenManager.generateAuthzToken(req));
    } catch (TokenManagerException e) {
      if (e.getReason().equals(Reason.UN_AUTHORIZED)) {
        logger.log(Level.SEVERE, "Failed to authenticate the user with the given credentials", e);
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,
            "Failed to authenticate the user with the given credentials");
      } else {
        logger.log(Level.SEVERE,
            "Unexpected errors were encountered while trying to generate the token", e);
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            "Unexpected errors were encountered while trying to generate the token");
      }
    }
  }



  @Inject
  public void setTokenManager(TokenManager tokenManager) {
    this.tokenManager = tokenManager;
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ServletContext context = config.getServletContext();
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
