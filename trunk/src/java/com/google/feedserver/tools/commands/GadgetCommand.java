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

import com.google.feedserver.tools.FeedClientCommand;
import com.google.feedserver.util.FileUtil;
import com.google.gdata.client.GoogleService;

/**
 * Super class for all commands related to gadget management.
 */
public abstract class GadgetCommand extends FeedClientCommand {

  public static final String PRIVATE_GADGET_SPEC = "PrivateGadgetSpec";
  public static final String PRIVATE_GADGET = "PrivateGadget";

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

  public GadgetCommand(GoogleService service, FileUtil fileUtil) {
    super(service, fileUtil);
  }
}
