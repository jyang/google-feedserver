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

package com.google.feedserver.samples;

import com.google.feedserver.client.FeedServerClient;
import com.google.feedserver.util.FeedServerClientException;
import com.google.gdata.client.GoogleService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Employee search sample client.
 */
public class EmployeeSearch {
  public static void main(String[] args) throws Exception {
    EmployeeSearch s = new EmployeeSearch();
    s.search(null, null);
  }

  /**
   * Employee JavaBean
   */
  public static class Employee {
    protected long id;
    protected String lastName;
    protected String firstName;
    protected String jobTitle;
    protected String location;
    protected String phoneOffice;

    public long getId() {
      return id;
    }

    public void setId(long id) {
      this.id = id;
    }

    public String getLastName() {
      return lastName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }

    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public String getJobTitle() {
      return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
      this.jobTitle = jobTitle;
    }

    public String getLocation() {
      return location;
    }

    public void setLocation(String location) {
      this.location = location;
    }

    public String getPhoneOffice() {
      return phoneOffice;
    }

    public void setPhoneOffice(String phoneOffice) {
      this.phoneOffice = phoneOffice;
    }
  }

  protected void search(String name, String location)
      throws MalformedURLException, FeedServerClientException {
    GoogleService service = new GoogleService("esp", getClass().getName());
    FeedServerClient<Employee> client = new FeedServerClient<Employee>(service, Employee.class);
    URL feedUrl = new URL("http://localhost:8080/resource/employee");
    List<Employee> employees = client.getEntities(feedUrl);
    for (Employee e: employees) {
      System.out.println(e.getId() + ": " + e.getFirstName() + " " + e.getLastName());
    }
  }
}
