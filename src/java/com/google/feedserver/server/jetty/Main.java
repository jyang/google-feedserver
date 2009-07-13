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
import com.google.feedserver.filters.SimpleOAuthFilter;
import com.google.feedserver.filters.SignedRequestFilter;
import com.google.feedserver.filters.SimpleKeyMananger;
import com.google.feedserver.manager.FeedServerProvider;
import com.google.feedserver.samples.config.AllowAllAclValidator;
import com.google.feedserver.samples.configstore.SampleFileSystemFeedConfigStore;
import com.google.feedserver.server.servlet.GetAuthTokenServlet;
import com.google.feedserver.server.servlet.GuiceServletContextListener;
import com.google.feedserver.server.servlet.MethodOverrideServletFilter;
import com.google.feedserver.util.CommonsCliHelper;
import com.google.feedserver.util.SimpleCommandLineParser;
import com.google.xdp.XdServletFilter;

import org.apache.abdera.protocol.server.ServiceManager;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.EventListener;
import java.util.logging.Logger;

import javax.servlet.Filter;

/**
 * Starts the Google FeedServer server using Jetty.
 * 
 * @author rakeshs101981@gmail.com (Rakesh Shete)
 */
public class Main {
  private static Logger log = Logger.getLogger(Main.class.getName());

  public static final String OAUTH_SIGNED_FETCH_FILTER_CLASS_NAME =
	  SimpleOAuthFilter.class.getName();

  public static String port_FLAG = "8080";
  public static final String port_HELP = "Port number to run FeedServer at.  Defaults to " +
      port_FLAG;

  public static String serverName_FLAG = getServerName();
  public static final String serverName_HELP = "Name of server used as author in Atom feeds.  " +
      "Defaults to " + serverName_FLAG;

  public static String useAuth_FLAG = "false";
  public static final String useAuth_HELP = "When true, provides a login servlet at " +
      "/accounts/ClientLogin that mimics Google's ClientLogin to get an authentication token " +
      "that FeedServer can then verify.  Defaults to " + useAuth_FLAG;

  public static String useOAuthSignedFetch_FLAG = "false";
  public static final String useOAuthSignedFetch_HELP = "When true, FeedServer expects requests " +
      "sent to it to be signed with OAuth signed fetch and verifies them to get viewer " +
      "information.  A value other than true or false is a regarded as the class " +
      "name of the OAuth signed fetch filter to be used.  Value true is equivalent to " +
      OAUTH_SIGNED_FETCH_FILTER_CLASS_NAME + ".  Defaults to " + useOAuthSignedFetch_FLAG;

  protected CommonsCliHelper commandLine;

  public static void main(String[] args) throws Exception {
    new Main(args);
  }

  public Main(String[] args) throws Exception {
    // parse command line flags
    commandLine = new CommonsCliHelper();
    commandLine.register(Main.class);
    commandLine.parse(args);

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

    if (useAuth_FLAG.equalsIgnoreCase("true")) {
      ServletHolder servletHolder2 = new ServletHolder(new GetAuthTokenServlet());
      context.addServlet(servletHolder2, "/accounts/ClientLogin");
      context.addFilter(SignedRequestFilter.class, "/*", org.mortbay.jetty.Handler.DEFAULT);
      EventListener listener = new GuiceServletContextListener();
      context.addEventListener(listener);
      log.info("Starting the feedserver to accept signed requests");
    } else if (!useOAuthSignedFetch_FLAG.equalsIgnoreCase("false")) {
      // Register the OAuth filter
      SimpleKeyMananger sKeyManager = new SimpleKeyMananger();
      Filter of = createOAuthFilter(useOAuthSignedFetch_FLAG.equalsIgnoreCase("true") ?
   		  OAUTH_SIGNED_FETCH_FILTER_CLASS_NAME : useOAuthSignedFetch_FLAG, sKeyManager);
      FilterHolder fh = new FilterHolder(of);
      context.addFilter(fh, "/*", org.mortbay.jetty.Handler.DEFAULT);
      log.info("Starting the feedserver to accept OAuth signed requests");
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

  public static String getHostName() {
    try {
      return InetAddress.getLocalHost().getHostName().toLowerCase();
    } catch (UnknownHostException e) {
      return "localhost";
    }
  }

  public static String getServerName() {
    return "feedserver@" + getHostName() + ":" + port_FLAG;
  }
}
