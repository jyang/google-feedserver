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

import org.apache.commons.beanutils.ConvertUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * JavaBean utilities
 *
 * @author jyang@google.com (Jun Yang)
 */
public class BeanUtil {

  /**
   * Converts a JavaBean to a collection of properties
   * @param bean The JavaBean to convert
   * @return A map of properties
   */
  public Map<String, Object> convertBeanToProperties(Object bean)
      throws IntrospectionException, IllegalArgumentException,
          IllegalAccessException, InvocationTargetException {
    Map<String, Object> properties = new HashMap<String, Object>();
    BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
    for (PropertyDescriptor p : beanInfo.getPropertyDescriptors()) {
      String name = p.getName();
      Method reader = p.getReadMethod();
      if (reader != null) {
        Object value = reader.invoke(bean);
        properties.put(name, value);
      }
    }
    return properties;
  }

  /**
   * Applies a collection of properties to a JavaBean.  Converts String and
   * String[] values to correct property types
   * @param properties A map of the properties to set on the JavaBean
   * @param bean The JavaBean to set the properties on
   */
  public void convertPropertiesToBean(
      Map<String, Object> properties, Object bean)
      throws IntrospectionException, IllegalArgumentException,
          IllegalAccessException, InvocationTargetException {
    BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
    
    // Verify bean has required atom constructs.
    try {
      bean.getClass().getMethod("getName", new Class[0]);
    } catch (SecurityException e) {
      throw new RuntimeException(e);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("Bean must have 'name' member!");
    }
    
    for (PropertyDescriptor p : beanInfo.getPropertyDescriptors()) {
      String name = p.getName();
      Object value = properties.get(name);
      Method reader = p.getReadMethod();
      Method writer = p.getWriteMethod();
      // we only care about "complete" properties
      if (reader != null && writer != null) {
        if (value != null) {
          Class<?> propertyType = writer.getParameterTypes()[0];
          Class<?> valueType = value.getClass();
          if (!propertyType.isAssignableFrom(valueType)) {
            // convert string input values to property type
            if (valueType == String.class) {
              value = ConvertUtils.convert((String) value, propertyType);
            } else if (valueType == String[].class) {
              value = ConvertUtils.convert((String[]) value, propertyType);
            } else if (valueType == Object[].class) {
              // best effort conversion
              Object[] objectValues = (Object[]) value;
              String[] stringValues = new String[objectValues.length];
              for (int i = 0; i < objectValues.length; i++) {
                stringValues[i] = objectValues[i] == null ?
                    null : objectValues[i].toString();
              }
              value = ConvertUtils.convert(stringValues, propertyType);
            }
          }
        }

        writer.invoke(bean, value);
      }
    }
  }

  /**
   * Compares two JavaBeans for equality by comparing their properties and the
   * class of the beans.
   * @param bean1 Bean to compare
   * @param bean2 Bean to compare
   * @return True if {@code bean2} has the same properties with the same
   *     values as {@code bean1} and if they share the same class.
   */
  public boolean equals(Object bean1, Object bean2)
      throws IntrospectionException, IllegalArgumentException,
          IllegalAccessException, InvocationTargetException {
    if (bean1.getClass() != bean2.getClass()) {
      return false;
    }

    BeanInfo bean1Info =
        Introspector.getBeanInfo(bean1.getClass(), Object.class);
    for (PropertyDescriptor p : bean1Info.getPropertyDescriptors()) {
      String name = p.getName();
      Method reader = p.getReadMethod();
      if (reader != null) {
        Object value1 = reader.invoke(bean1);
        Object value2 = reader.invoke(bean2);
        if ((value1 != null && !value1.equals(value2)) ||
            (value2 != null && !value2.equals(value1))) {
          return false;
        }
      }
    }
    return true;
  }
}
