/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

/**
 * Simple fragment of textual SQL.
 * <p>
 * This would typically be straightforward SQL.
 */
final class TextSqlFragment extends SqlFragment {

  /**
   * The text of the fragment.
   */
  private final String _text;

  /**
   * Creates an instance with text.
   * 
   * @param text  the text of the fragment, not null
   */
  TextSqlFragment(String text, boolean endOfLine) {
    if (text == null) {
      throw new IllegalArgumentException("Text must be specified");
    }
    if (endOfLine) {
      text = text.trim();
      if (text.length() == 0) {
        _text = "";
      } else {
        _text = text + " ";
      }
    } else {
      _text = text;
    }
  }

  //-------------------------------------------------------------------------
  @Override
  void toSQL(StringBuilder buf, SqlFragments fragments, SqlParams params, int[] loopIndex) {
    // handle LOOPINDEX, which is a text-like tag not surrounded by whitespace
    String text = applyLoopIndex(_text, loopIndex);
    buf.append(text);
  }

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return getClass().getSimpleName() + ":" + _text;
  }

}
