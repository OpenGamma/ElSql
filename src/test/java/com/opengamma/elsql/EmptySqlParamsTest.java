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
public class EmptySqlParamsTest {

  public void test_constructor_Map() {
    SqlParams test = EmptySqlParams.INSTANCE;
    assertEquals(false, test.contains("x"));
    assertEquals(null, test.get("x"));
  }

}
