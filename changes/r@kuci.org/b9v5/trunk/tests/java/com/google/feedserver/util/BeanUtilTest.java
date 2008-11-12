// Copyright 2008 Google Inc.  All Rights Reserved.

package com.google.feedserver.util;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * BeanUtil tests.
 *
 * @author jyang@google.com (Jun Yang)
 */
public class BeanUtilTest extends TestCase {

  protected BeanUtil beanUtil = new BeanUtil();

  public static class InternalBean {
    protected String string;
    protected String[] strings;

    public String getString() {
      return string;
    }

    public void setString(String value) {
      string = value;
    }

    public String[] getStrings() {
      return strings;
    }

    public void setStrings(String[] values) {
      strings = values;
    }
  }

  public static class Bean {

    protected String string;
    protected int integer;
    protected long longInteger;
    protected String[] strings;
    protected int[] integers;
    protected long[] longIntegers;
    protected InternalBean internalBean;
    protected InternalBean[] internalBeans;

    public String getString() {
      return string;
    }

    public void setString(String value) {
      string = value;
    }

    public int getInt() {
      return integer;
    }

    public void setInt(int value) {
      integer = value;
    }

    public long getLong() {
      return longInteger;
    }

    public void setLong(long value) {
      longInteger = value;
    }

    public String[] getStrings() {
      return strings;
    }

    public void setStrings(String[] values) {
      strings = values;
    }

    public int[] getInts() {
      return integers;
    }

    public void setInts(int[] values) {
      integers = values;
    }

    public long[] getLongs() {
      return longIntegers;
    }

    public void setLongs(long[] values) {
      longIntegers = values;
    }

    public InternalBean getInternalBean() {
      return internalBean;
    }

    public void setInternalBean(InternalBean internalBean) {
      this.internalBean = internalBean;
    }

    public InternalBean[] getInternalBeans() {
      return internalBeans;
    }

    public void setInternalBeans(InternalBean[] internalBeans) {
      this.internalBeans = internalBeans;
    }
  }

