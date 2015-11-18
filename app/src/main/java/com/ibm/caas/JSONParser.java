/*
 ********************************************************************
 * Licensed Materials - Property of IBM                             *
 *                                                                  *
 * Copyright IBM Corp. 2015 All rights reserved.                    *
 *                                                                  *
 * US Government Users Restricted Rights - Use, duplication or      *
 * disclosure restricted by GSA ADP Schedule Contract with          *
 * IBM Corp.                                                        *
 *                                                                  *
 * DISCLAIMER OF WARRANTIES. The following [enclosed] code is       *
 * sample code created by IBM Corporation. This sample code is      *
 * not part of any standard or IBM product and is provided to you   *
 * solely for the purpose of assisting you in the development of    *
 * your applications. The code is provided "AS IS", without         *
 * warranty of any kind. IBM shall not be liable for any damages    *
 * arising out of your use of the sample code, even if they have    *
 * been advised of the possibility of such damages.                 *
 ********************************************************************
 */

package com.ibm.caas;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * This class parses the JSON payload received in response to a content request.
 */
class JSONParser {
  /**
   * Log tag for this class.
   */
  private static final String LOG_TAG = JSONParser.class.getSimpleName();
  /**
   * Type converters from string based on a type index.
   */
  enum Converter {
    /**
     * Convert to <code>null</code> for absent values.
     */
    ABSENT(0) {
      @Override
      Object convert(String source) throws Exception {
        return null;
      }
    },
    /**
     * Convert to string.
     */
    PLAIN_TEXT(1),
    /**
     * Convert to string.
     */
    HTML(2),
    /**
     * Convert to a {@link URL}.
     */
    URL(3) /*{
      @Override
      Object convert(String source) throws Exception {
        return source == null ? null : new URL(source);
      }
    }*/,
    /**
     * Convert to a {@link java.util.Date}.
     */
    DATE(4) {
      @Override
      Object convert(String source) throws Exception {
        if ((source == null) || "".equals(source.trim())) {
          return null;
        }
        // example date: "2015-04-16T14:49:44.848Z"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.parse(source);
      }
    },
    /**
     * Convert to a {@link Number}.
     */
    NUMBER(5) {
      @Override
      Object convert(String source) throws Exception {
        return (source == null) || "".equals(source.trim()) ? null : Double.valueOf(source);
      }
    };

    /**
     * The index associated with the converter.
     */
    final int index;

    /**
     * Initialize with the specified index.
     * @param index the index associated with the converter.
     */
    private Converter(int index) {
      this.index = index;
    }

    /**
     * Covert the source string into an instance of the target type.
     * @param source the source string to convert.
     * @return the converted string.
     * @throws Exception if any error occurs during the conversion.
     */
    Object convert(String source) throws Exception {
      return source;
    }
  }

  /**
   * Convenience mapping of indexes to corresponding converters.
   */
  private static Map<Integer, Converter> convertersMap;
  static  {
    Map<Integer, Converter> map = new TreeMap<Integer, Converter>();
    for (Converter c: Converter.values()) {
      map.put(c.index, c);
    }
    convertersMap = Collections.unmodifiableMap(map);
  }
  /**
   * Mapping of element index to corresponding element name.
   */
  private Map<Integer, String> elementIndexes;
  /**
   * Mapping of element index to corresponding property name.
   */
  private Map<Integer, String> propertyIndexes;
  /**
   * The list of content items resulting from parsing the JSON.
   */
  private CAASContentItemsList result = null;

  /**
   * Createa parser instance and parse the input JSON.
   * @param source the input JSON to parse.
   * @throws Exception if any error occurs while parsing.
   */
  JSONParser(String source) throws Exception {
    JSONObject json = null;
    try {
      json = new JSONObject(source);
    } catch(JSONException e) {
      throw new CAASException(e, "com.ibm.caas.unparseableResponseBody", e.getMessage());
    }
    result = parseListProperties(json);
    JSONObject header = null;
    try {
      header = json.getJSONObject("header");
    } catch(JSONException e) {
      throw new CAASException(e, "com.ibm.caas.unparseableResponseBody", e.getMessage());
    }
    elementIndexes = parseIndexes(header, "elementIndex");
    Log.d(LOG_TAG, "elementIndexes = " + elementIndexes);
    propertyIndexes = parseIndexes(header, "propertyIndex");
    Log.d(LOG_TAG, "propertyIndexes = " + propertyIndexes);
    List<CAASContentItem> list = parseValues(json);
    result.setContentItems(list);
    for (int i=0; i<list.size(); i++) {
      Log.d(LOG_TAG, "item[" + i + "] = " + list.get(i));
    }
  }

