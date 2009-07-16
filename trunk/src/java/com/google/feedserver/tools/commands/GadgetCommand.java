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

import com.google.feedserver.client.FeedServerClient;
import com.google.feedserver.tools.FeedClient;
import com.google.feedserver.tools.FeedClientCommand;
import com.google.feedserver.util.FileUtil;
import com.google.gdata.client.GoogleService;

/**
 * Super class for all commands related to gadget management.
 */
public abstract class GadgetCommand extends FeedClientCommand {

  public static final String PRIVATE_GADGET_SPEC = "PrivateGadgetSpec";
  public static final String PRIVATE_GADGET = "PrivateGadget";
  public static final String ERROR_GADGET_ALREADY_EXISTS = "Gadget already exists";

  protected FeedServerClient<GadgetSpecEntity> specClient;
  protected FeedServerClient<GadgetDirEntity> dirClient;

  /**
   * Gadget spec entity bean.
   */
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

  /**
   * Gadget directory entity bean.
   */
  public static class GadgetDirEntity {
    protected String id;
    protected String url;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
  }

  public GadgetCommand(GoogleService service, FileUtil fileUtil) {
    super(service, fileUtil);

    specClient = new FeedServerClient<GadgetSpecEntity>(service, GadgetSpecEntity.class);
    dirClient = new FeedServerClient<GadgetDirEntity>(service, GadgetDirEntity.class);
  }

  protected String getCanonicalGadgetSpecUrl(String gadgetName) {
    return FeedClient.HOST_DEFAULT + "/a/" + getDomainName() + "/g/" + PRIVATE_GADGET_SPEC + "/" +
        gadgetName;
  }
}
