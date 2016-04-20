/**
 * User: Administrator
 */
package com.jenkov.db.itf.mapping;

import com.jenkov.db.itf.PersistenceException;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.sql.ResultSet;

/**
 * Represents one method from an object to the database, and/or from the database to the object. Both way mappings
 * possible in the same object method.
 *
 * <br/><br/>
 * There can only be one method method containing a given
 * database column name, and only one method method containing any given object method. While method "overloading"
 * would seem nice, meaning that you can have many mappings from different query/database fields to the same
 * object methods all gathered in one object method, this cannot be done for the following reasons:
 *
 * <br/><br/>
 * <ol>
 * <li>With many different database column names in the object method it can be hard to know which method mappings
 * are used when.</li>
 * <li>Also, you risk getting into situations where the query result actually contains a column name that one of you
 * method mappings are pointing too, in a situation where you don't want that column mapped to that method in the
 * object.</li>
 *
 * <br/><br/>
 * While the above problems could have been solved using some sort of method method "shadowing" or aliasing
 * ignoring certain fields or having them mapped to other names, this has not (yet) been implemented. Also, using
 * this kind of shadowing and aliasing could cause trouble all over the place if the original object method that
 * these shadowings and aliases was based on, is changed. Instead of shadowing and aliasing the focus has been
 * to make it easy to create new object mappings from scratch.
 *
 * @author Jakob Jenkov, Jenkov Development
 */
public interface IObjectMapping {

    /**
     * Returns the name of the database table that this object method maps to.
     * @return The database table name mapped to.
     */
    public String       getTableName();


    /**
     * Sets the name of the database table that this object method maps to.
     * @param tableName The database table name to map to.
     */
    public void         setTableName(String tableName);


    /**
     * Returns the class that this object method maps.
     * @return The class mapped by this object method.
     */
    public Class        getObjectClass();

    /**
     * Sets the class that this object method maps.
     * @param type The class mapped by this object method.
     */
    public void         setObjectClass(Class type);

    /**
     * Returns true if this object mapping maps to auto generated keys.
     * Note: It only returns true if a column of the primary key is
     * auto generated. Non-key columns are not taken into account.
     *
     * @return True if any column of the primary key is auto generated. False if not.
     */
    public boolean hasAutoGeneratedKeys();

    /**
     * Returns an IKeyValue instance matching the primary key of
     * this object mapping, and with the key values extracted
     * from the given object. The object must be of the same class
     * as the class this object mapping maps to (the class
     * returned from getObjectClass()).
     *
     * @param object The object containing the primary key values to extract into the
     *        IKeyValue instance.
     * @return An IKeyValue matching the IKey primary key of this object mapping,
     *        with the values extracted from this object.
     */
    public IKeyValue    getPrimaryKeyValueForObject(Object object, IKeyValue keyValue) throws PersistenceException;


    /**
     * Returns the primary key value as an IKeyValue instance for the record the ResultSet is
     * pointing to.
     * @param result
     * @return
     * @throws PersistenceException
     */
    public IKeyValue    getPrimaryKeyValueForRecord(ResultSet result, IKeyValue keyValue) throws PersistenceException;

    /**
     * Returns the current primary key mapping.
     * This <code>IKey</code>
     * instance replaces the previous primary key methods <code>getPrimaryKeyColumn(),
     * setPrimaryKeyColumn(...), getPrimaryKeyGetterMapping() and getPrimaryKeySetterMapping()
     * </code>. This new key mapping object supports compound keys, which the previous methods
     * did not.
     * @return The current primary key mapping.
     */
    public IKey getPrimaryKey();

    /**
     * Sets the current primary key mapping.
     * This <code>IKey</code>
     * instance replaces the previous primary key methods <code>getPrimaryKeyColumn(),
     * setPrimaryKeyColumn(...), getPrimaryKeyGetterMapping() and getPrimaryKeySetterMapping()
     * </code>. This new key mapping object supports compound keys, which the previous methods
     * did not.
     * @param key The key mapping to use as the primary key mapping.
     */
    public void setPrimaryKey(IKey key);

 
    /**
     * Adds a getter mapping method to this object method.
     * @param mapping The method method to add.
     * @throws PersistenceException If the given method method is not valid
     *   (is not null, and does at least contain object method and column name);
     */
    public void         addGetterMapping(IGetterMapping mapping) throws PersistenceException;


