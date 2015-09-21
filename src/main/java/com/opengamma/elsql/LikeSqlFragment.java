/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

/**
 * Representation of LIKE(variable).
 * <p>
 * This handles switching between LIKE and = based on the presence of wildcards.
 */
final class LikeSqlFragment extends OperatorSqlFragment {

  /**
   * Creates an instance.
   * 
   * @param variable  the variable to base the LIKE on, not null
   */
  LikeSqlFragment(String variable) {
    super(variable);
  }

  //-------------------------------------------------------------------------
  @Override
  void toSQL(StringBuilder buf, SqlFragments fragments, SqlParams params, int[] loopIndex) {
    String var = applyLoopIndex(_variable, loopIndex);
    Object val = params.get(var);
    if (val == null) {
      buf.append("IS NULL ");
    } else {
      if (fragments.getConfig().isLikeWildcard(val.toString())) {
        buf.append("LIKE ");
        super.toSQL(buf, fragments, params, loopIndex);
        buf.append(fragments.getConfig().getLikeSuffix());
      } else {
        buf.append("= ");
        super.toSQL(buf, fragments, params, loopIndex);
      }
    }
  }

}
