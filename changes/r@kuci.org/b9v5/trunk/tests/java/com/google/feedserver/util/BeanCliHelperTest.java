/* Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.feedserver.util;

import junit.framework.TestCase;

/**
 * Tests for the {@link BeanCliHelper} class.
 * 
 * @author rayc@google.com (Ray Colline)
 */
public class BeanCliHelperTest extends TestCase {                                                   
  
  // Test data
  public static final String testBeanConfFile = "<entity>\n" +
      "<option1>test1</option1>\n" +
      "<option2>test2</option2>\n" +
      "<intOption3>3</intOption3>\n" +
      "<boolOption4>true</boolOption4>\n" +
      "</entity>";
  
  public static final String brokenIntegerConfFile = "<entity>\n" +
      "<option1>test1</option1>\n" +
      "<option2>test2</option2>\n" +
      "<intOption3>dafsa</intOption3>\n" +
      "<boolOption4>true</boolOption4>\n" +
      "</entity>";
  
  public static final String brokenBooleanConfFile = "<entity>\n" +
      "<option1>test1</option1>\n" +
      "<option2>test2</option2>\n" +
      "<intOption3>3</intOption3>\n" +
      "<boolOption4>asdfsaf</boolOption4>\n" +
      "</entity>";
  
  public static final String brokenXmlConfFile = "<entity>\n" +
      "<broken>\n" + 
      "<option1>test1</option1>\n" +
      "<option2>test2</option2>\n" +
      "<intOption3>dafsa</intOption3>\n" +
      "<boolOption4>true</boolOption4>\n" +
      "</entity>";
  
  public static final String[] testArgs = { 
      "--option1", "clitest1", 
      "--option2", "clitest2",
      "--intOption3", "6",
      "--noboolOption4" 
  };
  
  public static final String[] incompleteArgs = { 
      "--confFile", "/some/fake/path/to/a/file",
      "--option1", "clitest1", 
      "--noboolOption4" 
  };
  
  public static final String[] extraArgs = { 
      "--option1", "clitest1", 
      "--option2", "clitest2",
      "--intOption3", "clidafsa",
      "--extraArg", "imextrawoot",
      "--boolOption4" 
  };
  
  public static final String[] confFileOnly = {
      "--confFile", "/some/fake/path/to/a/file"
  };
  
  // end Test data.
  
  private TestBean testBean;
  private BeanCliHelper beanCliHelper;
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    testBean = new TestBean();
    beanCliHelper = new BeanCliHelper();
  }
  
  @Override
  protected void tearDown() throws Exception {
    testBean = null;
    beanCliHelper = null;
    super.tearDown();
  }
  
  public void testSuccessfulArgParse() throws ConfigurationBeanException {
    beanCliHelper.register(testBean);
    beanCliHelper.parse(testArgs);
    assertEquals("clitest1", testBean.getOption1());
    assertEquals("clitest2", testBean.getOption2());
    assertEquals(6, testBean.getIntOption3().intValue());
    assertEquals(false, testBean.getBoolOption4().booleanValue());
  }
  
  public void testSuccessfulConfFileOnlyConfig() throws ConfigurationBeanException {
    beanCliHelper.register(testBean);
    beanCliHelper.setTestFileContents(testBeanConfFile);
    beanCliHelper.parse(confFileOnly);
    assertEquals("test1", testBean.getOption1());
    assertEquals("test2", testBean.getOption2());
    assertEquals(3, testBean.getIntOption3().intValue());
    assertEquals(true, testBean.getBoolOption4().booleanValue());
  }
  
  public void testSuccessfulConfFileAndArgs() throws ConfigurationBeanException {
    beanCliHelper.register(testBean);
    beanCliHelper.setTestFileContents(testBeanConfFile);
    beanCliHelper.parse(incompleteArgs);
    // From Args
    assertEquals("clitest1", testBean.getOption1());
    // From Conf file
    assertEquals("test2", testBean.getOption2());
    // From Conf file
    assertEquals(3, testBean.getIntOption3().intValue());
    // From Args
    assertEquals(false, testBean.getBoolOption4().booleanValue());
  }
  
  public void testExtraArgs() {
    beanCliHelper.register(testBean);
    try {
      beanCliHelper.parse(extraArgs);
    } catch (ConfigurationBeanException e) {
      assertTrue(e.getCause().getMessage().startsWith("Unrecognized"));
      return;
    }
    fail("Expected Unrecognized option error.");
  }
  
  public void testBrokenIntValue() {
    beanCliHelper.register(testBean);
    beanCliHelper.setTestFileContents(brokenIntegerConfFile);
    try {
      beanCliHelper.parse(confFileOnly);
    } catch (ConfigurationBeanException e) {
      assertTrue(e.getCause() instanceof IllegalArgumentException);
      return;
    }
    fail("Excepted conversion error");
  }
  
  /**
   * Test bean that is used in the {@link BeanCliHelperTest} unit test.
   * 
   * @author rayc@google.com (Ray Colline)
   */
  public static class TestBean {

    @ConfigFile
    @Flag
    private String confFile;
    
    @Flag
    private String option1;
    
    @Flag
    private String option2;
    
    @Flag
    private Integer intOption3;
    
    @Flag
    private Boolean boolOption4;
    
    public String getConfFile() {
      return confFile;
    }
    public void setConfFile(String confFileTest) {
      this.confFile= confFileTest;
    }
    public String getOption1() {
      return option1;
    }
    public void setOption1(String option1) {
      this.option1 = option1;
    }
    public String getOption2() {
      return option2;
    }
    public void setOption2(String option2) {
      this.option2 = option2;
    }
    public Integer getIntOption3() {
      return intOption3;
    }
    public void setIntOption3(Integer intOption3) {
      this.intOption3 = intOption3;
    }
    public Boolean getBoolOption4() {
      return boolOption4;
    }
    public void setBoolOption4(Boolean boolOption4) {
      this.boolOption4 = boolOption4;
    }
  }
}
