/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
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
   * An empty array.
   */
  private static final int[] NO_LOOP = new int[] {-1};

  /**
   * The map of known elsql.
   */
  private final Map<String, NameSqlFragment> _map;
  /**
   * The config.
   */
  private final ElSqlConfig _config;

  //-------------------------------------------------------------------------
  // parse a set of resources, where names in later resources override names in earlier ones
  // throws an IllegalArgumentException when none of the resources exists
  static SqlFragments parseResource(URL[] resources, ElSqlConfig config) {
    List<List<String>> files = new ArrayList<List<String>>();
    boolean resourceFound = false;
    for (URL resource : resources) {
      if (resource != null) {
        resourceFound = true;
        List<String> lines = loadResource(resource);
        files.add(lines);
      }
    }
    if (!resourceFound) {
      throw new IllegalArgumentException("No matching resource was found");
    }
    return parse(files, config);
  }

  // convert a resource to a list of lines
  static List<String> loadResource(URL resource) {
    InputStream in = null;
    try {
      in = resource.openStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
      List<String> list = new ArrayList<String>();
      String line = reader.readLine();
      while (line != null) {
          list.add(line);
          line = reader.readLine();
      }
      return list;
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException ignored) {
      }
    }
  }

  // package scoped for testing
  static SqlFragments parse(List<String> lines) {
    ArrayList<List<String>> files = new ArrayList<List<String>>();
    files.add(lines);
    return parse(files, ElSqlConfig.DEFAULT);
  }

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
    if (name == null) {
      throw new IllegalArgumentException("Fragment name must not be null");
    }
    if (params == null) {
      throw new IllegalArgumentException("SqlParams must not be null");
    }
    NameSqlFragment fragment = getFragment(name);
    StringBuilder buf = new StringBuilder(1024);
    fragment.toSQL(buf, this, params, NO_LOOP);
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
    if (name == null) {
      throw new IllegalArgumentException("Fragment name must not be null");
    }
    NameSqlFragment fragment = _map.get(name);
    if (fragment == null) {
      throw new IllegalArgumentException("Unknown fragment name: " + name);
    }
    return fragment;
  }

}
