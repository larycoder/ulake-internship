ULake Core
==================

This is the low-level data lake core, responsible for communicating with different backends for different purposes.

ULake Core supports three types of data:

* *Unstructured data*, or raw data, is stored in a low-level object storage. We provide an abstraction interface to support different object storage backends. An [OpenIO](https://openio.io) implementation is provided.
* *Semi-structured data* is stored as-is as raw data into the core. Schema of the semi-structured data is then extracted after ingestion for later query. The core is not responsible for generation of these schema. Another module should provide this functionality.
* *Relational data* is stored in a relational database management system. We also provide a general purpose interface to support different relational database management systems.

ULake requires [MariaDB](https://mariadb.org) as the persistence store for its metadata. Configuration to this database is specified in the [Application Configuration](./src/main/resources/application.properties) file.




