package com.jenkov.db.itf;

import com.jenkov.db.itf.mapping.ICustomObjectMapper;
import com.jenkov.db.itf.mapping.IObjectMapper;
import com.jenkov.db.itf.mapping.IObjectMappingCache;
import com.jenkov.db.impl.init.DatabaseInitializer;
import com.jenkov.db.scope.IScopeFactory;

import javax.sql.DataSource;

/**
 * This interface represents a persistence configuration which is a collection of Butterfly Persistence compononents
 * used to achieve persistence in a certain way. You can have different persistence configurations suiting
 * different databases or situations etc if helps you. The persistence configuration also makes it easier
 * to pass around all the different components to be used in a specific situation, for instance read-by-primary-key,
 * or inser / updateBatch / delete etc.
 *
 * @author Jakob Jenkov,  Jenkov Development
 */
public interface IPersistenceConfiguration {

    /**
     * Returns the <code>Database</code> instance representing the database this instance
     * is specialized for.
     * @return The <code>Database</code> instance represeting the database this instance
     * is specialized for.
     */
    public Database getDatabase();


    /**
     * Sets the database this configuration is specialized for. Note that simply setting the
     * database instance isn't enough to target the entire configuration to another database.
     * If you change the database instance you will have to change the other components manually,
     * or you will have a semantically incoherent persistence configuration. In other words: Do
     * not change the database configuration unless you know what you are doing.
     */
    public void setDatabase(Database database);

    /**
     * Gets the data source associated with this persistence configuration.
     * @return The data source associated with this persistence configuration.
     */
    public DataSource getDataSource();

    /**
     * Sets the data source associated with this persistence configuration.
     */
    public void setDataSource(DataSource dataSource);

    /**
     * Returns the scope factory matching the data source set on this persistence configuration.
     * A scope factory is automatically created when a DataSource is set on an IPersistenceConfiguration. 
     * @return The scope factory matching the data source set on this persistence configuration.
     */
    public IScopeFactory getScopeFactory();

    /**
     * Returns the key by which this persistence configuration is stored internally
     * in the MrPersister class.
     * @return The key by which this persistence configuration is stored internally
     * in the MrPersister class.
     */
    public Object getConfigurationKey();

    /**
     * Sets the key by which this persistence configuration is stored internally
     * in the MrPersister class. Note: Changing the key in the
     * <code>IPersistenceConfiguration</code> instance will not
     * remap the instance stored in the MrPersister class. You will have to
     * remove the previously stored <code>IPersistenceConfiguration</code>
     * yourself.
     *
     * <br/><br/>
     * Calling the updateBatch method of a <code>IPersistenceConfiguration</code>
     * instance will however store that instance by the new key. But the instance will
     * remain mapped to the old key as well in the MrPersister class.
     * @param key key by which this persistence configuration is stored internally
     * in the MrPersister class, until you specifically remove that instance
     * from the MrPersister class.
     */
    public void   setConfigurationKey(Object key);


    /**
     * Returns the object mapper used in this persistence configuration.
     * @return The <code>IObjectMapper</code> instance set in this persistence configuration.
     */
    public IObjectMapper getObjectMapper();

    /**
     * Sets the object mapper to be used with this persistence configuration.
     * @param mapper The <code>IObjectMapper</code> instance to use with this persistence configuration.
     */
    public void          setObjectMapper(IObjectMapper mapper);


    /**
     * Returns the object mapping cache used in this persistence configuration.
     * @return The <code>IObjectMappingCache</code> instance set in this persistence configuration.
     */
    public IObjectMappingCache getObjectMappingCache();

    /**
     * Sets the object mapping cache to be used in this persistence configuration.
     * @param cache The <code>IObjectMappingCache</code> instance to use in this persistence configuration.
     */
    public void                setObjectMappingCache(IObjectMappingCache cache);


    /**
     * Returns the object cache used in this persistence configuration. Note: No object caches have
     * been implemented yet. This method is here for future implementation.
     * @return
     */
    /*
    public IObjectCache getObjectCache();
    public void         setObjectCache(IObjectCache cache);
    */


    /**
     * Returns the object reader used in this persistence configuration.
     * @return The <code>IObjectReader</code> instance set in this persistence configuration.
     */
    public IObjectReader getObjectReader();