  /**
   * Parse the values from the specified JSON object.
   * @param json the JSON object to broswe and parse.
   * @return a list of {@link CAASContentItem)s.
   * @throws Exception if any error occurs while parsing.
   */
  List<CAASContentItem> parseValues(JSONObject json) throws Exception {
    List<CAASContentItem> items = new ArrayList<CAASContentItem>();
    JSONObject elementTypes = null;
    JSONObject header = json.getJSONObject("header");
    String section = "elementTypeIndex";
    if (header.has(section)) {
      try {
        elementTypes = header.getJSONObject(section);
      } catch(JSONException e) {
        throw new CAASException(e, "com.ibm.caas.unparseableHeaderSection", section, e.getMessage());
      }
    }
    Log.d(LOG_TAG, "parseValues() : elementTypes=" + elementTypes);
    JSONArray array = json.getJSONArray("values");
    try {
      array = json.getJSONArray("values");
    } catch(JSONException e) {
      throw new CAASException(e, "com.ibm.caas.unparseableValues", e.getMessage());
    }
    List<String> itemErrors = new ArrayList<String>();
    for (int i=0; i<array.length(); i++) {
      CAASContentItem item = new CAASContentItem();
      JSONArray values = null;
      try {
        values = array.getJSONArray(i);
      } catch(JSONException e) {
        itemErrors.add(Utils.localize("com.ibm.caas.unparseableItemValues", i, e.getMessage()));
        //throw new CAASException(e, "com.ibm.caas.unparseableValues", e.getMessage());
        continue;
      }
      for (Map.Entry<Integer, String> elementEntry: elementIndexes.entrySet()) {
        String name = elementEntry.getValue();
        int n = -1;
        try {
          Converter converter = Converter.PLAIN_TEXT;
          if ((elementTypes != null) && elementTypes.has(name)) {
            n = elementTypes.getInt(name);
            if (values.isNull(n)) {
              // null type means the element is there but has no value defined
              converter = null;
            } else {
              int converterIndex = values.getInt(n);
              converter = convertersMap.get(converterIndex);
            }
          }
          String value = values.getString(elementEntry.getKey());
          //Log.d(LOG_TAG, "parseValues() : name=" + name  + ", value=" + value + ", converter=" + converter + " (n=" + n + ")");
          if (converter == null) {
            item.setElement(name, null);
          } else if ((value != null) && !"".equals(value)) {
            Object objectValue = "null".equals(value) ? null : convertValue(value, converter);
            item.setElement(name, objectValue);
          }
        } catch(Exception e) {
          //Log.e(LOG_TAG, "type conversion error: ", e);
          itemErrors.add(Utils.localize("com.ibm.caas.parseableElementValue", name, i, e.getMessage()));
        }
      }
      for (Map.Entry<Integer, String> propertyEntry: propertyIndexes.entrySet()) {
        String name = propertyEntry.getValue();
        String value = values.getString(propertyEntry.getKey());
        try {
          //Log.d(LOG_TAG, "parseValues() : name=" + name  + ", value=" + value);
          if ((value == null) || "".equals(value)) continue;
          Object objectValue = convertPropertyValue(name, value);
          item.setProperty(name, objectValue);
          //Log.d(LOG_TAG, "parseValues() : name=" + name  + ", converted value=" + objectValue);
        } catch(Exception e) {
          itemErrors.add(Utils.localize("com.ibm.caas.parseablePropertyValue", name, i, e.getMessage()));
        }
      }
      items.add(item);
    }
    if (items.isEmpty() && !itemErrors.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      for (String error: itemErrors) {
        sb.append('\n').append(error);
      }
      //Log.d(LOG_TAG, "items errors: " + sb.toString());
      throw new CAASException("com.ibm.caas.unparseableItems", sb.toString());
    }
    return items;
  }

