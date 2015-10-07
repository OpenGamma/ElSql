/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.elsql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parse of elsql formatted SQL.
 * <p>
 * The parser reads the file line by line and creates the named fragments of SQL for later use.
 * The format is whitespace-aware, with indentation defining blocks (where curly braces would be used in Java).
 * <p>
 * This class is mutable and intended for use by a single thread.
 */
final class ElSqlParser {

  /**
   * An identifier.
   */
  private static final String IDENTIFIER = "[A-Za-z0-9_]+";
  /**
   * A variable.
   */
  private static final String VARIABLE = "[:](?:[A-Za-z0-9_]+|[$]?[{][A-Za-z0-9_.]+[}])";
  /**
   * A variable or literal number.
   */
  private static final String VARIABLE_OR_LITERAL = "[0-9]+|" + VARIABLE;
  /**
   * A variable with possible LOOPINDEX suffix.
   */
  private static final String VARIABLE_LOOPINDEX =
      "[:](?:[A-Za-z0-9_]+(?:@LOOPINDEX[123]?)?|[$]?[{][A-Za-z0-9_.]+(?:@LOOPINDEX[123]?)?[}])";
  /**
   * Text to match in AND/OR/IF.
   */
  private static final String QUERY_TEXT = "[A-Za-z0-9_]+";

  /**
   * The regex for @NAME(identifier).
   */
  private static final Pattern NAME_PATTERN = Pattern.compile(
      "[ ]*[@]NAME[(](" + IDENTIFIER + ")[)][ ]*");
  /**
   * The regex for @AND(variable = query).
   */
  private static final Pattern AND_PATTERN = Pattern.compile(
      "[ ]*[@]AND[(](" + VARIABLE_LOOPINDEX + ")" + "([ ]?[=][ ]?" + QUERY_TEXT + ")?" + "[)][ ]*");
  /**
   * The regex for @OR(variable = query).
   */
  private static final Pattern OR_PATTERN = Pattern.compile(
      "[ ]*[@]OR[(](" + VARIABLE_LOOPINDEX + ")" + "([ ]?[=][ ]?" + QUERY_TEXT + ")?" + "[)][ ]*");
  /**
   * The regex for @IF(variable = query).
   */
  private static final Pattern IF_PATTERN = Pattern.compile(
      "[ ]*[@]IF[(](" + VARIABLE_LOOPINDEX + ")" + "([ ]?[=][ ]?" + QUERY_TEXT + ")?" + "[)][ ]*");
  /**
   * The regex for @LOOP(variable)
   */
  private static final Pattern LOOP_PATTERN = Pattern.compile(
      "[ ]*[@]LOOP[(](" + VARIABLE_OR_LITERAL + ")[)][ ]*");
  /**
   * The regex for @INCLUDE(variable|identifier)
   */
  private static final Pattern INCLUDE_PATTERN = Pattern.compile(
      "[@]INCLUDE[(](" + VARIABLE + "|" + IDENTIFIER + ")[)](.*)");
  /**
   * The regex for @PAGING(offsetVariableOrLiteral,fetchVariableOrLiteral)
   */
  private static final Pattern PAGING_PATTERN = Pattern.compile(
      "[@]PAGING[(](" + VARIABLE_OR_LITERAL + ")[ ]?[,][ ]?(" + VARIABLE_OR_LITERAL + ")[)](.*)");
  /**
   * The regex for @OFFSETFETCH(offsetVariableOrLiteral,fetchVariableOrLiteral)
   */
  private static final Pattern OFFSET_FETCH_PATTERN = Pattern.compile(
      "[@]OFFSETFETCH[(](" + VARIABLE_OR_LITERAL + ")[ ]?[,][ ]?(" + VARIABLE_OR_LITERAL + ")[)](.*)");
  /**
   * The regex for @FETCH(fetchVariableOrLiteral)
   */
  private static final Pattern FETCH_PATTERN = Pattern.compile(
      "[@]FETCH[(](" + VARIABLE_OR_LITERAL + ")[)](.*)");
  /**
   * The regex for @VALUE(variable)
   */
  private static final Pattern VALUE_PATTERN = Pattern.compile(
      "[@]VALUE[(](" + VARIABLE_LOOPINDEX + ")[)]( )*(.*)");
  /**
   * The regex for @LIKE and @EQUALS :variable
   */
  private static final Pattern OPERATOR_VARIABLE_PATTERN = Pattern.compile(
      "([^:])*(" + VARIABLE_LOOPINDEX + ")(.*)");
  /**

  /**
   * The input.
   */
  private final List<Line> _lines = new ArrayList<Line>();
  /**
   * The parsed output.
   */
  private Map<String, NameSqlFragment> _namedFragments = new LinkedHashMap<String, NameSqlFragment>();

