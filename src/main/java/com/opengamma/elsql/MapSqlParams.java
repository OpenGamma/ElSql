/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import java.util.Map;

/**
 * Provides access to SQL parameters from a map.
 */
final class MapSqlParams implements SqlParams {

  /**
   * The source.
   */
  private final Map<String, Object> _map;

  /**
   * Creates an instance based on a map.
   * 
   * @param map  the map
   */
  MapSqlParams(Map<String, Object> map) {
    _map = map;
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
