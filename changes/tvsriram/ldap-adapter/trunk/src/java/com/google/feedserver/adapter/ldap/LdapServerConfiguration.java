/**
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.feedserver.adapter.ldap;

import java.util.ArrayList;
import java.util.List;

public class LdapServerConfiguration {
  // Role information
  private String roleDn;
  private String password;
  
  // Server Configurations
  private ArrayList<LdapServer> ldapServers = new ArrayList<LdapServer>();


  public void setRoleDn(String dn) {
    roleDn = dn;
  }
  
  public void setRolePassword(String password) {
    this.password = password; 
  }
  
  public String getRoleDn() {
    return roleDn;
  }
  
  public String getRolePassword() {
    return password;
  }
  
  public List<LdapServer> getServers() {
    return ldapServers;
  }
  
  public void addServer(LdapServer server) {
    ldapServers.add(server);
  }
  
  public String getProviderUrl() {
    String url = "ldap://";
    String hostname = "localhost";
    if (ldapServers.size() > 0) {
      LdapServer server = ldapServers.get(0);
      hostname = server.getServerAddress().getHostName();
      url += hostname;
      short port = server.getServerPort();
      if (port > 0) {
        url += ":" + port;
      }
      url += "/";
    } else {
      url += hostname + "/";
    }
    return url;
  }
}
