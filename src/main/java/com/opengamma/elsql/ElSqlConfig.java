/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

/**
 * Configuration that provides support for differences between databases.
 * <p>
 * The class provides SQL fragments that the tags use to build the complete SQL.
 * Some standard implementations have been provided, but subclasses may be added.
 * <p>
 * Implementations must be thread-safe.
 */
public class ElSqlConfig {

  /**
   * A constant for the standard set of config, based on ANSI SQL.
   */
  public static final ElSqlConfig DEFAULT = new ElSqlConfig("Default");
  /**
   * A constant for the config needed for Postgres, the same as {@code DEFAULT}.
   */
  public static final ElSqlConfig POSTGRES = new PostgresElSqlConfig();
  /**
   * A constant for the config needed for HSQL, which escapes the LIKE clause.
   */
  public static final ElSqlConfig HSQL = new HsqlElSqlConfig();
  /**
   * A constant for the config needed for MySQL, which uses LIMIT-OFFSET instead
   * of FETCH-OFFSET.
   */
  public static final ElSqlConfig MYSQL = new MySqlElSqlConfig();
  /**
   * A constant for the config needed for Oracle RDBMS.
   */
  public static final ElSqlConfig ORACLE = new OracleElSqlConfig();
  /**
   * A constant for the config needed for SQL Server 2008, which pages in a different way.
   */
  public static final ElSqlConfig SQL_SERVER_2008 = new SqlServer2008ElSqlConfig();
  /**
   * A constant for the config needed for Vertica, the same as {@code DEFAULT}.
   */
  public static final ElSqlConfig VERTICA = new VerticaElSqlConfig();

  /**
   * The descriptive name.
   */
  private final String _name;

  /**
   * Creates an instance.
   * 
   * @param name  a descriptive name for the config, not null
   */
  public ElSqlConfig(String name) {
    _name = name;
  }

  /**
   * Gets the config name.
   * 
   * @return the config name, not null
   */
  public final String getName() {
    return _name;
  }

