/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class FragmentTest {

  public void test_NameSqlFragment() {
    NameSqlFragment test = new NameSqlFragment("test");
    assertEquals("NameSqlFragment:test []", test.toString());
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_NameSqlFragment_null() {
    new NameSqlFragment(null);
  }

  //-------------------------------------------------------------------------
  public void test_ContainerSqlFragment() {
    ContainerSqlFragment test = new ContainerSqlFragment();
    assertEquals("[]", test.toString());
  }

  //-------------------------------------------------------------------------
  public void test_IncludeSqlFragment_id() {
    IncludeSqlFragment test = new IncludeSqlFragment("test");
    assertEquals("IncludeSqlFragment:test", test.toString());
  }

  public void test_IncludeSqlFragment_var() {
    IncludeSqlFragment test = new IncludeSqlFragment(":test");
    assertEquals("IncludeSqlFragment::test", test.toString());
  }

  public void test_IncludeSqlFragment_varExtended() {
    IncludeSqlFragment test = new IncludeSqlFragment(":{test}");
    assertEquals("IncludeSqlFragment::{test}", test.toString());
  }

  public void test_IncludeSqlFragment_varExtendedDollar() {
    IncludeSqlFragment test = new IncludeSqlFragment(":${test}");
    assertEquals("IncludeSqlFragment::${test}", test.toString());
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_IncludeSqlFragment_null() {
    new IncludeSqlFragment(null);
  }

  //-------------------------------------------------------------------------
  public void test_ValueSqlFragment() {
    ValueSqlFragment test = new ValueSqlFragment(":test", true);
    assertEquals("ValueSqlFragment:test", test.toString());
  }

  public void test_ValueSqlFragment_extended() {
    ValueSqlFragment test = new ValueSqlFragment(":{test}", true);
    assertEquals("ValueSqlFragment:test", test.toString());
  }

  public void test_ValueSqlFragment_extendedDollar() {
    ValueSqlFragment test = new ValueSqlFragment(":${test}", true);
    assertEquals("ValueSqlFragment:${test}", test.toString());
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_ValueSqlFragment_null() {
    new ValueSqlFragment(null, true);
  }

  //-------------------------------------------------------------------------
  public void test_TextSqlFragment_eol() {
    TextSqlFragment test = new TextSqlFragment("test", true);
    assertEquals("TextSqlFragment:test ", test.toString());
  }

  public void test_TextSqlFragment_eol_empty() {
    TextSqlFragment test = new TextSqlFragment("", true);
    assertEquals("TextSqlFragment:", test.toString());
  }

  public void test_TextSqlFragment_notEol() {
    TextSqlFragment test = new TextSqlFragment("test", false);
    assertEquals("TextSqlFragment:test", test.toString());
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_TextSqlFragment_null() {
    new TextSqlFragment(null, true);
  }

  //-------------------------------------------------------------------------
  public void test_EqualsSqlFragment() {
    EqualsSqlFragment test = new EqualsSqlFragment(":test");
    assertEquals("EqualsSqlFragment:test []", test.toString());
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_EqualsSqlFragment_null() {
    new EqualsSqlFragment(null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_EqualsSqlFragment_notVariable() {
    new EqualsSqlFragment("test");
  }

  //-------------------------------------------------------------------------
  public void test_LikeSqlFragment() {
    LikeSqlFragment test = new LikeSqlFragment(":test");
    assertEquals("LikeSqlFragment:test []", test.toString());
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_LikeSqlFragment_null() {
    new LikeSqlFragment(null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_LikeSqlFragment_notVariable() {
    new LikeSqlFragment("test");
  }

  //-------------------------------------------------------------------------
  public void test_LoopSqlFragment() {
    LoopSqlFragment test = new LoopSqlFragment(":test");
    assertEquals("LoopSqlFragment []", test.toString());
  }

  //-------------------------------------------------------------------------
  public void test_OffsetFetchSqlFragment() {
    OffsetFetchSqlFragment test = new OffsetFetchSqlFragment(":test");
    assertEquals("OffsetFetchSqlFragment []", test.toString());
  }

  //-------------------------------------------------------------------------
  public void test_PagingSqlFragment() {
    PagingSqlFragment test = new PagingSqlFragment(":test", ":bar");
    assertEquals("PagingSqlFragment []", test.toString());
  }

  //-------------------------------------------------------------------------
  public void test_WhereSqlFragment() {
    WhereSqlFragment test = new WhereSqlFragment();
    assertEquals("WhereSqlFragment []", test.toString());
  }

  //-------------------------------------------------------------------------
  public void test_AndSqlFragment() {
    AndSqlFragment test = new AndSqlFragment(":var", "match");
    assertEquals("var", test.getVariable());
    assertEquals("match", test.getMatchValue());
    assertEquals("AndSqlFragment:var []", test.toString());
  }

  public void test_AndSqlFragment_nullMatch() {
    AndSqlFragment test = new AndSqlFragment(":var", null);
    assertEquals("var", test.getVariable());
    assertEquals(null, test.getMatchValue());
    assertEquals("AndSqlFragment:var []", test.toString());
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_AndSqlFragment_nullVariable() {
    new AndSqlFragment(null, "match");
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_AndSqlFragment_notVariable() {
    new AndSqlFragment("test", "match");
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_AndSqlFragment_notVariableTooShort() {
    new AndSqlFragment(":", "match");
  }

  //-------------------------------------------------------------------------
  public void test_OrSqlFragment() {
    OrSqlFragment test = new OrSqlFragment(":var", "match");
    assertEquals("var", test.getVariable());
    assertEquals("match", test.getMatchValue());
    assertEquals("OrSqlFragment:var []", test.toString());
  }

  public void test_OrSqlFragment_nullMatch() {
    OrSqlFragment test = new OrSqlFragment(":var", null);
    assertEquals("var", test.getVariable());
    assertEquals(null, test.getMatchValue());
    assertEquals("OrSqlFragment:var []", test.toString());
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_OrSqlFragment_nullVariable() {
    new OrSqlFragment(null, "match");
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_OrSqlFragment_notVariable() {
    new OrSqlFragment("test", "match");
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_OrSqlFragment_notVariableTooShort() {
    new OrSqlFragment(":", "match");
  }

}
