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
package com.google.feedserver.server;

import com.google.feedserver.filters.SimpleOAuthFilter;

/**
 * Server configuration set as command line flags
 */
public class FlagConfig {

  public static final String OAUTH_SIGNED_FETCH_FILTER_CLASS_NAME =
      SimpleOAuthFilter.class.getName();

  public static String port_FLAG = "8080";
  public static final String port_HELP = "Port number to run FeedServer at.  Defaults to " +
      port_FLAG;

  public static String enableAuth_FLAG = "false";
  public static final String enableAuth_HELP = "When true, provides a login servlet at " +
      "/accounts/ClientLogin that mimics Google's ClientLogin to get an authentication token " +
      "that FeedServer can then verify.  Defaults to " + enableAuth_FLAG;

  public static String enableOAuthSignedFetch_FLAG = "false";
  public static final String enableOAuthSignedFetch_HELP = "When true, FeedServer expects requests " +
      "sent to it to be signed with OAuth signed fetch and verifies them to get viewer " +
      "information.  A value other than true or false is a regarded as the class " +
      "name of the OAuth signed fetch filter to be used.  Value true is equivalent to " +
      OAUTH_SIGNED_FETCH_FILTER_CLASS_NAME + ".  Defaults to " + enableOAuthSignedFetch_FLAG;

  public static String enableAccessControl_FLAG = "false";
  public static final String enableAccessControl_HELP = "When true, access control is enabled; " +
      "disabled otherwise.  Defaults to " + enableAccessControl_FLAG;
}
