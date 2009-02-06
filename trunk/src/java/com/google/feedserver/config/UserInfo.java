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

package com.google.feedserver.config;


/**
 * Preference for user accessing the feed.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public interface UserInfo {

  /**
   * Enumeration of properties that can be set in {@link UserInfo}.
   * 
   * @author abhinavk@google.com (Abhinav Khandelwal)
   * 
   */
  public enum UserInfoProperties {
    NICK_NAME("nick"), FIRST_NAME("fname"), LAST_NAME("lname"), COUTNRY("country"),
    LANGUAGE("lang"), LOCALE("local"), EMAIL("email");

    private String prefName;

    private UserInfoProperties(String name) {
      this.prefName = name;
    }

    public String getName() {
      return prefName;
    }
  }

  public Object getPrefrence(UserInfoProperties property);

  public String getNickName();

  public String getFirstName();

  public String getLastName();

  public String getCountry();

  public String getLanguage();

  public String getLocale();

  public String getEmail();

  public void setPrefrence(UserInfoProperties property, Object value);

  public void setNickName(String nickName);

  public void setFirstName(String fName);

  public void setLastName(String lName);

  public void setCountry(String country);

  public void setLanguage(String lang);

  public void setLocale(String locale);

  public void setEmail(String email);
}
