/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import java.net.URL;
import java.util.Map;

/**
 * Main entry point, providing access to a bundle of elsql formatted SQL.
 * <p>
 * The bundle encapsulates the SQL needed for a particular feature.
 * This will typically correspond to a data access object, or set of related tables.
 * <p>
 * This class has no dependencies on any external libraries.
 * Similar functionality is available from the {@link ElSqlBundle} class,
 * which provides integration with the Spring framework.
 * <p>
 * This class is immutable and thread-safe.
 */
public final class ElSql {

  /**
   * The fragments.
   */
  private final SqlFragments _fragments;

  /**
   * Loads external SQL based for the specified type.
   * <p>
   * The type is used to identify the location and name of the ".elsql" file.
   * The loader will attempt to find and use two files, using the full name of
   * the type to query the class path for resources.
   * <p>
   * The first resource searched for is optional - the file will have the suffix
   * "-ConfigName.elsql", such as "com/foo/Bar-MySql.elsql".
   * The second resource searched for is mandatory - the file will just have the
   * ".elsql" suffix, such as "com/foo/Bar.elsql".
   * <p>
   * The config is designed to handle some, but not all, database differences.
   * Other differences should be handled by creating and using a database specific
   * override file (the first optional resource is the override file).
   * 
   * @param config  the config, not null
   * @param type  the type, not null
   * @return the bundle, not null
   * @throws IllegalArgumentException if the input cannot be parsed or if no matching resource exists
   */
  public static ElSql of(ElSqlConfig config, Class<?> type) {
    if (config == null) {
      throw new IllegalArgumentException("Config must not be null");
    }
    if (type == null) {
      throw new IllegalArgumentException("Type must not be null");
    }
    URL baseResource = type.getResource(type.getSimpleName() + ".elsql");
    URL configResource = type.getResource(type.getSimpleName() + "-" + config.getName() + ".elsql");
    return parse(config, baseResource, configResource);
  }

  /**
   * Parses a bundle from a resource locating a file, specify the config.
   * <p>
   * This parses a list of resources, expressed as {@code URL}s.
   * Named blocks in later resources override blocks with the same name in earlier resources.
   * <p>
   * The config is designed to handle some, but not all, database differences.
   * Other differences are handled via the override resources passed in.
   * <p>
   * Each resource is a {@link URL}. A null URL is permitted and ignored.
   * This allows classpath resources, obtained from {@link Class#getResource(String)}
   * or {@link ClassLoader#getResource(String)} to be called and passed in directly
   * as those methods return null when the target does not exist.
   * 
   * @param config  the config to use, not null
   * @param resources  the resources to load, not null, may contain nulls which are ignored
   * @return the external identifier, not null
   * @throws IllegalArgumentException if the input cannot be parsed or if none of the resources exists
   */
  public static ElSql parse(ElSqlConfig config, URL... resources) {
    if (config == null) {
      throw new IllegalArgumentException("Config must not be null");
    }
    if (resources == null) {
      throw new IllegalArgumentException("Resources must not be null");
    }
    return new ElSql(SqlFragments.parseResource(resources, config));
  }

  //-------------------------------------------------------------------------
  /**
   * Creates an instance.
   * 
   * @param fragments  the fragments to use, not null
   */
  private ElSql(SqlFragments fragments) {
    if (fragments == null) {
      throw new IllegalArgumentException("Fragment map must not be null");
    }
    _fragments = fragments;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the configuration object.
   * 
   * @return the config, not null
   */
  public ElSqlConfig getConfig() {
    return _fragments.getConfig();
  }

  /**
   * Returns a copy of this bundle with a different configuration.
   * <p>
   * This does not reload the underlying resources.
   * 
   * @param config  the new config, not null
   * @return a bundle with the config updated, not null
   */
  public ElSql withConfig(ElSqlConfig config) {
    return new ElSql(_fragments.withConfig(config));
  }

  //-------------------------------------------------------------------------
  /**
   * Finds SQL for a named fragment key, without specifying parameters.
   * <p>
   * This finds, processes and returns a named block from the bundle.
   * Note that if the SQL contains tags that depend on variables, like AND or LIKE,
   * then an error will be thrown.
   * 
   * @param name  the name, not null
   * @return the SQL, not null
   * @throws IllegalArgumentException if there is no fragment with the specified name
   * @throws RuntimeException if a problem occurs
   */
  public String getSql(String name) {
    return _fragments.getSql(name, EmptySqlParams.INSTANCE);
  }

  /**
   * Finds SQL for a named fragment key, providing the SQL parameters.
   * <p>
   * This finds, processes and returns a named block from the bundle.
   * The parameters are used to provide intelligent processing of SQL based on
   * the actual data in the request.
   * <p>
   * See {@link MapSqlParams} and {@link SpringSqlParams}.
   * 
   * @param name  the name, not null
   * @param params  the SQL parameters, not null
   * @return the SQL, not null
   * @throws IllegalArgumentException if there is no fragment with the specified name
   * @throws RuntimeException if a problem occurs
   */
  public String getSql(String name, SqlParams params) {
    return _fragments.getSql(name, params);
  }

  /**
   * Finds SQL for a named fragment key, providing a map of SQL parameters.
   * <p>
   * This finds, processes and returns a named block from the bundle.
   * The parameters are used to provide intelligent processing of SQL based on
   * the actual data in the request.
   * 
   * @param name  the name, not null
   * @param params  the SQL parameters, not null
   * @return the SQL, not null
   * @throws IllegalArgumentException if there is no fragment with the specified name
   * @throws RuntimeException if a problem occurs
   */
  public String getSql(String name, Map<String, Object> params) {
    return _fragments.getSql(name, new MapSqlParams(params));
  }

}
