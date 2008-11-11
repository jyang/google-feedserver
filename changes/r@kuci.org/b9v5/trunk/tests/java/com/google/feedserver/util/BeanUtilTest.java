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

import java.util.HashMap;
import java.util.Map;

/**
 * Tests for the {@link BeanUtil} class.
 */
public class BeanUtilTest extends TestCase {

  protected BeanUtil beanUtil = new BeanUtil();

  public static class Bean {

    protected String string;
    protected int integer;
    protected long longInteger;
    protected String[] strings;
    protected int[] integers;
    protected long[] longIntegers;

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
  }

  public void testConvertBeanToProperties() throws Exception {
    Bean bean = new Bean();
    bean.setString("string0");
    bean.setInt(1234);
    bean.setLong(5678L);
    bean.setStrings(new String[]{"string0", "string1"});
    bean.setInts(new int[]{100, 200});
    bean.setLongs(new long[]{300L, 400L});
    Map<String, Object> properties = beanUtil.convertBeanToProperties(bean);

    assertEquals(6, properties.size());
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
}
