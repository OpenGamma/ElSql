/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Representation of INCLUDE(key).
 * <p>
 * This can include another named SQL fragment or directly include a parameter.
 */
final class IncludeSqlFragment extends SqlFragment {

  /**
   * The include key.
   */
  private final String _includeKey;

  /**
   * Creates an instance.
   * 
   * @param includeKey  the include key, not null
   */
  IncludeSqlFragment(String includeKey) {
    if (includeKey == null) {
      throw new IllegalArgumentException("Include key must be specified");
    }
    _includeKey = includeKey;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the include key.
   * 
   * @return the include key, not null
   */
  String getIncludeKey() {
    return _includeKey;
  }

  //-------------------------------------------------------------------------
  @Override
  protected void toSQL(StringBuilder buf, ElSqlBundle bundle, SqlParameterSource paramSource) {
    String key = _includeKey;
    if (key.startsWith(":")) {
      key = paramSource.getValue(_includeKey.substring(1)).toString();
    }
    NameSqlFragment unit = bundle.getFragment(key);
    unit.toSQL(buf, bundle, paramSource);
  }

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return getClass().getSimpleName() + ":" + _includeKey;
  }

}
