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

package com.google.feedserver.util;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.ByteConverter;
import org.apache.commons.beanutils.converters.CharacterConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JavaBean utilities
 */
public class BeanUtil {

  private static List<Class<?>> primitiveTypes;

  /*
   * Setup ConvertUtils to throw exceptions if conversions fail. This is needed
   * for version 1.7. 1.8 has a more elegant way to handle this.
   */
  static {
    ConvertUtils.register(new BooleanConverter(), Boolean.class);
    ConvertUtils.register(new BooleanConverter(), Boolean.TYPE);
    ConvertUtils.register(new ByteConverter(), Byte.class);
    ConvertUtils.register(new ByteConverter(), Byte.TYPE);
    ConvertUtils.register(new CharacterConverter(), Character.class);
    ConvertUtils.register(new CharacterConverter(), Character.TYPE);
    ConvertUtils.register(new FloatConverter(), Float.class);
    ConvertUtils.register(new FloatConverter(), Float.TYPE);
    ConvertUtils.register(new IntegerConverter(), Integer.class);
    ConvertUtils.register(new IntegerConverter(), Integer.TYPE);
    ConvertUtils.register(new LongConverter(), Long.class);
    ConvertUtils.register(new LongConverter(), Long.TYPE);
    ConvertUtils.register(new ShortConverter(), Short.class);
    ConvertUtils.register(new ShortConverter(), Short.TYPE);

    primitiveTypes = new ArrayList<Class<?>>();
    primitiveTypes.add(Boolean.class);
    primitiveTypes.add(Byte.class);
    primitiveTypes.add(Character.class);
    primitiveTypes.add(Double.class);
    primitiveTypes.add(Float.class);
    primitiveTypes.add(Integer.class);
    primitiveTypes.add(Long.class);
    primitiveTypes.add(Short.class);
    primitiveTypes.add(String.class);
  }

