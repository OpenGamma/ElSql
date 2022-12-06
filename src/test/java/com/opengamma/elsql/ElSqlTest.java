/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URL;

import org.junit.jupiter.api.Test;

/**
 * Test.
 */
public class ElSqlTest {

  @Test
  public void test_of_noOverride() {
    ElSql test = ElSql.of(ElSqlConfig.DEFAULT, ElSql.class);
    assertEquals(ElSqlConfig.DEFAULT, test.getConfig());
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo"));
    assertEquals("SELECT * FROM bar ", test.getSql("TestBar"));
  }

  @Test
  public void test_of_noOverride_withConfig() {
    ElSql test = ElSql.of(ElSqlConfig.DEFAULT, ElSql.class);
    assertEquals(ElSqlConfig.DEFAULT, test.getConfig());
    test.withConfig(ElSqlConfig.HSQL);  // resources not reloaded
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo"));
    assertEquals("SELECT * FROM bar ", test.getSql("TestBar"));
  }

  @Test
  public void test_of_dbOverride() {
    ElSql test = ElSql.of(ElSqlConfig.HSQL, ElSql.class);
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo"));
    assertEquals("SELECT * FROM bar, foo ", test.getSql("TestBar"));
  }

  @Test
  public void test_of_nullConfig() {
    assertThrows(IllegalArgumentException.class, () -> ElSql.of(null, ElSql.class));
  }

  @Test
  public void test_of_nullClass() {
    assertThrows(IllegalArgumentException.class, () -> ElSql.of(ElSqlConfig.DEFAULT, null));
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_parse_nullConfig() {
    assertThrows(IllegalArgumentException.class, () -> ElSql.parse(null, new URL[0]));
  }

  @Test
  public void test_parse_nullClass() {
    assertThrows(IllegalArgumentException.class, () -> ElSql.parse(ElSqlConfig.DEFAULT, (URL[]) null));
  }

  @Test
  public void test_parse_noExistingResource() {
    URL[] resources = new URL[] { getClass().getResource("NAME_OF_NON_EXISTING_RESOURCE.elsql") };
    assertThrows(IllegalArgumentException.class, () -> ElSql.parse(ElSqlConfig.DEFAULT, resources));
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_getSql() {
    ElSql test = ElSql.of(ElSqlConfig.DEFAULT, ElSql.class);
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo"));
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo", EmptySqlParams.INSTANCE));
  }

}
