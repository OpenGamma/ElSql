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
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Provides access to a bundle of elsql formatted SQL, integrated with the Spring framework.
 * <p>
 * The bundle encapsulates the SQL needed for a particular feature.
 * This will typically correspond to a data access object, or set of related tables.
 * <p>
 * This class is immutable and thread-safe.
 */
public final class ElSqlBundle {

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
   * @throws IllegalArgumentException if the input cannot be parsed
   */
  public static ElSqlBundle of(ElSqlConfig config, Class<?> type) {
    if (config == null) {
      throw new IllegalArgumentException("Config must not be null");
    }
    if (type == null) {
      throw new IllegalArgumentException("Type must not be null");
    }
    ClassPathResource baseResource = new ClassPathResource(type.getSimpleName() + ".elsql", type);
    ClassPathResource configResource = new ClassPathResource(type.getSimpleName() + "-" + config.getName() + ".elsql", type);
    return parse(config, baseResource, configResource);
  }

  /**
   * Parses a bundle from a resource locating a file, specify the config.
   * <p>
   * This parses a list of resources. Named blocks in later resources override
   * blocks with the same name in earlier resources.
   * <p>
   * The config is designed to handle some, but not all, database differences.
   * Other differences are handled via the override resources passed in.
   * 
   * @param config  the config to use, not null
   * @param resources  the resources to load, not null
   * @return the external identifier, not null
   * @throws IllegalArgumentException if the input cannot be parsed
   */
  public static ElSqlBundle parse(ElSqlConfig config, Resource... resources) {
    if (config == null) {
      throw new IllegalArgumentException("Config must not be null");
    }
    if (resources == null) {
      throw new IllegalArgumentException("Resources must not be null");
    }
    return parseResource(resources, config);
  }

  private static ElSqlBundle parseResource(Resource[] resources, ElSqlConfig config) {
    List<List<String>> files = new ArrayList<List<String>>();
    for (Resource resource : resources) {
      if (resource.exists()) {
        List<String> lines = loadResource(resource);
        files.add(lines);
      }
    }
    return new ElSqlBundle(SqlFragments.parse(files, config));
  }

  // package scoped for testing
  static ElSqlBundle parse(List<String> lines) {
    ArrayList<List<String>> files = new ArrayList<List<String>>();
    files.add(lines);
    return new ElSqlBundle(SqlFragments.parse(files, ElSqlConfig.DEFAULT));
  }

  private static List<String> loadResource(Resource resource) {
    InputStream in = null;
    try {
      in = resource.getInputStream();
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

  /**
   * Creates an instance.
   * 
   * @param fragments  the fragments to use, not null
   */
  private ElSqlBundle(SqlFragments fragments) {
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
  public ElSqlBundle withConfig(ElSqlConfig config) {
    return new ElSqlBundle(_fragments.withConfig(config));
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
   * Finds SQL for a named fragment key.
   * <p>
   * This finds, processes and returns a named block from the bundle.
   * 
   * @param name  the name, not null
   * @param paramSource  the Spring SQL parameters, not null
   * @return the SQL, not null
   * @throws IllegalArgumentException if there is no fragment with the specified name
   * @throws RuntimeException if a problem occurs
   */
  public String getSql(String name, SqlParameterSource paramSource) {
    return _fragments.getSql(name, new SpringSqlParams(paramSource));
  }

}
