/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * Test.
 */
public class ElSqlBundleTest {

  @Test
  public void test_of_noOverride() {
    ElSqlBundle test = ElSqlBundle.of(ElSqlConfig.DEFAULT, ElSql.class);
    assertEquals(ElSqlConfig.DEFAULT, test.getConfig());
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo"));
    assertEquals("SELECT * FROM bar ", test.getSql("TestBar"));
  }

  @Test
  public void test_of_noOverride_withConfig() {
    ElSqlBundle test = ElSqlBundle.of(ElSqlConfig.DEFAULT, ElSql.class);
    assertEquals(ElSqlConfig.DEFAULT, test.getConfig());
    test.withConfig(ElSqlConfig.HSQL);  // resources not reloaded
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo"));
    assertEquals("SELECT * FROM bar ", test.getSql("TestBar"));
  }

  @Test
  public void test_of_dbOverride() {
    ElSqlBundle test = ElSqlBundle.of(ElSqlConfig.HSQL, ElSql.class);
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo"));
    assertEquals("SELECT * FROM bar, foo ", test.getSql("TestBar"));
  }

  @Test
  public void test_of_nullConfig() {
    assertThrows(IllegalArgumentException.class, () -> ElSqlBundle.of(null, ElSqlBundle.class));
  }

  @Test
  public void test_of_nullClass() {
    assertThrows(IllegalArgumentException.class, () -> ElSqlBundle.of(ElSqlConfig.DEFAULT, null));
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_parse_nullConfig() {
    assertThrows(IllegalArgumentException.class, () -> ElSqlBundle.parse(null, new Resource[0]));
  }

  @Test
  public void test_parse_nullClass() {
    assertThrows(IllegalArgumentException.class, () -> ElSqlBundle.parse(ElSqlConfig.DEFAULT, (Resource[]) null));
  }

  @Test
  public void test_parse_noExistingResource() {
    Resource resource = new ClassPathResource("NAME_OF_NON_EXISTING_RESOURCE.elsql");
    assertThrows(IllegalArgumentException.class, () -> ElSqlBundle.parse(ElSqlConfig.DEFAULT, resource));
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_getSql() {
    ElSqlBundle test = ElSqlBundle.of(ElSqlConfig.DEFAULT, ElSql.class);
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo"));
    assertEquals("SELECT * FROM foo ", test.getSql("TestFoo", new MapSqlParameterSource()));
  }

}
