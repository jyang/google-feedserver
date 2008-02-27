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
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

public class LdapQueryRequest {

  private String[] requiredAttributes;
  private String baseDn;
  private String filter;
  private String scope;
  private LdapServerConfiguration config;
  
  private static final Executor executor = Executors.newCachedThreadPool();
  
  private static Logger logger = Logger.getLogger(
      LdapQueryRequest.class.getCanonicalName());
  
  public LdapQueryRequest(String base, String scope, String filter,
      String[] attrs, LdapServerConfiguration config) {
    logger.info("Create Query Request " + base);
    baseDn = base;
    this.scope = scope;
    this.filter = filter;
    this.config = config;
    logger.info("Config is " + config.getRoleDn());
    requiredAttributes = attrs;
  }
  
  public List<LdapQueryResponse> processRequest() throws LdapQueryException {
    CompletionService<LdapQueryException> completion = 
        new ExecutorCompletionService<LdapQueryException>(executor);
    LdapAuthenticationContext context = new LdapAuthenticationContext(config,
        this);
    LdapAuthenticationTask task = new LdapAuthenticationTask();
    task.initialize(context);
    completion.submit(task);
    Future<LdapQueryException> exceptionResult = null;
    try {
      exceptionResult = completion.take();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return null;
    }
    if (exceptionResult != null) {
      try {
        exceptionResult.get();
      } catch (InterruptedException ex) {
        ex.printStackTrace();
        throw new LdapQueryException(ex.toString());
      } catch (ExecutionException ex) {
        ex.printStackTrace();
        throw new LdapQueryException(ex.toString());
      }
      return context.getResponses(); 
    }
    throw new LdapQueryException("Invalid processing of query");
  }
  
  private class LdapAuthenticationContext {
    LdapServerConfiguration config;
    LdapQueryRequest request;
    List<LdapQueryResponse> responses = null;
    
    public LdapAuthenticationContext(LdapServerConfiguration config,
        LdapQueryRequest request) {
      this.config = config;
      this.request = request;
      responses = null;
    }
    
    public List<LdapQueryResponse> getResponses() {
      return responses;
    }
  }
  
  private class LdapAuthenticationTask 
      implements Callable<LdapQueryException> {

    LdapAuthenticationContext context;
    
    public void initialize(LdapAuthenticationContext context) {
      this.context = context;
    }
    
    public LdapQueryException call() throws Exception {
      logger.info("Starting ldap query ");
      logger.info("Starting ldap query " + config.getRoleDn());
      Hashtable<String, String> env = new Hashtable<String, String>();
      env.put(
          Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory"
      );
      env.put(Context.REFERRAL, "follow");
      
      env.put(Context.SECURITY_AUTHENTICATION, "simple");
      env.put(Context.SECURITY_PRINCIPAL, context.config.getRoleDn());
      env.put(Context.SECURITY_CREDENTIALS, context.config.getRolePassword());
      
      String url = config.getProviderUrl();
      if (baseDn != null) {
        url += baseDn;
      }
      env.put(Context.PROVIDER_URL, url);
      logger.info("Provide URL is " + url);
      LdapContext ldapContext = null;
      ldapContext = new InitialLdapContext(env, null);
      
      try {
        logger.info("Starting ldap search query" + requiredAttributes);
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setReturningAttributes(requiredAttributes);
        NamingEnumeration<SearchResult> ldapResults = ldapContext.search("",
            filter, controls);
        context.responses = new ArrayList<LdapQueryResponse>();
        while (ldapResults.hasMore()) {
          SearchResult result = ldapResults.next();
          logger.info("ldap search result " + result);
          Attributes attrs = result.getAttributes();
          NamingEnumeration<? extends Attribute> all = attrs.getAll();
          LdapQueryResponse response = new LdapQueryResponse(
              result.getNameInNamespace());
          while (all.hasMore()) {
            Attribute attr = all.next();
            logger.info("ldap search result " + attr.getID() + ":" 
                         + (String) attr.get(0));
            response.addAttribute(attr.getID(), (String) attr.get(0));
          }
          context.responses.add(response);
        }
      } catch (NameNotFoundException ex) {
        ex.printStackTrace();
        LdapQueryException e = new LdapQueryException(ex.toString());
        return e;
      }
      logger.info("Ending ldap query");
      return null;
    }
  }
  
  private class LdapQueryTask implements Callable<LdapQueryException> {

    public LdapQueryException call() throws Exception {
      // TODO Auto-generated method stub
      return null;
    }
  }
}

