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

package com.google.feedserver.tools;

import com.google.feedserver.client.FeedServerClient;
import com.google.feedserver.util.CommonsCliHelper;
import com.google.feedserver.util.FeedServerClientException;
import com.google.gdata.client.GoogleService;
import com.google.gdata.util.AuthenticationException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * FeedServer client tool for managing gadgets.
 *
 * -op uploadUserGadget -url .../a/example.com/user/john.doe@example.com/g/PrivateGadgetSpec
 * -gadgetSpecEntityFile <path to spec file>
 */
public class FeedServerClientGadgetTool extends FeedServerClientTool {

  public static final String APP_NAME = FeedServerClientGadgetTool.class.getName();

  public static final String OP_UPLOAD_USER_GADGET = "uploadUserGadget";

  public static final void main(String[] args) throws MalformedURLException,
      AuthenticationException, FeedServerClientException, IOException {
    new FeedServerClientGadgetTool().run(args);
  }

  @Override
  protected void processRequest(CommonsCliHelper cliHelper) throws FeedServerClientException,
      MalformedURLException, IOException, AuthenticationException {
    if (OP_UPLOAD_USER_GADGET.equals(op_FLAG)) {
      getUserCredentials();
      uploadUserGadget(gadgetSpecEntityFile_FLAG);
    } else {
      super.processRequest(cliHelper);
    }
  }

  public static class GadgetSpecEntity {
    protected String name;
    protected String specContent;
    public GadgetSpecEntity() {}
    public GadgetSpecEntity(String name) { setName(name); }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecContent() { return specContent; }
    public void setSpecContent(String specContent) { this.specContent = specContent; }
  }

  protected void uploadUserGadget(String gadgetSpecFilePath) throws IOException {
    GoogleService service = new GoogleService(serviceName_FLAG, APP_NAME);
    FeedServerClient<GadgetSpecEntity> client = new FeedServerClient<GadgetSpecEntity>(
        feedServerClient.getService(), GadgetSpecEntity.class);
    File gadgetSpecFile = new File(gadgetSpecFilePath);
    String gadgetName = gadgetSpecFile.getName();
    URL feedUrl = new URL(url_FLAG);

    GadgetSpecEntity entity = new GadgetSpecEntity(gadgetName);
    try {
      client.deleteEntity(feedUrl, entity);
    } catch (FeedServerClientException e) {
      // entity doesn't exist
    }

    try {
      entity.setSpecContent(fileUtil.readFileContents(gadgetSpecFilePath));
      client.insertEntity(feedUrl, entity);
      System.out.println("Gadget '" + gadgetName + "' uploaded");
    } catch (FeedServerClientException e) {
      System.err.println("Error: Failed to upload gadget '" + gadgetName + "': " + e.getMessage());
    }
  }
}
