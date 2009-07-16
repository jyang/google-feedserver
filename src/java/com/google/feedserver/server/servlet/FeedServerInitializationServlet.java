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

import com.google.feedserver.config.FeedServerConfiguration;
import com.google.feedserver.configstore.FeedConfigStore;
import com.google.feedserver.samples.config.AllowAllAclValidator;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * The initialization servlet required to initialize the feed config store
 * details when running in a container other than Jetty. eg: Tomcat
 * <p>
 * It expects the following as init parameters to be configured in the web.xml:
 * <ul>
 * <li>
 * FEED_CONFIG_STORE_CLASS : The fully qualified name of the
 * {@link FeedConfigStore} concrete class</li>
 * </ul>
 * </p>
 * <p>
 * </p>
 * 
 * @author rakeshs101981@gmail.com (Rakesh Shete)
 */
public class FeedServerInitializationServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private Logger logger = Logger.getLogger(FeedServerInitializationServlet.class.getName());

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    // Get the feed config store as init parameter
    String feedConfigStoreClass = config.getInitParameter("FEED_CONFIG_STORE_CLASS");
    String wrapperManagerClassName = config.getInitParameter("WRAPPER_MANAGER_CLASS");

    try {
      FeedConfigStore feedConfigStore =
          (FeedConfigStore) Class.forName(feedConfigStoreClass).newInstance();
      FeedServerConfiguration feedConfig = FeedServerConfiguration.createIntance(feedConfigStore);
      feedConfig.setAclValidator(new AllowAllAclValidator());
      feedConfig.setWrapperManagerClassName(wrapperManagerClassName);
      logger.info(" Created feed config : " + feedConfig + " with feed store : " + feedConfigStore);
    } catch (InstantiationException e) {
      logger.log(Level.SEVERE,
          "Problems encountered while creating an instance of  feed config store : "
              + feedConfigStoreClass, e);
    } catch (IllegalAccessException e) {
      logger.log(Level.SEVERE,
          "Problems encountered while creating an instance of  feed config store : "
              + feedConfigStoreClass, e);
    } catch (ClassNotFoundException e) {
      logger
          .log(Level.SEVERE, "No class definition found for  feed config store : "
              + feedConfigStoreClass
              + " Please ensure that the class exists with the packaged jars", e);
    }
  }



}
