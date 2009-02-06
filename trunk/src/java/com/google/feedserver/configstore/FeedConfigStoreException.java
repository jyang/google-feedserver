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

package com.google.feedserver.configstore;


/**
 * Exceptions for {@link FeedConfigStore}.
 * 
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 */
public class FeedConfigStoreException extends Exception {
  /**
   * Reason for {@link FeedConfigStoreException}.
   * 
   * @author abhinavk@gmail.com (Abhinav Khandelwal)
   * 
   */
  public enum Reason {
    OK(0), FEED_DOES_NOT_EXIST(100), DOMAIN_NOT_ALLOWED(200), DOMAIN_NOT_FOUND(201),
    INTERNAL_ERROR(300), ADAPTER_CONFIG_DOES_NOT_EXIST(400), INVALID_ADAPTER_CONFIGURATION(401),
    ADAPTER_DOES_NOT_EXIST(402), UNKNOWN_ERROR(500);

    private final int reasonCode;

    private Reason(int reasonCode) {
      this.reasonCode = reasonCode;
    }

    public int getReasonCode() {
      return reasonCode;
    }

    public static Reason getReasonFromCode(int reasonCode) {
      for (Reason reason : values()) {
        if (reason.getReasonCode() == reasonCode) {
          return reason;
        }
      }
      return null;
    }
  }

  private final Reason reason;
  private final String message;

  public FeedConfigStoreException(Reason reason, String msg) {
    this.reason = reason;
    this.message = msg;
  }

  public FeedConfigStoreException(Reason reason, String msg, Throwable t) {
    super(msg, t);
    this.reason = reason;
    this.message = msg;
  }

  @Override
  public String getMessage() {
    return this.message;
  }

  public Reason getReason() {
    return this.reason;
  }
}
