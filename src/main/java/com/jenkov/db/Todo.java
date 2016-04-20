package com.jenkov.db;

/**
 * This class does nothing but contain a "to do" list of things to do in the Butterfly Persistence project.
 */

public class Todo {

    //FEATURES
    //todo check that AbstractDao calls the setPersistenceConfiguration ( or otherwise passed through synchronized code)
    //     upon auto-generation of object mappings, forcing the generating thread to store it's working memory in main memory.


    //todo implement a clearCaches() method on the IPersistenceConfiguration.
    //todo IDataSynchronizer
    //todo IDataSource       -- or ObjectInputStream?? -- or Iterator??
    //todo IDataDestination  -- or ObjectOutputStream??


    //UNIT TESTS
    //todo unit tests of all GetterMapping / SetterMapping subclasses
    //todo unit tests of correct connection handling - all components
    //todo unit tests of correct exception  handling - all components
    //todo unit tests of JdbcUtil.insertParameter()


    //PERFORMANCE TESTS
    //todo persistence overhead tests on various databases


    //PERFORMANCE TUNINGS
    //todo On large result sets, instead of iterating a lot of column names that the current object
    //     method doesn't have for each object read (list reads), find out first which
    //     columns are in fact in the object and the result set, and just iterate these for each
    //      successive object read from the result set.


    //todo cache the information about whether an object has a constructor taking a ResultSet as a parameter...
    //     to speed up object creation. Or perhaps use object mappings as object instance factory, thus
    //     encapsulating this information and code inside the object mappings.



}
