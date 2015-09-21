/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

/**
 * Representation of EQUALS(variable).
 * <p>
 * This handles switching between = and IS NULL based on the value of the parameter.
 */
final class EqualsSqlFragment extends OperatorSqlFragment {

  /**
   * Creates an instance.
   * 
   * @param variable the variable to base the LIKE on, not null
   */
  EqualsSqlFragment(String variable) {
    super(variable);
  }

  // -------------------------------------------------------------------------
  @Override
  void toSQL(StringBuilder buf, SqlFragments fragments, SqlParams params, int[] loopIndex) {
    String var = applyLoopIndex(_variable, loopIndex);
    Object val = params.get(var);
    if (val == null) {
      buf.append("IS NULL ");
    } else {
      buf.append("= ");
      super.toSQL(buf, fragments, params, loopIndex);
    }
  }

}
