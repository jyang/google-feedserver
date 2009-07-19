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

package com.google.feedserver.tools;

import com.google.feedserver.resource.Acl;
import com.google.feedserver.resource.AuthorizedEntity;
import com.google.feedserver.resource.ResourceInfo;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class FeedServerClientAclToolTest extends TestCase {
  protected static final String USER0 = "user0@example.com";
  protected static final String USER1 = "user1@example.com";
  protected static final String USER9 = "user9@example.com";

  protected FeedServerClientAclTool aclTool;
  protected AuthorizedEntity authorizedEntity_r_0;
  protected AuthorizedEntity authorizedEntity_r_0_1;
  protected AuthorizedEntity authorizedEntity_u_0;
  protected AuthorizedEntity authorizedEntity_r_9;
  protected AuthorizedEntity authorizedEntity_u_9;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    aclTool  = new FeedServerClientAclTool();
    authorizedEntity_r_0 = new AuthorizedEntity(AuthorizedEntity.OPERATION_RETRIEVE, new String[]{USER0});
    authorizedEntity_r_0_1 = new AuthorizedEntity(
        AuthorizedEntity.OPERATION_RETRIEVE, new String[]{USER0, USER1});
    authorizedEntity_u_0 = new AuthorizedEntity(AuthorizedEntity.OPERATION_UPDATE, new String[]{USER0});
    authorizedEntity_r_9 = new AuthorizedEntity(AuthorizedEntity.OPERATION_RETRIEVE, new String[]{USER9});
    authorizedEntity_u_9 = new AuthorizedEntity(AuthorizedEntity.OPERATION_UPDATE, new String[]{USER9});
  }

  protected static void assertAuthorizedEntityEquals(AuthorizedEntity ae1, AuthorizedEntity ae2) {
    assertEquals(ae1.getOperation(), ae2.getOperation());
    assertEquals(ae1.getEntities().length, ae2.getEntities().length);
    for (int i = 0; i < ae1.getEntities().length; i++) {
      assertEquals(ae1.getEntities()[i], ae2.getEntities()[i]);
    }
  }

  /**
   * Empty authorized entities.  Add one principal with one operation.  Should get one authorized
   * entity with one principal.
   */
  public void testAddAcl0() {
    Acl acl = new Acl("acl0", new ResourceInfo("resourceRule0", ResourceInfo.RESOURCE_TYPE_FEED), null);
    aclTool.addAcl(Arrays.asList(acl), 'r', USER9);

    assertEquals(1, acl.getAuthorizedEntities().length);
    assertAuthorizedEntityEquals(authorizedEntity_r_9, acl.getAuthorizedEntities()[0]);
  }

  /**
   * Adding the same twice to empty should give you just one.
   */
  public void testAddAcl1() {
    Acl acl = new Acl("acl0", new ResourceInfo("resourceRule0", ResourceInfo.RESOURCE_TYPE_FEED),
        new AuthorizedEntity[0]);
    List<Acl> acls = Arrays.asList(acl);
    aclTool.addAcl(acls, 'r', USER9);
    aclTool.addAcl(acls, 'r', USER9);

    assertEquals(1, acl.getAuthorizedEntities().length);
    assertAuthorizedEntityEquals(authorizedEntity_r_9, acl.getAuthorizedEntities()[0]);
  }

  public void testAddAcl2() {
    Acl acl = new Acl("acl0", new ResourceInfo("resourceRule0", ResourceInfo.RESOURCE_TYPE_FEED), null);
    List<Acl> acls = Arrays.asList(acl);
    aclTool.addAcl(acls, 'r', USER9);
    aclTool.removeAcl(acls, 'u', USER9);

    assertEquals(1, acl.getAuthorizedEntities().length);
    assertAuthorizedEntityEquals(authorizedEntity_r_9, acl.getAuthorizedEntities()[0]);
  }

  /**
   * Empty authorized entities.  Add one principal with two operations.  Should get two authorized
   * entities each with one principal.
   */
  public void testAddAcl3() {
    Acl acl = new Acl("acl0", new ResourceInfo("resourceRule0", ResourceInfo.RESOURCE_TYPE_FEED), null);
    List<Acl> acls = Arrays.asList(acl);
    aclTool.addAcl(acls, 'r', USER9);
    aclTool.addAcl(acls, 'u', USER9);

    assertEquals(2, acl.getAuthorizedEntities().length);
    assertAuthorizedEntityEquals(authorizedEntity_r_9, acl.getAuthorizedEntities()[0]);
    assertAuthorizedEntityEquals(authorizedEntity_u_9, acl.getAuthorizedEntities()[1]);
  }

  /**
   * One authorized entity with one principal.  Add another principal.  Should get one authorized
   * entity with two principals.
   */
  public void testAddAcl4() {
    Acl acl = new Acl("acl0", new ResourceInfo("resourceRule0", ResourceInfo.RESOURCE_TYPE_FEED),
        new AuthorizedEntity[] {authorizedEntity_r_0});
    aclTool.addAcl(Arrays.asList(acl), 'r', USER9);

    assertEquals(1, acl.getAuthorizedEntities().length);

    assertEquals(2, acl.getAuthorizedEntities()[0].getEntities().length);
    assertAuthorizedEntityEquals(authorizedEntity_r_0, acl.getAuthorizedEntities()[0]);
    assertAuthorizedEntityEquals(authorizedEntity_r_9, acl.getAuthorizedEntities()[1]);
  }

  /**
   * One authorized entity with two principals.  Add another principal.  Should get one authorized
   * entity with three principals.
   */
  public void testAddAcl5() {
    Acl acl = new Acl("acl0", new ResourceInfo("resourceRule0", ResourceInfo.RESOURCE_TYPE_FEED),
        new AuthorizedEntity[] {authorizedEntity_r_0_1});
    aclTool.addAcl(Arrays.asList(acl), 'r', USER9);

    assertEquals(1, acl.getAuthorizedEntities().length);
    assertEquals(3, acl.getAuthorizedEntities()[0].getEntities().length);
    assertEquals(USER0, acl.getAuthorizedEntities()[0].getEntities()[0]);
    assertEquals(USER1, acl.getAuthorizedEntities()[0].getEntities()[1]);
    assertEquals(USER9, acl.getAuthorizedEntities()[0].getEntities()[2]);
  }

  /**
   * Two authorized entities, one with two principals and the other one principal.  Add another
   * principal.  First authorized entity should have three principals and second unchanged.
   */
  public void testAddAcl6() {
    Acl acl = new Acl("acl0", new ResourceInfo("resourceRule0", ResourceInfo.RESOURCE_TYPE_FEED),
        new AuthorizedEntity[] {authorizedEntity_r_0_1, authorizedEntity_u_0});
    aclTool.addAcl(Arrays.asList(acl), 'r', USER9);

    assertEquals(2, acl.getAuthorizedEntities().length);

    assertEquals(AuthorizedEntity.OPERATION_RETRIEVE, acl.getAuthorizedEntities()[0].getOperation());
    assertEquals(3, acl.getAuthorizedEntities()[0].getEntities().length);
    assertEquals(USER0, acl.getAuthorizedEntities()[0].getEntities()[0]);
    assertEquals(USER1, acl.getAuthorizedEntities()[0].getEntities()[1]);
    assertEquals(USER9, acl.getAuthorizedEntities()[0].getEntities()[2]);

    assertEquals(AuthorizedEntity.OPERATION_UPDATE, acl.getAuthorizedEntities()[1].getOperation());
    assertEquals(1, acl.getAuthorizedEntities()[1].getEntities().length);
    assertEquals(USER0, acl.getAuthorizedEntities()[1].getEntities()[0]);
  }

  /**
   * Removing from empty should result in empty still.
   */
  public void testRemoveAcl0() {
    Acl acl = new Acl("acl0", new ResourceInfo("resourceRule0", ResourceInfo.RESOURCE_TYPE_FEED),
        new AuthorizedEntity[0]);
    aclTool.removeAcl(Arrays.asList(acl), 'r', USER9);

    assertEquals(0, acl.getAuthorizedEntities().length);
  }

  /**
   * Removing a non-existing principal should be no-op. 
   */
  public void testRemoveAcl1() {
    Acl acl = new Acl("acl0", new ResourceInfo("resourceRule0", ResourceInfo.RESOURCE_TYPE_FEED),
        new AuthorizedEntity[]{authorizedEntity_r_0});
    aclTool.removeAcl(Arrays.asList(acl), 'r', USER9);

    assertEquals(1, acl.getAuthorizedEntities().length);
    assertEquals(1, acl.getAuthorizedEntities()[0].getEntities().length);
    assertEquals(USER0, acl.getAuthorizedEntities()[0].getEntities()[0]);
  }

  /**
   * Removing a principal with non-existing operation should be no-op. 
   */
  public void testRemoveAcl2() {
    Acl acl = new Acl("acl0", new ResourceInfo("resourceRule0", ResourceInfo.RESOURCE_TYPE_FEED),
        new AuthorizedEntity[]{authorizedEntity_r_0});
    aclTool.removeAcl(Arrays.asList(acl), 'd', USER0);

    assertEquals(1, acl.getAuthorizedEntities().length);
    assertEquals(1, acl.getAuthorizedEntities()[0].getEntities().length);
    assertEquals(USER0, acl.getAuthorizedEntities()[0].getEntities()[0]);
  }

  /**
   * Two authorized entities with one principal each.  Remove another principal.  Should be
   * unchanged.
   */
  public void testRemoveAcl3() {
    Acl acl = new Acl("acl0", new ResourceInfo("resourceRule0", ResourceInfo.RESOURCE_TYPE_FEED),
        new AuthorizedEntity[] {authorizedEntity_r_0, authorizedEntity_u_0});
    aclTool.removeAcl(Arrays.asList(acl), 'r', USER9);

    assertEquals(2, acl.getAuthorizedEntities().length);

    assertEquals(1, acl.getAuthorizedEntities()[0].getEntities().length);
    assertEquals(AuthorizedEntity.OPERATION_RETRIEVE, acl.getAuthorizedEntities()[0].getOperation());
    assertEquals(USER0, acl.getAuthorizedEntities()[0].getEntities()[0]);

    assertEquals(1, acl.getAuthorizedEntities()[1].getEntities().length);
    assertEquals(AuthorizedEntity.OPERATION_UPDATE, acl.getAuthorizedEntities()[1].getOperation());
    assertEquals(USER0, acl.getAuthorizedEntities()[1].getEntities()[0]);
  }

  /**
   * One authorized entity with one principal.  Remove that principal.  Should get zero authorized
   * entities.
   */
  public void testRemoveAcl4() {
    Acl acl = new Acl("acl0", new ResourceInfo("resourceRule0", ResourceInfo.RESOURCE_TYPE_FEED),
        new AuthorizedEntity[] {authorizedEntity_r_0});
    aclTool.removeAcl(Arrays.asList(acl), 'r', USER0);

    assertEquals(0, acl.getAuthorizedEntities().length);
  }

  /**
   * One authorized entity with two principals.  Remove second principal.  Should get one
   * authorized entity of first principal.
   */
  public void testRemoveAcl5() {
    Acl acl = new Acl("acl0", new ResourceInfo("resourceRule0", ResourceInfo.RESOURCE_TYPE_FEED),
        new AuthorizedEntity[] {authorizedEntity_r_0_1});
    aclTool.removeAcl(Arrays.asList(acl), 'r', USER1);

    assertEquals(1, acl.getAuthorizedEntities().length);
    assertEquals(1, acl.getAuthorizedEntities()[0].getEntities().length);
    assertEquals(USER0, acl.getAuthorizedEntities()[0].getEntities()[0]);
  }

  /**
   * One authorized entity with two principals.  Remove first principal.  Should get one
   * authorized entity of second principal.
   */
  public void testRemoveAcl6() {
    Acl acl = new Acl("acl0", new ResourceInfo("resourceRule0", ResourceInfo.RESOURCE_TYPE_FEED),
        new AuthorizedEntity[] {authorizedEntity_r_0_1});
    aclTool.removeAcl(Arrays.asList(acl), 'r', USER0);

    assertEquals(1, acl.getAuthorizedEntities().length);
    assertEquals(1, acl.getAuthorizedEntities()[0].getEntities().length);
    assertEquals(USER1, acl.getAuthorizedEntities()[0].getEntities()[0]);
  }
}
