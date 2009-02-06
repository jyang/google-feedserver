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
package com.google.feedserver.testing;

import com.google.feedserver.client.FeedServerEntry;
import com.google.feedserver.util.ContentUtil;
import com.google.feedserver.util.FeedServerClientException;
import com.google.gdata.data.Entry;
import com.google.gdata.data.OtherContent;
import com.google.gdata.util.XmlBlob;

import org.easymock.IArgumentMatcher;
import org.easymock.classextension.EasyMock;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides data and utilities for testing the feedserver clients.
 * 
 * @author r@kuci.org (Ray Colline)
 */
public class FeedServerClientTestUtil {

  public static final String NAME = "vehicle0";
  public static final String OWNER = "Joe";
  public static final String PRICE = "23000";
  public static final String[] PROPERTY_NAMES = {"make", "model", "year"};
  public static final String[] PROPERTY_VALUES = {"Honda", "Civic Hybrid", "2007"};

  public static final String ENTRY_XML =
      "<entity>\n" + "  <name>vehicle0</name>\n" + "  <owner>Joe</owner>\n"
          + "  <price>23000</price>\n" + "  <propertyName repeatable='true'>make</propertyName>\n"
          + "  <propertyName>model</propertyName>\n" + "  <propertyName>year</propertyName>\n"
          + "  <propertyValue repeatable='true'>Honda</propertyValue>\n"
          + "  <propertyValue>Civic Hybrid</propertyValue>\n"
          + "  <propertyValue>2007</propertyValue>\n" + "</entity>\n";

  private VehicleBean sampleVehicleBean;
  private Map<String, Object> sampleVehicleMap;
  private FeedServerEntry vehicleEntry;

  /**
   * Populates bean with static info to match XML. The XML and the bean should
   * be interchangable using utilities provided by FeedServerClient.
   */
  public FeedServerClientTestUtil() {
    // Setup sample bean.
    sampleVehicleBean = new VehicleBean();
    sampleVehicleBean.setName(NAME);
    sampleVehicleBean.setOwner(OWNER);
    sampleVehicleBean.setPrice(PRICE);
    sampleVehicleBean.setPropertyName(PROPERTY_NAMES);
    sampleVehicleBean.setPropertyValue(PROPERTY_VALUES);

    // Setup sample map.
    sampleVehicleMap = new HashMap<String, Object>();
    sampleVehicleMap.put("name", NAME);
    sampleVehicleMap.put("owner", OWNER);
    sampleVehicleMap.put("price", PRICE);
    sampleVehicleMap.put("propertyName", PROPERTY_NAMES);
    sampleVehicleMap.put("propertyValue", PROPERTY_VALUES);

    // Create populated gdata entry.
    XmlBlob xmlBlob = new XmlBlob();
    xmlBlob.setBlob(ENTRY_XML);
    OtherContent xmlContent = new OtherContent();
    xmlContent.setXml(xmlBlob);
    xmlContent.setXml(xmlBlob);
    xmlContent.setMimeType(ContentUtil.APPLICATION_XML);
    vehicleEntry = new FeedServerEntry();
    vehicleEntry.setXmlBlob(xmlBlob);
    vehicleEntry.setContent(xmlContent);
  }

  /**
   * Compares two vehicle beans to see if they are identical.
   * 
   * @param expected the expected bean.
   * @param actual the actual bean.
   * @return true if they are identical, false otherwise.
   */
  public boolean isEqual(VehicleBean expected, VehicleBean actual) {
    if (expected.getName().equals(actual.getName())
        && expected.getOwner().equals(actual.getOwner())
        && expected.getPrice().equals(actual.getPrice())) {
      // In the vehicle example the amount of property names and property values
      // is identical.
      // They are also in determistic order.
      for (int index = 0; index < expected.getPropertyName().length; index++) {
        if ((!expected.getPropertyName()[index].equals(actual.getPropertyName()[index]))
            || (!expected.getPropertyValue()[index].equals(actual.getPropertyValue()[index]))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Compares two typeless maps to see if they are identical.
   * 
   * @param expected the expected entry represented as a map.
   * @param actual the actual entry represented as a map.
   * @return true if they are identical, false otherwise.
   * @throws NullPointerException if map isnt properly formatted
   * @throws ClassCastException if map is not of "typeless" variety.
   * @throws IndexOutOfBoundsException if actual map has less entries than
   *         expected map.
   */
  public boolean isEqual(Map<String, Object> expected, Map<String, Object> actual) {
    for (String key : expected.keySet()) {
      if (expected.get(key) instanceof String) {
        if (!expected.get(key).equals(actual.get(key))) {
          return false;
        }
      } else if (expected.get(key) instanceof Object[]) {
        Object[] expectedRepeatedValue = (Object[]) expected.get(key);
        Object[] actualRepeatedValue = (Object[]) actual.get(key);
        for (int index = 0; index < expectedRepeatedValue.length; index++) {
          if (!((String) expectedRepeatedValue[index]).equals(actualRepeatedValue[index])) {
            return false;
          }
        }
      }
    }
    return true;
  }

  public VehicleBean getSampleVehicleBean() {
    return sampleVehicleBean;
  }

  public FeedServerEntry getVehicleEntry() {
    return vehicleEntry;
  }

  public Map<String, Object> getSampleVehicleMap() {
    return sampleVehicleMap;
  }

  /**
   * Easymock helper function to compare {@link Entry} objects.
   * 
   * @param in object to compare.
   * @return an Entry to pass through.
   */
  public static FeedServerEntry eqEntry(FeedServerEntry in) {
    EasyMock.reportMatcher(new EntryEquals(in));
    return null;
  }

  /**
   * Entry equivalence argument matcher for easymock to check {@link Entry}
   * objects.
   * 
   * @author r@kuci.org (Ray Colline)
   */
  public static class EntryEquals implements IArgumentMatcher {

    private FeedServerEntry expected;

    public EntryEquals(FeedServerEntry expected) {
      this.expected = expected;
    }

    public boolean matches(Object actual) {
      if (!(actual instanceof FeedServerEntry)) {
        return false;
      }
      try {
        return new FeedServerClientTestUtil().isEqual(expected.getEntity(VehicleBean.class),
            ((FeedServerEntry) actual).getEntity(VehicleBean.class));
      } catch (FeedServerClientException e) {
        return false;
      }
    }


    public void appendTo(StringBuffer buffer) {
      buffer.append("eqException( expected: ");
      buffer.append(expected.getXmlBlob().getBlob());
    }
  }
}