  public void testInvalidIntegerThrowsException() throws Exception {
    Bean bean = new Bean();
    bean.setString("test");
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put("int", "invalidint");
    properties.put("long", 5678L);
    properties.put("strings", new String[]{"string0", "string1"});
    properties.put("ints", new int[]{100, 200});
    properties.put("longs", new long[]{300L, 400L});
    try {
      beanUtil.convertPropertiesToBean(properties, bean);
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().startsWith("Conversion"));
      return;
    }
    fail("did not get illegal argument exception");
  }

  public void testUnsetPropertiesShouldRetainDefaults() throws Exception {
    Bean bean = new Bean();
    bean.setString("test");
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put("int", 1234);
    properties.put("long", 5678L);
    properties.put("strings", new String[]{"string0", "string1"});
    properties.put("ints", new int[]{100, 200});
    properties.put("longs", new long[]{300L, 400L});

    Map<String, Object> internalProperties = new HashMap<String, Object>();
    internalProperties.put("strings", new String[]{"string0", "string1"});

    properties.put("internalBean", internalProperties);
    properties.put("internalBeans", new Map[] {internalProperties});

    beanUtil.convertPropertiesToBean(properties, bean);
    // Check we left our previously set value
    assertTrue("test".equals(bean.getString()));

    // Check that the rest of the values were set ok.
    assertEquals(1234, bean.getInt());
    assertEquals(5678L, bean.getLong());

    String[] strings = bean.getStrings();
    assertEquals(2, strings.length);
    assertEquals("string0", strings[0]);
    assertEquals("string1", strings[1]);

    int[] ints = bean.getInts();
    assertEquals(2, ints.length);
    assertEquals(100, ints[0]);
    assertEquals(200, ints[1]);

    long[] longs = bean.getLongs();
    assertEquals(2, longs.length);
    assertEquals(300L, longs[0]);
    assertEquals(400L, longs[1]);

    InternalBean internalBean = bean.getInternalBean();
    strings = internalBean.getStrings();
    assertEquals(2, strings.length);
    assertEquals("string0", strings[0]);
    assertEquals("string1", strings[1]);


    InternalBean[] internalBeans = bean.getInternalBeans();
    strings = internalBeans[0].getStrings();
    assertEquals(2, strings.length);
    assertEquals("string0", strings[0]);
    assertEquals("string1", strings[1]);
  }

  @SuppressWarnings("unchecked")
  public void testConvertBeanToProperties() throws Exception {
    Bean bean = new Bean();
    bean.setString("string0");
    bean.setInt(1234);
    bean.setLong(5678L);
    bean.setStrings(new String[]{"string0", "string1"});
    bean.setInts(new int[]{100, 200});
    bean.setLongs(new long[]{300L, 400L});

    InternalBean internalBean = new InternalBean();
    internalBean.setStrings(new String[]{"string0", "string1"});
    bean.setInternalBean(internalBean);
    bean.setInternalBeans(new InternalBean[] {internalBean, internalBean});

    Map<String, Object> properties = beanUtil.convertBeanToProperties(bean);

    assertEquals(8, properties.size());
    assertEquals("string0", properties.get("string"));
    assertEquals(1234, properties.get("int"));
    assertEquals(5678L, properties.get("long"));

    String[] strings = (String[]) properties.get("strings");
    assertEquals(2, strings.length);
    assertEquals("string0", strings[0]);
    assertEquals("string1", strings[1]);

    int[] integers = (int[]) properties.get("ints");
    assertEquals(2, integers.length);
    assertEquals(100, integers[0]);
    assertEquals(200, integers[1]);

    long[] longs = (long[]) properties.get("longs");
    assertEquals(2, longs.length);
    assertEquals(300L, longs[0]);
    assertEquals(400L, longs[1]);

    Map<String, Object> internalBeanMap =
        (Map<String, Object>) properties.get("internalBean");
    strings = (String[]) internalBeanMap.get("strings");
    assertEquals(2, strings.length);
    assertEquals("string0", strings[0]);
    assertEquals("string1", strings[1]);


    Map<String, Object>[] internalBeanMaps =
        (Map<String, Object>[]) properties.get("internalBeans");
    strings = (String[]) internalBeanMaps[0].get("strings");
    assertEquals(2, strings.length);
    assertEquals("string0", strings[0]);
    assertEquals("string1", strings[1]);
    strings = (String[]) internalBeanMaps[1].get("strings");
    assertEquals(2, strings.length);
    assertEquals("string0", strings[0]);
    assertEquals("string1", strings[1]);
  }

  public void testConvertPropertiesToBean() throws Exception {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put("string", "string0");
    properties.put("int", 1234);
    properties.put("long", 5678L);
    properties.put("strings", new String[]{"string0", "string1"});
    properties.put("ints", new int[]{100, 200});
    properties.put("longs", new long[]{300L, 400L});
    Bean bean = new Bean();
    beanUtil.convertPropertiesToBean(properties, bean);

    assertEquals("string0", bean.getString());
    assertEquals(1234, bean.getInt());
    assertEquals(5678L, bean.getLong());

    String[] strings = bean.getStrings();
    assertEquals(2, strings.length);
    assertEquals("string0", strings[0]);
    assertEquals("string1", strings[1]);

    int[] ints = bean.getInts();
    assertEquals(2, ints.length);
    assertEquals(100, ints[0]);
    assertEquals(200, ints[1]);

    long[] longs = bean.getLongs();
    assertEquals(2, longs.length);
    assertEquals(300L, longs[0]);
    assertEquals(400L, longs[1]);
  }

  public void testConvertPropertiesToBeanWithTypeConversions()
      throws Exception {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put("string", "string0");
    properties.put("int", "1234");
    properties.put("long", "5678");
    properties.put("strings", new String[]{"string0", "string1"});
    properties.put("ints", new String[]{"100", "200"});
    properties.put("longs", new String[]{"300", "400"});
    Bean bean = new Bean();
    beanUtil.convertPropertiesToBean(properties, bean);

    assertEquals("string0", bean.getString());
    assertEquals(1234, bean.getInt());
    assertEquals(5678L, bean.getLong());

    String[] strings = bean.getStrings();
    assertEquals(2, strings.length);
    assertEquals("string0", strings[0]);
    assertEquals("string1", strings[1]);

    int[] ints = bean.getInts();
    assertEquals(2, ints.length);
    assertEquals(100, ints[0]);
    assertEquals(200, ints[1]);

    long[] longs = bean.getLongs();
    assertEquals(2, longs.length);
    assertEquals(300L, longs[0]);
    assertEquals(400L, longs[1]);
  }


  public void testIsBean() {
    assertTrue(beanUtil.isBean(Bean.class));

    assertFalse(beanUtil.isBean(String.class));
    assertFalse(beanUtil.isBean(String[].class));
    assertFalse(beanUtil.isBean(Integer.class));
    assertFalse(beanUtil.isBean(Integer[].class));
    assertFalse(beanUtil.isBean(Boolean.class));
    assertFalse(beanUtil.isBean(Boolean[].class));
    assertFalse(beanUtil.isBean(int.class));
    assertFalse(beanUtil.isBean(int[].class));
    assertFalse(beanUtil.isBean(boolean.class));
    assertFalse(beanUtil.isBean(boolean[].class));
    assertFalse(beanUtil.isBean(long.class));
    assertFalse(beanUtil.isBean(long[].class));
    assertFalse(beanUtil.isBean(float.class));
    assertFalse(beanUtil.isBean(float[].class));
    assertFalse(beanUtil.isBean(double.class));
    assertFalse(beanUtil.isBean(double[].class));
  }
}
