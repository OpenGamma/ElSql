/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representation of a list of child units.
 */
class ContainerSqlFragment extends SqlFragment {

  /**
   * The fragments.
   */
  private final List<SqlFragment> _fragments = new ArrayList<SqlFragment>();

  /**
   * Creates an empty container.
   */
  ContainerSqlFragment() {
  }

  //-------------------------------------------------------------------------
  /**
   * Adds a fragment to the list in the container.
   * 
   * @param childFragment  the child fragment, not null
   */
  void addFragment(SqlFragment childFragment) {
    _fragments.add(childFragment);
  }

  /**
   * Gets the list of fragments.
   * 
   * @return the unmodifiable list of fragments, not null
   */
  List<SqlFragment> getFragments() {
    return Collections.unmodifiableList(_fragments);
  }

  //-------------------------------------------------------------------------
  @Override
  void toSQL(StringBuilder buf, SqlFragments fragments, SqlParams params, int[] loopIndex) {
    for (SqlFragment fragment : _fragments) {
      fragment.toSQL(buf, fragments, params, loopIndex);
    }
  }

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return getFragments().toString();
  }

}
