/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

/**
 * Representation of IF(expression).
 * <p>
 * This outputs the contents if the conditional is true.
 */
final class IfSqlFragment extends ConditionalSqlFragment {

  /**
   * Creates an instance.
   * 
   * @param variable  the variable to determine whether to include the AND on, not null
   * @param matchValue  the value to match, null to match on existence
   */
  IfSqlFragment(String variable, String matchValue) {
    super(variable, matchValue);
  }

  //-------------------------------------------------------------------------
  @Override
  void toSQL(StringBuilder buf, SqlFragments fragments, SqlParams params, int[] loopIndex) {
    if (isMatch(params, loopIndex)) {
      super.toSQL(buf, fragments, params, loopIndex);
    }
  }

}
