/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import java.util.Arrays;

/**
 * Representation of a loop.
 * <p>
 * This loops over the children a number of times.
 */
final class LoopSqlFragment extends ContainerSqlFragment {

  /**
   * The size variable name (starting with a colon) or numeric literal.
   */
  private final String _sizeVariable;

  /**
   * Creates an instance.
   * 
   * @param variable  the variable to determine the loop size, not null
   */
  LoopSqlFragment(String variable) {
    _sizeVariable = variable;
  }

  //-------------------------------------------------------------------------
  @Override
  void toSQL(StringBuilder buf, SqlFragments fragments, SqlParams params, int[] loopIndex) {
    // find loop size
    int size = extractSize(params);
    // loop
    int[] childLoopIndex = Arrays.copyOf(loopIndex, loopIndex.length + 1);
    for (int i = 0; i < size; i++) {
      StringBuilder part = new StringBuilder();
      childLoopIndex[childLoopIndex.length - 1] = i;
      super.toSQL(part, fragments, params, childLoopIndex);
      int joinIndex = part.indexOf("@LOOPJOIN ");
      if (joinIndex >= 0) {
        if (i >= (size - 1)) {
          part.setLength(joinIndex);
        } else {
          part.delete(joinIndex, joinIndex + 10);
        }
      }
      buf.append(part);
    }
  }

  private int extractSize(SqlParams params) {
    if (_sizeVariable.startsWith(":")) {
      String var = extractVariableName(_sizeVariable);
      Object sizeObj = params.get(var);
      if (sizeObj instanceof Number) {
        return ((Number) sizeObj).intValue();
      } else if (sizeObj instanceof String) {
        return Integer.parseInt((String) sizeObj);
      } else if (sizeObj == null) {
        throw new IllegalArgumentException("Loop size variable not found: " + var);
      } else {
        throw new IllegalArgumentException("Loop size variable must be Number or String: " + var);
      }
    } else {
      return Integer.parseInt(_sizeVariable);
    }
  }

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + getFragments();
  }

}