  /**
   * Creates the parser.
   * 
   * @param lines  the lines, not null
   */
  ElSqlParser(List<String> lines) {
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      _lines.add(new Line(line, i + 1));
    }
  }

  //-------------------------------------------------------------------------
  /**
   * Parse the input returning the named sections.
   * 
   * @return the map of named sections, keyed by name, not null
   */
  Map<String, NameSqlFragment> parse() {
    rejectTabs();
    parseNamedSections();
    return _namedFragments;
  }

  /**
   * Ensure that there are no tabs.
   */
  private void rejectTabs() {
    for (Line line : _lines) {
      if (line.containsTab()) {
        throw new IllegalArgumentException("Tab character not permitted: " + line);
      }
    }
  }

  /**
   * Parses the top-level named sections.
   */
  private void parseNamedSections() {
    ContainerSqlFragment containerFragment = new ContainerSqlFragment();
    parseContainerSection(containerFragment, _lines.listIterator(), -1);
  }

  /**
   * Parses a container section.
   * <p>
   * A container is any section indented to the same amount.
   * 
   * @param container  the container to add to, not null
   * @param lineIterator  the iterator, not null
   * @param indent  the current indent, negative if no indent
   */
  private void parseContainerSection(ContainerSqlFragment container, ListIterator<Line> lineIterator, int indent) {
    while (lineIterator.hasNext()) {
      Line line = lineIterator.next();
      if (line.isComment()) {
        lineIterator.remove();
        continue;
      }
      if (line.indent() <= indent) {
        lineIterator.previous();
        return;
      }
      String trimmed = line.lineTrimmed();
      if (trimmed.startsWith("@NAME")) {
        Matcher nameMatcher = NAME_PATTERN.matcher(trimmed);
        if (nameMatcher.matches() == false) {
          throw new IllegalArgumentException("@NAME found with invalid format: " + line);
        }
        NameSqlFragment nameFragment = new NameSqlFragment(nameMatcher.group(1));
        parseContainerSection(nameFragment, lineIterator, line.indent());
        if (nameFragment.getFragments().size() == 0) {
          throw new IllegalArgumentException("@NAME found with no subsequent indented lines: " + line);
        }
        container.addFragment(nameFragment);
        _namedFragments.put(nameFragment.getName(), nameFragment);
        
      } else if (indent < 0) {
        throw new IllegalArgumentException("Invalid fragment found at root level, only @NAME is permitted: " + line);
        
      } else if (trimmed.startsWith("@PAGING")) {
        Matcher pagingMatcher = PAGING_PATTERN.matcher(trimmed);
        if (pagingMatcher.matches() == false) {
          throw new IllegalArgumentException("@PAGING found with invalid format: " + line);
        }
        PagingSqlFragment whereFragment = new PagingSqlFragment(pagingMatcher.group(1), pagingMatcher.group(2));
        parseContainerSection(whereFragment, lineIterator, line.indent());
        if (whereFragment.getFragments().size() == 0) {
          throw new IllegalArgumentException("@PAGING found with no subsequent indented lines: " + line);
        }
        container.addFragment(whereFragment);
        
      } else if (trimmed.startsWith("@WHERE")) {
        if (trimmed.equals("@WHERE") == false) {
          throw new IllegalArgumentException("@WHERE found with invalid format: " + line);
        }
        WhereSqlFragment whereFragment = new WhereSqlFragment();
        parseContainerSection(whereFragment, lineIterator, line.indent());
        if (whereFragment.getFragments().size() == 0) {
          throw new IllegalArgumentException("@WHERE found with no subsequent indented lines: " + line);
        }
        container.addFragment(whereFragment);
        
      } else if (trimmed.startsWith("@AND")) {
        Matcher andMatcher = AND_PATTERN.matcher(trimmed);
        if (andMatcher.matches() == false) {
          throw new IllegalArgumentException("@AND found with invalid format: " + line);
        }
        AndSqlFragment andFragment = new AndSqlFragment(andMatcher.group(1), extractVariable(andMatcher.group(2)));
        parseContainerSection(andFragment, lineIterator, line.indent());
        if (andFragment.getFragments().size() == 0) {
          throw new IllegalArgumentException("@AND found with no subsequent indented lines: " + line);
        }
        container.addFragment(andFragment);
        
      } else if (trimmed.startsWith("@OR")) {
        Matcher orMatcher = OR_PATTERN.matcher(trimmed);
        if (orMatcher.matches() == false) {
          throw new IllegalArgumentException("@OR found with invalid format: " + line);
        }
        OrSqlFragment orFragment = new OrSqlFragment(orMatcher.group(1), extractVariable(orMatcher.group(2)));
        parseContainerSection(orFragment, lineIterator, line.indent());
        if (orFragment.getFragments().size() == 0) {
          throw new IllegalArgumentException("@OR found with no subsequent indented lines: " + line);
        }
        container.addFragment(orFragment);
        
      } else if (trimmed.startsWith("@IF")) {
        Matcher ifMatcher = IF_PATTERN.matcher(trimmed);
        if (ifMatcher.matches() == false) {
          throw new IllegalArgumentException("@IF found with invalid format: " + line);
        }
        IfSqlFragment ifFragment = new IfSqlFragment(ifMatcher.group(1), extractVariable(ifMatcher.group(2)));
        parseContainerSection(ifFragment, lineIterator, line.indent());
        if (ifFragment.getFragments().size() == 0) {
          throw new IllegalArgumentException("@IF found with no subsequent indented lines: " + line);
        }
        container.addFragment(ifFragment);
        
      } else if (trimmed.startsWith("@LOOP")) {
        if (trimmed.startsWith("@LOOPINDEX") || trimmed.startsWith("@LOOPJOIN")) {
          parseLine(container, line);
        } else {
          Matcher loopMatcher = LOOP_PATTERN.matcher(trimmed);
          if (loopMatcher.matches() == false) {
            throw new IllegalArgumentException("@LOOP found with invalid format: " + line);
          }
          LoopSqlFragment loopFragment = new LoopSqlFragment(loopMatcher.group(1));
          parseContainerSection(loopFragment, lineIterator, line.indent());
          if (loopFragment.getFragments().size() == 0) {
            throw new IllegalArgumentException("@LOOP found with no subsequent indented lines: " + line);
          }
          container.addFragment(loopFragment);
        }
        
      } else {
        parseLine(container, line);
      }
    }
  }

  /**
   * Extracts a variable from the input.
   * 
   * @param text  the text to parse, may be null
   * @return the variable, null if none
   */
  private String extractVariable(String text) {
    if (text == null) {
      return null;
    }
    text = text.trim();
    if (text.startsWith("=")) {
      return extractVariable(text.substring(1));
    }
    return text;
  }

  /**
   * Parses a single line, or remainder of a line.
   * 
   * @param container  the container to add to, not null
   * @param line  the line to parse, not null
   */
  private void parseLine(ContainerSqlFragment container, Line line) {
    String trimmed = line.lineTrimmed();
    if (trimmed.length() == 0) {
      return;
    }
    if (trimmed.contains("@INCLUDE")) {
      parseIncludeTag(container, line);
      
    } else  if (trimmed.contains("@LIKE")) {
      parseOperatorTag(container, line, "@LIKE");
      
    } else  if (trimmed.contains("@EQUALS")) {
      parseOperatorTag(container, line, "@EQUALS");
        
    } else  if (trimmed.contains("@OFFSETFETCH")) {
      parseOffsetFetchTag(container, line);
      
    } else  if (trimmed.contains("@FETCH")) {
      parseFetchTag(container, line);
      
    } else  if (trimmed.contains("@VALUE")) {
      parseValueTag(container, line);
      
    } else  if (trimmed.contains("@LOOPJOIN")) {
      TextSqlFragment textFragment = new TextSqlFragment(trimmed, line.endOfLine());
      container.addFragment(textFragment);
      
    } else if (trimmed.startsWith("@")) {
      throw new IllegalArgumentException("Unknown tag at start of line: " + line);
      
    } else {
      TextSqlFragment textFragment = new TextSqlFragment(trimmed, line.endOfLine());
      container.addFragment(textFragment);
    }
  }

  /**
   * Parse INCLUDE tag.
   * <p>
   * This tag can appear anywhere in a line.
   * It substitutes the entire content of the named section in at this point.
   * The text before is treated as simple text.
   * The text after is parsed.
   * 
   * @param container  the container to add to, not null
   * @param line  the line to parse, not null
   */
  private void parseIncludeTag(ContainerSqlFragment container, Line line) {
    Line[] split = line.split(line.lineTrimmed().indexOf("@INCLUDE"));
    parseLine(container, split[0]);
    String trimmed = split[1].lineTrimmed();
    
    Matcher matcher = INCLUDE_PATTERN.matcher(trimmed);
    if (matcher.matches() == false) {
      throw new IllegalArgumentException("@INCLUDE found with invalid format: " + line);
    }
    IncludeSqlFragment includeFragment = new IncludeSqlFragment(matcher.group(1));
    container.addFragment(includeFragment);
    
    Line subLine = split[1].splitRemainder(matcher.start(2));
    parseLine(container, subLine);
  }

  /**
   * Parse Operator (LIKE or EQUALS) tag and optional END tag (ENDLIKE or ENDEQUALS)
   * <p>
   * This tag can appear anywhere in a line.
   * The text before is treated as simple text.
   * The text after is parsed.
   * 
   * @param container  the container to add to, not null
   * @param line  the line to parse, not null
   */
  private void parseOperatorTag(ContainerSqlFragment container, Line line, String tagName) {
    Line[] split = line.split(line.lineTrimmed().indexOf(tagName));
    parseLine(container, split[0]);
    String trimmed = split[1].lineTrimmed();
    
    String content = trimmed.substring(tagName.length());
    String endTag = "@END" + tagName.substring(1);
    int end = trimmed.indexOf(endTag);
    int remainderIndex = trimmed.length();
    if (end >= 0) {
      content = trimmed.substring(tagName.length(), end);
      remainderIndex = end + endTag.length();
    }
    TextSqlFragment contentTextFragment = new TextSqlFragment(content, line.endOfLine());
    Matcher matcher = OPERATOR_VARIABLE_PATTERN.matcher(content);
    if (matcher.matches() == false) {
      throw new IllegalArgumentException(tagName + " found with invalid format: " + line);
    }
    String variable = matcher.group(2);
    OperatorSqlFragment operatorFragment = tagName.equals("@LIKE")
        ? new LikeSqlFragment(variable)
        : new EqualsSqlFragment(variable);
    
    container.addFragment(operatorFragment);
    operatorFragment.addFragment(contentTextFragment);
    
    Line subLine = split[1].splitRemainder(remainderIndex);
    parseLine(container, subLine);
  }

  /**
   * Parse OFFSET/FETCH tag.
   * <p>
   * This tag can appear anywhere in a line.
   * The text before is treated as simple text.
   * The text after is parsed.
   * 
   * @param container  the container to add to, not null
   * @param line  the line to parse, not null
   */
  private void parseOffsetFetchTag(ContainerSqlFragment container, Line line) {
    Line[] split = line.split(line.lineTrimmed().indexOf("@OFFSETFETCH"));
    parseLine(container, split[0]);
    String trimmed = split[1].lineTrimmed();
    
    String offsetVariable = ":paging_offset";
    String fetchVariable = ":paging_fetch";
    int remainderIndex = 12;
    if (trimmed.startsWith("@OFFSETFETCH(")) {
      Matcher matcher = OFFSET_FETCH_PATTERN.matcher(trimmed);
      if (matcher.matches() == false) {
        throw new IllegalArgumentException("@OFFSETFETCH found with invalid format: " + line);
      }
      offsetVariable = matcher.group(1);
      fetchVariable = matcher.group(2);
      remainderIndex = matcher.start(3);
    }
    OffsetFetchSqlFragment pagingFragment = new OffsetFetchSqlFragment(offsetVariable, fetchVariable);
    container.addFragment(pagingFragment);
    
    Line subLine = split[1].splitRemainder(remainderIndex);
    parseLine(container, subLine);
  }

  /**
   * Parse FETCH tag.
   * <p>
   * This tag can appear anywhere in a line.
   * The text before is treated as simple text.
   * The text after is parsed.
   * 
   * @param container  the container to add to, not null
   * @param line  the line to parse, not null
   */
  private void parseFetchTag(ContainerSqlFragment container, Line line) {
    Line[] split = line.split(line.lineTrimmed().indexOf("@FETCH"));
    parseLine(container, split[0]);
    String trimmed = split[1].lineTrimmed();
    
    String fetchVariable = ":paging_fetch";
    int remainderIndex = 6;
    if (trimmed.startsWith("@FETCH(")) {
      Matcher matcherVariable = FETCH_PATTERN.matcher(trimmed);
      if (matcherVariable.matches()) {
        fetchVariable = matcherVariable.group(1);
        remainderIndex = matcherVariable.start(2);
      } else {
        throw new IllegalArgumentException("@FETCH found with invalid format: " + line);
      }
    }
    OffsetFetchSqlFragment pagingFragment = new OffsetFetchSqlFragment(fetchVariable);
    container.addFragment(pagingFragment);
    
    Line subLine = split[1].splitRemainder(remainderIndex);
    parseLine(container, subLine);
  }

  /**
   * Parse VALUE tag.
   * <p>
   * This tag can appear anywhere in a line.
   * The text before is treated as simple text.
   * The text after is parsed.
   * 
   * @param container  the container to add to, not null
   * @param line  the line to parse, not null
   */
  private void parseValueTag(ContainerSqlFragment container, Line line) {
    Line[] split = line.split(line.lineTrimmed().indexOf("@VALUE"));
    parseLine(container, split[0]);
    Line lineStartingAtTag = split[1];
    
    Matcher matcher = VALUE_PATTERN.matcher(lineStartingAtTag.lineTrimmed());
    if (matcher.matches() == false) {
      throw new IllegalArgumentException("@VALUE found with invalid format: " + line);
    }
    String followingSpaces = matcher.group(2);
    Line subLine = lineStartingAtTag.splitRemainder(matcher.start(3));
    boolean followWithSpace = (followingSpaces != null && followingSpaces.length() > 0) || subLine.lineTrimmed().isEmpty();
    
    ValueSqlFragment valueFragment = new ValueSqlFragment(matcher.group(1), followWithSpace);
    container.addFragment(valueFragment);
    
    parseLine(container, subLine);
  }

  //-------------------------------------------------------------------------
  /**
   * Representation of a single line in the input.
   */
  static final class Line {
    private final String _line;
    private final String _trimmed;
    private final int _lineNumber;
    private final boolean _endOfLine;

    Line(String line, int lineNumber) {
      _line = line;
      int commentPos = line.indexOf("--");
      if (commentPos >= 0) {
        _trimmed = line.substring(0, commentPos).trim();
      } else {
        _trimmed = line.trim();
      }
      _lineNumber = lineNumber;
      _endOfLine = true;
    }

    Line(String line, String trimmed, int lineNumber, boolean endOfLine) {
      _line = line;
      _trimmed = trimmed;
      _lineNumber = lineNumber;
      _endOfLine = endOfLine;
    }

    String line() {
      return _line;
    }

    String lineTrimmed() {
      return _trimmed;
    }

    int lineNumber() {
      return _lineNumber;
    }

    boolean endOfLine() {
      return _endOfLine;
    }

    boolean containsTab() {
      return _line.contains("\t");
    }

    boolean isComment() {
      return _trimmed.startsWith("--") || _trimmed.length() == 0;
    }

    int indent() {
      for (int i = 0; i < _line.length(); i++) {
        if (_line.charAt(i) != ' ') {
          return i;
        }
      }
      return _line.length();
    }

    Line[] split(int trimmedIndex) {
      String before = _trimmed.substring(0, trimmedIndex);
      String after = _trimmed.substring(trimmedIndex);
      return new Line[] {new Line(before, before, _lineNumber, false), new Line(after, after, _lineNumber, _endOfLine)};
    }

    Line splitRemainder(int trimmedIndex) {
      String after = _trimmed.substring(trimmedIndex);
      return new Line(after, after, _lineNumber, _endOfLine);
    }

    @Override
    public String toString() {
      return "Line " + lineNumber();
    }
  }

}
