/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The map of SQL fragments
 * <p>
 * This class is immutable and thread-safe.
 */
final class SqlFragments {

  /**
   * The map of known elsql.
   */
  private final Map<String, NameSqlFragment> _map;
  /**
   * The config.
   */
  private final ElSqlConfig _config;

  //-------------------------------------------------------------------------
  // parse the files
  static SqlFragments parse(List<List<String>> files, ElSqlConfig config) {
    Map<String, NameSqlFragment> parsed = new LinkedHashMap<String, NameSqlFragment>();
    for (List<String> lines : files) {
      ElSqlParser parser = new ElSqlParser(lines);
      parsed.putAll(parser.parse());
    }
    return new SqlFragments(parsed, config);
  }

  //-------------------------------------------------------------------------
  /**
   * Creates an instance..
   * 
   * @param map  the map of names, not null
   * @param config  the config to use, not null
   */
  SqlFragments(Map<String, NameSqlFragment> map, ElSqlConfig config) {
    if (map == null) {
      throw new IllegalArgumentException("Fragment map must not be null");
    }
    if (config == null) {
      throw new IllegalArgumentException("Config must not be null");
    }
    _map = map;
    _config = config;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the configuration object.
   * 
   * @return the config, not null
   */
  ElSqlConfig getConfig() {
    return _config;
  }

  /**
   * Returns a copy of this bundle with a different configuration.
   * <p>
   * This does not reload the underlying resources.
   * 
   * @param config  the new config, not null
   * @return a bundle with the config updated, not null
   */
  SqlFragments withConfig(ElSqlConfig config) {
    return new SqlFragments(_map, config);
  }

  //-------------------------------------------------------------------------
  /**
   * Finds SQL for a named fragment key.
   * <p>
   * This finds, processes and returns a named block from the bundle.
   * 
   * @param name  the name, not null
   * @param params  the Spring SQL parameters, not null
   * @return the SQL, not null
   * @throws IllegalArgumentException if there is no fragment with the specified name
   * @throws RuntimeException if a problem occurs
   */
  String getSql(String name, SqlParams params) {
    NameSqlFragment fragment = getFragment(name);
    StringBuilder buf = new StringBuilder(1024);
    fragment.toSQL(buf, this, params, -1);
    return buf.toString();
  }

  /**
   * Gets a fragment by name.
   * 
   * @param name  the name, not null
   * @return the fragment, not null
   * @throws IllegalArgumentException if there is no fragment with the specified name
   */
  NameSqlFragment getFragment(String name) {
    NameSqlFragment fragment = _map.get(name);
    if (fragment == null) {
      throw new IllegalArgumentException("Unknown fragment name: " + name);
    }
    return fragment;
  }

}
