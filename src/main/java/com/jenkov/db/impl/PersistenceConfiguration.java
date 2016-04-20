package com.jenkov.db.impl;

import com.jenkov.db.PersistenceManager;
import com.jenkov.db.scope.IScopeFactory;
import com.jenkov.db.scope.ScopeFactory;
import com.jenkov.db.impl.mapping.ObjectMappingCache;
import com.jenkov.db.impl.mapping.ObjectMapper;
import com.jenkov.db.impl.mapping.ObjectMappingFactory;
import com.jenkov.db.impl.init.DatabaseInitializer;
import com.jenkov.db.itf.*;
import com.jenkov.db.itf.mapping.ICustomObjectMapper;
import com.jenkov.db.itf.mapping.IObjectMapper;
import com.jenkov.db.itf.mapping.IObjectMappingCache;

import javax.sql.DataSource;

/**
 * This class is an implementation of the <code>IPersistenceConfiguration</code> interface.
 * All the JavaDoc is included in that interface.
 */

public class PersistenceConfiguration implements IPersistenceConfiguration{

    protected PersistenceManager  persistenceManager       = null;
    protected Object              configurationKey         = null;

    protected IObjectReader       reader                   = new ObjectReader      ();
    protected IObjectWriter       writer                   = new ObjectWriter      ();
    protected IObjectMapper       mapper                   = null;
    protected IObjectMappingCache mappingCache             = new ObjectMappingCache();
    protected ICustomObjectMapper customObjectMapper       = null;

    protected ISqlGenerator       sqlGenerator             = new SqlGenerator();
    protected ISqlCache           readByPrimaryKeySqlCache = new SqlCache();
    protected ISqlCache           insertSqlCache           = new SqlCache();
    protected ISqlCache           updateSqlCache           = new SqlCache();
    protected ISqlCache           deleteSqlCache           = new SqlCache();

    protected Database            database                 = null;
    protected DataSource          dataSource               = null;

    protected DatabaseInitializer databaseInitializer      = new DatabaseInitializer();
    protected IScopeFactory       scopeFactory             = null;


    public PersistenceConfiguration(PersistenceManager persistenceManager){
        this(null, persistenceManager);
    }

    public PersistenceConfiguration(Database database, PersistenceManager persistenceManager){
        this.database = database;
        this.persistenceManager = persistenceManager;
        this.mapper = new ObjectMapper(new ObjectMappingFactory());
    }

    public synchronized Database getDatabase() {
        return this.database;
    }

    public synchronized void setDatabase(Database database) {
        this.database = database;
        this.reader.setDatabase(database);
        this.writer.setDatabase(database);
    }

    public synchronized DataSource getDataSource() {
        return dataSource;
    }

    public synchronized void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        setScopeFactory(new ScopeFactory(dataSource));
    }

    public IScopeFactory getScopeFactory() {
        return scopeFactory;
    }

    public void setScopeFactory(IScopeFactory scopeFactory) {
        this.scopeFactory = scopeFactory;
    }

    public synchronized Object getConfigurationKey() {
        return configurationKey;
    }

    public synchronized void setConfigurationKey(Object configurationKey) {
        this.configurationKey = configurationKey;
    }

    public synchronized PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public synchronized IObjectMapper getObjectMapper() {
        return this.mapper;
    }

    public synchronized void setObjectMapper(IObjectMapper mapper) {
        this.mapper = mapper;
    }

    public synchronized ICustomObjectMapper getCustomObjectMapper() {
        return customObjectMapper;
    }

    public synchronized void setCustomObjectMapper(ICustomObjectMapper customObjectMapper) {
        this.customObjectMapper = customObjectMapper;
    }

    public synchronized IObjectMappingCache getObjectMappingCache() {
        return this.mappingCache;
    }

    public synchronized void setObjectMappingCache(IObjectMappingCache cache) {
        this.mappingCache = cache;
    }

    public synchronized IObjectCache getObjectCache() {
        return null;
    }

    public synchronized void setObjectCache(IObjectCache cache) {

    }

    public synchronized IObjectReader getObjectReader() {
        return this.reader;
    }

    public synchronized void setObjectReader(IObjectReader reader) {
        this.reader = reader;
    }

    public synchronized IObjectWriter getObjectWriter() {
        return this.writer;
    }

    public synchronized void setObjectWriter(IObjectWriter writer) {
        this.writer = writer;
    }

    public synchronized ISqlGenerator getSqlGenerator() {
        return this.sqlGenerator;
    }

    public synchronized void setSqlGenerator(ISqlGenerator generator) {
        this.sqlGenerator = generator;
    }


    public synchronized ISqlCache getInsertSqlCache() {
        return this.insertSqlCache;
    }

    public synchronized void setInsertSqlCache(ISqlCache cache) {
        this.insertSqlCache = cache;
    }

    public synchronized ISqlCache getUpdateSqlCache() {
        return this.updateSqlCache;
    }

    public synchronized void setUpdateSqlCache(ISqlCache cache) {
        this.updateSqlCache = cache;
    }

    public synchronized ISqlCache getDeleteSqlCache() {
        return this.deleteSqlCache;
    }

    public synchronized void setDeleteSqlCache(ISqlCache cache) {
        this.deleteSqlCache = cache;
    }

    public synchronized ISqlCache getReadByPrimaryKeySqlCache() {
        return readByPrimaryKeySqlCache;
    }

    public synchronized void setReadByPrimaryKeySqlCache(ISqlCache readByPrimaryKeySqlCache) {
        this.readByPrimaryKeySqlCache = readByPrimaryKeySqlCache;
    }

    public DatabaseInitializer getDatabaseInitializer() {
        return databaseInitializer;
    }

    public void setDatabaseInitializer(DatabaseInitializer databaseInitializer) {
        this.databaseInitializer = databaseInitializer;
    }
}
