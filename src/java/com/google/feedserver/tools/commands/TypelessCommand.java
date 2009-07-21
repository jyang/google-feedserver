/*
 * Copyright 2009 Google Inc.
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

package com.google.feedserver.tools.commands;

import com.google.feedserver.client.TypelessFeedServerClient;
import com.google.feedserver.tools.FeedClient;
import com.google.feedserver.tools.FeedClientCommand;
import com.google.feedserver.util.FileUtil;
import com.google.gdata.client.GoogleService;
import com.google.gdata.util.AuthenticationException;

public abstract class TypelessCommand extends FeedClientCommand {

  protected TypelessFeedServerClient typelessClient;

  public TypelessCommand(GoogleService service, FileUtil fileUtil) {
    super(service, fileUtil);

    typelessClient = new TypelessFeedServerClient(service);
    try {
      typelessClient.setUserCredentials(
          FeedClient.userEmail_FLAG, FeedClient.password_FLAG);
    } catch (AuthenticationException e) {
      System.err.println(e.getMessage());
    }
  }
}
