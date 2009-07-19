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
package com.google.feedserver.wrappers;

import static org.easymock.classextension.EasyMock.isA;

import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.server.RequestContext;
import org.easymock.classextension.EasyMock;

import com.google.feedserver.adapters.AbstractManagedCollectionAdapter;
import com.google.feedserver.adapters.FeedServerAdapterException;
import com.google.feedserver.config.UserInfo;
import com.google.feedserver.resource.AuthorizedEntity;

import junit.framework.TestCase;

public class ResourceConnectionInfoWrapperTest extends TestCase {
  public static final String USER1_EMAIL = "user1@example.com";
  public static final String USER2_EMAIL = "user2@example.com";

  public static final String WRAPPER_CONFIG_BEFORE = "<entity><acl repeatable='true'>" +
      "<resourceInfo><resourceRule>/</resourceRule></resourceInfo>" +
      "<authorizedEntities repeatable='true'>";
  public static final String WRAPPER_CONFIG_AFTER = "</authorizedEntities></acl></entity>";

  AbstractManagedCollectionAdapter targetAdapterMock;
  RequestContext requestMock;
  UserInfo userInfoMock;
  Entry entryMock;

  protected static interface AdapterCall {
    public void invoke(AbstractManagedCollectionAdapter adapter) throws FeedServerAdapterException;
  }

  protected void prepare(AdapterCall adapterCall) throws Exception {
    targetAdapterMock = EasyMock.createMock(AbstractManagedCollectionAdapter.class);
    requestMock = EasyMock.createMock(RequestContext.class);
    userInfoMock = EasyMock.createMock(UserInfo.class);
    entryMock = EasyMock.createMock(Entry.class);

    EasyMock.expect(targetAdapterMock.getAbdera()).andReturn(null);
    EasyMock.expect(targetAdapterMock.getConfiguration()).andReturn(null);
    EasyMock.expect(requestMock.getAttribute(
        RequestContext.Scope.REQUEST, AbstractManagedCollectionAdapter.USER_INFO))
        .andReturn(userInfoMock);
    EasyMock.expect(userInfoMock.getEmail()).andReturn(USER1_EMAIL);
    adapterCall.invoke(targetAdapterMock);

    EasyMock.replay(targetAdapterMock);
    EasyMock.replay(requestMock);
    EasyMock.replay(userInfoMock);
    EasyMock.replay(entryMock);
  }

  protected void finish() throws Exception {
    EasyMock.verify(targetAdapterMock);
	EasyMock.verify(requestMock);
	EasyMock.verify(userInfoMock);
	EasyMock.verify(entryMock);

	super.tearDown();
  }

  protected String getWrapperConfig(String operation, String[] principals) {
    StringBuilder wrapperConfig = new StringBuilder();
    wrapperConfig.append(WRAPPER_CONFIG_BEFORE);

    if (operation != null) {
      wrapperConfig.append("<operation>");
      wrapperConfig.append(operation);
      wrapperConfig.append("</operation>");
    }

    if (principals != null) {
      for (String principal: principals) {
        wrapperConfig.append("<entities repeatable='true'>");
        wrapperConfig.append(principal);
        wrapperConfig.append("</entities>");
      }
    }

    wrapperConfig.append(WRAPPER_CONFIG_AFTER);
    return wrapperConfig.toString();
  }

  // create

  public void testCheckAccessSuccessOnCreateEntry() throws Exception {
    String[][] principalx = new String[][] {
        new String[]{USER1_EMAIL}, new String[]{USER1_EMAIL, USER2_EMAIL}};
    for (String[] principals: principalx) {
      prepare(new AdapterCall() {
        @Override
        public void invoke(AbstractManagedCollectionAdapter adapter)
            throws FeedServerAdapterException {
          EasyMock.expect(adapter.createEntry(isA(RequestContext.class), isA(Entry.class)))
              .andReturn(null);
        }
      });

      ResourceConnectionInfoWrapper aclWrapper =
      	new ResourceConnectionInfoWrapper(targetAdapterMock, getWrapperConfig(
              AuthorizedEntity.OPERATION_CREATE, principals));
      aclWrapper.createEntry(requestMock, entryMock);

      finish();
    }
  }

  public void testCheckAccessFailureOnCreateEntry() throws Exception {
    String[][] principalx = new String[][] {new String[]{USER2_EMAIL}, new String[]{}};
    for (String[] principals: principalx) {
      prepare(new AdapterCall() {
        @Override
        public void invoke(AbstractManagedCollectionAdapter adapter) {}
      });

      ResourceConnectionInfoWrapper aclWrapper =
          new ResourceConnectionInfoWrapper(targetAdapterMock, getWrapperConfig(
              AuthorizedEntity.OPERATION_CREATE, principals));
      try {
        aclWrapper.createEntry(requestMock, entryMock);
        fail("access should have been denied");
      } catch (FeedServerAdapterException e) {
        // expected
      }

      finish();
    }
  }

  // retrieve

  public void testCheckAccessSuccessOnRetrieveEntry() throws Exception {
    String[][] principalx = new String[][] {
        new String[]{USER1_EMAIL}, new String[]{USER1_EMAIL, USER2_EMAIL}};
    for (String[] principals: principalx) {
      prepare(new AdapterCall() {
        @Override
        public void invoke(AbstractManagedCollectionAdapter adapter)
            throws FeedServerAdapterException {
          EasyMock.expect(adapter.retrieveEntry(isA(RequestContext.class), isA(Object.class)))
              .andReturn(null);
        }
      });

      ResourceConnectionInfoWrapper aclWrapper =
      	new ResourceConnectionInfoWrapper(targetAdapterMock, getWrapperConfig(
              AuthorizedEntity.OPERATION_RETRIEVE, principals));
      aclWrapper.retrieveEntry(requestMock, "123");

      finish();
    }
  }

