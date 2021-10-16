package com.jenkov.db.scope;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * A wrapper around the original connection which can ignore calls to
 * commit, rollback and close, if these methods are called from within
 * either a connection or transaction scope. Users of the scoping features
 * will not use this class directly.
 *
 * Copyright Jenkov Development
 *
 * @author Jakob Jenkov
 */
public class ScopingConnection implements Connection {

    protected Connection        connection        = null;
    protected ScopingDataSource scopingDataSource = null;


    public ScopingConnection(Connection connection, ScopingDataSource scopingDataSource) {
        this.connection = connection;
        this.scopingDataSource = scopingDataSource;
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        //do nothing - not supported
    }

    @Override
    public String getSchema() throws SQLException {
        return null;
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        //do nothing - not supported
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        //do nothing - not supported
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    /**
     * Returns the wrapped connection. For some operations the original connection is necessary,
     * for instance when working with Oracle AQ (Advanced Queues).
     */
    public Connection getOriginalConnection(){
        return this.connection;
    }

    public Statement createStatement() throws SQLException {
        return this.connection.createStatement();
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.connection.prepareStatement(sql);
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        return this.connection.prepareCall(sql);
    }

    public String nativeSQL(String sql) throws SQLException {
        return this.connection.nativeSQL(sql);
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if(!this.scopingDataSource.isInsideTransactionScope()){
            this.connection.setAutoCommit(autoCommit);
        }
    }

    public boolean getAutoCommit() throws SQLException {
        return this.connection.getAutoCommit();
    }

    public void commit() throws SQLException {
        if(!this.scopingDataSource.isInsideTransactionScope()){
            this.connection.commit();
        }
    }

    public void rollback() throws SQLException {
        if(!this.scopingDataSource.isInsideTransactionScope()){
            this.connection.rollback();
        }
    }

    public void close() throws SQLException {
        if(!this.scopingDataSource.isInsideConnectionScope() && ! this.scopingDataSource.isInsideTransactionScope()){
            this.connection.close();
        }
    }

    public boolean isClosed() throws SQLException {
        return this.connection.isClosed();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return this.connection.getMetaData();
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        this.connection.setReadOnly(readOnly);
    }

    public boolean isReadOnly() throws SQLException {
        return this.connection.isReadOnly();
    }

    public void setCatalog(String catalog) throws SQLException {
        this.connection.setCatalog(catalog);
    }

    public String getCatalog() throws SQLException {
        return this.connection.getCatalog();
    }

    public void setTransactionIsolation(int level) throws SQLException {
        this.connection.setTransactionIsolation(level);
    }

    public int getTransactionIsolation() throws SQLException {
        return this.connection.getTransactionIsolation();
    }

    public SQLWarning getWarnings() throws SQLException {
        return this.connection.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        this.connection.clearWarnings();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.connection.createStatement(resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

//    JDK 1.5
//    public Map<String, Class<?>> getTypeMap() throws SQLException {
//        return this.connection.getTypeMap();
//    }
//
//    JDK 1.5
//    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
//        this.connection.setTypeMap(map);
//    }

    //JDK 1.4
    public Map getTypeMap() throws SQLException {
        return this.connection.getTypeMap();
    }

    public void setHoldability(int holdability) throws SQLException {
        this.connection.setHoldability(holdability);
    }

    public int getHoldability() throws SQLException {
        return this.connection.getHoldability();
    }

    public Savepoint setSavepoint() throws SQLException {
        return this.connection.setSavepoint();
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        return this.connection.setSavepoint(name);
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        this.connection.rollback(savepoint);
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.connection.releaseSavepoint(savepoint);
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return this.connection.prepareStatement(sql, autoGeneratedKeys);
    }

    public PreparedStatement prepareStatement(String sql, int columnIndexes[]) throws SQLException {
        return this.connection.prepareStatement(sql, columnIndexes);
    }

    public PreparedStatement prepareStatement(String sql, String columnNames[]) throws SQLException {
        return this.connection.prepareStatement(sql, columnNames);
    }

    public void setTypeMap(Map<String, Class<?>> stringClassMap) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Clob createClob() throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Blob createBlob() throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public NClob createNClob() throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public SQLXML createSQLXML() throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isValid(int i) throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setClientInfo(String s, String s1) throws SQLClientInfoException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getClientInfo(String s) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Properties getClientInfo() throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Array createArrayOf(String s, Object[] objects) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Struct createStruct(String s, Object[] objects) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T> T unwrap(Class<T> tClass) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
