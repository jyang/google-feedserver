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

import com.google.feedserver.config.FeedServerConfiguration;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * This filter sets the serverURI in the feed server configuration that is
 * required to be included as part of feed entry id
 * 
 * @author rakeshs@google.com (Rakesh Shete)
 * 
 */
public class ServerURIFilter implements Filter {

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.Filter#destroy()
   */
  @Override
  public void destroy() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
   * javax.servlet.ServletResponse, javax.servlet.FilterChain)
   */
  @Override
  public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) arg0;

    String serverURI =
        req.getScheme() + "://" + req.getLocalAddr() + ":" + req.getServerPort()
            + req.getContextPath();

    // Set the serverURI for the request
    FeedServerConfiguration config = FeedServerConfiguration.getIntance();
    config.setSeverURI(serverURI);

    // Forward the request to other filters
    chain.doFilter(arg0, arg1);

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
   */
  @Override
  public void init(FilterConfig arg0) throws ServletException {
  }

}
