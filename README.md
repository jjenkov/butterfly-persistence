# Butterfly Persistence
Butterfly Persistence is a simple, no nonsense Java JDBC based persistence API. It aims to provide a simple relational persistence API. 
Its features include automatic/manual connection management, easier JDBC operations via JDBC templates, 
simple object relational mapping, and map reading for dynamic queries. 
It provides a simple and pragmatic approach to persistence and will either help you, or get out of the way and let 
you do the job manually.

Butterfly Persistence can auto-generate most SQL for simple CRUD operations.
For advanced queries you simply write the SQL yourself.  

Butterfly Persistence can auto-map database tables to your POJOs at runtime, so you don't have to write all
that boring boilerplate code yourself. Butterfly Persistence uses Java Reflection and JDBC DatabaseMetadata 
to do that. You can, of course, modify the auto-mapping, or write your own, custom mapping.

Read more about Butterfly Persistence here:

http://tutorials.jenkov.com/butterfly-persistence/index.html


## Butterfly Persistence History
The first version of Butterfly Persistence was released in 2005 - before JPA was a standard, and before
Hibernate had reached its dominant position it has today. Back then it made a lot of sense with a
lightweight persistence API like Butterfly Persistence. The latest version of Butterfly Persistence
was released in 2009.

Today you are probably better off going with something more "modern", more widely used and documented
such as Hibernate / JPA.

I am mostly keeping the code online here, because some users have told me they still use
Butterfly Persistence, and prefer it to other, more heavy weight alternatives.

### Java 11 Support + Maven Builds
I am currently working on getting Butterfly Persistence to build using Java 11 and Maven. 
This also requires upgrading JUnit to 5.4+ and finding my old test mock toolkit Butterfly Testing Tools
too ! 

This should make it easier to use Butterfly Persistence with newer Java versions. 


## Why SQL?
For some types of projects it can be easier to just write the SQL to get the data you want from the database,
rather than having to write some Object Query Language syntax, and hope it translates into the SQL you need.

SQL can give you access to database specific features.

Most developers already know SQL - so they don't have to learn yet another query language.



