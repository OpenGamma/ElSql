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
  void toSQL(StringBuilder buf, SqlFragments fragments, SqlParams params, int[] loopIndex) {
    String key = _includeKey;
    if (key.startsWith(":")) {
      String var = extractVariableName(key);
      if (params.contains(var)) {
        key = params.get(var).toString();
      }
    }
    NameSqlFragment unit = fragments.getFragment(key);
    unit.toSQL(buf, fragments, params, loopIndex);
  }

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return getClass().getSimpleName() + ":" + _includeKey;
  }

}
