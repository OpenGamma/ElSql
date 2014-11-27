/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

/**
 * Provides access to SQL parameters.
 * <p>
 * This interface abstracts the mechanism of obtaining the SQL parameters.
 */
public interface SqlParams {

  /**
   * Checks whether the variable exists.
   * 
   * @param variable  the variable
   * @return true if it exists
   */
  public boolean contains(String variable);

  /**
   * Gets the variable, returning null if not found.
   * 
   * @param variable  the variable
   * @return the value associated with the variable
   */
  public Object get(String variable);

}
