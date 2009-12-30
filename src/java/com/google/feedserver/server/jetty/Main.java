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

package com.google.feedserver.server.jetty;

import com.google.feedserver.config.FeedServerConfiguration;
import com.google.feedserver.filters.KeyManager;
import com.google.feedserver.filters.SignedRequestFilter;
import com.google.feedserver.filters.SimpleKeyMananger;
import com.google.feedserver.manager.FeedServerProvider;
import com.google.feedserver.samples.config.AllowAllAclValidator;
import com.google.feedserver.samples.configstore.SampleFileSystemFeedConfigStore;
import com.google.feedserver.samples.manager.XmlWrapperManager;
import com.google.feedserver.server.FlagConfig;
import com.google.feedserver.server.servlet.GetAuthTokenServlet;
import com.google.feedserver.server.servlet.GuiceServletContextListener;
import com.google.feedserver.server.servlet.MethodOverrideServletFilter;
import com.google.feedserver.util.CommonsCliHelper;
import com.google.feedserver.util.SimpleCommandLineParser;
import com.google.xdp.XdServletFilter;

import org.apache.abdera.protocol.server.ServiceManager;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EventListener;
import java.util.logging.Logger;

import javax.servlet.Filter;

/**
 * Starts the Google FeedServer server using Jetty.
 * 
 * @author rakeshs101981@gmail.com (Rakesh Shete)
 */
public class Main {
  private static Logger logger = Logger.getLogger(Main.class.getName());

  protected CommonsCliHelper commandLine;

  public static void main(String[] args) throws Exception {
    new Main(args);
  }

  public Main(String[] args) throws Exception {
    // parse command line flags
    commandLine = new CommonsCliHelper();
    commandLine.register(FlagConfig.class);
    commandLine.parse(args);

    SampleFileSystemFeedConfigStore feedConfigStore = new SampleFileSystemFeedConfigStore();
    logger.info("Created a file system config store");
    FeedServerConfiguration config = FeedServerConfiguration.createIntance(feedConfigStore);
    config.setAclValidator(new AllowAllAclValidator());
    config.initialize(new SimpleCommandLineParser(args));
    config.setWrapperManagerClassName(XmlWrapperManager.class.getName());
    // set up server
    Server server = new Server(config.getPort());
//    server.getConnectors()[0].setHost("localhost");  // listen on localhost:{port} only
    Context context = new Context(server, "/", Context.SESSIONS);

    // Add the Abdera servlet
    ServletHolder servletHolder = new ServletHolder(new GVizServlet());
    servletHolder.setInitParameter(ServiceManager.PROVIDER, FeedServerProvider.class.getName());
    context.addServlet(servletHolder, "/*");

    // Register the filters
    context.addFilter(XdServletFilter.class, "/*", Handler.DEFAULT);
    context.addFilter(MethodOverrideServletFilter.class, "/*", Handler.DEFAULT);

    if (FlagConfig.enableAuth_FLAG.equalsIgnoreCase("true")) {
      ServletHolder servletHolder2 = new ServletHolder(new GetAuthTokenServlet());
      context.addServlet(servletHolder2, "/accounts/ClientLogin");
      context.addFilter(SignedRequestFilter.class, "/*", org.mortbay.jetty.Handler.DEFAULT);
      EventListener listener = new GuiceServletContextListener();
      context.addEventListener(listener);
      logger.info("Starting FeedServer to accept signed requests");
    } else if (!FlagConfig.enableOAuthSignedFetch_FLAG.equalsIgnoreCase("false")) {
      // Register the OAuth filter
      SimpleKeyMananger sKeyManager = new SimpleKeyMananger();
      Filter of = createOAuthFilter(FlagConfig.enableOAuthSignedFetch_FLAG.equalsIgnoreCase("true") ?
   		  FlagConfig.OAUTH_SIGNED_FETCH_FILTER_CLASS_NAME : FlagConfig.enableOAuthSignedFetch_FLAG, sKeyManager);
      FilterHolder fh = new FilterHolder(of);
      context.addFilter(fh, "/*", org.mortbay.jetty.Handler.DEFAULT);
      logger.info("Starting FeedServer to accept OAuth signed requests");
    }

    // start server
    server.start();
    server.join();
  }
  
  protected Filter createOAuthFilter(String filterClassName, KeyManager keyManager)
      throws ClassNotFoundException, SecurityException, NoSuchMethodException,
      IllegalArgumentException, InstantiationException, IllegalAccessException,
      InvocationTargetException {
    Class<?> c = Class.forName(filterClassName);
    Constructor<?> constructor = c.getConstructor(KeyManager.class);
    return (Filter) constructor.newInstance(keyManager);
  }
}
