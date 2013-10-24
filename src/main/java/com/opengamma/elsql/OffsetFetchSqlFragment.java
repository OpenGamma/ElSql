/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Representation of OFFSETFETCH.
 * <p>
 * This outputs an OFFSET-FETCH type clauses.
 */
final class OffsetFetchSqlFragment extends ContainerSqlFragment {

  /**
   * The offset variable.
   */
  private final String _offsetVariable;
  /**
   * The fetch variable or numeric amount.
   */
  private final String _fetchVariable;

  /**
   * Creates an instance.
   * 
   * @param fetchVariable  the fetch variable, not null
   */
  OffsetFetchSqlFragment(String fetchVariable) {
    _offsetVariable = null;
    _fetchVariable = fetchVariable;
  }

  /**
   * Creates an instance.
   * 
   * @param offsetVariable  the offset variable, not null
   * @param fetchVariable  the fetch variable, not null
   */
  OffsetFetchSqlFragment(String offsetVariable, String fetchVariable) {
    _offsetVariable = offsetVariable;
    _fetchVariable = fetchVariable;
  }

  //-------------------------------------------------------------------------
  @Override
  protected void toSQL(StringBuilder buf, ElSqlBundle bundle, SqlParameterSource paramSource, int loopIndex) {
    int offset = 0;
    int fetchLimit = 0;
    if (_offsetVariable != null && paramSource.hasValue(_offsetVariable)) {
      offset = ((Number) paramSource.getValue(_offsetVariable)).intValue();
    }
    if (paramSource.hasValue(_fetchVariable)) {
      fetchLimit = ((Number) paramSource.getValue(_fetchVariable)).intValue();
    } else if (_fetchVariable.matches("[0-9]+")) {
      fetchLimit = Integer.parseInt(_fetchVariable);
    }
    buf.append(bundle.getConfig().getPaging(offset, fetchLimit == Integer.MAX_VALUE ? 0 : fetchLimit));
  }

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + getFragments();
  }

}
