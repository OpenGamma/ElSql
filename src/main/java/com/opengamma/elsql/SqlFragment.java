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
  abstract void toSQL(StringBuilder buf, SqlFragments fragments, SqlParams params, int[] loopIndex);

  /**
   * Applies the loop index to the string.
   * 
   * @param text  the text to apply to, not null
   * @param loopIndex  the loop index
   * @return the applied text, not null
   */
  String applyLoopIndex(String text, int[] loopIndex) {
    String result = text;
    switch (loopIndex.length) {
      case 4:
        result = result.replace("@LOOPINDEX3", Integer.toString(loopIndex[3]));  // fall through
      case 3:
        result = result.replace("@LOOPINDEX2", Integer.toString(loopIndex[2]));  // fall through
      case 2:
        result = result.replace("@LOOPINDEX1", Integer.toString(loopIndex[1]));  // fall through
      case 1:
      default:
        result = result.replace("@LOOPINDEX", Integer.toString(loopIndex[loopIndex.length - 1]));
    }
    return result;
  }

}
