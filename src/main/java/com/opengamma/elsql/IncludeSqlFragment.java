/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

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
  @Override
  protected void toSQL(StringBuilder buf, ElSqlBundle bundle, SqlParams params, int loopIndex) {
    String key = _includeKey;
    if (key.startsWith(":") && params.contains(_includeKey.substring(1))) {
      key = params.get(_includeKey.substring(1)).toString();
    }
    NameSqlFragment unit = bundle.getFragment(key);
    unit.toSQL(buf, bundle, params, loopIndex);
  }

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return getClass().getSimpleName() + ":" + _includeKey;
  }

}
