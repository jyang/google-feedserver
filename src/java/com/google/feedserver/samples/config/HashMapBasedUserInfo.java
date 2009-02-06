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

package com.google.feedserver.samples.config;

import com.google.feedserver.config.UserInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Sample hash map based implementation of {@link UserInfo}.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public class HashMapBasedUserInfo implements UserInfo {

  private Map<UserInfoProperties, Object> prefs;

  public HashMapBasedUserInfo() {
    this(new HashMap<UserInfoProperties, Object>());
  }

  public HashMapBasedUserInfo(Map<UserInfoProperties, Object> prefs) {
    this.prefs = new HashMap<UserInfoProperties, Object>(prefs);
  }

  @Override
  public String getCountry() {
    return getStringValue(prefs.get(UserInfoProperties.COUTNRY));
  }

  @Override
  public String getEmail() {
    return getStringValue(prefs.get(UserInfoProperties.EMAIL));
  }

  @Override
  public String getFirstName() {
    return getStringValue(prefs.get(UserInfoProperties.FIRST_NAME));
  }

  @Override
  public String getLanguage() {
    return getStringValue(prefs.get(UserInfoProperties.LANGUAGE));
  }

  @Override
  public String getLastName() {
    return getStringValue(prefs.get(UserInfoProperties.LAST_NAME));
  }

  @Override
  public String getLocale() {
    return getStringValue(prefs.get(UserInfoProperties.LOCALE));
  }

  @Override
  public String getNickName() {
    return getStringValue(prefs.get(UserInfoProperties.NICK_NAME));
  }

  @Override
  public Object getPrefrence(UserInfoProperties property) {
    return prefs.get(property);
  }

  @Override
  public void setCountry(String country) {
    prefs.put(UserInfoProperties.COUTNRY, country);
  }

  @Override
  public void setEmail(String email) {
    prefs.put(UserInfoProperties.EMAIL, email);
  }

  @Override
  public void setFirstName(String name) {
    prefs.put(UserInfoProperties.FIRST_NAME, name);
  }

  @Override
  public void setLanguage(String lang) {
    prefs.put(UserInfoProperties.LANGUAGE, lang);
  }

  @Override
  public void setLastName(String name) {
    prefs.put(UserInfoProperties.LAST_NAME, name);
  }

  @Override
  public void setLocale(String locale) {
    prefs.put(UserInfoProperties.LOCALE, locale);
  }

  @Override
  public void setNickName(String nickName) {
    prefs.put(UserInfoProperties.NICK_NAME, nickName);
  }

  @Override
  public void setPrefrence(UserInfoProperties property, Object value) {
    prefs.put(property, value);
  }

  private String getStringValue(Object value) {
    if (null == value) {
      return null;
    } else {
      return value.toString();
    }
  }
}
