package com.jenkov.db.itf.mapping;

import com.jenkov.db.itf.PersistenceException;

import java.lang.reflect.Method;

/**
 * This interface represents the functions of the object mapping factorie(s) used by Butterfly Persistence.
 * The purpose of the object mapping factory is to make it easier for the users of the API to
 * create customized object mappings and method mappings.
 *
 * 
 * The object mapping factory is also used by the object mapper in Butterfly Persistence.
 *
 * @author Jakob Jenkov, Jenkov Development
 */
public interface IObjectMappingFactory {

    /**
     * Creates an empty object mapping instance.
     * @return An empty object mapping instance.
     */
    public IObjectMapping createObjectMapping();


    /**
     * Creates an object mapping with the object class and table.
     * @param objectClass       The object class to map
     * @param tableName         The database table to map to
     * @return An object mapping with the object class, table name, and primary key column preset.
     */
    public IObjectMapping createObjectMapping(Class objectClass, String tableName);

    /**
     * Creates an object mapping with the object class, table name, and primary key column preset.
     * @param objectClass       The object class to map
     * @param tableName         The database table to map to
     * @param primaryKeyColumn  The name of the primary key column in that table
     * @return An object mapping with the object class, table name, and primary key column preset.
     */
    public IObjectMapping createObjectMapping(Class objectClass, String tableName, String primaryKeyColumn);

    /**
     * Returns a new object mapping that is an exact copy of the original.
     * Changing this new object mapping will not affect the original object
     * method.
     * @param source The object mapping to copy.
     * @return The copy of the object mapping as an independent object mapping instance.
     */
    public IObjectMapping copyObjectMapping(IObjectMapping source);


    /**
     * Creates an empty getter method mapping of the type that matches the
     * objectType parameter. The objectType parameter is the class of the
     * values that this getter method mapping will be able to extract from
     * target objects and insert into <code>PreparedStatement</code> instances.
     * For instance, if this getter method mapping is supposed to extract
     * values from the getter method <code>String getName()</code> the class
     * passed in the objectType parameter should be String.class .
     * @param objectType The class of the object returned by the getter (it's return type) to be mapped by this
     *                   getter method mapping.
     * @return An empty <code>IGetterMapping</code> instance.
     */
    public IGetterMapping createGetterMapping(Class objectType);


    public IGetterMapping createGetterMapping(Class methodOwner, String methodName, String columnName) throws PersistenceException;

    public IGetterMapping createGetterMapping(Class methodOwner, String methodName, String columnName, boolean isTableMapped) throws PersistenceException;

    /**
     * Creates a getter method mapping with the given <code>Method</code>, database column name, and
     * isTableMapped values prefilled. The getter method mapping will be of a type matching the
     * return type of the getter method.
     * @param method         The getter <code>Method</code> to map from.
     * @param dbFieldName    The database column name to map to.
     * @param isTableMapped  Set to true if the database column exists in a table.
     *                       False if not (if it only exists in a SQL query).
     * @return               An new <code>IGetterMapping</code> with the given parameters prefilled.
     */
    public IGetterMapping createGetterMapping(Method method, String dbFieldName, boolean isTableMapped);


    /**
     * Copies a getter method mapping. The copy is an independent getter method mapping instance.
     * Changing it will not affect the original getter method mapping.
     * @param source The <code>IGetterMapping</code> instance to copy.
     * @return A <code>IGetterMapping</code> that is equal to the original.
     */
    public IGetterMapping copyGetterMapping(IGetterMapping source);


    /**
     * Creates an empty setter method mapping of a type matching the objectType class. The objectType is
     * the class of the values that this setter method mapping will be able to set on target objects.
     * For instance, if this setter method is to set values on target objects using the setter method
     * <code>setName(String name)</code> then the class passed in the objectType parameter should be
     * String.class .
     * @param objectType The type/class that this setter method mapping will be able to set on target objects.
     * @return An empty <code>ISetterMapping</code> instance matching the given objectType.
     */
    public ISetterMapping createSetterMapping(Class objectType);

    public ISetterMapping createSetterMapping(Class methodOwner, String methodName, String columnName) throws PersistenceException;
    public ISetterMapping createSetterMapping(Class methodOwner, String methodName, Class parameterType, String columnName) throws PersistenceException;

    public ISetterMapping createSetterMapping(Class methodOwner, String methodName, Class parameterType, String columnName, boolean isTableMapped) throws PersistenceException;
    public ISetterMapping createSetterMapping(Class methodOwner, String methodName, String columnName, boolean isTableMapped) throws PersistenceException;


    /**
     * Creates a setter method mapping prefilled with the <code>Method</code>, database column name, and
     * isTableMapped values prefilled. The setter method mapping will be of a type matching the class
     * of setter methods parameter.
     * @param method         The setter <code>method</code> to map from.
     * @param dbFieldName    The database column to map to.
     * @param isTableMapped  Set to true if the database column exists in a table.
     *                       False if not (if for instance the database column only exists in an SQL query).
     * @return               A <code>ISetterMapping</code> with the passed parameter values prefilled,
     *                       matching the parameter type of the setter method.
     *
     */
    public ISetterMapping createSetterMapping(Method method, String dbFieldName, boolean isTableMapped);


