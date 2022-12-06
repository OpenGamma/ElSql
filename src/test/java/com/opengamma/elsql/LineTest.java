/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.opengamma.elsql.ElSqlParser.Line;

/**
 * Test.
 */
public class LineTest {

  @Test
  public void test_simple() {
    Line line = new Line("LINE", 1);
    assertEquals("LINE", line.line());
    assertEquals("LINE", line.lineTrimmed());
    assertEquals(1, line.lineNumber());
    assertEquals(false, line.containsTab());
    assertEquals(false, line.isComment());
    assertEquals(0, line.indent());
  }

  @Test
  public void test_simple_indent() {
    Line line = new Line("  LINE", 2);
    assertEquals("  LINE", line.line());
    assertEquals("LINE", line.lineTrimmed());
    assertEquals(2, line.lineNumber());
    assertEquals(false, line.containsTab());
    assertEquals(false, line.isComment());
    assertEquals(2, line.indent());
  }

  @Test
  public void test_comment() {
    Line line = new Line("--", 1);
    assertEquals("--", line.line());
    assertEquals("", line.lineTrimmed());
    assertEquals(1, line.lineNumber());
    assertEquals(false, line.containsTab());
    assertEquals(true, line.isComment());
    assertEquals(0, line.indent());
  }

  @Test
  public void test_comment_indent() {
    Line line = new Line("  -- comment", 2);
    assertEquals("  -- comment", line.line());
    assertEquals("", line.lineTrimmed());
    assertEquals(2, line.lineNumber());
    assertEquals(false, line.containsTab());
    assertEquals(true, line.isComment());
    assertEquals(2, line.indent());
  }

  @Test
  public void test_trailingComment_indent() {
    Line line = new Line("  SELECT * FROM foo  -- comment", 2);
    assertEquals("  SELECT * FROM foo  -- comment", line.line());
    assertEquals("SELECT * FROM foo", line.lineTrimmed());
    assertEquals(2, line.lineNumber());
    assertEquals(false, line.containsTab());
    assertEquals(false, line.isComment());
    assertEquals(2, line.indent());
  }

  @Test
  public void test_tab() {
    Line line = new Line("\t@ADD(:Test)", 2);
    assertEquals("\t@ADD(:Test)", line.line());
    assertEquals("@ADD(:Test)", line.lineTrimmed());
    assertEquals(2, line.lineNumber());
    assertEquals(true, line.containsTab());
    assertEquals(false, line.isComment());
    assertEquals(0, line.indent());
  }

}
