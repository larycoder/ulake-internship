# ULake Data Ingestion

This module is a part of ulake (datalake) and serve as a crawler.

# Develop Tools

1. java 11
2. gradle 7.6.1
3. quarkus 3.0.4.Final

# Development Guideline

The project structure comes up with 2 path: REST API and Crawler library.

Crawler lib is core of project, it provides some useful class to define and run a crawler process.
On the other hand, REST API services grant accessibility to user through HTTP.

The development architecture start with _models_ package for defining Entities (persistence data) and
Model (data object class). Then the stored data could be touchable through several data access class
defined in _persistence_ package. With these 2 package plus crawler lib in _crawler_ package, the services
are ready to develop inside package _services_ and ready to expose through endpoints in _resources_ package.

It is also importance to desire a good communication between each layer. For most case, it is recommended to
use the primitive type, however, in complicate situation, the custom data model class could be defined in _models_
package and will be preferred than HashMap, List.

The communication between user and runtime backend will be Json data. So the project need a proper lib to handle
Json format and _Jackson_ lib is the highest candidate. However, Json-like style is not recommend for internal
communication and exchange message.

Finally, the reusable code could be written in _utils_ package, but the project prefers sub-package for local problem
than an _utils_ class.
