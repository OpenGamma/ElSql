/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import static org.testng.AssertJUnit.assertEquals;

import java.net.URL;

import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class ElSqlTest {

  public void test_of_noOverride() {
    ElSql test = ElSql.of(ElSqlConfig.DEFAULT, ElSql.class);
    assertEquals(ElSqlConfig.DEFAULT, test.getConfig());
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo"));
    assertEquals("SELECT * FROM bar ", test.getSql("TestBar"));
  }

  public void test_of_noOverride_withConfig() {
    ElSql test = ElSql.of(ElSqlConfig.DEFAULT, ElSql.class);
    assertEquals(ElSqlConfig.DEFAULT, test.getConfig());
    test.withConfig(ElSqlConfig.HSQL);  // resources not reloaded
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo"));
    assertEquals("SELECT * FROM bar ", test.getSql("TestBar"));
  }

  public void test_of_dbOverride() {
    ElSql test = ElSql.of(ElSqlConfig.HSQL, ElSql.class);
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo"));
    assertEquals("SELECT * FROM bar, foo ", test.getSql("TestBar"));
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_of_nullConfig() {
    ElSql.of(null, ElSql.class);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_of_nullClass() {
    ElSql.of(ElSqlConfig.DEFAULT, null);
  }

  //-------------------------------------------------------------------------
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_parse_nullConfig() {
    ElSql.parse(null, new URL[0]);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_parse_nullClass() {
    ElSql.parse(ElSqlConfig.DEFAULT, (URL[]) null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_parse_noExistingResource() {
    URL[] resources = new URL[] { getClass().getResource("NAME_OF_NON_EXISTING_RESOURCE.elsql") };
    ElSql.parse(ElSqlConfig.DEFAULT, resources);
  }

  //-------------------------------------------------------------------------
  public void test_getSql() {
    ElSql test = ElSql.of(ElSqlConfig.DEFAULT, ElSql.class);
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo"));
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo", EmptySqlParams.INSTANCE));
  }

}
