package com.jenkov.db.impl;

import com.jenkov.db.itf.*;
import com.jenkov.db.itf.mapping.IObjectMapper;
import com.jenkov.db.itf.mapping.IObjectMapping;
import com.jenkov.db.itf.mapping.IObjectMappingCache;
import com.jenkov.db.util.ClassUtil;
import com.jenkov.db.util.JdbcUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jakob Jenkov,
 *         Copyright 2004 Jenkov Development
 */
public class ObjectDao implements IObjectDao {

    protected IPersistenceConfiguration configuration = null;

    protected Connection connection    = null;
    protected List       updateResults = new ArrayList();

    public ObjectDao(Connection connection, IPersistenceConfiguration configuration) {
        validate(connection);
        validate(configuration);
        this.configuration = configuration;
        this.connection    = connection;
    }

    public List getUpdateResults() {
        return updateResults;
    }

    public UpdateResult getUpdateResult(int index) {
        return (UpdateResult) getUpdateResults().get(index);
    }

    public UpdateResult getLastUpdateResult() {
        return getUpdateResult(getUpdateResults().size() -1 );
    }

    public long getLastGeneratedKeyAsLong() {
        return getLastUpdateResult().getLastGeneratedKeyAsLong();
    }

    public long getLastGeneratedKeyAsBigDecimal() {
        return getLastUpdateResult().getLastGeneratedKeyAsLong();
    }

    private void validate(IPersistenceConfiguration configuration) {
        if(configuration == null){
            throw new IllegalArgumentException("You must provide a non-null configuration");
        }
    }

    private void validate(Connection connection) {
        if(connection == null){
            throw new IllegalArgumentException("You must provide a non-null database connection.");
        }
    }

