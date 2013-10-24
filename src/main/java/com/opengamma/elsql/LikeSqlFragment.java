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
final class LikeSqlFragment extends ContainerSqlFragment {

  /**
   * The variable.
   */
  private final String _variable;

  /**
   * Creates an instance.
   * 
   * @param variable  the variable to base the LIKE on, not null
   */
  LikeSqlFragment(String variable) {
    if (variable == null) {
      throw new IllegalArgumentException("Variable must be specified");
    }
    if (variable.startsWith(":") == false || variable.length() < 2) {
      throw new IllegalArgumentException("Argument is not a variable (starting with a colon)");
    }
    _variable = variable.substring(1);
  }

  //-------------------------------------------------------------------------
  @Override
  protected void toSQL(StringBuilder buf, ElSqlBundle bundle, SqlParameterSource paramSource, int loopIndex) {
    String var = applyLoopIndex(_variable, loopIndex);
    Object val = paramSource.getValue(var);
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

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return getClass().getSimpleName() + ":" + _variable + " " + getFragments();
  }

}
