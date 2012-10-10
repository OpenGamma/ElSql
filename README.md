ElSql
=====

Manage SQL external to a Java application.

There are a number of techniques for creating SQL within an application. The main ones are:
* an Object Relational Mapper framework, such as Hibernate
* appending Strings, such as sql = "SELECT foo " + "FROM bar " + "WHERE ..."
* using a fluent API library, with methods like select("foo").from("bar").where(..)
* reading in an external file, such as a properties file

This library focuses on the last of these, using an external file.
It is designed for use with Spring, and uses the Spring SqlParameterSource class.
<p>
The key benefit is a simple external file that a DBA can understand, something
which is invaluable for later maintenance and debugging.
<p>
The file format is a file which typically has the suffix ".elsql".
Here is an example highlighting the structure:

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
