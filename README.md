# OgmTest

* Neo4j 2.2.5 Community Server
* Spring Data Neo4j 4.0.0.RELEASE
* neo4j-ogm 1.1.4-SNAPSHOT

Failing test where two entity types have multiple relationships between them.

The example in this case is Ownership and Licensee relationships between a User entity and another OwnedLicensedEntity. 

* The OwnedLicensedEntity has one ower and can have 0...n licensees. 
* The User entity can own 0...n OwnedLicensedEntities and can have licences for 0...n OwnedLicensedEntities.

See the wiki for a visual representation of the graph:
https://github.com/charliebonza/OgmTest/wiki
