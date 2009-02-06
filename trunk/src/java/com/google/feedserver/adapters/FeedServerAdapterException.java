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

package com.google.feedserver.adapters;


/**
 * Exceptions thrown by feedserver adapters.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public class FeedServerAdapterException extends Exception {
  /**
   * Reason for {@link FeedServerAdapterException}.
   * 
   * @author abhinavk@google.com (Abhinav Khandelwal)
   * 
   */
  public enum Reason {
    OK(0), OPERATION_NOT_SUPPORTED(50), COMMUNICATION_FAILURE(100), REMOTE_SERVER_ERROR(200),
    BAD_RESPONSE_FROM_REMOTE_SERVER(201), ENTRY_ALREADY_EXISTS(300), ENTRY_DOES_NOT_EXIST(301),
    INVALID_INPUT(400), NOT_AUTHORIZED(402), ADAPTER_CONFIGURATION_NOT_CORRECT(500),
    ERROR_EXECUTING_ADAPTER_REQUEST(501), FEED_CONFIGURATION_NOT_CORRECT(600),
    BAD_FEED_TYPE_CONFIG(601), MIXIN_ERROR_PARSING_CONFIGURATION(700),
    MIXIN_TARGET_ADAPTER_NOT_CONFIGURED(701), MIXIN_WRAPPER_NOT_CONFIGURED(702),
    MIXIN_WRAPPER_CONFIGURATION_NOT_FOUND(703), MIXIN_ERROR_APPLYING_WRAPPER(704), UNKNOWN_ERROR(
        1000);

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

  public FeedServerAdapterException(Reason reason, String msg) {
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