    public IPersistenceConfiguration getConfiguration() {
        return this.configuration;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void closeConnection() throws PersistenceException{
        JdbcUtil.close(connection);
    }

    public void setAutoCommit(boolean autoCommit) throws PersistenceException {
        try {
            this.connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            throw new PersistenceException("Error setting auto commit to " + autoCommit, e);
        }
    }

    public void commit() throws PersistenceException{
        try {
            this.connection.commit();
        } catch (SQLException e) {
            throw new PersistenceException("Error committing transaction", e);
        }
    }

    public void rollback() throws PersistenceException{
        try {
            this.connection.rollback();
        } catch (SQLException e) {
            throw new PersistenceException("Error rolling back transaction", e);
        }
    }

    /**
     * Returns the persistence configuration used by this DAO class.
     * @return The <code>IPersistenceConfiguration</code> used by this DAO class.
     * @throws PersistenceException If no persistence configuration is set.
     */
    protected IPersistenceConfiguration getConfigurationOrFail() throws PersistenceException{
//        if(this.configuration == null){
//            throw new PersistenceException("No persistence configuration set in DAO (is null)");
//        }
        return this.configuration;
    }

    /**
     * Returns the object mapping cache from the persistence configuration used by this DAO class.
     * If no object mapping cache is set null is returned.
     * @return The object mapping cache from the persistence configuration used by this DAO class. Null
     *         if no object mapping cache is set in the persistence configuration.
     * @throws PersistenceException If no persistence configuration is set for this DAO class.
     */
    protected IObjectMappingCache getObjectMappingCache() throws PersistenceException {
        return getConfigurationOrFail().getObjectMappingCache();
    }

    /**
     * Returns the object mapper set in the persistence configuration used by this DAO class.
     * @return The object mapper set in the persistence configuration used by this DAO class.
     * @throws PersistenceException If no object mapper is set in the used persistence configuration.
     */
    protected IObjectMapper getObjectMapper() throws PersistenceException {
        if(getConfigurationOrFail().getObjectMapper() == null){
            throw new PersistenceException("No object mapper set in persistence configuration in DAO (is null)");
        }

        return getConfigurationOrFail().getObjectMapper();
    }

    /**
     * Returns the object reader used in the persistence configuration used by this DAO class.
     * @return The <code>IObjectReader</code> instance set in the persistence configuration used
     *         by this DAO class.
     * @throws PersistenceException If no <code>IObjectReader</code> instance is set in the
     *         used persistence configuration.
     */
    protected IObjectReader getObjectReader() throws PersistenceException{
        if(getConfigurationOrFail().getObjectReader() == null){
            throw new PersistenceException("No object reader set in persistence configuration in DAO (is null)");
        }
        return getConfigurationOrFail().getObjectReader();
    }

    /**
     * Returns the object writer used in the persistence configuration used by this DAO class.
     * @return The <code>IObjectWriter</code> instance set in the persistence configuration used
     *         by this DAO class.
     * @throws PersistenceException If no <code>IObjectWriter</code> instance is set in the
     *         used persistence configuration.
     */
    protected IObjectWriter getObjectWriter() throws PersistenceException{
        if(getConfigurationOrFail().getObjectWriter() == null){
            throw new PersistenceException("No object writer set in persistence configuration in DAO (is null)");
        }
        return getConfigurationOrFail().getObjectWriter();
    }


    /**
     * Returns the SQL string stored in the given cache by the given object mapping key. If the
     * cache parameter is null, null is returned from this method.
     *
     * @param objectMappingKey The object mapping key by which the desired SQL string is stored.
     * @param cache      The <code>ISqlCache</code> instance in which the desired SQL string is stored.
     * @return           The SQL string if found. Null if no SQL string is stored in the cache by
     *                   this object mapping key. Null if the cache parameter is null.
     */
    protected String getSqlFromCache(Object objectMappingKey, ISqlCache cache) {
        if(cache == null) return null;
        return cache.getStatement(objectMappingKey);
    }

    /**
     * Stores the given SQL string in the given cache. If the cache parameter is null
     * nothing happens.
     *
     * @param objectMappingKey The object mapping key under which to store the SQL string.
     * @param cache      The <code>ISqlCache</code> to store the SQL string in.
     * @param sql        The SQL string to store.
     */
    protected void storeSqlInCache(Object objectMappingKey, ISqlCache cache, String sql){
        if(cache == null) return;
        cache.storeStatement(objectMappingKey, sql);
    }

    /**
     * Returns the SQL generator used in the persistence configuration used by this DAO class.
     * @return The <code>ISqlGenerator</code> instance set in the persistence configuration used by this DAO class.
     * @throws PersistenceException If no <code>ISqlGenerator</code> instance is set in the
     *         used persistence configuration.
     */
    protected ISqlGenerator getSqlGenerator() throws PersistenceException{
        if(getConfigurationOrFail().getSqlGenerator() == null) {
            throw new PersistenceException("No SQL generator set in persistence configuration in DAO (is null)");
        }
        return getConfigurationOrFail().getSqlGenerator();
    }



    /**
       * Returns the object mapping stored in the object mapping cache by the given method key.
       * If no object mapping is stored in the object mapping cache by that key, first the method
       * <code>createManualObjectMapping(objectMappingKey)</code> will be called to see if the
       * concrete AbstractDao subclass
       * has a manual object mapping it wants to use for the given object mapping key.
       *
       * 
       * If <code>createManualObjectMapping()</code> returns null, then this method will call
       * the <code>getTableName(objectMappingKey)</code> to see if the concrete AbstractDao subclass wants
       * to map the object to a certain database table, that cannot be guessed automatically
       * from the name of the mapped class. If <code>getTableName(objectMappingKey)</code> returns null,
       *
       * a new object mapping is auto-generated if possible. An object mapping can be generated if the method key
       * used is a <code>Class</code> instance (f.x. Employees.class), or an instance of
       * <code>ObjectMappingKey</code> with a <code>Class</code> instance filled in.
       * If an object mapping is generated it will be stored in the object
       * method cache under the given method key for later use.
       *
       * 
       * The <code>Class</code> instance should be the class of the object to be stored, meaning
       * if you want to store objects of the class Employee, the class instance should be
       * Employee.class.
       * @param objectMappingKey  The key by which the object mapping to return is stored.
       * @return            The object mapping stored by the given method key.
       * @throws PersistenceException If no object mapping is stored in the object mapping cache and
       *                    no object mapping could be generated, or if something goes wrong during the
       *                    generation of the object mapping if one can be generated.
       */
    protected IObjectMapping getObjectMapping(Object objectMappingKey) throws PersistenceException {
        return getObjectMapper().getObjectMapping(objectMappingKey, this.configuration, getConnection());
    }

    //============================
    // Single Object Read Methods
    //============================

    public Object readByPrimaryKey(Object objectMappingKey, Object primaryKey) throws PersistenceException {
        IObjectMapping mapping = getObjectMapping(objectMappingKey);
        String sql = getSqlFromCache(objectMappingKey, getConfigurationOrFail().getReadByPrimaryKeySqlCache());

        if(sql == null){
            sql       = getSqlGenerator().generateReadByPrimaryKeyStatement(mapping);
            storeSqlInCache(objectMappingKey, getConfigurationOrFail().getReadByPrimaryKeySqlCache(), sql);
        }
        return getObjectReader().readByPrimaryKey(mapping, primaryKey, sql, connection);
    }

    public <T> T readByPrimaryKey(Class<T> objectMappingKey, Object primaryKey) throws PersistenceException {
        return (T) readByPrimaryKey((Object) objectMappingKey, primaryKey);
    }


    public Object read(Object objectMappingKey, String sql) throws PersistenceException {
        return getObjectReader().read(getObjectMapping(objectMappingKey), sql, getConnection());
    }

    public <T> T read(Class<T> objectMappingKey, String sql) throws PersistenceException {
        return (T) read((Object) objectMappingKey, sql);
    }


    public Object read(Object objectMappingKey, ResultSet result) throws PersistenceException{
        return getObjectReader().read(getObjectMapping( objectMappingKey), result);
    }

    public <T> T read(Class<T> objectMappingKey, ResultSet result) throws PersistenceException{
        return (T) read((Object) objectMappingKey, result);
    }

    public Object read(Object objectMappingKey, Statement statement, String sql) throws PersistenceException {
        return getObjectReader().read(getObjectMapping( objectMappingKey), statement, sql);
    }

    public <T> T read(Class<T> objectMappingKey, Statement statement, String sql) throws PersistenceException {
        return (T) read((Object) objectMappingKey, statement, sql);
    }

    public Object read(Object objectMappingKey, PreparedStatement statement) throws PersistenceException {
        return getObjectReader().read(getObjectMapping(objectMappingKey), statement);
    }

    public <T> T read(Class<T> objectMappingKey, PreparedStatement statement) throws PersistenceException {
        return (T) read((Object) objectMappingKey, statement);
    }

    public Object read(Object objectMappingKey, String sql, Collection parameters) throws PersistenceException {
        return getObjectReader().read(getObjectMapping(objectMappingKey), sql, parameters, getConnection());
    }

    public <T> T read(Class<T> objectMappingKey, String sql, Collection parameters) throws PersistenceException {
        return (T) read((Object) objectMappingKey, sql, parameters);
    }

    public Object read(Object objectMappingKey, String sql, Object ...  parameters) throws PersistenceException {
        return getObjectReader().read(getObjectMapping(objectMappingKey), sql, parameters, getConnection());
    }

    public <T> T read(Class<T> objectMappingKey, String sql, Object ...  parameters) throws PersistenceException {
        return (T) read((Object) objectMappingKey, sql, parameters);
    }

    //==============================
     // Object List Read Methods
     //==============================

    public List readListByPrimaryKeys(Object objectMappingKey, Collection primaryKeys) throws PersistenceException {
        if(primaryKeys.size() == 0) return new ArrayList();
        IObjectMapping mapping = getObjectMapping(objectMappingKey);
        String  sql = getSqlGenerator().generateReadListByPrimaryKeysStatement(mapping, primaryKeys.size());
        return getObjectReader().readListByPrimaryKeys(mapping, primaryKeys, sql, getConnection());
    }

    public <T> List<T> readListByPrimaryKeys(Class<T> objectMappingKey, Collection primaryKeys) throws PersistenceException {
        return readListByPrimaryKeys((Object) objectMappingKey, primaryKeys);
    }

    public List readList(Object objectMappingKey, String sql) throws PersistenceException {
        return getObjectReader().readList(getObjectMapping(objectMappingKey), sql, connection);
    }

    public <T> List<T> readList(Class<T> objectMappingKey, String sql) throws PersistenceException {
        return readList((Object) objectMappingKey, sql);
    }

    public List readList(Object objectMappingKey, ResultSet result) throws PersistenceException {
        return getObjectReader().readList(getObjectMapping(objectMappingKey), result);
    }

    public <T> List<T> readList(Class<T> objectMappingKey, ResultSet result) throws PersistenceException {
        return readList((Object) objectMappingKey, result);
    }

    public List readList(Object objectMappingKey, Statement statement, String sql) throws PersistenceException{
        return getObjectReader().readList(getObjectMapping(objectMappingKey), statement, sql);
    }

    public <T> List<T> readList(Class<T> objectMappingKey, Statement statement, String sql) throws PersistenceException{
        return readList((Object) objectMappingKey, statement, sql);
    }

    public List readList(Object objectMappingKey, PreparedStatement statement) throws PersistenceException{
        return getObjectReader().readList(getObjectMapping(objectMappingKey), statement);
    }

    public <T> List<T> readList(Class<T> objectMappingKey, PreparedStatement statement) throws PersistenceException{
        return readList((Object) objectMappingKey, statement);
    }

    public List readList(Object objectMappingKey, String sql, Collection parameters) throws PersistenceException{
        return getObjectReader().readList(getObjectMapping(objectMappingKey), sql, parameters, getConnection());
    }

    public <T> List<T> readList(Class<T> objectMappingKey, String sql, Collection parameters) throws PersistenceException{
        return readList((Object) objectMappingKey, sql, parameters);
    }

    public List readList(Object objectMappingKey, String sql, Object ...  parameters) throws PersistenceException{
        return getObjectReader().readList(getObjectMapping(objectMappingKey), sql, parameters, getConnection());
    }

    public <T> List<T> readList(Class<T> objectMappingKey, String sql, Object ...  parameters) throws PersistenceException{
        return readList((Object) objectMappingKey, sql, parameters);
    }
    //*******************************
    // Filtered List Reads
    //*******************************




    public List readList(Object objectMappingKey, String sql, IReadFilter filter) throws PersistenceException {
        return getObjectReader().readList(getObjectMapping(objectMappingKey), sql, getConnection(), filter);
    }

    public <T> List<T> readList(Class<T> objectMappingKey, String sql, IReadFilter filter) throws PersistenceException {
        return readList((Object) objectMappingKey, sql, filter);
    }

    public List readList(Object objectMappingKey, ResultSet result, IReadFilter filter) throws PersistenceException{
        return getObjectReader().readList(getObjectMapping(objectMappingKey), result, filter);
    }

    public <T> List<T> readList(Class<T> objectMappingKey, ResultSet result, IReadFilter filter) throws PersistenceException{
        return readList((Object) objectMappingKey, result, filter);
    }

    public List readList(Object objectMappingKey, Statement statement, String sql, IReadFilter filter) throws PersistenceException{
        return getObjectReader().readList(getObjectMapping(objectMappingKey), statement, sql, filter);
    }

    public <T> List<T> readList(Class<T> objectMappingKey, Statement statement, String sql, IReadFilter filter) throws PersistenceException{
        return readList((Object) objectMappingKey, statement, sql, filter);
    }

    public List readList(Object objectMappingKey, PreparedStatement statement, IReadFilter filter) throws PersistenceException{
        return getObjectReader().readList(getObjectMapping(objectMappingKey), statement, filter);
    }

    public <T> List<T> readList(Class<T> objectMappingKey, PreparedStatement statement, IReadFilter filter) throws PersistenceException{
        return readList((Object) objectMappingKey, statement, filter);
    }

    
    public List readList(Object objectMappingKey, String sql, IReadFilter filter, Collection parameters) throws PersistenceException {
        return getObjectReader().readList(getObjectMapping(objectMappingKey), sql, parameters, getConnection(), filter);
    }

    public <T> List<T> readList(Class<T> objectMappingKey, String sql, IReadFilter filter, Collection parameters) throws PersistenceException {
        return readList((Object) objectMappingKey, sql, filter, parameters);
    }


    public List readList(Object objectMappingKey, String sql, IReadFilter filter, Object ...  parameters) throws PersistenceException {
        return getObjectReader().readList(getObjectMapping(objectMappingKey), sql, parameters, getConnection(), filter);
    }

    public <T> List<T> readList(Class<T> objectMappingKey, String sql, IReadFilter filter, Object ...  parameters) throws PersistenceException {
        return readList((Object) objectMappingKey, sql, filter, parameters);
    }


    // ***********************************
    //  Insert, Update and Delete Methods
    // ***********************************

  public int insert(Object object) throws PersistenceException{
        return insert(object.getClass(), object);
    }


    public int insert(Object objectMappingKey, Object object) throws PersistenceException {
        IObjectMapping  mapping = getObjectMapping(objectMappingKey);
        String          sql     = getSqlFromCache(mapping, getConfigurationOrFail().getInsertSqlCache());

        sql = generateAndStoreInsertSql(sql, mapping);
        UpdateResult updateResult = getObjectWriter().insert(mapping, object, sql, getConnection());
        this.updateResults.add(updateResult);
        return updateResult.getAffectedRecords()[0];
    }

    private String generateAndStoreInsertSql(String sql, IObjectMapping mapping) throws PersistenceException {
        if(sql == null){
            sql = getSqlGenerator().generateInsertStatement(mapping);
            storeSqlInCache(mapping, getConfigurationOrFail().getInsertSqlCache(), sql);
        }
        return sql;
    }


    public int[] insertBatch(Collection objects) throws PersistenceException {
        if(objects.size() > 0){
            Iterator iterator = objects.iterator();
            Class objectMappingKey = iterator.next().getClass();
            return insertBatch(objectMappingKey, objects);
        }
        return new int[0];
    }

    public int[] insertBatch(Object objectMappingKey, Collection objects) throws PersistenceException {
        IObjectMapping  mapping = getObjectMapping(objectMappingKey);
        String          sql     = getSqlFromCache(mapping, getConfigurationOrFail().getInsertSqlCache());

        sql = generateAndStoreInsertSql(sql, mapping);
        UpdateResult updateResult = getObjectWriter().insertBatch(mapping, objects, sql, getConnection());
        this.updateResults.add(updateResult);
        return updateResult.getAffectedRecords();
    }


    public int update(Object object) throws PersistenceException{
        return update(object.getClass(), object);
    }

    public int update(Object objectMappingKey, Object object) throws PersistenceException {
        IObjectMapping  mapping = getObjectMapping(objectMappingKey);
        String          sql     = getSqlFromCache(mapping, getConfigurationOrFail().getUpdateSqlCache());
        sql = generateAndStoreUpdateSql(sql, mapping);

        UpdateResult updateResult = getObjectWriter().update(mapping, object, sql, getConnection());
        this.updateResults.add(updateResult);
        return updateResult.getAffectedRecords()[0];
    }

    public int updateByPrimaryKey(Object object, Object oldPrimaryKeyValue) throws PersistenceException {
        return updateByPrimaryKey(object.getClass(), object, oldPrimaryKeyValue);
    }


    public int updateByPrimaryKey(Object objectMappingKey, Object object, Object oldPrimaryKeyValue) throws PersistenceException {
        IObjectMapping  mapping = getObjectMapping(objectMappingKey);
        String          sql     = getSqlFromCache(mapping, getConfigurationOrFail().getUpdateSqlCache());

        sql = generateAndStoreUpdateSql(sql, mapping);

        UpdateResult updateResult = getObjectWriter().update(mapping, object, oldPrimaryKeyValue, sql, connection);
        this.updateResults.add(updateResult);
        return updateResult.getAffectedRecords()[0];
    }



    public int[] updateBatch(Collection objects) throws PersistenceException {
        if(objects.size() > 0){
            Iterator iterator = objects.iterator();
            Class objectMappingKey = iterator.next().getClass();
            return updateBatch(objectMappingKey, objects);
        }
        return new int[0];
    }

    public int[] updateBatch(Object objectMappingKey, Collection objects) throws PersistenceException {
        IObjectMapping  mapping = getObjectMapping(objectMappingKey);
        String          sql     = getSqlFromCache(mapping, getConfigurationOrFail().getUpdateSqlCache());

        sql = generateAndStoreUpdateSql(sql, mapping);

        UpdateResult updateResult = getObjectWriter().updateBatch(mapping, objects, sql, getConnection());
        this.updateResults.add(updateResult);
        return updateResult.getAffectedRecords();
    }



    public int[] updateBatchByPrimaryKeys(Collection objects, Collection oldPrimaryKeys) throws PersistenceException{
         if(objects.size() > 0){
             Iterator iterator = objects.iterator();
             Class objectMappingKey = iterator.next().getClass();
             return updateBatchByPrimaryKeys(objectMappingKey, objects, oldPrimaryKeys);
         }
         return new int[0];
     }

public int[] updateBatchByPrimaryKeys(Object objectMappingKey, Collection objects, Collection oldPrimaryKeys) throws PersistenceException {
        IObjectMapping  mapping = getObjectMapping(objectMappingKey);
        String          sql     = getSqlFromCache(mapping, getConfigurationOrFail().getUpdateSqlCache());

        sql = generateAndStoreUpdateSql(sql, mapping);

        UpdateResult updateResult = getObjectWriter().updateBatch(mapping, objects, oldPrimaryKeys, sql, connection);
        this.updateResults.add(updateResult);
        return updateResult.getAffectedRecords();
    }


    public int delete(Object object) throws PersistenceException{
        return delete(object.getClass(), object);
    }


    public int delete(Object objectMappingKey, Object object) throws PersistenceException {
        IObjectMapping  mapping = getObjectMapping(objectMappingKey);
        String          sql     = getSqlFromCache(mapping, getConfigurationOrFail().getDeleteSqlCache());

        if(sql == null){
            sql = getSqlGenerator().generateDeleteStatement(mapping);
            storeSqlInCache(mapping, getConfigurationOrFail().getDeleteSqlCache(), sql);
        }

        UpdateResult updateResult = getObjectWriter().delete(mapping, object, sql, getConnection());
        this.updateResults.add(updateResult);
        return updateResult.getAffectedRecords()[0];
    }


    public int[] deleteBatch(Collection objects) throws PersistenceException{
        if(objects.size() > 0){
            Iterator iterator = objects.iterator();
            Class objectMappingKey = iterator.next().getClass();
            return deleteBatch(objectMappingKey, objects);
        }
        return new int[0];
    }


    public int[] deleteBatch(Object objectMappingKey, Collection objects) throws PersistenceException {
        IObjectMapping  mapping = getObjectMapping(objectMappingKey);
        String          sql     = getSqlFromCache(mapping, getConfigurationOrFail().getDeleteSqlCache());

        sql = generateAndStoreDeleteSql(sql, mapping);

        UpdateResult updateResult = getObjectWriter().deleteBatch(mapping, objects, sql, getConnection());
        this.updateResults.add(updateResult);
        return updateResult.getAffectedRecords();
    }



    public int deleteByPrimaryKey(Object objectMappingKey, Object primaryKey) throws PersistenceException {
        IObjectMapping  mapping = getObjectMapping(objectMappingKey);
        String          sql     = getSqlFromCache(mapping, getConfigurationOrFail().getDeleteSqlCache());

        sql = generateAndStoreDeleteSql(sql, mapping);

        UpdateResult updateResult = getObjectWriter().deleteByPrimaryKey(mapping, primaryKey, sql, getConnection());
        this.updateResults.add(updateResult);
        return updateResult.getAffectedRecords()[0];
    }


    public int[] deleteBatchByPrimaryKeys(Object objectMappingKey, Collection primaryKeys) throws PersistenceException {
        IObjectMapping  mapping = getObjectMapping(objectMappingKey);
        String          sql     = getSqlFromCache(mapping, getConfigurationOrFail().getDeleteSqlCache());

        sql = generateAndStoreDeleteSql(sql, mapping);

        UpdateResult updateResult = getObjectWriter().deleteByPrimaryKeysBatch(mapping, primaryKeys, sql, getConnection());
        this.updateResults.add(updateResult);
        return updateResult.getAffectedRecords();
    }

    /*
    private int executeStatement(PreparedStatement statement, String sql, Object parameters) throws PersistenceException {
        try{
            return statement.executeUpdate();
        } catch(SQLException e){
            throw new PersistenceException("Error executing PreparedStatement update" +
                    "\nSQL: " + sql + "\nParameters: " + parameters.toString() , e);
        }
    }

    private PreparedStatement prepareStatement(String sql) throws PersistenceException {
        return JdbcUtil.prepareStatement(getConnection(), sql);
    }
    */


    private String generateAndStoreUpdateSql(String sql, IObjectMapping mapping) throws PersistenceException {
        if(sql == null){
            sql = getSqlGenerator().generateUpdateStatement(mapping);
            storeSqlInCache(mapping, getConfigurationOrFail().getUpdateSqlCache(), sql);
        }
        return sql;
    }

    private String generateAndStoreDeleteSql(String sql, IObjectMapping mapping) throws PersistenceException {
        if(sql == null){
            sql = getSqlGenerator().generateDeleteStatement(mapping);
            storeSqlInCache(mapping, getConfigurationOrFail().getDeleteSqlCache(), sql);
        }
        return sql;
    }
}