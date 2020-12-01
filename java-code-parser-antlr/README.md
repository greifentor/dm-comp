# Java Code Reader

A blue print for a Java code reader.

This version reads:

- Source code files with classes, imports and package name.
- Classes with its annotations, attributes and methods.
- Attributes with type name and annotations.
- Methods with name, annotations and formal parameters.
- Formal parameters with names, type names and annotations.

The read source code files will be converted to CompilationUnit objects.


## Requirements

* Maven 3.5.x
* Java 11


## Build

Just check out and build with `mvn clean install`


## Usage

Embbed the code in your project and use the class `de.ollie.blueprints.codereader.java.JavaCodeConverter` to convert source code file contents to a CompilationUnit object.