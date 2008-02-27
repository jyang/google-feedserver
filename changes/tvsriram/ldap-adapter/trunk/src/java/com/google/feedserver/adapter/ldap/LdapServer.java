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

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LdapServer {
  private InetAddress location;
  private short port;
  private int maxConnections = 1;
  
  public LdapServer(String host, short port, boolean ssl, int maxConn) 
      throws UnknownHostException {
    location = InetAddress.getByName(host);
    this.port = port;
    maxConnections = maxConn;
  }
  
  public InetAddress getServerAddress() {
    return location;
  }
  
  public short getServerPort() {
    return port;
  }
  
  @Override
  public String toString() {
    return ("Location " + location + " Port: " + port + "Max Conn: " +
            maxConnections);
  }
}
