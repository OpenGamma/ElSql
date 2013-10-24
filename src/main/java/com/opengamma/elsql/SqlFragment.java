/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Single fragment in the elsql AST.
 */
abstract class SqlFragment {

  /**
   * Convert this fragment to SQL, appending it to the specified buffer.
   * 
   * @param buf  the buffer to append to, not null
   * @param bundle  the elsql bundle for context, not null
   * @param paramSource  the SQL parameters, not null
   * @param loopIndex  the current loopIndex
   */
  protected abstract void toSQL(StringBuilder buf, ElSqlBundle bundle, SqlParameterSource paramSource, int loopIndex);

  /**
   * Applies the loop index to the string.
   * 
   * @param text  the text to apply to, not null
   * @param loopIndex  the loop index
   * @return the applied text, not null
   */
  protected String applyLoopIndex(String text, int loopIndex) {
    return text.replace("@LOOPINDEX", Integer.toString(loopIndex));
  }

}
