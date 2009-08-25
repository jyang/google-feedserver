// Copyright 2009 Google Inc. All Rights Reserved.
package com.google.feedserver.samples.wrappers.gviz;

import com.google.feedserver.metadata.BooleanTypeInfo;
import com.google.feedserver.metadata.EntityInfo;
import com.google.feedserver.metadata.NumberTypeInfo;
import com.google.feedserver.metadata.PropertyInfo;
import com.google.feedserver.metadata.SimpleDateTypeInfo;
import com.google.feedserver.metadata.TextTypeInfo;
import com.google.feedserver.metadata.TimeOfDayTypeInfo;
import com.google.feedserver.metadata.TypeInfo;
import com.google.visualization.datasource.datatable.value.BooleanValue;
import com.google.visualization.datasource.datatable.value.DateValue;
import com.google.visualization.datasource.datatable.value.NumberValue;
import com.google.visualization.datasource.datatable.value.TextValue;
import com.google.visualization.datasource.datatable.value.TimeOfDayValue;
import com.google.visualization.datasource.datatable.value.Value;
import com.google.visualization.datasource.datatable.value.ValueType;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/** 
 * A utility class for converting GViz ValueTypes to their matching TypeInfo
 * feed types.
 * @author Eran W.
 */
public class GVizTypeConverter {

  private static final String FEED_DATE_FORMAT = "yyyy-MM-dd";
  private static final String FEED_TIME_FORMAT = "HH:mm:ss";

  /**
   * @private
   */
  private GVizTypeConverter() {
  }

  /**
   * A set of known TypeInfo registered by their names.
   */
  private static Map<String, ValueType> KNOWN_TYPES = new HashMap<String, ValueType>();

  static {
    KNOWN_TYPES.put(TextTypeInfo.NAME, ValueType.TEXT);
    KNOWN_TYPES.put(SimpleDateTypeInfo.NAME, ValueType.DATE);
    KNOWN_TYPES.put(TimeOfDayTypeInfo.NAME, ValueType.TIMEOFDAY);
    KNOWN_TYPES.put(NumberTypeInfo.NAME, ValueType.NUMBER);
    KNOWN_TYPES.put(BooleanTypeInfo.NAME, ValueType.BOOLEAN);
  }

  /**
   * Returns a ValueType matching the given TypeInfo type name.
   * Unknown types are regarded as TEXT.
   * @param typeInfoName The name of the TypeInfo to map.
   * @return The mapping between the given TypeInfo name and the ValueType.
   */
  public static ValueType getValueType(String typeInfoName) {
    if (KNOWN_TYPES.containsKey(typeInfoName)) {
      return KNOWN_TYPES.get(typeInfoName);
    } else {
      return ValueType.TEXT;
    }
  }

  /**
   * Returns a mapping from the given object and ValueType to a Value of the
   * corresponding ValueType.
   * @param valueType The ValueType of which the returned value should be.
   * @param value The given object to map.
   * @return A representation of the given object in the given ValueType type.
   * @throws ParseException 
   */
  public static Value getValue(ValueType valueType, Object value) throws ParseException {
    // For repeatable types we take the first value. Visualization feeds do
    // not accept repeatable values of feeds.
    if (value.getClass().isArray()) {
      value = ((Object[]) value)[0];
    }
    if (valueType.equals(ValueType.DATE)) {
      DateFormat format = new SimpleDateFormat(FEED_DATE_FORMAT);
      return parseDate(value.toString(), format);
    }
    if (valueType.equals(ValueType.TIMEOFDAY)) {
      DateFormat format = new SimpleDateFormat(FEED_TIME_FORMAT);
      return parseTimeOfDay(value.toString(), format);
    }    
    if (valueType.equals(ValueType.BOOLEAN)) {
      return BooleanValue.getInstance(Boolean.parseBoolean(value.toString()));
    }
    if (valueType.equals(ValueType.NUMBER)) {
      return new NumberValue(Double.parseDouble(value.toString()));
    }
    if (valueType.equals(ValueType.TEXT)) {
      return new TextValue(value.toString());
    }
    return null;
  }

  /**
   * Returns true if the GVizTypeConverter can handle the given type.
   * @param typeInfo The type to convert.
   * @return True if the TypeInfo can be converted to a GViz type.
   */
  public static boolean isKnownType(TypeInfo typeInfo) {
    return KNOWN_TYPES.keySet().contains(typeInfo.getName());
  }

  /**
   * Returns true if the given TypeInfo does not have nested properties.
   * @param type The TypeInfo to check.
   * @return true if the given TypeInfo does not have nested properties.
   */
  public static boolean isSimpleType(TypeInfo type) {
    return (type == null) || (type.getProperties() == null)
    || (type.getProperties().size() == 0);
  }

  /**
   * Returns true if the given TypeInfo is recursive, i.e. has (recursively) a
   * sub property of the same type.
   * @param entityInfo The entity info for the feed data source.
   * @param type The checked TypeInfo.
   * @param visitedTypes A list of nodes checked in the recursive method.
   * @return True only if the given TypeInfo is recursive.
   */
  public static boolean isRecursiveType(
      EntityInfo entityInfo,
      TypeInfo type,
      Set<String> visitedTypes) {
    // A simple type cannot be recursive.
    if (isSimpleType(type)) {
      return false;
    }

    try {
      // Mark this type as visited.
      visitedTypes.add(type.getName());

      // Check if any of the sub    types are recursive or already visited.
      for (PropertyInfo property : type.getProperties()) {
        TypeInfo subType = entityInfo.getType(property.getTypeName());
        if (subType == null) {
          continue;
        }
        if (visitedTypes.contains(subType.getName())
            || isRecursiveType(entityInfo, subType, visitedTypes)) {
          return true;
        }
      }
      return false;
    } finally {
      visitedTypes.remove(type.getName());
    }
  }
  
  /**
   * Parses a string to a date value.
   *
   * @param val The string to parse.
   * @param format The date format.
   * @return A date value based on the given string.
   * @throws ParseException If val cannot be parsed into a date.
   */
  private static DateValue parseDate(String val, DateFormat format)
      throws ParseException {
    Date date = format.parse(val);
    GregorianCalendar gc = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
    gc.setTime(date);
    return new DateValue(gc);
  }

  /**
   * Parses a string to a time of day value.
   *
   * @param val The string to parse.
   * @param format The date format.
   * @return A time of day value based on the given string.
   * @throws ParseException If val cannot be parsed into a date.
   */
  private static TimeOfDayValue parseTimeOfDay(String val, DateFormat format)
      throws ParseException {
    Date date = format.parse(val);
    GregorianCalendar gc = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
    gc.setTime(date);
    return new TimeOfDayValue(gc);
  }
}
