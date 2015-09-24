/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

/**
 * Representation of OFFSETFETCH.
 * <p>
 * This outputs an OFFSET-FETCH type clauses.
 */
final class OffsetFetchSqlFragment extends ContainerSqlFragment {

  /**
   * The offset variable name (starting with a colon) or numeric literal.
   */
  private final String _offsetVariable;
  /**
   * The fetch limit variable name (starting with a colon) or numeric literal.
   */
  private final String _fetchVariable;

  /**
   * Creates an instance.
   *
   * @param fetchVariable  the fetch limit variable name (starting with a colon) or numeric literal, not null
   */
  OffsetFetchSqlFragment(String fetchVariable) {
    _offsetVariable = null;
    _fetchVariable = fetchVariable;
  }

  /**
   * Creates an instance.
   *
   * @param offsetVariable  the offset variable name (starting with a colon) or numeric literal, not null
   * @param fetchVariable  the fetch limit variable name (starting with a colon) or numeric literal, not null
   */
  OffsetFetchSqlFragment(String offsetVariable, String fetchVariable) {
    _offsetVariable = offsetVariable;
    _fetchVariable = fetchVariable;
  }

  //-------------------------------------------------------------------------
  @Override
  void toSQL(StringBuilder buf, SqlFragments fragments, SqlParams params, int[] loopIndex) {
    int offset = 0;
    int fetchLimit = 0;
    if (_offsetVariable != null && _offsetVariable.startsWith(":") && _offsetVariable.length() > 1) {
      String offsetVariableName = _offsetVariable.substring(1);
      if (params.contains(offsetVariableName)) {
        offset = ((Number) params.get(offsetVariableName)).intValue();
      }
    } else if (_offsetVariable != null && _offsetVariable.matches("[0-9]+")) {
      offset = Integer.parseInt(_offsetVariable);
    }
    if (_fetchVariable.startsWith(":") && _fetchVariable.length() > 1) {
      String fetchVariableName = _fetchVariable.substring(1);
      if (params.contains(fetchVariableName)) {
        fetchLimit = ((Number) params.get(fetchVariableName)).intValue();
      }
    } else if (_fetchVariable.matches("[0-9]+")) {
      fetchLimit = Integer.parseInt(_fetchVariable);
    }
    buf.append(fragments.getConfig().getPaging(offset, fetchLimit == Integer.MAX_VALUE ? 0 : fetchLimit));
  }

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + getFragments();
  }

}
