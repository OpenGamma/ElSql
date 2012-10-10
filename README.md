ElSql
=====
Manage SQL external to a Java application.

ElSql. Short for "External SQL".
Pronounced "else-Q-L" where the letters are pronounced quicker than the "else".


Overview
--------
There are a number of techniques for creating SQL within a Java application.
Choosing one as opposed to another can make a significant difference to the feel of coding the application.

The main techniques are:
* an Object Relational Mapper framework, such as JPA or Hibernate
* appending Strings, such as sql = `"SELECT foo " + "FROM bar " + "WHERE ..."`
* using a fluent API library, with methods like `select("foo").from("bar").where(..)`
* reading in an external file, such as a properties file

This library focuses on the last of these - using an external file.
It is designed for use with Spring, and uses the Spring SqlParameterSource class.

The key benefit is a simple external file that a DBA can understand, something
which is invaluable for later maintenance and debugging.

The file format is essentially a DSL with a very small number of tags that handle the
majority of common difficult cases. It has the suffix ".elsql":

     -- an example comment
     @NAME(SelectBlogs)
       SELECT @INCLUDE(CommonFields)
       FROM blogs
       WHERE id = :id
         @AND(:date)
           date > :date
     @NAME(CommonFields)
       title, author, content

* two dashes are used for comments
* tags start with the @ symbol
* the primary blocks are @NAME(name) - the name refers to the block
* indentation is used to create blocks - indented lines "belong" to the parent less-indented line
* variables start with a colon, as this is integrated with Spring
* the various tags aim to handle over 80% of your needs

It is not intended that the DSL format should handle all cases, as that would be too complex.
The most complex cases should probably be dealt with in normal Java code.
The file can also be overridden in parts, which allows weird database SQL syntaxes to be handled.


Motivation
----------
While many find JPA and Hibernate type solutions to be suitable for them, they are not ones that
your author has ever been overly enthused about. Adding a new abstraction between two views of the
world - object and relational - often leads to tricky corner cases and complications.
For the simple cases straight SQL is quick and simple.
For the hardest cases you generally need to write the SQL anyway.
It is fair to ask if there enough middle ground to justify learning and using the tool.

Appending strings to build SQL is something best avoided if possible. It makes it very hard to
extract the actual SQL by the DBA for later change. A fluent library neatens up the string generation,
but still deeply encodes the SQL within the Java application.

Instead of these approaches, the author wanted the simplest possible library to store and manage a
file full of SQL (or near SQL). By defining a very simple DSL that integrates naturally into the SQL,
both DBAs and developers can understand the same file and work with it.
Since no such small and isolated library could be found, one was written.
