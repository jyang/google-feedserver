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

package com.google.feedserver.authentication;


/**
 * The exception class to be used with {@link TokenManager}
 * 
 * @author rakeshs101981@gmail.com (Rakesh Shete)
 * 
 */
public class TokenManagerException extends Exception {
  private final String message;
  private Reason reason;

  public enum Reason {
    UN_AUTHORIZED(401), UNEXPECTED_ERROR(500);

    private final int reasonCode;

    private Reason(int reasonCode) {
      this.reasonCode = reasonCode;
    }

    /**
     * Returns the reason code
     * 
     * @return The reason code
     */
    public int getReasonCode() {
      return reasonCode;
    }

    /**
     * Returns the enum for the given reason code
     * 
     * @param reasonCode The reason code
     * @return The enum for the given reson code
     */
    public Reason getReasonForCode(int reasonCode) {
      for (Reason reason : values()) {
        if (reasonCode == reason.getReasonCode()) {
          return reason;
        }
      }
      return null;
    }
  }


  /**
   * Minimal constructor
   * 
   * @param message The message
   * @param reason The reason for the exception
   * @param t The original cause
   */
  public TokenManagerException(String message, Reason reason, Throwable t) {
    super(t);
    this.message = message;
    this.reason = reason;
  }

  /**
   * Returns the message
   * 
   * @return The message
   */
  @Override
  public String getMessage() {
    return message;
  }

  /**
   * Returns the reason
   * 
   * @return The reason
   */
  public Reason getReason() {
    return this.reason;
  }



}
