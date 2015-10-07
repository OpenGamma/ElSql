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
  final String _variable;

  /**
   * Creates an instance.
   * 
   * @param variable  the variable to base the Operator on, not null
   */
  OperatorSqlFragment(String variable) {
    _variable = extractVariableName(variable);
  }
  
  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return getClass().getSimpleName() + ":" + _variable + " " + getFragments();
  }

}
