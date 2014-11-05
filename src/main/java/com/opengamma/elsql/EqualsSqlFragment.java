package com.opengamma.elsql;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Representation of EQUALS(variable).
 * <p>
 * This handles switching between = and IS NULL based on the value of the parameter.
 */
public final class EqualsSqlFragment extends ContainerSqlFragment {

  /**
   * The variable.
   */
  private final String _variable;

  /**
   * Creates an instance.
   * 
   * @param variable the variable to base the LIKE on, not null
   */
  EqualsSqlFragment(String variable) {
    if (variable == null) {
      throw new IllegalArgumentException("Variable must be specified");
    }
    if (variable.startsWith(":") == false || variable.length() < 2) {
      throw new IllegalArgumentException("Argument is not a variable (starting with a colon)");
    }
    _variable = variable.substring(1);
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

  // -------------------------------------------------------------------------
  @Override
  public String toString() {
    return getClass().getSimpleName() + ":" + _variable + " " + getFragments();
  }

}