  //-------------------------------------------------------------------------
  /**
   * Checks if the value contains a wildcard.
   * <p>
   * The default implementation matches % and _, using backslash as an escape character.
   * This matches Postgres and other databases.
   * 
   * @param value  the value to check, not null
   * @return true if the value contains wildcards
   */
  public boolean isLikeWildcard(String value) {
    boolean escape = false;
    for (int i = 0; i < value.length(); i++) {
      char ch = value.charAt(i);
      if (escape) {
        escape = false;
      } else {
        if (ch == '\\') {
          escape = true;
        } else if (ch == '%' || ch == '_') {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Gets the suffix to add after LIKE, which would typically be an ESCAPE clause.
   * <p>
   * The default implementation returns an empty string.
   * This matches Postgres and other databases.
   * <p>
   * The returned SQL must be end in a space if non-empty.
   * 
   * @return the suffix to add after LIKE, not null
   */
  public String getLikeSuffix() {
    return "";
  }

  //-------------------------------------------------------------------------
  /**
   * Alters the supplied SQL to add paging, such as OFFSET-FETCH.
   * <p>
   * The default implementation calls {@link #getPaging(int, int)}.
   * <p>
   * The returned SQL must be end in a space if non-empty.
   * 
   * @param selectToPage  the SELECT statement to page, not null
   * @param offset  the OFFSET amount, zero to start from the beginning
   * @param fetchLimit  the FETCH/LIMIT amount, zero to fetch all
   * @return the updated SELECT, not null
   */
  public String addPaging(String selectToPage, int offset, int fetchLimit) {
    return selectToPage + (selectToPage.endsWith(" ") ? "" : " ") + getPaging(offset, fetchLimit);
  }

  /**
   * Gets the paging SQL, such as OFFSET-FETCH.
   * <p>
   * The default implementation uses 'FETCH FIRST n ROWS ONLY' or
   * 'OFFSET n ROWS FETCH NEXT n ROWS ONLY'.
   * This matches Postgres, HSQL and other databases.
   * <p>
   * The returned SQL must be end in a space if non-empty.
   * 
   * @param offset  the OFFSET amount, zero to start from the beginning
   * @param fetchLimit  the FETCH/LIMIT amount, zero to fetch all
   * @return the SQL to use, not null
   */
  public String getPaging(int offset, int fetchLimit) {
    if (fetchLimit == 0 && offset == 0) {
      return "";
    }
    if (fetchLimit == 0) {
      return "OFFSET " + offset + " ROWS ";
    }
    if (offset == 0) {
      return "FETCH FIRST " + fetchLimit + " ROWS ONLY ";
    }
    return "OFFSET " + offset + " ROWS FETCH NEXT " + fetchLimit + " ROWS ONLY ";
  }

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return "ElSqlConfig[" + _name + "]";
  }

  //-------------------------------------------------------------------------
  /**
   * Class for Postgres.
   */
  private static class PostgresElSqlConfig extends ElSqlConfig {
    public PostgresElSqlConfig() {
      super("Postgres");
    }
  }

  //-------------------------------------------------------------------------
  /**
   * Class for HSQL.
   */
  private static class HsqlElSqlConfig extends ElSqlConfig {
    public HsqlElSqlConfig() {
      super("HSQL");
    }
    @Override
    public String getLikeSuffix() {
      return "ESCAPE '\\' ";
    }
  }

  //-------------------------------------------------------------------------
  /**
   * Class for MySQL.
   */
  private static class MySqlElSqlConfig extends ElSqlConfig {
    public MySqlElSqlConfig() {
      super("MySql");
    }
    @Override
    public String getPaging(int offset, int fetchLimit) {
      if (fetchLimit == 0 && offset == 0) {
        return "";
      }
      if (fetchLimit == 0) {
        return "OFFSET " + offset + " ";
      }
      if (offset == 0) {
        return "LIMIT " + fetchLimit + " ";
      }
      return "LIMIT " + fetchLimit + " OFFSET " + offset + " ";
    }
  }

  //-------------------------------------------------------------------------
  /**
   * Class for Oracle RDBMS.
   * Oracle 12c can use the SQL standard verbose OFFSET/FETCH
   * http://www.oracle-base.com/articles/12c/row-limiting-clause-for-top-n-queries-12cr1.php
   */
  private static class OracleElSqlConfig extends ElSqlConfig {
    public OracleElSqlConfig() {
      super("Oracle");
    }
    @Override
		public String addPaging(String selectToPage, int offset, int fetchLimit) {
      if (fetchLimit == 0 && offset == 0) {
        return selectToPage;
      }
      if (offset == 0 && fetchLimit > 0) {
        return "SELECT * FROM ( " + selectToPage + " ) where rownum <= " + fetchLimit;
      }
      int start = offset;
      int end = offset + fetchLimit;
      return "SELECT * FROM (SELECT  row_.*,rownum rownum_ FROM ( " + selectToPage +
          " ) row_ where rownum <= "+ end +")  WHERE rownum_  > " + start;
    }
    @Override
    public String getPaging(int offset, int fetchLimit) {
      throw new UnsupportedOperationException();
    }
  }

  //-------------------------------------------------------------------------
  /**
   * Class for SQL server 2008.
   */
  private static class SqlServer2008ElSqlConfig extends ElSqlConfig {
    public SqlServer2008ElSqlConfig() {
      super("SqlServer2008");
    }
    @Override
    public String addPaging(String selectToPage, int offset, int fetchLimit) {
      if (fetchLimit == 0 && offset == 0) {
        // SQL Server needs a SELECT TOP with ORDER BY in an inner query, otherwise it complains
        return selectToPage.replaceFirst("SELECT ", "SELECT TOP " + Integer.MAX_VALUE + " ");
      }
      int start = offset + 1;
      int end = offset + fetchLimit;
      String columns = selectToPage.substring(selectToPage.indexOf("SELECT ") + 7, selectToPage.indexOf(" FROM "));
      String from = selectToPage.substring(selectToPage.indexOf(" FROM ") + 6, selectToPage.indexOf(" ORDER BY "));
      String order = selectToPage.substring(selectToPage.indexOf(" ORDER BY ") + 10);
      String inner = "SELECT " + columns + ", ROW_NUMBER() OVER (ORDER BY " + order.trim() + ") AS ROW_NUM FROM " + from;
      return "SELECT * FROM (" + inner + ") AS ROW_TABLE WHERE ROW_NUM >= " + start + " AND ROW_NUM <= " + end;
    }
    @Override
    public String getLikeSuffix() {
      return "ESCAPE '\\' ";
    }
    @Override
    public String getPaging(int offset, int fetchLimit) {
      throw new UnsupportedOperationException();
    }
    @Override
    public boolean isLikeWildcard(String value) {
      boolean escape = false;
      for (int i = 0; i < value.length(); i++) {
        char ch = value.charAt(i);
        if (escape) {
          escape = false;
        } else {
          if (ch == '\\') {
            escape = true;
          } else if (ch == '%' || ch == '_' || ch == '[') {
            return true;
          }
        }
      }
      return false;
    }
  }

  //-------------------------------------------------------------------------
  /**
   * Class for Vertica.
   */
  private static class VerticaElSqlConfig extends ElSqlConfig {
    public VerticaElSqlConfig() {
      super("Vertica");
    }
  }

}
