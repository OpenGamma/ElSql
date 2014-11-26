/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

/**
 * Representation of a binary operator which mutates based upon the bound variable on the right hand side.
 */
abstract class OperatorSqlFragment extends ContainerSqlFragment {
  
  /**
   * The variable.
   */
  protected final String _variable;

  /**
   * Creates an instance.
   * 
   * @param variable  the variable to base the Operator on, not null
   */
  OperatorSqlFragment(String variable) {
    if (variable == null) {
      throw new IllegalArgumentException("Variable must be specified");
    }
    if (variable.startsWith(":") == false || variable.length() < 2) {
      throw new IllegalArgumentException("Argument is not a variable (starting with a colon)");
    }
    _variable = variable.substring(1);
  }
  
  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return getClass().getSimpleName() + ":" + _variable + " " + getFragments();
  }

}
