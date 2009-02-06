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


import javax.servlet.http.HttpServletRequest;

/**
 * The interface that defines the methods for generating authz tokens and
 * validating the authz token given with a request
 * <p>
 * Concrete implementations can make use of various request parameters & header
 * values to generate authz tokens and validate the same
 * </p>
 * 
 * @author rakeshs101981@gmail.com (Rakesh Shete)
 */
public interface TokenManager {


  /**
   * This method generates a authorization token with the given request
   * parameters and headers
   * 
   * @param request The http request
   * @return The authzToken to be used with each request for authorization
   * 
   * @throws TokenManagerException If authentication fails or if problems are
   *         encountered while generating authz token
   */
  String generateAuthzToken(HttpServletRequest request) throws TokenManagerException;

  /**
   * Validates the authorization token
   * 
   * @param request The http request
   * @return True if authz token validation is successful and false otherwise
   * @throws TokenManagerException If problems are encountered while validating
   *         authz token
   */
  boolean validateAuthzToken(HttpServletRequest request) throws TokenManagerException;

}