    /**
     * Sets the object reader to be used with this persistence configuration.
     * @param reader The <code>IObjectReader</code> instance to use with this persistence configuration
     */
    public void          setObjectReader(IObjectReader reader);


    /**
     * Returns the object writer used in this persistence configuration.
     * @return The <code>IObjectWriter</code> instance set in this persistence configuration.
     */
    public IObjectWriter getObjectWriter();

    /**
     * Sets the object writer to use with this persistence configuration.
     * @param writer The <code>IObjectWriter</code> instance to use with this persistence configuration.
     */
    public void          setObjectWriter(IObjectWriter writer);


    /**
     * Returns the SQL generator used with this persistence configuration.
     * @return The <code>ISqlGenerator</code> instance set in this persistence configuration.
     */
    public ISqlGenerator getSqlGenerator();

    /**
     * Sets the SQL generator to be used with this persistence configuration.
     * @param generator The <code>ISqlGenerator</code> instance to be used with this persistence configuration.
     */
    public void          setSqlGenerator(ISqlGenerator generator);


    /**
     * Returns the SQL cache used to store read-by-primary-key SQL statements in this persistence configuration.
     * @return The <code>ISqlCache</code> instance used to store read-by-primary-key SQL statements in
     *         this persistence configuration.
     */
    public ISqlCache     getReadByPrimaryKeySqlCache();

    /**
     * Sets the SQL cache to be used to store read-by-primary-key SQL statements in this persistence configuration.
     * @param cache The <code>ISqlCache</code> instance to be used to store read-by-primary-key SQL statements
     *              in this persistence configuration.
     */
    public void          setReadByPrimaryKeySqlCache(ISqlCache cache);


    /**
     * Returns the SQL cache used to store insert SQL statements in this persistence configuration.
     * @return The <code>ISqlCache</code> instance used to store insert SQL statements in
     *         this persistence configuration.
     */
    public ISqlCache     getInsertSqlCache();

    /**
     * Sets the SQL cache to be used to store insert SQL statements in this persistence configuration.
     * @param cache The <code>ISqlCache</code> instance to be used to store insert SQL statements
     *              in this persistence configuration.
     */
    public void          setInsertSqlCache(ISqlCache cache);


    /**
     * Returns the SQL cache used to store updateBatch SQL statements in this persistence configuration.
     * @return The <code>ISqlCache</code> instance used to store updateBatch SQL statements in
     *         this persistence configuration.
     */
    public ISqlCache     getUpdateSqlCache();

    /**
     * Sets the SQL cache to be used to store updateBatch SQL statements in this persistence configuration.
     * @param cache The <code>ISqlCache</code> instance to be used to store updateBatch SQL statements
     *              in this persistence configuration.
     */
    public void          setUpdateSqlCache(ISqlCache cache);


    /**
     * Returns the SQL cache used to store delete statements in this persistence configuration.
     * @return The <code>ISqlCache</code> instance used to store delete SQL statements in
     *         this persistence configuration.
     */
    public ISqlCache     getDeleteSqlCache();

    /**
     * Sets the SQL cache to be used to store delete SQL statements in this persistence configuration.
     * @param cache The <code>ISqlCache</code> instance to be used to store delete SQL statements
     *              in this persistence configuration.
     */
    public void          setDeleteSqlCache(ISqlCache cache);


    /**
     * Returns the custom object mapper of this persistence configuration.
     * Currently unused. May be deprecated in a future release. Set
     * the custom mappers directly on the ObjectMappingKey instances instead.
     *
     * @return the custom object mapper of this persistence configuration.
     */
    public ICustomObjectMapper getCustomObjectMapper();

    /**
     * Sets the custom object mapper of this persistence configuration.
     * Currently unused. May be deprecated in a future release. Set
     * the custom mappers directly on the ObjectMappingKey instances instead.
     * @param customMapper The custom object mapper to set on this persistence configuration.
     */
    public void                setCustomObjectMapper(ICustomObjectMapper customMapper);


    /**
     * Returns the DatabaseInitializer used in this configuration.
     * @return The DatabaseInitializer used in this configuration.
     */
    public DatabaseInitializer getDatabaseInitializer();


    /**
     * Sets the DatabaseInitializer to use in this configuration.
     * @param initializer The DatabaseInitializer to use in this configuration.
     */
    public void setDatabaseInitializer(DatabaseInitializer initializer);


}
