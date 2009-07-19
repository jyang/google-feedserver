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
package com.google.feedserver.resource;

/**
 * AuthorizedEntity bean / resource
 */
public class AuthorizedEntity {
  public static final String OPERATION_CREATE = "create";
  public static final String OPERATION_RETRIEVE = "retrieve";
  public static final String OPERATION_UPDATE = "update";
  public static final String OPERATION_DELETE = "delete";

  protected String operation;
  protected String[] entities;

  public AuthorizedEntity() {
    entities = new String[0];
  }

  public AuthorizedEntity(String operation, String[] entities) {
    setOperation(operation);
    setEntities(entities);
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public String[] getEntities() {
    return entities;
  }

  public void setEntities(String[] entities) {
    this.entities = entities == null ? new String[0] : entities;
  }

  public String lookupOperation(char op) {
    switch(op) {
      case 'c': return OPERATION_CREATE;
      case 'r': return OPERATION_RETRIEVE;
      case 'u': return OPERATION_UPDATE;
      case 'd': return OPERATION_DELETE;
        default: return null;
    }
  }
}