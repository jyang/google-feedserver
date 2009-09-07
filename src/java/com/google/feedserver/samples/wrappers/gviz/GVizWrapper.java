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
package com.google.feedserver.samples.wrappers.gviz;

import com.google.feedserver.adapters.AbstractManagedCollectionAdapter;
import com.google.feedserver.adapters.FeedServerAdapterException;
import com.google.feedserver.adapters.FeedServerAdapterException.Reason;
import com.google.feedserver.metadata.EntityInfo;
import com.google.feedserver.metadata.FeedInfo;
import com.google.feedserver.metadata.PropertyInfo;
import com.google.feedserver.metadata.TypeInfo;
import com.google.feedserver.wrappers.ManagedCollectionAdapterWrapper;
import com.google.visualization.datasource.DataSourceHelper;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.DataSourceParameters;
import com.google.visualization.datasource.base.ReasonType;
import com.google.visualization.datasource.base.ResponseStatus;
import com.google.visualization.datasource.base.StatusType;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.Value;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;
import com.google.visualization.datasource.render.JsonRenderer;

import com.ibm.icu.util.ULocale;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GVizWrapper extends ManagedCollectionAdapterWrapper {

  public static final String PARAMETER_TQ = "tq";
  public static final String PARAMETER_TQH = "tqh";
  public static final String PARAMETER_TQX = "tqx";
  public static final String PARAMETER_OUTPUT = "alt";


  /**
   * Constructs a new GVIZWrapper with the given adapter and wrapper config
   * string
   * @param targetAdapter The Adapter used by this wrapper.
   * @param wrapperConfig The wrapper configuration.
   */
  public GVizWrapper(AbstractManagedCollectionAdapter targetAdapter, String wrapperConfig) {
    super(targetAdapter, wrapperConfig);
  }

  /**
   * Constructs a new GVIZWrapper with the given Abdera and feed configuration
   * This is a wrapper constructor to be used when this wrapper is defined and
   * used as an adapter in AdapterConfig feed.
   * The config in AdapterConfig defines the target adapter for this wrapper and
   * the configuration data for this wrapper.
   * @param abdera instance of Abdera
   * @param config The feed configuration
   */
  public GVizWrapper(Abdera abdera, FeedConfiguration config) {
    super(abdera, config);
  }

  @Override
  public Feed retrieveFeed(RequestContext request) throws FeedServerAdapterException {
    String tq = null;
    String tqh = null;
    String tqx = null;
    String out = null;

    if (request != null) {
      out = request.getParameter(PARAMETER_OUTPUT);
      tq = request.getParameter(PARAMETER_TQ);
      tqh = request.getParameter(PARAMETER_TQH);
      tqx = request.getParameter(PARAMETER_TQX);
    }

    if (!"gviz".equalsIgnoreCase(out)) {
      return super.retrieveFeed(request);
    }
    
    FeedInfo feedInfo = getFeedInfo(request);
    if (feedInfo == null) {
      throw new FeedServerAdapterException(Reason.ADAPTER_CONFIGURATION_NOT_CORRECT,
      "Missing feed info in configuration");
    }
    Feed dataFeed = super.retrieveFeed(request);

    DataTable dataTable = null;
    DataSourceParameters dataSourceParameters = null;
    String response = null;
    try {
      dataTable = getDataTable(dataFeed, feedInfo);
      Query query = DataSourceHelper.parseQuery(tq);
      dataTable = DataSourceHelper.applyQuery(query, dataTable, ULocale.getDefault());
      dataSourceParameters = new DataSourceParameters(tqx);
      response = JsonRenderer.renderJsonResponse(
          dataSourceParameters,
          new ResponseStatus(StatusType.OK),
          dataTable,
          true).toString();
    } catch (DataSourceException e) {
      response = JsonRenderer.renderJsonResponse(dataSourceParameters,
          ResponseStatus.getModifiedResponseStatus(ResponseStatus.createResponseStatus(e)),
          null, true).toString();
    }
    return generateGVizOutput(response);       
  }   

  private Feed generateGVizOutput(String response)
  throws FeedServerAdapterException {
    Feed gvizFeed = createFeed();
    Map<String, Object> result = new HashMap<String, Object>();
    result.put(FeedConfiguration.ENTRY_ELEM_NAME_ID, "GvizEntry");
    result.put("GvizEntry", response);
    createEntryFromProperties(gvizFeed, result);
    return gvizFeed;
  }


  public DataTable getDataTable(Feed dataFeed, FeedInfo feedInfo) throws DataSourceException {
    try {
      List<ColumnDescription> columnDescriptions = getColumnDescriptionsFromFeedInfo(feedInfo);
      return getDataTableFromFeed(dataFeed, columnDescriptions);
    } catch (FeedServerAdapterException e) {
      throw new DataSourceException(ReasonType.INTERNAL_ERROR, e.getMessage());
    }
  }

  public DataTable getDataTableFromFeed(Feed dataFeed, List<ColumnDescription> columnDescriptions) 
  throws FeedServerAdapterException {
    DataTable result = new DataTable();
    result.addColumns(columnDescriptions);

    // each map represents a row in the data table
    for (Map<String, Object> entry : getPropertyMapForFeed(dataFeed)) {
      TableRow row = new TableRow();
      try {
        for (ColumnDescription description : columnDescriptions) {
          String columnId = description.getId();
          ValueType valueType = description.getType();
          Object oValue = getEntryValue(entry, columnId);
          if (oValue != null) {
            Value value = GVizTypeConverter.getValue(valueType, oValue);
            row.addCell(value);
          } else {
            row.addCell(Value.getNullValueFromValueType(valueType));
          }        
        }
        result.addRow(row);
      } catch (TypeMismatchException e) {
        throw new FeedServerAdapterException(
            FeedServerAdapterException.Reason.INVALID_INPUT, e.getMessageToUser());
      } catch (ParseException e) {
        throw new FeedServerAdapterException(
            FeedServerAdapterException.Reason.INVALID_INPUT, e.getLocalizedMessage());
      }
    }
    return result;
  }

  /**
   * Returns the value of the entry that corresponds to the given columnId.
   * The entryMap contains a value for simple types and Maps of key value for
   * nested types. In this case the columnId is separated using the
   * COLUMN_SEPERATOR until the map containing the value is found.
   * @param entryMap The map containing key value pairs for this entry.
   * @param columnId the columnId.
   * @return The columnId value in the entry
   */
  private static Object getEntryValue(Map<String, Object> entryMap, String columnId) {
    while (!entryMap.containsKey(columnId)
        && columnId.contains(COLUMN_SEPARATOR)) {
      String prefix = columnId.substring(0, columnId.indexOf(COLUMN_SEPARATOR));
      String suffix = columnId.substring(columnId.indexOf(COLUMN_SEPARATOR)
          + COLUMN_SEPARATOR.length());

      @SuppressWarnings("unchecked")
      Map<String, Object> innerMap = (Map<String, Object>) entryMap.get(prefix);
      entryMap = innerMap;
      columnId = suffix;
    }
    return entryMap.get(columnId);
  }


  protected static final String COLUMN_SEPARATOR = "@#@";


  public static List<ColumnDescription> getColumnDescriptionsFromFeedInfo(FeedInfo feedInfo)
  throws FeedServerAdapterException {
    List<ColumnDescription> columnDescriptions = new ArrayList<ColumnDescription>();
    EntityInfo entityInfo = feedInfo.getEntityInfo();
    Collection<PropertyInfo> properties = entityInfo.getProperityInfo();
    for (PropertyInfo property : properties) {
      Set<String> set = new HashSet<String>();
      if (GVizTypeConverter.isRecursiveType(
          entityInfo,
          entityInfo.getType(property.getTypeName()),
          set)) {
        throw new FeedServerAdapterException(
            FeedServerAdapterException.Reason.INVALID_INPUT,
            "recursive type " + property.getTypeName());
      }
      columnDescriptions.addAll(getColumnDescriptionsForProperty(entityInfo, property, ""));      
    }
    return columnDescriptions;
  }  

  private static List<ColumnDescription> getColumnDescriptionsForProperty(
      EntityInfo entityInfo,
      PropertyInfo property,
      String prefix) throws FeedServerAdapterException {
    List<ColumnDescription> result = new ArrayList<ColumnDescription>();
    TypeInfo type = entityInfo.getType(property.getTypeName());
    if (GVizTypeConverter.isSimpleType(type) || GVizTypeConverter.isKnownType(type)) {
      result.add(getColumnDescriptionForSimpleType(property, prefix));      
    } else {
      for (PropertyInfo propertyInfo : type.getProperties()) {
        result.addAll(getColumnDescriptionsForProperty(
            entityInfo, propertyInfo, prefix + property.getName()));
      }
    }
    return result;
  }

  private static ColumnDescription getColumnDescriptionForSimpleType(
      PropertyInfo property, String prefix) throws FeedServerAdapterException {
    if (!prefix.equals("")) {
      prefix = prefix + COLUMN_SEPARATOR;
    }
    String id = prefix + property.getName();
    String label = property.getLabel();
    String typeName = property.getTypeName();
    if ((id == null) || (label == null) || (typeName == null)) {
      //logger.log(Level.SEVERE, "unknown or missing property - " + id);
      throw new FeedServerAdapterException
      (FeedServerAdapterException.Reason.BAD_FEED_TYPE_CONFIG,
          "unknown or missing property - " + id);
    }
    ValueType valueType = GVizTypeConverter.getValueType(typeName);
    return new ColumnDescription(id, valueType, label);    
  }
}