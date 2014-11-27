/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

/**
 * Provides access to an empty set of SQL parameters.
 */
final class EmptySqlParams implements SqlParams {

  /**
   * Constant instance.
   */
  static final SqlParams INSTANCE = new EmptySqlParams();

  /**
   * Creates an instance.
   */
  private EmptySqlParams() {
  }

  //-------------------------------------------------------------------------
  @Override
  public boolean contains(String variable) {
    return false;
  }

  @Override
  public Object get(String variable) {
    return null;
  }

}
