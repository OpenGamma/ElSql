/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * Test.
 */
public class SpringSqlParamsTest {

  @Test
  public void test_constructor_Map() {
    MapSqlParameterSource source = new MapSqlParameterSource();
    source.addValue("a", "b");
    SpringSqlParams test = new SpringSqlParams(source);
    assertEquals(true, test.contains("a"));
    assertEquals("b", test.get("a"));
    assertEquals(false, test.contains("x"));
    assertEquals(null, test.get("x"));
  }

  @Test
  public void test_constructor_Map_null() {
    assertThrows(IllegalArgumentException.class, () -> new SpringSqlParams(null));
  }

}