  public void testCheckAccessFailureOnRetrieveEntry() throws Exception {
    String[][] principalx = new String[][] {new String[]{USER2_EMAIL}, new String[]{}};
    for (String[] principals: principalx) {
      prepare(new AdapterCall() {
        @Override
        public void invoke(AbstractManagedCollectionAdapter adapter) {}
      });

      ResourceConnectionInfoWrapper aclWrapper =
          new ResourceConnectionInfoWrapper(targetAdapterMock, getWrapperConfig(
              AuthorizedEntity.OPERATION_RETRIEVE, principals));
      try {
        aclWrapper.retrieveEntry(requestMock, "123");
        fail("access should have been denied");
      } catch (FeedServerAdapterException e) {
        // expected
      }

      finish();
    }
  }

  // update

  public void testCheckAccessSuccessOnUpdateEntry() throws Exception {
    String[][] principalx = new String[][] {
        new String[]{USER1_EMAIL}, new String[]{USER1_EMAIL, USER2_EMAIL}};
    for (String[] principals: principalx) {
      prepare(new AdapterCall() {
        @Override
        public void invoke(AbstractManagedCollectionAdapter adapter)
            throws FeedServerAdapterException {
          EasyMock.expect(adapter.updateEntry(
              isA(RequestContext.class), isA(Object.class), isA(Entry.class))).andReturn(null);
        }
      });

      ResourceConnectionInfoWrapper aclWrapper =
      	new ResourceConnectionInfoWrapper(targetAdapterMock, getWrapperConfig(
              AuthorizedEntity.OPERATION_UPDATE, principals));
      aclWrapper.updateEntry(requestMock, "123", entryMock);

      finish();
    }
  }

  public void testCheckAccessFailureOnUpdateEntry() throws Exception {
    String[][] principalx = new String[][] {new String[]{USER2_EMAIL}, new String[]{}};
    for (String[] principals: principalx) {
      prepare(new AdapterCall() {
        @Override
        public void invoke(AbstractManagedCollectionAdapter adapter) {}
      });

      ResourceConnectionInfoWrapper aclWrapper =
          new ResourceConnectionInfoWrapper(targetAdapterMock, getWrapperConfig(
              AuthorizedEntity.OPERATION_UPDATE, principals));
      try {
        aclWrapper.updateEntry(requestMock, "123", entryMock);
        fail("access should have been denied");
      } catch (FeedServerAdapterException e) {
        // expected
      }

      finish();
    }
  }

  // delete

  public void testCheckAccessSuccessOnDeleteEntry() throws Exception {
    String[][] principalx = new String[][] {
        new String[]{USER1_EMAIL}, new String[]{USER1_EMAIL, USER2_EMAIL}};
    for (String[] principals: principalx) {
      prepare(new AdapterCall() {
        @Override
        public void invoke(AbstractManagedCollectionAdapter adapter)
            throws FeedServerAdapterException {
          adapter.deleteEntry(isA(RequestContext.class), isA(Object.class));
        }
      });

      ResourceConnectionInfoWrapper aclWrapper =
      	new ResourceConnectionInfoWrapper(targetAdapterMock, getWrapperConfig(
              AuthorizedEntity.OPERATION_DELETE, principals));
      aclWrapper.deleteEntry(requestMock, "123");

      finish();
    }
  }

  public void testCheckAccessFailureOnDeleteEntry() throws Exception {
    String[][] principalx = new String[][] {new String[]{USER2_EMAIL}, new String[]{}};
    for (String[] principals: principalx) {
      prepare(new AdapterCall() {
        @Override
        public void invoke(AbstractManagedCollectionAdapter adapter) {}
      });

      ResourceConnectionInfoWrapper aclWrapper =
          new ResourceConnectionInfoWrapper(targetAdapterMock, getWrapperConfig(
              AuthorizedEntity.OPERATION_DELETE, principals));
      try {
        aclWrapper.deleteEntry(requestMock, "123");
        fail("access should have been denied");
      } catch (FeedServerAdapterException e) {
        // expected
      }

      finish();
    }
  }

  // query/list

  public void testCheckAccessSuccessOnRetrieveFeed() throws Exception {
    String[][] principalx = new String[][] {
        new String[]{USER1_EMAIL}, new String[]{USER1_EMAIL, USER2_EMAIL}};
    for (String[] principals: principalx) {
      prepare(new AdapterCall() {
        @Override
        public void invoke(AbstractManagedCollectionAdapter adapter)
            throws FeedServerAdapterException {
          EasyMock.expect(adapter.retrieveFeed(isA(RequestContext.class)))
              .andReturn(null);
        }
      });

      ResourceConnectionInfoWrapper aclWrapper =
      	new ResourceConnectionInfoWrapper(targetAdapterMock, getWrapperConfig(
              AuthorizedEntity.OPERATION_RETRIEVE, principals));
      aclWrapper.retrieveFeed(requestMock);

      finish();
    }
  }

  public void testCheckAccessFailureOnRetrieveFeed() throws Exception {
    String[][] principalx = new String[][] {new String[]{USER2_EMAIL}, new String[]{}};
    for (String[] principals: principalx) {
      prepare(new AdapterCall() {
        @Override
        public void invoke(AbstractManagedCollectionAdapter adapter) {}
      });

      ResourceConnectionInfoWrapper aclWrapper =
          new ResourceConnectionInfoWrapper(targetAdapterMock, getWrapperConfig(
              AuthorizedEntity.OPERATION_RETRIEVE, principals));
      try {
        aclWrapper.retrieveFeed(requestMock);
        fail("access should have been denied");
      } catch (FeedServerAdapterException e) {
        // expected
      }

      finish();
    }
  }
}
