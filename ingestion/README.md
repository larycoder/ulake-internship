# ULake Data Ingestion
This module is a part of ulake (datalake) and serve as a crawler.

# Develop Tools
1. java 11
2. gradle 7.1.1
3. quarkus 2.8.1.Final

# Development Guideline
The project structure comes up with 2 path: REST API and Crawler library.

Crawler lib is core of project, it provides some useful class to define and run a crawler process.
 On the other hand, REST API services grant accessibility to user through HTTP.

The development architecture start with *models* package for defining Entities (persistence data) and
Model (data object class). Then the stored data could be touchable through several data access class
defined in *persistence* package. With these 2 package plus crawler lib in *crawler* package, the services
are ready to develop inside package *services* and ready to expose through endpoints in *resources* package.

It is also importance to desire a good communication between each layer. For most case, it is recommended to
use the primitive type, however, in complicate situation, the custom data model class could be defined in *models*
package and will be preferred than HashMap, List.

The communication between user and runtime backend will be Json data. So the project need a proper lib to handle
Json format and *Jackson* lib is the highest candidate. However, Json-like style is not recommend for internal
communication and exchange message.

Finally, the reusable code could be written in *utils* package, but the project prefers sub-package for local problem
than an *utils* class.
