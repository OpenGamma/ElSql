/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import static org.testng.AssertJUnit.assertEquals;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class ElSqlBundleTest {

  public void test_of_noOverride() {
    ElSqlBundle test = ElSqlBundle.of(ElSqlConfig.DEFAULT, ElSql.class);
    assertEquals(ElSqlConfig.DEFAULT, test.getConfig());
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo"));
    assertEquals("SELECT * FROM bar ", test.getSql("TestBar"));
  }

  public void test_of_noOverride_withConfig() {
    ElSqlBundle test = ElSqlBundle.of(ElSqlConfig.DEFAULT, ElSql.class);
    assertEquals(ElSqlConfig.DEFAULT, test.getConfig());
    test.withConfig(ElSqlConfig.HSQL);  // resources not reloaded
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo"));
    assertEquals("SELECT * FROM bar ", test.getSql("TestBar"));
  }

  public void test_of_dbOverride() {
    ElSqlBundle test = ElSqlBundle.of(ElSqlConfig.HSQL, ElSql.class);
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo"));
    assertEquals("SELECT * FROM bar, foo ", test.getSql("TestBar"));
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_of_nullConfig() {
    ElSqlBundle.of(null, ElSqlBundle.class);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_of_nullClass() {
    ElSqlBundle.of(ElSqlConfig.DEFAULT, null);
  }

  //-------------------------------------------------------------------------
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_parse_nullConfig() {
    ElSqlBundle.parse(null, new Resource[0]);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_parse_nullClass() {
    ElSqlBundle.parse(ElSqlConfig.DEFAULT, (Resource[]) null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_parse_noExistingResource() {
    Resource resource = new ClassPathResource("NAME_OF_NON_EXISTING_RESOURCE.elsql");
    ElSqlBundle.parse(ElSqlConfig.DEFAULT, resource);
  }

  //-------------------------------------------------------------------------
  public void test_getSql() {
    ElSqlBundle test = ElSqlBundle.of(ElSqlConfig.DEFAULT, ElSql.class);
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo"));
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo", new MapSqlParameterSource()));
  }

}
