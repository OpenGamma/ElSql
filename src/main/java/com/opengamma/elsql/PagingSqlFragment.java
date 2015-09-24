/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

/**
 * Representation of paging over an SQL clause.
 */
final class PagingSqlFragment extends ContainerSqlFragment {

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
   * @param offsetVariable  the offset variable name (starting with a colon) or numeric literal, not null
   * @param fetchVariable  the fetch limit variable name (starting with a colon) or numeric literal, not null
   */
  PagingSqlFragment(String offsetVariable, String fetchVariable) {
    _offsetVariable = offsetVariable;
    _fetchVariable = fetchVariable;
  }

  //-------------------------------------------------------------------------
  @Override
  void toSQL(StringBuilder buf, SqlFragments fragments, SqlParams params, int[] loopIndex) {
    int oldLen = buf.length();
    super.toSQL(buf, fragments, params, loopIndex);
    int newLen = buf.length();
    String select = buf.substring(oldLen, newLen);
    if (select.startsWith("SELECT ")) {
      buf.setLength(oldLen);
      buf.append(applyPaging(select, fragments, params));
    }
  }

  /**
   * Applies the paging.
   * 
   * @param selectToPage  the contents of the enclosed block, not null
   * @param fragments  the SQL fragments for context, not null
   * @param params  the SQL arguments, not null
   */
  String applyPaging(String selectToPage, SqlFragments fragments, SqlParams params) {
    int offset = 0;
    int fetchLimit = 0;
    if (_offsetVariable.startsWith(":") && _offsetVariable.length() > 1) {
      String offsetVariableName = _offsetVariable.substring(1);
      if (params.contains(offsetVariableName)) {
        offset = ((Number) params.get(offsetVariableName)).intValue();
      }
    } else if (_offsetVariable.matches("[0-9]+")) {
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
    return fragments.getConfig().addPaging(selectToPage, offset, fetchLimit == Integer.MAX_VALUE ? 0 : fetchLimit);
  }

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + getFragments();
  }

}
