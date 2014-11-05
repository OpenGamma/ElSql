/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class ElSqlBundleTest {

  public void test_name_1name_1line() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource());
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource());
    assertEquals("SELECT * FROM foo ", sql1);
    String sql2 = bundle.getSql("Test2", new MapSqlParameterSource());
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource());
    assertEquals("SELECT * FROM foo WHERE TRUE ", sql1);
    String sql2 = bundle.getSql("Test2", new MapSqlParameterSource());
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource());
    assertEquals("SELECT * FROM WHERE TRUE ", sql1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_name_invalidFormat1() {
    List<String> lines = Arrays.asList(
        "@NAME("
    );
    ElSqlBundle.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_invalidFormat2() {
    List<String> lines = Arrays.asList(
        "@NAME()"
    );
    ElSqlBundle.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_name_invalidFormat3() {
    List<String> lines = Arrays.asList(
        "@NAME(!)"
    );
    ElSqlBundle.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_name_notFound() {
    ElSqlBundle bundle = ElSqlBundle.parse(new ArrayList<String>());
    bundle.getSql("Unknown", new MapSqlParameterSource());
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource());
    assertEquals("SELECT * FROM foo WHERE TRUE ", sql1);
    String sql2 = bundle.getSql("Table", new MapSqlParameterSource());
    assertEquals("foo ", sql2);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_insert_name_notFound() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM @INCLUDE(Table) WHERE TRUE",
        "  "
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    bundle.getSql("Test1", new MapSqlParameterSource());
  }

  //-------------------------------------------------------------------------
  public void test_insert_variable() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM @INCLUDE(:var) WHERE TRUE",
        "  ",
        "@NAME(Table)",
        "  foo"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", "Table"));
    assertEquals("SELECT * FROM foo WHERE TRUE ", sql1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_insert_variable_notFound() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM @INCLUDE(:var) WHERE TRUE",
        "  "
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    bundle.getSql("Test1", new MapSqlParameterSource());
  }

  //-------------------------------------------------------------------------
  public void test_like_equals() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE var @LIKE :var"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", "val"));
    assertEquals("SELECT * FROM foo WHERE var = :var ", sql1);
  }

  public void test_like_likePercent() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE var @LIKE :var"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", "va%"));
    assertEquals("SELECT * FROM foo WHERE var LIKE :var ", sql1);
  }

  public void test_like_likeUnderscore() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE var @LIKE :var"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", "va_"));
    assertEquals("SELECT * FROM foo WHERE var LIKE :var ", sql1);
  }

  public void test_likeEndLike_equals() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE (var @LIKE :var @ENDLIKE)"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", "val"));
    assertEquals("SELECT * FROM foo WHERE (var = :var ) ", sql1);
  }

  public void test_likeEndLike_like() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE (var @LIKE :var @ENDLIKE)"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", "va%l"));
    assertEquals("SELECT * FROM foo WHERE (var LIKE :var ) ", sql1);
  }

  public void test_likeEndLike_like_configEscape() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  WHERE (var @LIKE :var @ENDLIKE)"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    bundle = bundle.withConfig(ElSqlConfig.HSQL);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", "va%l"));
    assertEquals("SELECT * FROM foo WHERE (var LIKE :var ESCAPE '\\' ) ", sql1);
  }

  //-------------------------------------------------------------------------
  public void test_offsetFetch_bothDefaultVars() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @OFFSETFETCH"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource paramSource = new MapSqlParameterSource("paging_offset", 7).addValue("paging_fetch", 3);
    String sql1 = bundle.getSql("Test1", paramSource);
    assertEquals("SELECT * FROM foo OFFSET 7 ROWS FETCH NEXT 3 ROWS ONLY ", sql1);
  }

  public void test_offsetFetch_offsetDefaultVar() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @OFFSETFETCH"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource paramSource = new MapSqlParameterSource("paging_offset", 7);
    String sql1 = bundle.getSql("Test1", paramSource);
    assertEquals("SELECT * FROM foo OFFSET 7 ROWS ", sql1);
  }

  public void test_offsetFetch_fetchDefaultVar() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @OFFSETFETCH"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource paramSource = new MapSqlParameterSource("paging_fetch", 3);
    String sql1 = bundle.getSql("Test1", paramSource);
    assertEquals("SELECT * FROM foo FETCH FIRST 3 ROWS ONLY ", sql1);
  }

  public void test_offsetFetch_specifiedVars() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @OFFSETFETCH(:offset, :fetch) ENDFOO"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource paramSource = new MapSqlParameterSource("offset", 7).addValue("fetch", 3);
    String sql1 = bundle.getSql("Test1", paramSource);
    assertEquals("SELECT * FROM foo OFFSET 7 ROWS FETCH NEXT 3 ROWS ONLY ENDFOO ", sql1);
  }

  //-------------------------------------------------------------------------
  public void test_paging_specifiedVars() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  @PAGING(:offset, :fetch)",
        "    SELECT * FROM foo ORDER BY bar "
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource paramSource = new MapSqlParameterSource("offset", 7).addValue("fetch", 3);
    String sql1 = bundle.getSql("Test1", paramSource);
    assertEquals("SELECT * FROM foo ORDER BY bar OFFSET 7 ROWS FETCH NEXT 3 ROWS ONLY ", sql1);
  }

  //-------------------------------------------------------------------------
  public void test_fetch_defaultVar() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @FETCH"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource paramSource = new MapSqlParameterSource("paging_fetch", 3);
    String sql1 = bundle.getSql("Test1", paramSource);
    assertEquals("SELECT * FROM foo FETCH FIRST 3 ROWS ONLY ", sql1);
  }

  public void test_fetch_specifiedVars() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @FETCH(:fetch) ENDFOO"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource paramSource = new MapSqlParameterSource("fetch", 4);
    String sql1 = bundle.getSql("Test1", paramSource);
    assertEquals("SELECT * FROM foo FETCH FIRST 4 ROWS ONLY ENDFOO ", sql1);
  }

  public void test_fetch_amount() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @FETCH(5) ENDFOO"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource paramSource = new MapSqlParameterSource();
    String sql1 = bundle.getSql("Test1", paramSource);
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource());
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_if_varPresent() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var)",
        "    WHERE var = :var"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", "val"));
    assertEquals("SELECT * FROM foo WHERE var = :var ", sql1);
  }

  public void test_if_varPresentNull() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var)",
        "    WHERE var = :var"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", null));
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_if_varPresentBooleanFalse() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var)",
        "    WHERE var = :var"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", Boolean.FALSE));
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_if_varPresentBooleanTrue() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var)",
        "    WHERE var = :var"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", Boolean.TRUE));
    assertEquals("SELECT * FROM foo WHERE var = :var ", sql1);
  }

  public void test_if_varPresentBooleanFalseEqualsFalse() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var = false)",
        "    WHERE var = :var"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", Boolean.FALSE));
    assertEquals("SELECT * FROM foo WHERE var = :var ", sql1);
  }

  public void test_if_varPresentBooleanFalseEqualsTrue() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var = true)",
        "    WHERE var = :var"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", Boolean.FALSE));
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_if_varPresentBooleanTrueEqualsFalse() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var = false)",
        "    WHERE var = :var"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", Boolean.TRUE));
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_if_varPresentBooleanTrueEqualsTrue() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var = true)",
        "    WHERE var = :var"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", Boolean.TRUE));
    assertEquals("SELECT * FROM foo WHERE var = :var ", sql1);
  }

  public void test_if_withMatch_varPresentMatch() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var = Hello)",
        "    WHERE var = :var"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", "Hello"));
    assertEquals("SELECT * FROM foo WHERE var = :var ", sql1);
  }

  public void test_if_withMatch_varPresentNoMatch() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var = Hello)",
        "    WHERE var = :var"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", "NoMatch"));
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_if_withMatch_varPresentMatchNull() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @IF(:var = Hello)",
        "    WHERE var = :var"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", null));
    assertEquals("SELECT * FROM foo ", sql1);
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource());
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", "val"));
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", "val"));
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("vax", "val"));
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource source = new MapSqlParameterSource("var", "val").addValue("vax", "val");
    String sql1 = bundle.getSql("Test1", source);
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource());
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", "Point"));
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", "NoPoint"));
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", null));
    assertEquals("SELECT * FROM foo ", sql1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_and_invalidFormat1() {
    List<String> lines = Arrays.asList(
        "@AND("
    );
    ElSqlBundle.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_and_invalidFormat2() {
    List<String> lines = Arrays.asList(
        "@AND()"
    );
    ElSqlBundle.parse(lines);
  }


  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_and_invalidFormat3() {
    List<String> lines = Arrays.asList(
        "@AND(!)"
    );
    ElSqlBundle.parse(lines);
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource());
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", "val"));
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("var", "val"));
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    String sql1 = bundle.getSql("Test1", new MapSqlParameterSource("vax", "val"));
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource source = new MapSqlParameterSource("var", "val").addValue("vax", "val");
    String sql1 = bundle.getSql("Test1", source);
    assertEquals("SELECT * FROM foo WHERE var = :var OR vax = :vax ", sql1);
  }

  //-------------------------------------------------------------------------
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_topLevelNotName1() {
    List<String> lines = Arrays.asList(
        "@AND(:var)",
        "  var = :var"
    );
    ElSqlBundle.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_topLevelNotName2() {
    List<String> lines = Arrays.asList(
        "SELECT foo FROM bar"
    );
    ElSqlBundle.parse(lines);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_unknownTagAtStartLine() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo",
        "  @UNKNOWN"
    );
    ElSqlBundle.parse(lines);
  }

  //-------------------------------------------------------------------------
  public void test_value() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM @VALUE(:var), vax"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource source = new MapSqlParameterSource("var", "mytable");
    String sql1 = bundle.getSql("Test1", source);
    assertEquals("SELECT * FROM mytable, vax ", sql1);
  }

  public void test_value_null() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM base @VALUE(:additionaltables)"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource source = new MapSqlParameterSource("additionaltables", null);
    String sql1 = bundle.getSql("Test1", source);
    assertEquals("SELECT * FROM base ", sql1);
  }

  public void test_valueLike() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT DISTINCT doc_id FROM main",
        "  WHERE main.id = :doc.id",
        "  AND doc.id IN ( SELECT id FROM @VALUE(:table_prefix)_idkey WHERE key_scheme @LIKE :scheme @ENDLIKE )"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource source = new MapSqlParameterSource("table_prefix", "mytable").addValue("scheme", "myscheme");
    String sql1 = bundle.getSql("Test1", source);
    assertEquals("SELECT DISTINCT doc_id FROM main WHERE main.id = :doc.id " +
        "AND doc.id IN ( SELECT id FROM mytable_idkey WHERE key_scheme = :scheme ) ", sql1);
  }

  public void test_valueFetch() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  AA @VALUE(:var) @FETCH"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource source = new MapSqlParameterSource("var", "mytable").addValue("paging_fetch", 10);
    String sql1 = bundle.getSql("Test1", source);
    assertEquals("AA mytable FETCH FIRST 10 ROWS ONLY ", sql1);
  }

  //-------------------------------------------------------------------------
  public void test_loopNoJoin() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo WHERE",
        "  @LOOP(:size)",
        "    (a = :a@LOOPINDEX AND b = :b@LOOPINDEX)"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource source = new MapSqlParameterSource("size", 2)
      .addValue("a0", "name").addValue("b0", "bob")
      .addValue("b0", "type").addValue("b1", "doctor");
    String sql1 = bundle.getSql("Test1", source);
    assertEquals("SELECT * FROM foo WHERE (a = :a0 AND b = :b0) (a = :a1 AND b = :b1) ", sql1);
  }

  public void test_loopWithJoin() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo WHERE",
        "  @LOOP(:size)",
        "    (a = :a@LOOPINDEX AND b = :b@LOOPINDEX)",
        "    @LOOPJOIN OR"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource source = new MapSqlParameterSource("size", 2)
      .addValue("a0", "name").addValue("b0", "bob")
      .addValue("b0", "type").addValue("b1", "doctor");
    String sql1 = bundle.getSql("Test1", source);
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource source = new MapSqlParameterSource("size", "1")
      .addValue("a0", "name").addValue("b0", "bob");
    String sql1 = bundle.getSql("Test1", source);
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource source = new MapSqlParameterSource("size", 0);
    String sql1 = bundle.getSql("Test1", source);
    assertEquals("SELECT * FROM foo ", sql1);
  }

  public void test_loopWithJoin_like() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo WHERE",
        "  @LOOP(:size)",
        "    (a = :a@LOOPINDEX AND b @LIKE :b@LOOPINDEX@ENDLIKE)",
        "    @LOOPJOIN OR"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource source = new MapSqlParameterSource("size", 2)
      .addValue("a0", "name").addValue("b0", "bob")
      .addValue("b0", "type").addValue("b1", "doctor");
    String sql1 = bundle.getSql("Test1", source);
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
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource source = new MapSqlParameterSource("size", 2)
      .addValue("a0", "name").addValue("b0", "bob")
      .addValue("b0", "type").addValue("b1", "doctor");
    String sql1 = bundle.getSql("Test1", source);
    assertEquals("SELECT * FROM foo WHERE (a = :a0 AND b = :b0 ) OR (a = :a1 AND b = :b1 ) ", sql1);
  }

  public void test_loopWithJoin_value() {
    List<String> lines = Arrays.asList(
        "@NAME(Test1)",
        "  SELECT * FROM foo WHERE",
        "  @LOOP(:size)",
        "    (a = :a@LOOPINDEX AND b = @VALUE(:b@LOOPINDEX))",
        "    @LOOPJOIN OR"
    );
    ElSqlBundle bundle = ElSqlBundle.parse(lines);
    MapSqlParameterSource source = new MapSqlParameterSource("size", 2)
      .addValue("a0", "name").addValue("b0", "bob")
      .addValue("a1", "type").addValue("b1", "doctor");
    String sql1 = bundle.getSql("Test1", source);
    assertEquals("SELECT * FROM foo WHERE (a = :a0 AND b = bob) OR (a = :a1 AND b = doctor) ", sql1);
  }

}