    /**
     * Copies the original setter method mapping. The copy is an independent <code>ISetterMapping</code>
     * instance. Changing the copy will not affect the original.
     * @param source The <code>ISetterMapping</code> instance to copy.
     * @return       A <code>ISetterMapping</code> that is equal to the original.
     */
    public ISetterMapping copySetterMapping(ISetterMapping source);


    /**
     * Adds a getter method to the given object mapping. The getter method will
     * map from the getter method with the given method name to the column with
     * the given column name.
     *
     * 
     * The parameter <code>isTableMapped</code> tells whether
     * the column exists in a table in the database, or only in an SQL query. This
     * information is used when generating SQL for reads and writes.
     *
     * @param mapping       The object mapping to add the getter method to.
     * @param methodName    The name of the method to map from.
     * @param columnName    The name of the column to map to.
     * @param isTableMapped Set to true if the column exists in a database table.
     *                      False if the column only exists in an SQL query.
     * @throws NoSuchMethodException If no method is found with the given method name.
     * @throws PersistenceException  If the object mapping does not contain an object class
     *                      (<code>getObjectClass()== null</code>).
     */
    public void addGetterMapping(IObjectMapping mapping, String methodName,
                                           String columnName, boolean isTableMapped)
    throws NoSuchMethodException, PersistenceException;

    /**
     * Adds a getter method to the given object mapping. The getter method will
     * map from the getter method with the given method name to the column with
     * the given column name.
     *
     * 
     * The parameter <code>isTableMapped</code> tells whether
     * the column exists in a table in the database, or only in an SQL query. This
     * information is used when generating SQL for reads and writes.
     *
     * @param mapping       The object mapping to add the getter method to.
     * @param methodName    The name of the method to map from.
     * @param columnName    The name of the column to map to.
     * @param isTableMapped Set to true if the column exists in a database table.
     *                      False if the column only exists in an SQL query.
     * @param isAutoGenerated Set to true if the column is auto generated by the database. False if not.
     * @throws NoSuchMethodException If no method is found with the given method name.
     * @throws PersistenceException  If the object mapping does not contain an object class
     *                      (<code>getObjectClass()== null</code>).
     */
    public void addGetterMapping(IObjectMapping mapping, String methodName, String columnName,
        boolean isTableMapped, boolean isAutoGenerated)
    throws NoSuchMethodException, PersistenceException;


    /**
     * Adds a setter method to the given object mapping. The setter method will map
     * from the setter method with the given name to the column with the supplied
     * column name.
     *
     * 
     * The parameter <code>isTableMapped</code> tells whether
     * the column exists in a table in the database, or only in an SQL query. This
     * information is used when generating SQL for reads and writes.
     *
     * @param mapping       The object mapping to add the setter method to.
     * @param methodName    The name of the method to map from.
     * @param columnName    The name of the column to map to.
     * @param isTableMapped Set to true if the column exists in a database table.
     *                      False if the column only exists in an SQL query.
     * @throws NoSuchMethodException If no method is found with the given method name.
     * @throws PersistenceException  If the object mapping does not contain an object class
     *                      (<code>getObjectClass()== null</code>).
     */
    public void addSetterMapping(IObjectMapping mapping, String methodName,
        String columnName, boolean isTableMapped)
    throws NoSuchMethodException, PersistenceException;


    /**
     * Adds a setter method to the given object mapping. The setter method will map
     * from the setter method with the given name to the column with the supplied
     * column name.
     *
     * The <code>parameterType</code> parameter tells which setter method to use,
     * if you have more than one setter method with the same name, but different
     * parameter types (overloaded setter methods).
     *
     * The parameter <code>isTableMapped</code> tells whether
     * the column exists in a table in the database, or only in an SQL query. This
     * information is used when generating SQL for reads and writes.
     *
     * @param mapping       The object mapping to add the setter method to.
     * @param methodName    The name of the method to map from.
     * @param parameterType The parameter type of the setter method to map,
     *                      in case of overloaded setter methods.
     * @param columnName    The name of the column to map to.
     * @param isTableMapped Set to true if the column exists in a database table.
     *                      False if the column only exists in an SQL query.
     * @throws NoSuchMethodException If no method is found with the given method name and parameter type.
     * @throws PersistenceException  If the object mapping does not contain an object class
     *                      (<code>getObjectClass()== null</code>).
     */
    public void addSetterMapping(IObjectMapping mapping, String methodName, Class parameterType,
        String columnName, boolean isTableMapped)
    throws NoSuchMethodException, PersistenceException;

    /**
     * Creates a new <code>IKey</code> instance.  Key instances are used
     * to represent database keys. For instance primary keys and foreign keys.
     *
     * @return The IKey instance just created.
     */
    public IKey createKey();
}
