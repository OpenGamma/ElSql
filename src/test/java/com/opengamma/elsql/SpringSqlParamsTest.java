/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import static org.testng.AssertJUnit.assertEquals;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class SpringSqlParamsTest {

  public void test_constructor_Map() {
    MapSqlParameterSource source = new MapSqlParameterSource();
    source.addValue("a", "b");
    SpringSqlParams test = new SpringSqlParams(source);
    assertEquals(true, test.contains("a"));
    assertEquals("b", test.get("a"));
    assertEquals(false, test.contains("x"));
    assertEquals(null, test.get("x"));
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_constructor_Map_null() {
    new SpringSqlParams(null);
  }

}
