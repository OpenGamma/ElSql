/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

/**
 * Representation of VALUE(variable).
 * <p>
 * This includes the value of a parameter.
 */
final class ValueSqlFragment extends SqlFragment {

  /**
   * The variable to output.
   */
  private final String _valueVariable;
  /**
   * Whether to follow with a space.
   */
  private final boolean _followWithSpace;

  /**
   * Creates an instance.
   * 
   * @param valueVariable  the value variable, not null
   * @param followWithSpace  whether to follow by a space
   */
  ValueSqlFragment(String valueVariable, boolean followWithSpace) {
    _valueVariable = extractVariableName(valueVariable);
    _followWithSpace = followWithSpace;
  }

  //-------------------------------------------------------------------------
  @Override
  void toSQL(StringBuilder buf, SqlFragments fragments, SqlParams params, int[] loopIndex) {
    String var = applyLoopIndex(_valueVariable, loopIndex);
    Object value = params.get(var);
    if (value != null) {
      buf.append(value);
      if (_followWithSpace) {
        buf.append(' ');
      }
    }
  }

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return getClass().getSimpleName() + ":" + _valueVariable;
  }

}
