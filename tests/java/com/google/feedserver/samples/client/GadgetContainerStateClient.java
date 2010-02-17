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

package com.google.feedserver.samples.client;

import com.google.feedserver.client.FeedServerClient;
import com.google.feedserver.util.FeedServerClientException;
import com.google.gdata.client.GoogleService;
import com.google.gdata.util.AuthenticationException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Sample client that manipulates private gadget ContainerState feed.
 */
public class GadgetContainerStateClient {

  public static class GadgetState {
    protected String id;
    protected String name;
    protected String specUrl;
    protected long updated;
    protected String serverUrl;
    protected int columnIndex;
    protected int rowIndex;
    protected String height;
    protected String title;
    protected String[] prefName;
    protected String[] prefValue;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getSpecUrl() {
      return specUrl;
    }

    public void setSpecUrl(String specUrl) {
      this.specUrl = specUrl;
    }

    public long getUpdated() {
      return updated;
    }

    public void setUpdated(long updated) {
      this.updated = updated;
    }

    public String getServerUrl() {
      return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
      this.serverUrl = serverUrl;
    }

    public int getColumnIndex() {
      return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
      this.columnIndex = columnIndex;
    }

    public int getRowIndex() {
      return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
      this.rowIndex = rowIndex;
    }

    public String getHeight() {
      return height;
    }

    public void setHeight(String height) {
      this.height = height;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String[] getPrefName() {
      return prefName;
    }

    public void setPrefName(String[] prefName) {
      this.prefName = prefName;
    }

    public String[] getPrefValue() {
      return prefValue;
    }

    public void setPrefValue(String[] prefValue) {
      this.prefValue = prefValue;
    }
  }


  /**
   * Gets gadget container state of a Sites page.
   * @param domainName Domain name
   * @param siteName Site name
   * @param containerId Gadget container id (page WUID)
   * @param uriEncodedEmail URI encoded user email address e.g. "john.doe%40domain.com"
   * @return List of gadget states
   * @throws FeedServerClientException
   * @throws MalformedURLException
   * @throws AuthenticationException
   */
  public List<GadgetState> getContainerState(String domainName, String siteName,
      String containerId, String uriEncodedEmail)
      throws FeedServerClientException, MalformedURLException, AuthenticationException {
    FeedServerClient<GadgetState> feedServerClient = getFeedServerClient();
    return feedServerClient.getEntities(new URL(getContainerStateFeedUrl(domainName, siteName,
        containerId, uriEncodedEmail)));
  }

  /**
   * Inserts a new gadget into a gadget container.
   * @param domainName Domain name
   * @param siteName Site name
   * @param containerId Gadget container id (page WUID)
   * @param uriEncodedEmail URI encoded user email address e.g. "john.doe%40domain.com"
   * @param gadgetState Gadget state to insert
   * @return Inserted gadget state
   * @throws AuthenticationException
   * @throws MalformedURLException
   * @throws FeedServerClientException
   */
  public GadgetState insertGadgetState(String domainName, String siteName,
      String containerId, String uriEncodedEmail, GadgetState gadgetState)
      throws AuthenticationException, MalformedURLException, FeedServerClientException {
    return getFeedServerClient().insertEntity(new URL(getContainerStateFeedUrl(domainName, siteName,
        containerId, uriEncodedEmail)), gadgetState);
  }

  protected FeedServerClient<GadgetState> getFeedServerClient() throws AuthenticationException {
    return new FeedServerClient<GadgetState>(getService(), GadgetState.class);
  }
  
  protected String getContainerStateFeedUrl(String domainName, String siteName,
      String containerId, String uriEncodedEmail) {
    return "http://feedserver-enterprise.googleusercontent.com/a/" + domainName + "/user/" +
        siteName + "_" + containerId + "_" + uriEncodedEmail + "/g/ContainerState";
  }

  protected GoogleService getService() throws AuthenticationException {
    GoogleService service = new GoogleService("esp", getClass().getName());
    service.setUserCredentials("admin@example.com", "...");
    return service;
  }
}
