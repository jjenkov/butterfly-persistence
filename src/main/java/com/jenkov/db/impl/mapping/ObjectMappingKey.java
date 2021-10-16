package com.jenkov.db.impl.mapping;

import com.jenkov.db.itf.mapping.ICustomObjectMapper;
import com.jenkov.db.itf.mapping.IObjectMapping;
import com.jenkov.db.itf.mapping.IKey;
import com.jenkov.db.itf.mapping.IGetterMapping;
import com.jenkov.db.itf.PersistenceException;

import java.util.*;

/**
 * This class is a fast implementation of an object mapping key. The reason it is fast is,
 * that the id returned by <code>getId()</code> (an int) is used both in the <code>hashCode()</code> call and the
 * <code>equals(Object o)</code> call. These two methods are used when the object mapping key is used
 * in hash maps, as is the case with the default implementation of the object mapping cache.
 *
 * Using a string or some other class as the key may be slightly slower, since the <code>hashCode()</code>
 * and the <code>equals()</code> calls may be slower than this implementation.
 *
 * <br/><br/>
 * <code>ObjectMappingKey</code> instances can also contain an <code>ICustomObjectMapper</code> instance which will
 * create, or assist in creating, the object mapping this <code>ObjectMappingKey</code> instance represents.
 *
 * <br/><br/>
 * When creating object mapping keys,
 * assign them to a constant in some class of yours, like the example below (reading user
 * instances from the database):
 *
 * <br/><br/>
 *
 * <code>
 * public class ObjectMappingKeys{  <br/>
 *                                  <br/>
 * &nbsp;&nbsp;&nbsp;public USER_READ   = ObjectMappingKey.createInstance(User.class, "User Read");  <br/>
 * &nbsp;&nbsp;&nbsp;public USER_INSERT = ObjectMappingKey.createInstance(User.class, "User Insert");  <br/>
 * &nbsp;&nbsp;&nbsp;public USER_UPDATE = ObjectMappingKey.createInstance(User.class, "User Update");  <br/>
 * &nbsp;&nbsp;&nbsp;public USER_UPDATE_LAST_LOGIN = ObjectMappingKey.createInstance(User.class, "User Update Last Login");  <br/>
 * &nbsp;&nbsp;&nbsp;public USER_UPDATE_PASSWORD = ObjectMappingKey.createInstance(User.class, "User Update Password");  <br/>
 * &nbsp;&nbsp;&nbsp;public USER_DEACTIVATE = ObjectMappingKey.createInstance(User.class, "User Deactivate");  <br/>
 * &nbsp;&nbsp;&nbsp;public USER_DELETE = ObjectMappingKey.createInstance(User.class, "User Delete");  <br/>
 *                                  <br/>
 * }                                <br/>

 *
 */

public class ObjectMappingKey {

    private static int nextId = 0;

    private int                 id          = 0;
    private Class               objectClass = null;
    private ICustomObjectMapper objectMapper= null;


    private ObjectMappingKey(int id, Class objectClass, ICustomObjectMapper mapper){
        this.id           = id;
        this.objectClass  = objectClass;
        this.objectMapper = mapper;
    }

    /**
     * Returns the id of this object mapping key. This id is provided by the user at creation time
     * and must be unique across all instances of ObjectMappingKey.
     * @return The id of this object mapping key.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the class mapped by the object mapping this <code>ObjectMappingKey</code>
     * instance is key for.
     * Setting the class for the object mapping key is optional. It is just a help
     * for you to identify object mapping keys at runtime.
     * Setting the class also enables the AbstractDao subclasses to automatically generate
     * an object mapping for this object mapping key, if none is cached already.
     *
     * @return The class mapped by the object mapping stored in the object mapping cache
     * under this object mapping key.
     */
    public Class getObjectClass(){
        return this.objectClass;
    }


    public ICustomObjectMapper getCustomObjectMapper(){
        return this.objectMapper;
    }


    public String toString() {
        return "id = " + this.id + ", " + getObjectClass();
    }

