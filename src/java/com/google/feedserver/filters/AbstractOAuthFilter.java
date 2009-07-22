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

import net.oauth.OAuthException;
import net.oauth.OAuthServiceProvider;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.logging.Logger;

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
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 * 
 */
public abstract class AbstractOAuthFilter implements Filter {

  private static Logger logger = Logger.getLogger(SimpleOAuthFilter.class.getName());

  protected OAuthServiceProvider provider;
  protected KeyManager keyManager;
  protected OAuthValidator validator;

  /**
   * The key manager class name to be read as configuration parameter
   */
  public static final String KEY_MANAGER_CLASS = "KEY_MANAGER";

  public AbstractOAuthFilter() {
  }

  /**
   * OAuth filter for FeedServer.
   * 
   * @param keyManager {@link KeyManager} that stores key for OAuth
   *        verification.
   */
  public AbstractOAuthFilter(KeyManager keyManager) {
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
  abstract public String authenticate(HttpServletRequest request)
  	  throws IOException, OAuthException, URISyntaxException;

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
        logger.severe("Problems encountered while creating an instance of key manager : "
            + keyManagerClassName);
      } catch (IllegalAccessException e) {
        logger.severe("Problems encountered while creating an instance of key manager : "
            + keyManagerClassName);
      } catch (ClassNotFoundException e) {
        logger.severe("Unable to locate and create an instance of key manager : "
            + keyManagerClassName + " Ensure that the required class is packaged with the jars");
      }
    }
  }

  public void destroy() {
  }

  protected String urlDecode(String s) {
    try {
      return URLDecoder.decode(s, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      // UTF-8 is indeed supported; won't come here
      return s;
    }
  }
}
