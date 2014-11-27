/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Provides access to SQL parameters from Spring.
 * <p>
 * This class has a dependency on the Spring framework.
 */
public final class SpringSqlParams implements SqlParams {

  /**
   * The source.
   */
  private final SqlParameterSource _source;

  /**
   * Creates an instance based on a source.
   * 
   * @param source  the source
   */
  public SpringSqlParams(SqlParameterSource source) {
    if (source == null) {
      throw new IllegalArgumentException("Source must not be null");
    }
    _source = source;
  }

  //-------------------------------------------------------------------------
  @Override
  public boolean contains(String variable) {
    return _source.hasValue(variable);
  }

  @Override
  public Object get(String variable) {
    return (_source.hasValue(variable) ? _source.getValue(variable) : null);
  }

}
