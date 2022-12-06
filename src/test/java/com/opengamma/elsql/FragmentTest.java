/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Test.
 */
public class FragmentTest {

  @Test
  public void test_NameSqlFragment() {
    NameSqlFragment test = new NameSqlFragment("test");
    assertEquals("NameSqlFragment:test []", test.toString());
  }

  @Test
  public void test_NameSqlFragment_null() {
    assertThrows(IllegalArgumentException.class, () -> new NameSqlFragment(null));
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_ContainerSqlFragment() {
    ContainerSqlFragment test = new ContainerSqlFragment();
    assertEquals("[]", test.toString());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_IncludeSqlFragment_id() {
    IncludeSqlFragment test = new IncludeSqlFragment("test");
    assertEquals("IncludeSqlFragment:test", test.toString());
  }

  @Test
  public void test_IncludeSqlFragment_var() {
    IncludeSqlFragment test = new IncludeSqlFragment(":test");
    assertEquals("IncludeSqlFragment::test", test.toString());
  }

  @Test
  public void test_IncludeSqlFragment_varExtended() {
    IncludeSqlFragment test = new IncludeSqlFragment(":{test}");
    assertEquals("IncludeSqlFragment::{test}", test.toString());
  }

  @Test
  public void test_IncludeSqlFragment_varExtendedDollar() {
    IncludeSqlFragment test = new IncludeSqlFragment(":${test}");
    assertEquals("IncludeSqlFragment::${test}", test.toString());
  }

  @Test
  public void test_IncludeSqlFragment_null() {
    assertThrows(IllegalArgumentException.class, () -> new IncludeSqlFragment(null));
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_ValueSqlFragment() {
    ValueSqlFragment test = new ValueSqlFragment(":test", true);
    assertEquals("ValueSqlFragment:test", test.toString());
  }

  @Test
  public void test_ValueSqlFragment_extended() {
    ValueSqlFragment test = new ValueSqlFragment(":{test}", true);
    assertEquals("ValueSqlFragment:test", test.toString());
  }

  @Test
  public void test_ValueSqlFragment_extendedDollar() {
    ValueSqlFragment test = new ValueSqlFragment(":${test}", true);
    assertEquals("ValueSqlFragment:${test}", test.toString());
  }

  @Test
  public void test_ValueSqlFragment_null() {
    assertThrows(IllegalArgumentException.class, () -> new ValueSqlFragment(null, true));
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_TextSqlFragment_eol() {
    TextSqlFragment test = new TextSqlFragment("test", true);
    assertEquals("TextSqlFragment:test ", test.toString());
  }

  @Test
  public void test_TextSqlFragment_eol_empty() {
    TextSqlFragment test = new TextSqlFragment("", true);
    assertEquals("TextSqlFragment:", test.toString());
  }

  @Test
  public void test_TextSqlFragment_notEol() {
    TextSqlFragment test = new TextSqlFragment("test", false);
    assertEquals("TextSqlFragment:test", test.toString());
  }

  @Test
  public void test_TextSqlFragment_null() {
    assertThrows(IllegalArgumentException.class, () -> new TextSqlFragment(null, true));
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_EqualsSqlFragment() {
    EqualsSqlFragment test = new EqualsSqlFragment(":test");
    assertEquals("EqualsSqlFragment:test []", test.toString());
  }

  @Test
  public void test_EqualsSqlFragment_null() {
    assertThrows(IllegalArgumentException.class, () -> new EqualsSqlFragment(null));
  }

  @Test
  public void test_EqualsSqlFragment_notVariable() {
    assertThrows(IllegalArgumentException.class, () -> new EqualsSqlFragment("test"));
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_LikeSqlFragment() {
    LikeSqlFragment test = new LikeSqlFragment(":test");
    assertEquals("LikeSqlFragment:test []", test.toString());
  }

  @Test
  public void test_LikeSqlFragment_null() {
    assertThrows(IllegalArgumentException.class, () -> new LikeSqlFragment(null));
  }

  @Test
  public void test_LikeSqlFragment_notVariable() {
    assertThrows(IllegalArgumentException.class, () -> new LikeSqlFragment("test"));
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_LoopSqlFragment() {
    LoopSqlFragment test = new LoopSqlFragment(":test");
    assertEquals("LoopSqlFragment []", test.toString());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_OffsetFetchSqlFragment() {
    OffsetFetchSqlFragment test = new OffsetFetchSqlFragment(":test");
    assertEquals("OffsetFetchSqlFragment []", test.toString());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_PagingSqlFragment() {
    PagingSqlFragment test = new PagingSqlFragment(":test", ":bar");
    assertEquals("PagingSqlFragment []", test.toString());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_WhereSqlFragment() {
    WhereSqlFragment test = new WhereSqlFragment();
    assertEquals("WhereSqlFragment []", test.toString());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_AndSqlFragment() {
    AndSqlFragment test = new AndSqlFragment(":var", "match");
    assertEquals("var", test.getVariable());
    assertEquals("match", test.getMatchValue());
    assertEquals("AndSqlFragment:var []", test.toString());
  }

  @Test
  public void test_AndSqlFragment_nullMatch() {
    AndSqlFragment test = new AndSqlFragment(":var", null);
    assertEquals("var", test.getVariable());
    assertEquals(null, test.getMatchValue());
    assertEquals("AndSqlFragment:var []", test.toString());
  }

  @Test
  public void test_AndSqlFragment_nullVariable() {
    assertThrows(IllegalArgumentException.class, () -> new AndSqlFragment(null, "match"));
  }

  @Test
  public void test_AndSqlFragment_notVariable() {
    assertThrows(IllegalArgumentException.class, () -> new AndSqlFragment("test", "match"));
  }

  @Test
  public void test_AndSqlFragment_notVariableTooShort() {
    assertThrows(IllegalArgumentException.class, () -> new AndSqlFragment(":", "match"));
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_OrSqlFragment() {
    OrSqlFragment test = new OrSqlFragment(":var", "match");
    assertEquals("var", test.getVariable());
    assertEquals("match", test.getMatchValue());
    assertEquals("OrSqlFragment:var []", test.toString());
  }

  @Test
  public void test_OrSqlFragment_nullMatch() {
    OrSqlFragment test = new OrSqlFragment(":var", null);
    assertEquals("var", test.getVariable());
    assertEquals(null, test.getMatchValue());
    assertEquals("OrSqlFragment:var []", test.toString());
  }

  @Test
  public void test_OrSqlFragment_nullVariable() {
    assertThrows(IllegalArgumentException.class, () -> new OrSqlFragment(null, "match"));
  }

  @Test
  public void test_OrSqlFragment_notVariable() {
    assertThrows(IllegalArgumentException.class, () -> new OrSqlFragment("test", "match"));
  }

  @Test
  public void test_OrSqlFragment_notVariableTooShort() {
    assertThrows(IllegalArgumentException.class, () -> new OrSqlFragment(":", "match"));
  }

}