    /**
     * Returns the hash code of this object mapping key. Since the id of the object
     * method key is unique, the id is also used as the hash code of the
     * object mapping key instance.
     * @return The hash code for this ObjectMappingKey instance.
     */
    public int hashCode() {
        return this.id;
    }

    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(! (obj instanceof ObjectMappingKey)) return false;
        return this.id == ((ObjectMappingKey) obj).getId();
    }



    /**
     * Creates an instance of <code>ObjectMappingKey</code> with the object class set only.
     * A unique id will be assigned to the <code>ObjectMappingKey</code> before it is returned.
     *
     * <br/><br/>
     * When creating object mapping keys, assign them to a constant in some class of yours.
     *
     * @param objectClass The class mapped by the object mapping that this ObjectMappingKey instance
     *                    is key for.
     * @return An <code>ObjectMappingKey</code> instance with the given name and object class set.
     */
    public static synchronized ObjectMappingKey createInstance(Class objectClass){
        return createInstance(objectClass, null);
    }


    /**
     * Creates an instance of <code>ObjectMappingKey</code> with both object class, name, and
     * a custom object mapper set.
     * A unique id will be assigned to the <code>ObjectMappingKey</code> before it is returned.
     * The <code>ICustomObjectMapper</code> will be used when creating an object mapping for
     * this object mapping key.
     *
     * <br/><br/>
     * When creating object mapping keys, assign them to a constant in some class of yours.
     *
     * @param objectClass The class mapped by the object mapping that this ObjectMappingKey instance
     *                    is key for.
     * @param mapper      The custom object mapper to attach to the created object mapping key
     * @return An <code>ObjectMappingKey</code> instance with the given name and object class set.
     */
    public static synchronized ObjectMappingKey createInstance(Class objectClass, ICustomObjectMapper mapper){
        ObjectMappingKey newKey = new ObjectMappingKey(nextId++, objectClass, mapper);
        return newKey;
    }

    /**
     * Creates an instance of <code>ObjectMappingKey</code> with both object class, and
     * a custom object mapper set. The custom object mapper will be set internally by this factory method.
     * The custom object mapper will mark all the columns in the columns array as auto generated.
     * A unique id will be assigned to the <code>ObjectMappingKey</code> before it is returned.
     * The <code>ICustomObjectMapper</code> will be used when creating an object mapping for
     * this object mapping key.
     *
     * <br/><br/>
     * When creating object mapping keys, assign them to a constant in some class of yours.
     *
     * @param objectClass The class mapped by the object mapping that this ObjectMappingKey instance
     *                    is key for.
     * @param columns     The columns to mark as auto-generated in the given ObjectMappingKey
     * @return An <code>ObjectMappingKey</code> instance with the given name and object class set.
     */
    public static synchronized ObjectMappingKey createInstanceForAutoGeneratedColumns(
            Class objectClass, final String[] columns){

        return createInstance(objectClass, createAutoGeneratedColumnsCustomMapper(columns));
        /*
        ObjectMappingKey newKey = new ObjectMappingKey(nextId++, objectClass, null, createAutoGeneratedColumnsCustomMapper(columns));
        objectMappingKeys.add(newKey);
        return newKey;
        */
    }




    /**
     * Creates an instance of <code>ObjectMappingKey</code> with both object class, table name, and
     * a custom object mapper set. The custom object mapper will be set internally by this factory method.
     * The custom object mapper will mark all the columns in the columns array as auto generated,
     * and return the given table name as the table to map the class to.
     * A unique id will be assigned to the <code>ObjectMappingKey</code> before it is returned.
     * The <code>ICustomObjectMapper</code> will be used when creating an object mapping for
     * this object mapping key.
     *
     * <br/><br/>
     * When creating object mapping keys, assign them to a constant in some class of yours.
     *
     * @param objectClass The class mapped by the object mapping that this ObjectMappingKey instance
     *                    is key for.
     * @param tableName   The name of the table to map this class to.
     * @return An <code>ObjectMappingKey</code> instance with the given name and object class set.
     */
    public static synchronized ObjectMappingKey createInstanceForCustomTableAutoGeneratedColumns(
            Class objectClass, final String[] columns, String tableName){

        return createInstance(objectClass, createCustomTableAutoGeneratedColumnsCustomMapper(columns, tableName));
        /*
        ObjectMappingKey newKey = new ObjectMappingKey(nextId++, objectClass, null,
                createCustomTableAutoGeneratedColumnsCustomMapper(columns, tableName));
        objectMappingKeys.add(newKey);
        return newKey;
        */
    }



    /**
     * Creates an instance of <code>ObjectMappingKey</code> with both object class and
     * a custom object mapper set. The custom object mapper will be set internally by this factory method.
     * The custom object mapper will mark all the columns in the primary key as auto generated.
     * A unique id will be assigned to the <code>ObjectMappingKey</code> before it is returned.
     * The <code>ICustomObjectMapper</code> will be used when creating an object mapping for
     * this object mapping key.
     *
     * <br/><br/>
     * When creating object mapping keys, assign them to a constant in some class of yours.
     *
     * @param objectClass The class mapped by the object mapping that this ObjectMappingKey instance
     *                    is key for.
     * @return An <code>ObjectMappingKey</code> instance with the given name and object class set.
     */
    public static synchronized ObjectMappingKey createInstanceForAutoGeneratedPrimaryKey(Class objectClass){
        return createInstance(objectClass, createAutoGeneratedPrimaryKeyCustomMapper());
        /*
        ObjectMappingKey newKey = new ObjectMappingKey(nextId++, objectClass, null, createAutoGeneratedPrimaryKeyCustomMapper());
        objectMappingKeys.add(newKey);
        return newKey;
        */
    }



    /**
     * Creates an instance of <code>ObjectMappingKey</code> with both object class, table name, and
     * a custom object mapper set. The custom object mapper will be set internally by this factory method.
     * The custom object mapper will mark all the columns in the primary key as auto generated,
     * and map the class to the given table name.
     * A unique id will be assigned to the <code>ObjectMappingKey</code> before it is returned.
     * The <code>ICustomObjectMapper</code> will be used when creating an object mapping for
     * this object mapping key.
     *
     * <br/><br/>
     * When creating object mapping keys, assign them to a constant in some class of yours.
     *
     * @param objectClass The class mapped by the object mapping that this ObjectMappingKey instance
     *                    is key for.
     * @param tableName   The name of the table to map that class to.
     * @return An <code>ObjectMappingKey</code> instance with the given name and object class set.
     */
    public static synchronized ObjectMappingKey createInstanceForCustomTableAutoGeneratedPrimaryKey(Class objectClass, String tableName){
        return createInstance(objectClass, createCustomTableAutoGeneratedPrimaryKeyCustomMapper(tableName));
        /*
        ObjectMappingKey newKey = new ObjectMappingKey(nextId++, objectClass, null,
                createCustomTableAutoGeneratedPrimaryKeyCustomMapper(tableName));
        objectMappingKeys.add(newKey);
        return newKey;
        */
    }


    /* Not necessary. Annotation Based Mapping is enabled in the ObjectReader.
    public static synchronized ObjectMappingKey createInstanceAnnotationBasedMapping(Class objectClass){
        return new ObjectMappingKey(nextId++, objectClass, null, new CustomObjectMapperAnnotationBased());
    }
    */

    private static CustomObjectMapperBase createAutoGeneratedColumnsCustomMapper(final String[] columns) {
        return new CustomObjectMapperBase(){
            public void modify(Object objectMappingKey, IObjectMapping mapping) throws PersistenceException {
                for(int i=0; i<columns.length; i++){
                    mapping.getGetterMapping(columns[i]).setAutoGenerated(true);
                }
            }
        };
    }

    private static CustomObjectMapperBase createCustomTableAutoGeneratedColumnsCustomMapper(final String[] columns,
                                                                                            final String tableName) {
        return new CustomObjectMapperBase(){
            public String getTableName(Object objectMappingKey) throws PersistenceException {
                return tableName;
            }

            public void modify(Object objectMappingKey, IObjectMapping mapping) throws PersistenceException {
                for(int i=0; i<columns.length; i++){
                    IGetterMapping getterMapping = mapping.getGetterMapping(columns[i]);
                    if(getterMapping == null){
                        throw new NullPointerException("No getter mapping found for column name " + columns[i] +
                                ". Perhaps the columns name is misspelled, or have the wrong case " +
                                "(some databases convert all column names to UPPERCASE internally).");
                    }
                    mapping.getGetterMapping(columns[i]).setAutoGenerated(true);
                }
            }
        };
    }


    private static CustomObjectMapperBase createAutoGeneratedPrimaryKeyCustomMapper() {
        return new CustomObjectMapperBase(){
            public void modify(Object objectMappingKey, IObjectMapping mapping) throws PersistenceException {
                IKey key = mapping.getPrimaryKey();
                Iterator iterator = key.getColumns().iterator();
                while(iterator.hasNext()){
                    String columnName = (String) iterator.next();
                    mapping.getGetterMapping(columnName).setAutoGenerated(true);
                }
            }
        };
    }

    private static CustomObjectMapperBase createCustomTableAutoGeneratedPrimaryKeyCustomMapper(final String tableName) {
        return new CustomObjectMapperBase(){
            public String getTableName(Object objectMappingKey) throws PersistenceException {
                return tableName;
            }

            public void modify(Object objectMappingKey, IObjectMapping mapping) throws PersistenceException {
                IKey key = mapping.getPrimaryKey();
                Iterator iterator = key.getColumns().iterator();
                while(iterator.hasNext()){
                    String columnName = (String) iterator.next();
                    mapping.getGetterMapping(columnName).setAutoGenerated(true);
                }
            }
        };
    }

}
