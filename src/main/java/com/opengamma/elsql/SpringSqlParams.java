/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Provides access to SQL parameters.
 * <p>
 * This interface abstracts the mechanism of obtaining the SQL parameters.
 */
final class SpringSqlParams implements SqlParams {

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
    _source = source;
  }

  /**
   * Checks whether the variable exists.
   * 
   * @param variable  the variable
   * @return true if it exists
   */
  public boolean contains(String variable) {
    return _source.hasValue(variable);
  }

  /**
   * Gets the variable, returning null if not found.
   * 
   * @param variable  the variable
   * @return the value associated with the variable
   */
  public Object get(String variable) {
    return (_source.hasValue(variable) ? _source.getValue(variable) : null);
  }

}
