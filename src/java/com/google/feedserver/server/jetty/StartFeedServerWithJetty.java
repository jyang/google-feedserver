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
import com.google.feedserver.filters.OAuthFilter;
import com.google.feedserver.filters.SignedRequestFilter;
import com.google.feedserver.filters.SimpleKeyMananger;
import com.google.feedserver.manager.FeedServerProvider;
import com.google.feedserver.samples.config.AllowAllAclValidator;
import com.google.feedserver.samples.configstore.SampleFileSystemFeedConfigStore;
import com.google.feedserver.server.servlet.GetAuthTokenServlet;
import com.google.feedserver.server.servlet.GuiceServletContextListener;
import com.google.feedserver.server.servlet.MethodOverrideServletFilter;
import com.google.feedserver.util.SimpleCommandLineParser;
import com.google.xdp.XdServletFilter;

import org.apache.abdera.protocol.server.ServiceManager;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;

import java.util.EventListener;
import java.util.logging.Logger;

/**
 * Starts the Google FeedServer server using Jetty
 * 
 * @author rakeshs
 */
public class StartFeedServerWithJetty {
  static Logger log = Logger.getLogger(StartFeedServerWithJetty.class.getName());

  public static void main(String[] args) throws Exception {
    Server server = runJetty(args);
    server.join();
  }

  public static Server runJetty(String[] args) throws Exception {

    boolean configureOAuthFilter = false;
    boolean signedRequest = false;

    // Check if the OAuth filter flag has been given as command line input
    if (args != null) {
      for (int i = 0; i < args.length; i++) {
        if (args[i].startsWith("authenticated")) {
          String signedRequests = args[i].substring(args[i].indexOf("=") + 1);
          if (signedRequests.equals("true")) {
            signedRequest = true;
          }
        } else if (args[i].startsWith("OAuth_authenticated")) {
          String oauthFilter = args[i].substring(args[i].indexOf("=") + 1);
          if (oauthFilter.equals("true")) {
            configureOAuthFilter = true;
          }
        }
      }
    }


    SampleFileSystemFeedConfigStore feedConfigStore = new SampleFileSystemFeedConfigStore();
    log.info("Created a file store");
    FeedServerConfiguration config = FeedServerConfiguration.createIntance(feedConfigStore);
    config.setAclValidator(new AllowAllAclValidator());
    config.initialize(new SimpleCommandLineParser(args));
    config.setWrapperManagerClassName("com.google.feedserver.samples.manager.XmlWrapperManager");
    // set up server
    Server server = new Server(config.getPort());
    Context context = new Context(server, "/", Context.SESSIONS);

    // Add the Abdera servlet
    ServletHolder servletHolder = new ServletHolder(new AbderaServlet());
    servletHolder.setInitParameter(ServiceManager.PROVIDER, FeedServerProvider.class.getName());
    context.addServlet(servletHolder, "/*");

    // Register the filters
    context.addFilter(XdServletFilter.class, "/*", Handler.DEFAULT);
    context.addFilter(MethodOverrideServletFilter.class, "/*", Handler.DEFAULT);


    if (signedRequest) {
      ServletHolder servletHolder2 = new ServletHolder(new GetAuthTokenServlet());
      context.addServlet(servletHolder2, "/accounts/ClientLogin");
      context.addFilter(SignedRequestFilter.class, "/*", org.mortbay.jetty.Handler.DEFAULT);
      EventListener listener = new GuiceServletContextListener();
      context.addEventListener(listener);
      log.info("Starting the feedserver to accept signed requests");
    } else if (configureOAuthFilter) {
      // Register the OAuth filter
      SimpleKeyMananger sKeyManager = new SimpleKeyMananger();
      OAuthFilter of = new OAuthFilter(sKeyManager);
      FilterHolder fh = new FilterHolder(of);
      context.addFilter(fh, "/*", org.mortbay.jetty.Handler.DEFAULT);
      log.info("Starting the feedserver to accept OAuth signed requests");
    }

    // start server
    server.start();

    return server;
  }
}
