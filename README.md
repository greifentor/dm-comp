# dm-comp
A comparator for data models from different source (e. g. liquibase xml files or a JDBC data source).

## Requirements
* Java: 11
* Maven: 3.5.x

## Build
Build with `mvn clean install`


## Modules

* *dm-comp-comparator* - contains all classes which are working on data model comparation.
* *dm-comp-liquibase* - a model reader implementation for liquibase XML files.
* *dm-comp-model* - contains all the model classes of the project.
