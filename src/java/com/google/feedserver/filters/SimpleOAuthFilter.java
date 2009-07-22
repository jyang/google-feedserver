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
import com.google.feedserver.config.UserInfo.UserInfoProperties;
import com.google.feedserver.samples.config.HashMapBasedUserInfo;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.server.OAuthServlet;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * OAuth filter for FeedServer. It uses {@link KeyManager} to store the public
 * consumer keys.
 * 
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 * 
 */
public class SimpleOAuthFilter extends AbstractOAuthFilter {

  private static final Logger logger = Logger.getLogger(SimpleOAuthFilter.class.getName());

  public SimpleOAuthFilter(KeyManager keyManager) {
    super(keyManager);
  }

  @Override
  public String authenticate(HttpServletRequest request) throws IOException, OAuthException,
      URISyntaxException {
    logger.info("verifying signed fetch ...");

    OAuthMessage message = OAuthServlet.getMessage(request, null);
    String consumerKey = message.getConsumerKey();
    String signatureMethod = message.getSignatureMethod();
    OAuthConsumer consumer = keyManager.getOAuthConsumer(provider, consumerKey, signatureMethod);
    if (null == consumer) {
      logger.info("signed fetch verification failed: consumer is null");
      throw new OAuthException("Unauthorized");
    }
    OAuthAccessor accessor = new OAuthAccessor(consumer);
    message.validateMessage(accessor, validator);

    logger.info("signed fetch verified");

    // Retrieve and set the user info with the OAuth parameters
    Map<UserInfoProperties, Object> oauthParams = new HashMap<UserInfoProperties, Object>();
    oauthParams.put(UserInfoProperties.EMAIL,
        urlDecode(message.getParameter("opensocial_viewer_email")));
    oauthParams.put(UserInfoProperties.VIEWER_ID, message.getParameter("opensocial_viewer_id"));
    oauthParams.put(UserInfoProperties.OWNER_EMAIL,
        urlDecode(message.getParameter("opensocial_owner_email")));
    oauthParams.put(UserInfoProperties.OWNER_ID, message.getParameter("opensocial_owner_id"));
    oauthParams.put(UserInfoProperties.APPLICATION_ID, message.getParameter("opensocial_app_id"));
    oauthParams.put(UserInfoProperties.APPLICATION_URL, message.getParameter("opensocial_app_url"));

    UserInfo userInfo = new HashMapBasedUserInfo(oauthParams);
    request.setAttribute(AbstractManagedCollectionAdapter.USER_INFO, userInfo);

    return message.getParameter("opensocial_viewer_id");
  }
}
