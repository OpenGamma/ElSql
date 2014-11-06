package com.opengamma.elsql;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Representation of EQUALS(variable).
 * <p>
 * This handles switching between = and IS NULL based on the value of the parameter.
 */
public final class EqualsSqlFragment extends OperatorSqlFragment {

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
  protected void toSQL(StringBuilder buf, ElSqlBundle bundle, SqlParameterSource paramSource, int loopIndex) {
    String var = applyLoopIndex(_variable, loopIndex);
    Object val = paramSource.getValue(var);
    if (val == null) {
      buf.append("IS NULL ");
    } else {
      buf.append("= ");
      super.toSQL(buf, bundle, paramSource, loopIndex);
    }
  }
}
