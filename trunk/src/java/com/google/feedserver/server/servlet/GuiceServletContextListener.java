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

import com.google.feedserver.authentication.TokenManagerModule;
import com.google.feedserver.filters.SignedRequestFilter;
import com.google.inject.Guice;
import com.google.inject.Injector;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * The listener that will create the GUICE {@link Injector} instance and stores
 * it in the servlet context which will be used by the
 * {@link GetAuthTokenServlet} and {@link SignedRequestFilter} for generating
 * authorization tokens and validating them resp.
 * 
 * @author rakeshs101981@gmail.com (Rakesh Shete)
 */
public class GuiceServletContextListener implements ServletContextListener {

  public static final String KEY = "injector";

  /**
   * Creates a GUICE Injector and adds it to the servlet context with "injector"
   * as the key
   * 
   * @param servletContextEvent The servlet context
   */
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    servletContextEvent.getServletContext().setAttribute(KEY, getInjector());
  }


  public void contextDestroyed(ServletContextEvent servletContextEvent) {
    servletContextEvent.getServletContext().removeAttribute(KEY);
  }

  /**
   * Creates and returns a GUICE injector instance
   * 
   * @return GUICE injector instance
   */
  private Injector getInjector() {
    return Guice.createInjector(new TokenManagerModule());
  }
}
