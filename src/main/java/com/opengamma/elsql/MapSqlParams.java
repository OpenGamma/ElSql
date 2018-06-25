/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides access to SQL parameters from a map.
 */
public final class MapSqlParams implements SqlParams {

  /**
   * The source.
   */
  private final Map<String, Object> _map;

  /**
   * Creates an instance based on a map.
   * 
   * @param map  the map, which is assigned, not copied
   */
  public MapSqlParams(Map<String, Object> map) {
    if (map == null) {
      throw new IllegalArgumentException("Map must not be null");
    }
    _map = map;
  }

  /**
   * Creates an instance based on one key-value pair.
   * 
   * @param key  the key
   * @param value  the value
   */
  public MapSqlParams(String key, Object value) {
    _map = Collections.singletonMap(key, value);
  }

  //-------------------------------------------------------------------------
  /**
   * Returns a new instance with the specified key-value pair added.
   * <p>
   * This copies the internal map and calls {@link Map#put(Object, Object)}.
   * 
   * @param key  the key to add
   * @param value  the value to add
   * @return the new instance with pair added
   */
  public MapSqlParams with(String key, Object value) {
    Map<String, Object> map = new HashMap<String, Object>(_map);
    map.put(key, value);
    return new MapSqlParams(map);
  }

  //-------------------------------------------------------------------------
  @Override
  public boolean contains(String variable) {
    return _map.containsKey(variable);
  }

  @Override
  public Object get(String variable) {
    return _map.get(variable);
  }

}
