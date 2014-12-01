/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

/**
 * Representation of WHERE.
 * <p>
 * This outputs a WHERE clause if at least one child was output.
 */
final class LoopSqlFragment extends ContainerSqlFragment {

  /**
   * The size variable.
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
  void toSQL(StringBuilder buf, SqlFragments fragments, SqlParams params, int loopIndex) {
    // find loop size
    Object sizeObj = params.get(_sizeVariable);
    int size;
    if (sizeObj instanceof Number) {
      size = ((Number) sizeObj).intValue();
    } else if (sizeObj instanceof String) {
      size = Integer.parseInt((String) sizeObj);
    } else {
      throw new IllegalArgumentException("Loop size variable must be Number or String: " + _sizeVariable);
    }
    // loop
    for (int i = 0; i < size; i++) {
      // index
      StringBuilder part = new StringBuilder();
      super.toSQL(part, fragments, params, i);
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

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + getFragments();
  }

}