  /**
   * Converts a JavaBean to a collection of properties
   * 
   * @param bean The JavaBean to convert
   * @return A map of properties
   */
  public Map<String, Object> convertBeanToProperties(Object bean) throws IntrospectionException,
      IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    Map<String, Object> properties = new HashMap<String, Object>();
    BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
    for (PropertyDescriptor p : beanInfo.getPropertyDescriptors()) {
      String name = p.getName();
      Method reader = p.getReadMethod();
      if (reader != null) {
        Object value = reader.invoke(bean);
        if (null != value) {
          if (isBean(value.getClass())) {
            if (value.getClass().isArray()) {
              Object[] valueArray = (Object[]) value;
              if (valueArray.length == 0) {
                properties.put(name, null);
              } else {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                for (Object object : (Object[]) value) {
                  list.add(convertBeanToProperties(object));
                }
                properties.put(name, list.toArray(new Map[0]));
              }
            } else {
              properties.put(name, convertBeanToProperties(value));
            }
          } else {
            properties.put(name, value);
          }
        }
      }
    }
    return properties;
  }

  public boolean isBean(Class<?> valueClass) {
    if (valueClass.isArray()) {
      return isBean(valueClass.getComponentType());
    } else if (valueClass.isPrimitive()) {
      return false;
    } else {
      return !primitiveTypes.contains(valueClass);
    }
  }

  /**
   * Applies a collection of properties to a JavaBean. Converts String and
   * String[] values to correct property types
   * 
   * @param properties A map of the properties to set on the JavaBean
   * @param bean The JavaBean to set the properties on
   */
  public void convertPropertiesToBean(Map<String, Object> properties, Object bean)
      throws IntrospectionException, IllegalArgumentException, IllegalAccessException,
      InvocationTargetException {
    BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
    for (PropertyDescriptor p : beanInfo.getPropertyDescriptors()) {
      String name = p.getName();
      Object value = properties.get(name);
      Method reader = p.getReadMethod();
      Method writer = p.getWriteMethod();
      // we only care about "complete" properties
      if (reader != null && writer != null && value != null) {
        Class<?> propertyType = writer.getParameterTypes()[0];
        if (isBean(propertyType)) {
          // this is a bean
          if (propertyType.isArray()) {
            propertyType = propertyType.getComponentType();
            Object beanArray = Array.newInstance(propertyType, 1);

            if (value.getClass().isArray()) {
              Object[] valueArrary = (Object[]) value;
              int length = valueArrary.length;
              beanArray = Array.newInstance(propertyType, length);
              for (int index = 0; index < valueArrary.length; ++index) {
                Object valueObject = valueArrary[index];
                fillBeanInArray(propertyType, beanArray, index, valueObject);
              }
            } else {
              fillBeanInArray(propertyType, beanArray, 0, value);
            }
            value = beanArray;
          } else {
            Object beanObject = createBeanObject(propertyType, value);
            value = beanObject;
          }
        } else {
          Class<?> valueType = value.getClass();
          if (!propertyType.isAssignableFrom(valueType)) {
            // convert string input values to property type
            try {
              if (valueType == String.class) {
                value = ConvertUtils.convert((String) value, propertyType);
              } else if (valueType == String[].class) {
                value = ConvertUtils.convert((String[]) value, propertyType);
              } else if (valueType == Object[].class) {
                // best effort conversion
                Object[] objectValues = (Object[]) value;
                String[] stringValues = new String[objectValues.length];
                for (int i = 0; i < objectValues.length; i++) {
                  stringValues[i] = objectValues[i] == null ? null : objectValues[i].toString();
                }
                value = ConvertUtils.convert(stringValues, propertyType);
              } else {
              }
            } catch (ConversionException e) {
              throw new IllegalArgumentException("Conversion failed for " + "property '" + name
                  + "' with value '" + value + "'", e);
            }

          }
        }
        // We only write values that are present in the map. This allows
        // defaults or previously set values in the bean to be retained.
        writer.invoke(bean, value);
      }
    }
  }

  /**
   * @param propertyType
   * @param valueArray
   * @param index
   * @param valueObject
   * @throws IntrospectionException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  @SuppressWarnings("unchecked")
  private void fillBeanInArray(Class<?> propertyType, Object valueArray, int index,
      Object valueObject) throws IntrospectionException, IllegalAccessException,
      InvocationTargetException {
    Object beanObject = createBeanObject(propertyType, valueObject);
    Array.set(valueArray, index, beanObject);
  }

  /**
   * @param propertyType
   * @param valueObject
   * @return Bean object
   * @throws IntrospectionException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  @SuppressWarnings("unchecked")
  private Object createBeanObject(Class<?> propertyType, Object valueObject)
      throws IntrospectionException, IllegalAccessException, InvocationTargetException {
    Object beanObject = createBeanInstance(propertyType);
    convertPropertiesToBean((Map) valueObject, beanObject);
    return beanObject;
  }

  /**
   * Compares two JavaBeans for equality by comparing their properties and the
   * class of the beans.
   * 
   * @param bean1 Bean to compare
   * @param bean2 Bean to compare
   * @return True if {@code bean2} has the same properties with the same values
   *         as {@code bean1} and if they share the same class.
   */
  public boolean equals(Object bean1, Object bean2) throws IntrospectionException,
      IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    if ((null == bean1) && (null == bean2)) {
      return true;
    } else if ((null == bean1) || (null == bean2)) {
      return false;
    }
    if (bean1.getClass() != bean2.getClass()) {
      return false;
    }
    if (bean1.getClass().isArray() && bean2.getClass().isArray()) {
      if (Array.getLength(bean1) != Array.getLength(bean2)) {
        return false;
      }
      for (int i = 0; i < Array.getLength(bean1); i++) {
        if (!equals(Array.get(bean1, i), Array.get(bean2, i))) {
          return false;
        }
      }
      return true;
    } else if (bean1.getClass().isArray()) {
      return false;
    } else if (bean2.getClass().isArray()) {
      return false;
    } else if (isBean(bean1.getClass())) {
      BeanInfo bean1Info;
      try {
        bean1Info = Introspector.getBeanInfo(bean1.getClass(), Object.class);
      } catch (IntrospectionException e) {
        return false;
      }
      for (PropertyDescriptor p : bean1Info.getPropertyDescriptors()) {
        String name = p.getName();
        Method reader = p.getReadMethod();
        if (reader != null) {
          try {
            Object value1 = reader.invoke(bean1);
            Object value2 = reader.invoke(bean2);
            if (!equals(value1, value2)) {
              return false;
            }
          } catch (IllegalArgumentException e) {
            return false;
          } catch (IllegalAccessException e) {
            return false;
          } catch (InvocationTargetException e) {
            return false;
          }
        }
      }
      return true;
    } else {
      return bean1.equals(bean2);
    }
  }

  public Object createBeanInstance(Class<?> objectClass) {
    Constructor<?> c;
    try {
      c = objectClass.getConstructor();
    } catch (SecurityException e) {
      return null;
    } catch (NoSuchMethodException e) {
      return null;
    }
    c.setAccessible(true);
    try {
      return c.newInstance();
    } catch (IllegalArgumentException e) {
    } catch (InstantiationException e) {
    } catch (IllegalAccessException e) {
    } catch (InvocationTargetException e) {
    }
    return null;
  }
}
