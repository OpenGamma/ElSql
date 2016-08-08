/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class SqlFragmentsTest {

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_constructor_nullMap() {
    new SqlFragments(null, ElSqlConfig.DEFAULT);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_constructor_nullConfig() {
    new SqlFragments(new HashMap<String, NameSqlFragment>(), null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_withConfig_null() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    bundle.withConfig(null);
  }

  //-------------------------------------------------------------------------
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_invalidTab() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "\tSELECT * FROM foo"
    );
    SqlFragments.parse(lines);
  }

  public void test_unknownTagMidLine() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT *",
        "  FROM @WIBBLE");
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", EmptySqlParams.INSTANCE);
    assertEquals("SELECT * FROM @WIBBLE ", sql1);
  }

  public void test_name_1name_1line_noParameters() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", EmptySqlParams.INSTANCE);
    assertEquals("SELECT * FROM foo ", sql1);
  }

  //-------------------------------------------------------------------------
  public void test_name_1name_1line() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", EmptySqlParams.INSTANCE);
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_name_2names_1line() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  ",
        "@NAME(Test2)",
        "  SELECT * FROM bar"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", EmptySqlParams.INSTANCE);
    assertEquals("SELECT * FROM foo ", sql1);
    String sql2 = bundle.getSql("Test2", EmptySqlParams.INSTANCE);
    assertEquals("SELECT * FROM bar ", sql2);
  }

  public void test_name_2names_2lines() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE TRUE",
        "  ",
        "@NAME(Test2)",
        "  SELECT * FROM bar",
        "  WHERE FALSE"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", EmptySqlParams.INSTANCE);
    assertEquals("SELECT * FROM foo WHERE TRUE ", sql1);
    String sql2 = bundle.getSql("Test2", EmptySqlParams.INSTANCE);
    assertEquals("SELECT * FROM bar WHERE FALSE ", sql2);
  }

  public void test_name_midComments() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM  -- first line",
        "--  foo",
        "  WHERE TRUE",
        "  "
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", EmptySqlParams.INSTANCE);
    assertEquals("SELECT * FROM WHERE TRUE ", sql1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_name_invalidFormat1() {
    List<String> lines = Arrays.asList(
        "@NAME("
    );
    SqlFragments.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_invalidFormat2() {
    List<String> lines = Arrays.asList(
        "@NAME()"
    );
    SqlFragments.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_name_invalidFormat3() {
    List<String> lines = Arrays.asList(
        "@NAME(!)"
    );
    SqlFragments.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_name_invalidFormat4() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "@NAME(Test2)"
    );
    SqlFragments.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_name_notFound() {
    SqlFragments bundle = SqlFragments.parse(new ArrayList<String>());
    bundle.getSql("Unknown", EmptySqlParams.INSTANCE);
  }

  //-------------------------------------------------------------------------
  public void test_insert_name() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM @INCLUDE(Table) WHERE TRUE",
        "  ",
        "@NAME(Table)",
        "  foo"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", EmptySqlParams.INSTANCE);
    assertEquals("SELECT * FROM foo WHERE TRUE ", sql1);
    String sql2 = bundle.getSql("Table", EmptySqlParams.INSTANCE);
    assertEquals("foo ", sql2);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_insert_name_notFound() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM @INCLUDE(Table) WHERE TRUE",
        "  "
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    bundle.getSql("Test1", EmptySqlParams.INSTANCE);
  }

  //-------------------------------------------------------------------------
  public void test_include_variable() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM @INCLUDE(:var) WHERE TRUE",
        "  ",
        "@NAME(Table)",
        "  foo"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "Table"));
    assertEquals("SELECT * FROM foo WHERE TRUE ", sql1);
  }

  public void test_include_variable_extendedFormat() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM @INCLUDE(:{var}) WHERE TRUE",
        "  ",
        "@NAME(Table)",
        "  foo"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "Table"));
    assertEquals("SELECT * FROM foo WHERE TRUE ", sql1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_include_variable_notFound() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM @INCLUDE(:var) WHERE TRUE",
        "  "
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    bundle.getSql("Test1", EmptySqlParams.INSTANCE);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_include_invalidFormat1() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM @INCLUDE(:var WHERE TRUE"
    );
    SqlFragments.parse(lines);
  }

  //-------------------------------------------------------------------------
  public void test_like_equals() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE var @LIKE :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "val"));
    assertEquals("SELECT * FROM foo WHERE var = :var ", sql1);
  }

  public void test_like_likePercent() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE var @LIKE :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "va%"));
    assertEquals("SELECT * FROM foo WHERE var LIKE :var ", sql1);
  }

  public void test_like_likeUnderscore() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE var @LIKE :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "va_"));
    assertEquals("SELECT * FROM foo WHERE var LIKE :var ", sql1);
  }

  public void test_like_isNull() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE var @LIKE :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", null));
    assertEquals("SELECT * FROM foo WHERE var IS NULL ", sql1);
  }

  public void test_likeEndLike_equals() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE (var @LIKE :var @ENDLIKE)"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "val"));
    assertEquals("SELECT * FROM foo WHERE (var = :var ) ", sql1);
  }

  public void test_likeEndLike_like() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE (var @LIKE :var @ENDLIKE)"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "va%l"));
    assertEquals("SELECT * FROM foo WHERE (var LIKE :var ) ", sql1);
  }

  public void test_likeEndLike_like_configEscape() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE (var @LIKE :var @ENDLIKE)"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    bundle = bundle.withConfig(ElSqlConfig.HSQL);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "va%l"));
    assertEquals("SELECT * FROM foo WHERE (var LIKE :var ESCAPE '\\' ) ", sql1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_like_invalidFormat1() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE (var @LIKE"
    );
    SqlFragments.parse(lines);
  }

  //-------------------------------------------------------------------------
  public void test_equals_equals()  {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE var @EQUALS :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "val"));
    assertEquals("SELECT * FROM foo WHERE var = :var ", sql1);   
  }

  public void test_equals_isNull() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE var @EQUALS :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", null));
    assertEquals("SELECT * FROM foo WHERE var IS NULL ", sql1);       
  }

  public void test_equalsEndEquals_equals() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE (var @EQUALS :var @ENDEQUALS)"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "val"));
    assertEquals("SELECT * FROM foo WHERE (var = :var ) ", sql1);
  }

  public void test_equalsEndEquals_isNull() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE (var @EQUALS :var @ENDEQUALS)"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", null));
    assertEquals("SELECT * FROM foo WHERE (var IS NULL ) ", sql1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_equals_invalidFormat1() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE (var @EQUALS"
    );
    SqlFragments.parse(lines);
  }

  //-------------------------------------------------------------------------
  public void test_offsetFetch_bothDefaultVars() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @OFFSETFETCH"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("paging_offset", 7).with("paging_fetch", 3);
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo OFFSET 7 ROWS FETCH NEXT 3 ROWS ONLY ", sql1);
  }

  public void test_offsetFetch_offsetDefaultVar() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @OFFSETFETCH"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("paging_offset", 7);
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo OFFSET 7 ROWS ", sql1);
  }

  public void test_offsetFetch_fetchDefaultVar() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @OFFSETFETCH"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("paging_fetch", 3);
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo FETCH FIRST 3 ROWS ONLY ", sql1);
  }

  public void test_offsetFetch_specifiedVars() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @OFFSETFETCH(:offset, :fetch) ENDFOO"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("offset", 7).with("fetch", 3);
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo OFFSET 7 ROWS FETCH NEXT 3 ROWS ONLY ENDFOO ", sql1);
  }

  public void test_offsetFetch_specifiedVars_extendedFormat() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @OFFSETFETCH(:{offset}, :{fetch}) ENDFOO"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("offset", 7).with("fetch", 3);
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo OFFSET 7 ROWS FETCH NEXT 3 ROWS ONLY ENDFOO ", sql1);
  }

  public void test_offsetFetch_specifiedLiterals() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @OFFSETFETCH(8, 4) ENDFOO"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", EmptySqlParams.INSTANCE);
    assertEquals("SELECT * FROM foo OFFSET 8 ROWS FETCH NEXT 4 ROWS ONLY ENDFOO ", sql1);
  }

  //-------------------------------------------------------------------------
  public void test_paging_specifiedVars() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  @PAGING(:offset, :fetch)",
        "    SELECT * FROM foo ORDER BY bar "
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("offset", 7).with("fetch", 3);
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo ORDER BY bar OFFSET 7 ROWS FETCH NEXT 3 ROWS ONLY ", sql1);
  }

  public void test_paging_specifiedLiterals() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  @PAGING(8, 4)",
        "    SELECT * FROM foo ORDER BY bar "
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", EmptySqlParams.INSTANCE);
    assertEquals("SELECT * FROM foo ORDER BY bar OFFSET 8 ROWS FETCH NEXT 4 ROWS ONLY ", sql1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_paging_invalidFormat1() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  @PAGING(:offset, :fetch",
        "    SELECT * FROM foo ORDER BY bar "
    );
    SqlFragments.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_paging_invalidFormat2() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  @PAGING(:offset, :fetch)",
        "  @PAGING(:offset, :fetch)"
    );
    SqlFragments.parse(lines);
  }

  //-------------------------------------------------------------------------
  public void test_fetch_defaultVar() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @FETCH"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("paging_fetch", 3);
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo FETCH FIRST 3 ROWS ONLY ", sql1);
  }

  public void test_fetch_specifiedVars() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @FETCH(:fetch) ENDFOO"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("fetch", 4);
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo FETCH FIRST 4 ROWS ONLY ENDFOO ", sql1);
  }

  public void test_fetch_amount() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @FETCH(5) ENDFOO"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = EmptySqlParams.INSTANCE;
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo FETCH FIRST 5 ROWS ONLY ENDFOO ", sql1);
  }

  //-------------------------------------------------------------------------
  public void test_if_varAbsent() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var)",
        "    WHERE var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", EmptySqlParams.INSTANCE);
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_if_varPresent() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var)",
        "    WHERE var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "val"));
    assertEquals("SELECT * FROM foo WHERE var = :var ", sql1);
  }

  public void test_if_varPresentNull() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var)",
        "    WHERE var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", null));
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_if_varPresentBooleanFalse() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var)",
        "    WHERE var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", Boolean.FALSE));
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_if_varPresentBooleanTrue() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var)",
        "    WHERE var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", Boolean.TRUE));
    assertEquals("SELECT * FROM foo WHERE var = :var ", sql1);
  }

  public void test_if_varPresentBooleanFalseEqualsFalse() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var = false)",
        "    WHERE var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", Boolean.FALSE));
    assertEquals("SELECT * FROM foo WHERE var = :var ", sql1);
  }

  public void test_if_varPresentBooleanFalseEqualsTrue() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var = true)",
        "    WHERE var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", Boolean.FALSE));
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_if_varPresentBooleanTrueEqualsFalse() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var = false)",
        "    WHERE var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", Boolean.TRUE));
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_if_varPresentBooleanTrueEqualsTrue() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var = true)",
        "    WHERE var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", Boolean.TRUE));
    assertEquals("SELECT * FROM foo WHERE var = :var ", sql1);
  }

  public void test_if_withMatch_varPresentMatch() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var = Hello)",
        "    WHERE var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "Hello"));
    assertEquals("SELECT * FROM foo WHERE var = :var ", sql1);
  }

  public void test_if_withMatch_varPresentNoMatch() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var = Hello)",
        "    WHERE var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "NoMatch"));
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_if_withMatch_varPresentMatchNull() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var = Hello)",
        "    WHERE var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", null));
    assertEquals("SELECT * FROM foo ", sql1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_if_invalidFormat1() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @IF(:var",
        "      var = :var"
    );
    SqlFragments.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_if_invalidFormat2() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @IF(:var)",
        "    @IF(:var)"
    );
    SqlFragments.parse(lines);
  }

  //-------------------------------------------------------------------------
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_where_invalidFormat1() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE true",
        "    @AND(:var)",
        "      var = :var"
    );
    SqlFragments.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_where_invalidFormat2() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "  @WHERE"
    );
    SqlFragments.parse(lines);
  }

  //-------------------------------------------------------------------------
  public void test_and_1and_varAbsent() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @AND(:var)",
        "      var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", EmptySqlParams.INSTANCE);
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_and_1and_varAbsent_extendedFormat() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @AND(:{var})",
        "      var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", EmptySqlParams.INSTANCE);
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_and_1and_varPresent() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @AND(:var)",
        "      var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "val"));
    assertEquals("SELECT * FROM foo WHERE var = :var ", sql1);
  }

  public void test_and_1and_varPresent_extendedFormat() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @AND(:{var})",
        "      var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "val"));
    assertEquals("SELECT * FROM foo WHERE var = :var ", sql1);
  }

  public void test_and_2and_varPresentAbsent() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @AND(:var)",
        "      var = :var",
        "    @AND(:vax)",
        "      vax = :vax"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "val"));
    assertEquals("SELECT * FROM foo WHERE var = :var ", sql1);
  }

  public void test_and_2and_varAbsentPresent() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @AND(:var)",
        "      var = :var",
        "    @AND(:vax)",
        "      vax = :vax"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("vax", "val"));
    assertEquals("SELECT * FROM foo WHERE vax = :vax ", sql1);
  }

  public void test_and_2and_varPresent() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @AND(:var)",
        "      var = :var",
        "    @AND(:vax)",
        "      vax = :vax"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("var", "val").with("vax", "val");
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo WHERE var = :var AND vax = :vax ", sql1);
  }

  public void test_and_withMatch_varAbsent() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @AND(:var = Point)",
        "      var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", EmptySqlParams.INSTANCE);
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_and_withMatch_varPresentMatch() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @AND(:var = Point)",
        "      var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "Point"));
    assertEquals("SELECT * FROM foo WHERE var = :var ", sql1);
  }

  public void test_and_withMatch_varPresentNoMatch() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @AND(:var = Point)",
        "      var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "NoPoint"));
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_and_withMatch_varPresentNull() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @AND(:var = Point)",
        "      var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", null));
    assertEquals("SELECT * FROM foo ", sql1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_and_invalidFormat1() {
    List<String> lines = Arrays.asList(
        "@AND("
    );
    SqlFragments.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_and_invalidFormat2() {
    List<String> lines = Arrays.asList(
        "@AND()"
    );
    SqlFragments.parse(lines);
  }


  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_and_invalidFormat3() {
    List<String> lines = Arrays.asList(
        "@AND(!)"
    );
    SqlFragments.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_and_invalidFormat4() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @AND(:var",
        "      var = :var"
    );
    SqlFragments.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_and_invalidFormat5() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @AND(:var)",
        "    @AND(:var)"
    );
    SqlFragments.parse(lines);
  }

  //-------------------------------------------------------------------------
  public void test_or_1or_varAbsent() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @OR(:var)",
        "      var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", EmptySqlParams.INSTANCE);
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_or_1or_varPresent() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @OR(:var)",
        "      var = :var"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "val"));
    assertEquals("SELECT * FROM foo WHERE var = :var ", sql1);
  }

  public void test_or_2or_varPresentAbsent() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @OR(:var)",
        "      var = :var",
        "    @OR(:vax)",
        "      vax = :vax"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("var", "val"));
    assertEquals("SELECT * FROM foo WHERE var = :var ", sql1);
  }

  public void test_or_2or_varAbsentPresent() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @OR(:var)",
        "      var = :var",
        "    @OR(:vax)",
        "      vax = :vax"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParams("vax", "val"));
    assertEquals("SELECT * FROM foo WHERE vax = :vax ", sql1);
  }

  public void test_or_2or_varPresent() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @OR(:var)",
        "      var = :var",
        "    @OR(:vax)",
        "      vax = :vax"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("var", "val").with("vax", "val");
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo WHERE var = :var OR vax = :vax ", sql1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_or_invalidFormat1() {
    List<String> lines = Arrays.asList(
        "@OR("
    );
    SqlFragments.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_or_invalidFormat2() {
    List<String> lines = Arrays.asList(
        "@OR()"
    );
    SqlFragments.parse(lines);
  }


  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_or_invalidFormat3() {
    List<String> lines = Arrays.asList(
        "@OR(!)"
    );
    SqlFragments.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_or_invalidFormat4() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @OR(:var",
        "      var = :var"
    );
    SqlFragments.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_or_invalidFormat5() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @OR(:var)",
        "    @OR(:var)"
    );
    SqlFragments.parse(lines);
  }

  //-------------------------------------------------------------------------
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_topLevelNotName1() {
    List<String> lines = Arrays.asList(
        "@AND(:var)",
        "  var = :var"
    );
    SqlFragments.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_topLevelNotName2() {
    List<String> lines = Arrays.asList(
        "SELECT foo FROM bar"
    );
    SqlFragments.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_unknownTagAtStartLine() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @UNKNOWN"
    );
    SqlFragments.parse(lines);
  }

  //-------------------------------------------------------------------------
  public void test_value() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM @VALUE(:var) WHERE true"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("var", "mytable");
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM mytable WHERE true ", sql1);
  }

  public void test_value_extendedFormat() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM @VALUE(:{var}) WHERE true"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("var", "mytable");
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM mytable WHERE true ", sql1);
  }

  public void test_value_followedByComma() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM @VALUE(:var), vax"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("var", "mytable");
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM mytable, vax ", sql1);
  }

  public void test_value_insertContainsComma() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM base @VALUE(:additionaltables)"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("additionaltables", ", mytable");
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM base , mytable ", sql1);
  }

  public void test_value_null() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM base @VALUE(:additionaltables)"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("additionaltables", null);
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM base ", sql1);
  }

  public void test_value_like() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT DISTINCT doc_id FROM main",
        "  WHERE main.id = :doc.id",
        "  AND doc.id IN ( SELECT id FROM @VALUE(:table_prefix)_idkey WHERE key_scheme @LIKE :scheme @ENDLIKE )"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("table_prefix", "mytable").with("scheme", "myscheme");
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT DISTINCT doc_id FROM main WHERE main.id = :doc.id " +
        "AND doc.id IN ( SELECT id FROM mytable_idkey WHERE key_scheme = :scheme ) ", sql1);
  }

  public void test_value_fetch() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  AA @VALUE(:var) @FETCH"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("var", "mytable").with("paging_fetch", 10);
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("AA mytable FETCH FIRST 10 ROWS ONLY ", sql1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_value_invalidFormat1() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  AA @VALUE(:var @FETCH"
    );
    SqlFragments.parse(lines);
  }

  //-------------------------------------------------------------------------
  public void test_loopNoJoin() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo WHERE",
        "  @LOOP(:size)",
        "    (a = :a@LOOPINDEX AND b = :b@LOOPINDEX)"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("size", 2)
      .with("a0", "name").with("b0", "bob")
      .with("b0", "type").with("b1", "doctor");
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo WHERE (a = :a0 AND b = :b0) (a = :a1 AND b = :b1) ", sql1);
  }

  public void test_loopNoJoin_extendedFormat() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo WHERE",
        "  @LOOP(:{size})",
        "    (a = :a@LOOPINDEX AND b = :b@LOOPINDEX)"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("size", 2)
      .with("a0", "name").with("b0", "bob")
      .with("b0", "type").with("b1", "doctor");
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo WHERE (a = :a0 AND b = :b0) (a = :a1 AND b = :b1) ", sql1);
  }

  public void test_loopNoJoin_hardCodedSize() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo WHERE",
        "  @LOOP(2)",
        "    (a = :a@LOOPINDEX AND b = :b@LOOPINDEX)"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("a0", "name")
      .with("b0", "bob")
      .with("b0", "type").with("b1", "doctor");
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo WHERE (a = :a0 AND b = :b0) (a = :a1 AND b = :b1) ", sql1);
  }

  public void test_loopWithJoin() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo WHERE",
        "  @LOOP(:size)",
        "    (a = :a@LOOPINDEX1 AND b = :b@LOOPINDEX)",
        "    @LOOPJOIN OR"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("size", 2)
      .with("a0", "name").with("b0", "bob")
      .with("b0", "type").with("b1", "doctor");
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo WHERE (a = :a0 AND b = :b0) OR (a = :a1 AND b = :b1) ", sql1);
  }

  public void test_loopWithJoin_sizeString() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo WHERE",
        "  @LOOP(:size)",
        "    (a = :a@LOOPINDEX AND b = :b@LOOPINDEX)",
        "    @LOOPJOIN OR"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("size", "1")
      .with("a0", "name").with("b0", "bob");
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo WHERE (a = :a0 AND b = :b0) ", sql1);
  }

  public void test_loopWithJoin_sizeZeroWithWhereTag() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @WHERE",
        "    @LOOP(:size)",
        "      (a = :a@LOOPINDEX AND b = :b@LOOPINDEX)",
        "      @LOOPJOIN OR"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("size", 0);
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_loopWithJoin_like() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo WHERE",
        "  @LOOP(:size)",
        "    (a = :a@LOOPINDEX AND b @LIKE :b@LOOPINDEX1@ENDLIKE)",
        "    @LOOPJOIN OR"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("size", 2)
      .with("a0", "name").with("b0", "bob")
      .with("b0", "type").with("b1", "doctor");
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo WHERE (a = :a0 AND b = :b0 ) OR (a = :a1 AND b = :b1 ) ", sql1);
  }

  public void test_loopWithJoin_likeSpace() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo WHERE",
        "  @LOOP(:size)",
        "    (a = :a@LOOPINDEX AND b @LIKE :b@LOOPINDEX @ENDLIKE)",
        "    @LOOPJOIN OR"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("size", 2)
      .with("a0", "name").with("b0", "bob")
      .with("b0", "type").with("b1", "doctor");
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo WHERE (a = :a0 AND b = :b0 ) OR (a = :a1 AND b = :b1 ) ", sql1);
  }

  public void test_loopWithJoin_value() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo WHERE",
        "  @LOOP(:size)",
        "    (a = :a@LOOPINDEX AND b = @VALUE(:b@LOOPINDEX1))",
        "    @LOOPJOIN OR"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("size", 2)
      .with("a0", "name").with("b0", "bob")
      .with("a1", "type").with("b1", "doctor");
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo WHERE (a = :a0 AND b = bob) OR (a = :a1 AND b = doctor) ", sql1);
  }

  public void test_loopWithJoin_value_extendedFormat() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo WHERE",
        "  @LOOP(:{size})",
        "    (a = :a@LOOPINDEX AND b = @VALUE(:{b@LOOPINDEX1}))",
        "    @LOOPJOIN OR"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("size", 2)
      .with("a0", "name").with("b0", "bob")
      .with("a1", "type").with("b1", "doctor");
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo WHERE (a = :a0 AND b = bob) OR (a = :a1 AND b = doctor) ", sql1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_loop_sizeBadType() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo WHERE",
        "  @LOOP(:size)",
        "    (a = :a@LOOPINDEX AND b = :b@LOOPINDEX)",
        "    @LOOPJOIN OR"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("size", new Date())
      .with("a0", "name").with("b0", "bob");
    bundle.getSql("Test1", params);
  }

  public void test_loopInLoopWithJoin() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo WHERE",
        "  @LOOP(:size1)",
        "    @LOOP(:size2)",
        "      (a = :a@LOOPINDEX1 AND b = :b@LOOPINDEX2)",
        "      @LOOPJOIN OR",
        "    @LOOPJOIN AND"
    );
    SqlFragments bundle = SqlFragments.parse(lines);
    SqlParams params = new MapSqlParams("size1", 2)
      .with("size2", "2")
      .with("a0", "name").with("b0", "bob")
      .with("b0", "type").with("b1", "doctor");
    String sql1 = bundle.getSql("Test1", params);
    assertEquals("SELECT * FROM foo WHERE (a = :a0 AND b = :b0) OR (a = :a0 AND b = :b1) " +
        "AND (a = :a1 AND b = :b0) OR (a = :a1 AND b = :b1) ", sql1);
  }

}
