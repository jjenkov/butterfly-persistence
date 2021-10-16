package com.jenkov.db.itf.mapping;

import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.IPersistenceConfiguration;

import java.sql.Connection;

/**
 * This interface represents the functions made available by the object mapper in Butterfly Persistence.
 * The object mapper is responsible for mapping a class to a database table automatically. The
 * result of such a method is an object mapping instance. See IObjectMapping for more info on
 * these.
 *
 * The object mapper uses a database name guesser, a database name determiner and a
 * database primary key determiner internally to do it's job.
 *
 *
 * @author Jakob Jenkov,  Jenkov Development
 */
public interface IObjectMapper {

    /**
     * Returns the database name guesser used by this object mapper.
     * @return The database name guesser used by this object mapper.
     */
    public IDbNameGuesser getDbNameGuesser();

    /**
     * Sets the database name determiner used by this object mapper.
     * @param guesser The databa name guesser to be used by this object mapper.
     */
    public void setDbNameGuesser(IDbNameGuesser guesser);


    /**
     * Returns the database name determiner used by this object mapper.
     * @return The database name determiner used by this object mapper.
     */
    public IDbNameDeterminer getDbNameDeterminer();

    /**
     * Sets the database name determiner to be used by this object mapper.
     * @param nameDeterminer The database name determiner to be used by this object mapper.
     */
    public void setDbNameDeterminer(IDbNameDeterminer nameDeterminer);


    /**
     * Returns the database primary key determiner used by this object mapper.
     * @return The database primary key determiner used by this object mapper.
     */
    public IDbPrimaryKeyDeterminer getDbPrimaryKeyDeterminer();

    /**
     * Sets the database primary key determiner to be used by this object mapper.
     * @param primaryKeyDeterminer The database primary key determiner to be used by this object mapper.
     */
    public void setDbPrimaryKeyDeterminer(IDbPrimaryKeyDeterminer primaryKeyDeterminer);


    /**
     * Returns an object mapping from the cache in the given configuration, if one already exists. If not
     * a new object mapping is tried generated, cached for later use, and returned.
     *
     * @param objectMappingKey The key under which to retrieve and/or store the object mapping.
     * @param configuration    The configuration containing the object mapping cache.
     * @param connection       A database connection used in case the mapper
     * @return                 An object mapping for the given object mapping key, persistence configuration and JDBC Connection
     * @throws PersistenceException If anything fails during generation of the object mapping key.
     */
    public IObjectMapping getObjectMapping(Object objectMappingKey, IPersistenceConfiguration configuration, Connection connection)
            throws PersistenceException;



    /**
     * Maps an object to the table provided as parameter, using method name guessing for the members/fields.
     * @param persistentObjectClass The class of the object to map.
     * @param  objectMapping An optional object mapping to add the method mappings to. If null is provided
     *          the object mapper will create an empty object mapping, add the method mappings to it and
     *          return it.
     * @param connection A connection to the database containing the table the objects should be mapped to.
     * @param databaseName The name of the database the table is residing in. In most cases this parameter
     *          can be null, since the connection is normally pointing to a certain database.
     * @param table The name of the database table to map the objects to. Optional. If null is given the
     *          object mapper will try to guess the table name from the objects class name.
     * @return An object mapping with guessed method names from the given table. If a method name for an
     *          object mapping (getter/setter) could not be guessed there will be no method method for
     *          that method.
     * @throws PersistenceException If anything goes wrong during the method.
     */
    public IObjectMapping mapToTable(Class persistentObjectClass, IObjectMapping objectMapping,
                                     Connection connection, String databaseName, String table) throws PersistenceException;


    /**
     * Maps an object to the table provided as parameter, using method name guessing for the members/fields.
     * @param persistentObjectClass The class of the object to map.
     * @param  objectMapping An optional object mapping to add the method mappings to. If null is provided
     *          the object mapper will create an empty object mapping, add the method mappings to it and
     *          return it.
     * @param connection A connection to the database containing the table the objects should be mapped to.
     * @param databaseName The name of the database the table is residing in. In most cases this parameter
     *          can be null, since the connection is normally pointing to a certain database.
     * @param table The name of the database table to map the objects to. Optional. If null is given the
     *          object mapper will try to guess the table name from the objects class name.
     * @return An object mapping with guessed method names from the given table. If a method name for an
     *          object mapping (getter/setter) could not be guessed there will be no method method for
     *          that method.
     * @throws PersistenceException If anything goes wrong during the method.
     */
    public IObjectMapping mapGettersToTable(Class persistentObjectClass, IObjectMapping objectMapping,
                                            Connection connection, String databaseName, String table) throws PersistenceException;


    /**
     * Maps an object to the table provided as parameter, using method name guessing for the members/fields.
     * @param persistentObjectClass The class of the object to map.
     * @param  objectMapping An optional object mapping to add the method mappings to. If null is provided
     *          the object mapper will create an empty object mapping, add the method mappings to it and
     *          return it.
     * @param connection A connection to the database containing the table the objects should be mapped to.
     * @param databaseName The name of the database the table is residing in. In most cases this parameter
     *          can be null, since the connection is normally pointing to a certain database.
     * @param table The name of the database table to map the objects to. Optional. If null is given the
     *          object mapper will try to guess the table name from the objects class name.
     * @return An object mapping with guessed method names from the given table. If a method name for an
     *          object mapping (getter/setter) could not be guessed there will be no method method for
     *          that method.
     * @throws PersistenceException If anything goes wrong during the method.
     */
    public IObjectMapping mapSettersToTable(Class persistentObjectClass, IObjectMapping objectMapping,
                                            Connection connection, String databaseName, String table) throws PersistenceException;


    /**
     * Maps an object to fields named the same as it's setters and adds the method mappings
     * to the provided object mapping. If null is provided as object mapping an empty object
     * method instance is created and used. An object containing a
     * setHomeAddress will have a method method with the method name "setHomeAddress". This method is very
     * useful when method objects to SQL queries instead of tables. With a self-mapped object you can read
     * values into it using an sql like: "select homeAddr as setHomeAddress from users". The method in the
     * result set will be called setHomeAddres. The object mapping will then translate that name to the
     * setHomeAddress(...) method (because that method is mapped to a method called "setHomeAddress" in the
     * object mapping).
     *
     * @param persistentObjectClass The class to map to itself.
     * @param objectMapping The object mapping to store the field mappings in.
     * @return An object mapping with all setters mapped to fields with the same name as the
     *          coresponding setter method.
     * @throws PersistenceException If anything fails during mapping of the class to columns with the same names as its fields.
     */
    public IObjectMapping mapSettersToSelf (Class persistentObjectClass, IObjectMapping objectMapping) throws PersistenceException;


}
