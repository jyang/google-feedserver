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

import com.google.feedserver.adapters.AbstractManagedCollectionAdapter;
import com.google.feedserver.config.UserInfo;
import com.google.feedserver.samples.config.HashMapBasedUserInfo;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;
import net.oauth.server.OAuthServlet;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OAuth filter for FeedServer. It uses {@link KeyManager} to store the public
 * consumer keys.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public class OAuthFilter implements Filter {

  private static Logger logger = Logger.getLogger(OAuthFilter.class);
  protected OAuthServiceProvider provider;
  protected KeyManager keyManager;
  protected OAuthValidator validator;

  /**
   * The key manager class name to be read as configuration parameter
   */
  public static final String KEY_MANAGER_CLASS = "KEY_MANAGER";

  public OAuthFilter() {
  }

  /**
   * OAuth filter for FeedServer.
   * 
   * @param keyManager {@link KeyManager} that stores key for OAuth
   *        verification.
   */
  public OAuthFilter(KeyManager keyManager) {
    initializeFilter(keyManager);
  }

  /**
   * Initializes the filter with the given key manager
   * 
   * @param keyManager The key manager instance to be used
   */
  private void initializeFilter(KeyManager keyManager) {
    this.keyManager = keyManager;
    provider = new OAuthServiceProvider(null, null, null);
    validator = new SimpleOAuthValidator();
    logger.info("Initialized the OAuth filter successfully with key manager : "
        + keyManager.toString());
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    try {
      authenticate((HttpServletRequest) request);
    } catch (OAuthException e) {
      sendError(response, HttpServletResponse.SC_UNAUTHORIZED);
      return;
    } catch (URISyntaxException e) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    chain.doFilter(request, response);
  }

  /**
   * clear Authenticate the signed fetch request.
   * 
   * @param request incoming HttpRequest.
   * @return viewer id for this request.
   */
  public String authenticate(HttpServletRequest request) throws IOException, OAuthException,
      URISyntaxException {
    OAuthMessage message = OAuthServlet.getMessage(request, null);
    String consumerKey = message.getConsumerKey();
    String signatureMethod = message.getSignatureMethod();
    OAuthConsumer consumer = keyManager.getOAuthConsumer(provider, consumerKey, signatureMethod);
    if (null == consumer) {
      throw new OAuthException("Not Authorized");
    }
    OAuthAccessor accessor = new OAuthAccessor(consumer);
    message.validateMessage(accessor, validator);

    // Retrieve and set the user info
    String userinfo = message.getParameter("opensocial_viewer_id");
    UserInfo userInfo = new HashMapBasedUserInfo();
    userInfo.setEmail(userinfo);
    request.setAttribute(AbstractManagedCollectionAdapter.USER_INFO, userInfo);
    return userinfo;
  }

  protected void sendError(ServletResponse response, int errorCode) throws IOException {
    HttpServletResponse resp = (HttpServletResponse) response;
    resp.sendError(errorCode);
  }


  /**
   * Read the key manager class name and configure it with this filter which
   * will be used when authenticating & authorizing the user
   * <p>
   * This is required when deploying and running the feedserver on containers
   * other than Jetty. eg: Tomcat
   * </p>
   * 
   * @param filterConfig The Filter config instance
   */
  public void init(FilterConfig filterConfig) {
    String keyManagerClassName = filterConfig.getInitParameter(KEY_MANAGER_CLASS);
    if (keyManagerClassName != null) {
      try {
        KeyManager keyManager = (KeyManager) Class.forName(keyManagerClassName).newInstance();
        // Initialize the filter with the key manager class
        initializeFilter(keyManager);
      } catch (InstantiationException e) {
        logger.error("Problems encountered while creating an instance of key manager : "
            + keyManagerClassName, e);
      } catch (IllegalAccessException e) {
        logger.error("Problems encountered while creating an instance of key manager : "
            + keyManagerClassName, e);
      } catch (ClassNotFoundException e) {
        logger.error("Unable to locate and create an instance of key manager : "
            + keyManagerClassName + " Ensure that the required class is packaged with the jars", e);
      }
    }
  }

  public void destroy() {
  }
}