  /**
   * Map the value indexes from the corresponding list of element or property fields.
   * @param header the header from which the list of fields is fetched.
   * @param indexesName the name of the list of fields to fetch.
   * @return a mapping of field indexes to the corresponding property or element names.
   * @throws CAASException if any error cocurs while parsing.
   */
  private Map<Integer, String> parseIndexes(JSONObject header, String indexesName) throws CAASException {
    try {
      Map<Integer, String> map = new HashMap<Integer, String>();
      if (header.has(indexesName)) {
        JSONObject json = header.getJSONObject(indexesName);
        @SuppressWarnings("unchecked")
        Iterator<String> it = json.keys();
        while (it.hasNext()) {
          String name = it.next();
          map.put(json.getInt(name), name);
        }
      }
      return map;
    } catch(JSONException e) {
      throw new CAASException(e, "com.ibm.caas.unparseableHeaderSection", indexesName, e.getMessage());
    }
  }

  /**
   * Parse the properties of the resulting items list, if they exist.
   * @param json the header JSON object which contains the list properties.
   * @return a {@link CAASContentItemsList} object containing the parsed list properties.
   */
  private CAASContentItemsList parseListProperties(JSONObject json) {
    CAASContentItemsList result = new CAASContentItemsList();
    Log.w(LOG_TAG, "in parseListProperties()");
    try {
      boolean hasProps = json.has("listProperties");
      //Log.w(LOG_TAG, "in parseListProperties() hasProps=" + hasProps);
      if (json.has("listProperties")) {
        JSONObject listProps = json.getJSONObject("listProperties");
        if (listProps.has("id")) {
          result.setOid(listProps.getString("id"));
          //Log.w(LOG_TAG, "in parseListProperties() id=" + result.getOid());
        }
        if (listProps.has("lastmodifieddate")) {
          String s = listProps.getString("lastmodifieddate");
          result.setLastModifiedDate((Date) Converter.DATE.convert(s));
          //Log.w(LOG_TAG, "in parseListProperties() lastModified=" + result.getLastModifiedDate());
        }
        if (listProps.has("pageSize")) {
          result.setPageSize(listProps.getInt("pageSize"));
          Log.w(LOG_TAG, "in parseListProperties() pageSize=" + result.getPageSize());
        }
        if (listProps.has("pageNumber")) {
          result.setPageNumber(listProps.getInt("pageNumber"));
          Log.w(LOG_TAG, "in parseListProperties() pageNumber=" + result.getPageNumber());
        }
        if (listProps.has("hasNext")) {
          result.setHasNextPage(listProps.getBoolean("hasNext"));
          Log.w(LOG_TAG, "in parseListProperties() hasNext=" + result.hasNextPage());
        }
      }
    } catch(Exception ignore) {
      // this doesn't prevent us from parsing the items
      Log.w(LOG_TAG, "exception in parseListProperties') : " + Log.getStackTraceString(ignore));
    }
    return result;
  }

  /**
   * Convert the value of a property into the appropriate type.
   * @param name the name of the property.
   * @param value the value to convert as a string.
   * @return the converted value.
   * @throws Exception if any error occurs during the conversion.
   */
  private Object convertPropertyValue(String name, String value) throws Exception {
    CAASContentItem.Property property = CAASContentItem.propertiesMap.get(name);
    if (property == null) {
      Log.w(LOG_TAG, "parseValues(name=" + name  + ", value=" + value + ") property = null");
      return value;
    }
    switch(property) {
      case CREATION_DATE:
      case EXPIRY_DATE:
      case LAST_MODIFIED_DATE:
      case PUBLISH_DATE:
        return convertValue(value, Converter.DATE);
      case ITEM_COUNT:
        return convertValue(value, Converter.NUMBER);
    }
    return value;
  }

  /**
   * Convert the specified string using the specified converter.
   * @param source the value to convert.
   * @param converter converts the input value into the appropriate type.
   * @return the converted value.
   * @throws Exception if any error occurs during the conversion.
   */
  private Object convertValue(String source, Converter converter) throws Exception {
    return (converter == null) ? source : converter.convert(source);
  }

  /**
   * Get the parsed content items.
   * @return a {@link CAASContentItemsList}.
   */
  CAASContentItemsList getResult() {
    return result;
  }
}
