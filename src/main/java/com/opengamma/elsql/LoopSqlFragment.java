/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

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
  protected void toSQL(StringBuilder buf, ElSqlBundle bundle, SqlParameterSource paramSource) {
    // find loop size
    Object sizeObj = paramSource.getValue(_sizeVariable);
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
      super.toSQL(part, bundle, paramSource);
      int replaceIndex = part.indexOf("@LOOPINDEX");
      while (replaceIndex >= 0) {
        part.replace(replaceIndex, replaceIndex + 10, Integer.toString(i));
        replaceIndex = part.indexOf("@LOOPINDEX");
      }
      // join
      replaceIndex = part.indexOf("@LOOPJOIN ");
      if (replaceIndex >= 0) {
        if (i >= (size - 1)) {
          part.setLength(replaceIndex);
        } else {
          part.delete(replaceIndex, replaceIndex + 10);
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