    /**
     * Adds a getter mapping method to this object method.
     * @param mapping The method method to add.
     * @throws PersistenceException If the given method method is not valid
     *   (is not null, and does at least contain object method and column name);
     */
    public void         addSetterMapping(ISetterMapping mapping) throws PersistenceException;


    /**
     * Removes the getter mapping method that maps to the provided
     * database column name (column in table).
     * @param columnName The database column name of the method method to remove.
     */
    public void         removeGetterMapping(String columnName);

    /**
     * Removes all getter mappings mapped to the given column names.
     * @param columnNames The column names to remove the getter mappings for.
     */
    public void         removeGetterMappings(String ... columnNames);

    /**
     * Removes the getter mapping method that maps from the given <code>Method</code>
     * to some database column.
     * @param method The method mapped from of the method method to remove.
     */
    public void         removeGetterMapping(Method method);


    public void replaceGetterMapping(String currentColumnName, String newColumnName) throws PersistenceException;



    /**
     * Removes the setter mapping method that maps to the given database column name
     * (column in table).
     * @param columnName The database column name of the setter method method to remove.
     */
    public void         removeSetterMapping(String columnName);

    /**
     * Removes all setter mappings mapped to the given column names.
     * @param columnNames The column names to remove the setter mappings for.
     */
    public void         removeSetterMappings(String ... columnNames);

    /**
     * Removes the setter method method that maps from the given <code>Method</code>
     * to some database column.
     * @param method The method mapped from of the method method to remove.
     */
    public void         removeSetterMapping(Method method);

    public void replaceSetterMapping(String currentColumnName, String newColumnName) throws PersistenceException;

    /**
     * Returns the method mappings that are mapped to getter methods on the
     * mapped object class.
     * @return A <code>Collection</code> of getter method mappings.
     */
    public Collection   getGetterMappings();


    /**
     * Returns the method mappings that are mapped to setter methods on the
     * mapped object class.
     * @return A <code>Collection</code> of setter method mappings.
     */
    public Collection   getSetterMappings();



    /**
     * Returns the getter method method that is mapped to the database column name passed in
     * parameter <code>columnName</code>. If no getter method method is found with that
     * database column name, null is returned.
     * @param  columnName The database column name to get the getter method method for.
     * @return The getter method method matching the given database column name. Null if no
     * matching getter method method is found.
     */
    public IGetterMapping getGetterMapping(String columnName);


    /**
     * Returns the getter method method that is mapped to the object method passed as
     * parameter <code>objectMethod</code>. If no getter method method is found matching
     * that <code>Method</code>, null is returned.
     * @param objectMethod The method to get the getter method method for.
     * @return The getter method method matching the given method. Null if no matching
     * getter method method is found.
     */
    public IGetterMapping getGetterMapping(Method objectMethod);


    /**
     * Returns the setter method method that is mapped to the database column name passed in
     * parameter <code>columnName</code>. If no setter method method is found with that
     * database column name, null is returned.
     * @param  columnName The database column name to get the setter method method for.
     * @return The setter method method matching the given database column name. Null if no
     * matching setter method method is found.
     */
    public ISetterMapping getSetterMapping(String columnName);


    /**
     * Returns the setter method method that is mapped to the object method passed as
     * parameter <code>objectMethod</code>. If no setter method method is found matching
     * that <code>Method</code>, null is returned.
     * @param objectMethod The method to get the setter method method for.
     * @return The setter method method matching the given method. Null if no matching
     * setter method method is found.
     */
    public ISetterMapping getSetterMapping(Method objectMethod);

    /*
    public List<INestedObjectMapping>  getNestedObjectMappings();
    public List<IRelatedObjectMapping> getRelatedObjectMappings();
    */
}

