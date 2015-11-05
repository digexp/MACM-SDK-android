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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Encapsulates the response received from the MACM server.
 * This extends <code>Iterable<Map.Entry<String, Object>></code> to allow Iterating over the entries
 * with an enhanced <code>for</code> loop, for example:
 * <pre>
 * CAASData data = ..;
 * for (Map.Entry&lt;String, Object&gt; entry: data) {
 *   System.out.println(
 *     "key = " + entry.getKey() +
 *     ", value = " + entry.getValue());
 * }
 * </pre>
 * <p>This class is package-protected so as not to be exposed to clients.
 */
class GenericMap implements Iterable<Map.Entry<String, Object>>, Serializable {
  /**
   * A {@link java.util.Map} backing this data object to store the attributes and their values.
   */
  private final Map<String, Object> map = new TreeMap<String, Object>();

  /**
   * Get the value of the attribute witht he specified name.
   * @param key the name of the attribute.
   * @param <T> the type of the attribute.
   * @return the attribute's value, or <code>null</code> if no attribute with this name exists.
   */
  @SuppressWarnings("unchecked")
  public <T> T get(String key) {
    return (T) map.get(key);
  }

  /**
   * Put the specified attribute value in this data object.
   * <p>This mehotd is package-protected so as not to be exposed to clients.
   * To expose it publicly, it must be be overriden with a 'public' qualifier in subclasses.
   * @param key the name of the attribute.
   * @param value the value of the attribute
   * @param <T> the type of the attribute.
   * @return the previous value, or <code>null</code> if no value existed previously.
   */
  @SuppressWarnings("unchecked")
  <T> T set(String key, T value) {
    return (T) map.put(key, value);
  }

  /**
   * Remove the specified attribute.
   * <p>This mehotd is package-protected so as not to be exposed to clients.
   * To expose it publicly, it must be be overriden with a 'public' qualifier in subclasses.
   * @param key the name of the attribute.
   * @param <T> the type of the attribute.
   * @return the attribute's value, or <code>null</code> if no attribute with this name exists.
   */
  @SuppressWarnings("unchecked")
  <T> T remove(String key) {
    return (T) map.remove(key);
  }

  /**
   * Determine whether this map contains the specified key.
   * @param key the key to check.
   * @return <code>true</code> if this map ocntains the key, <code>false</code> otherwise.
   */
  boolean has(String key) {
    return map.containsKey(key);
  }

  /**
   * Get a set of all the attribute names in this content item.
   * @return the attribute names as a <code>Set</code> of strings.
   */
  Set<String> keys() {
    return map.keySet();
  }

  /**
   * Get a set of all the attribute values in this content item.
   * @return the values as a collection of objects.
   */
  Collection<Object> values() {
    return map.values();
  }

  @Override
  public Iterator<Map.Entry<String, Object>> iterator() {
    return map.entrySet().iterator();
  }

  /**
   * Remove all entries in this content item,
   * making it effectively blank and ready to be reused.
   */
  void clear() {
    map.clear();
  }

  @Override
  public String toString() {
    return new StringBuilder(getClass().getSimpleName()).append(map).toString();
  }
}
