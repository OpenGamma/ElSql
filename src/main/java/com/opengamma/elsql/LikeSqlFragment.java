/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

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
  protected void toSQL(StringBuilder buf, ElSqlBundle bundle, SqlParameterSource paramSource, int loopIndex) {
    String var = applyLoopIndex(_variable, loopIndex);
    Object val = paramSource.getValue(var);
    if (val == null) {
      buf.append("IS NULL ");
    } else {
      String value = (val == null ? "" : val.toString());
      if (bundle.getConfig().isLikeWildcard(value)) {
        buf.append("LIKE ");
        super.toSQL(buf, bundle, paramSource, loopIndex);
        buf.append(bundle.getConfig().getLikeSuffix());
      } else {
        buf.append("= ");
        super.toSQL(buf, bundle, paramSource, loopIndex);
      }
    }
  }
}
