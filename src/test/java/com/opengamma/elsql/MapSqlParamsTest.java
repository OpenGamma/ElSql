/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Test.
 */
public class MapSqlParamsTest {

  @Test
  public void test_constructor_Map() {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("a", "b");
    MapSqlParams test = new MapSqlParams(map);
    assertEquals(true, test.contains("a"));
    assertEquals("b", test.get("a"));
  }

  @Test
  public void test_constructor_Map_null() {
    assertThrows(IllegalArgumentException.class, () -> new MapSqlParams(null));
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_constructor_KeyValue() {
    MapSqlParams test = new MapSqlParams("a", "b");
    assertEquals(true, test.contains("a"));
    assertEquals("b", test.get("a"));
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_with() {
    MapSqlParams test = new MapSqlParams(new HashMap<String, Object>());
    assertEquals(false, test.contains("a"));
    assertEquals(null, test.get("a"));
    test = test.with("a", "b");
    assertEquals(true, test.contains("a"));
    assertEquals("b", test.get("a"));
  }

}
