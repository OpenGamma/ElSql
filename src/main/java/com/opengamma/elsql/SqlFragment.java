/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

/**
 * Single fragment in the elsql AST.
 */
abstract class SqlFragment {

  /**
   * Convert this fragment to SQL, appending it to the specified buffer.
   * 
   * @param buf  the buffer to append to, not null
   * @param fragments  the SQL fragments for context, not null
   * @param params  the SQL parameters, not null
   * @param loopIndex  the current loopIndex
   */
  abstract void toSQL(StringBuilder buf, SqlFragments fragments, SqlParams params, int loopIndex);

  /**
   * Applies the loop index to the string.
   * 
   * @param text  the text to apply to, not null
   * @param loopIndex  the loop index
   * @return the applied text, not null
   */
  String applyLoopIndex(String text, int loopIndex) {
    return text.replace("@LOOPINDEX", Integer.toString(loopIndex));
  }

}
