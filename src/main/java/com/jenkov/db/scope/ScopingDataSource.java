package com.jenkov.db.scope;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A DataSource capable of scoping connections and transactions.
 * You can either use instances of this class directly, or use
 * it via a ScopeFactory.
 *
 * The ScopingDataSource needs a real DataSource implementation
 * to obtain the connections from.
 *
 * Using the ScopingDataSource directly to demarcate a connection scope can be done like this:
 *
 * <code>
 * ScopingDataSource scopingDataSource = new ScopingDataSource(dataSource);
 *
 * try{
 * &nbsp;&nbsp;    scopingDatasSource.beginConnectionScope();
 * &nbsp;&nbsp;    Connection connection = scopingDataSource.getConnection();
 * &nbsp;&nbsp;    //do something with connection
 * &nbsp;&nbsp;
 * &nbsp;&nbsp;    // same connection as previously returned
 * &nbsp;&nbsp;    Connection connection2 = scopingDataSource.getConnection();
 * &nbsp;&nbsp;    //do something with the connection
 * &nbsp;&nbsp;
 * &nbsp;&nbsp;    scopingDataSource.endConnectionScope();
 * } catch(Throwable t){
 * &nbsp;&nbsp;    scopingDataSource.endConnectionScope(t);
 * }
 * </code>
 *
 *
 * Demarcating a transaction scope can be done similarly, like this:
 *
 * <code>
 * try{
 * &nbsp;&nbsp;    scopingDatasSource.beginTransactionScope();
 * &nbsp;&nbsp;    Connection connection = scopingDataSource.getConnection();
 * &nbsp;&nbsp;    //do something with connection
 * &nbsp;&nbsp;
 * &nbsp;&nbsp;    // same connection as previously returned
 * &nbsp;&nbsp;    Connection connection2 = scopingDataSource.getConnection();
 * &nbsp;&nbsp;    //do something with the connection
 * &nbsp;&nbsp;
 * &nbsp;&nbsp;    scopingDataSource.endTransactionScope();
 * } catch(Throwable t){
 * &nbsp;&nbsp;    scopingDataSource.abortTransactionScope(t);
 * }
 * </code>
 *
 *
 *
 *
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class ScopingDataSource implements DataSource{

    protected DataSource dataSource = null;

    public Map connectionScopes  = new HashMap();
    public Map transactionScopes = new HashMap();

    public ScopingDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //****************************************
    // javax.sql.DataSource implementation
    //****************************************

    /**
     * Not supported.
     * @return 0
     * @throws SQLFeatureNotSupportedException Never.
     */
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("getParentLogger() is not supported");
    }

    public int getLoginTimeout() throws SQLException {
        return this.dataSource.getLoginTimeout();
    }
    public void setLoginTimeout(int seconds) throws SQLException {
        this.dataSource.setLoginTimeout(seconds);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return this.dataSource.getLogWriter();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        this.dataSource.setLogWriter(out);
    }

    /**
     * Returns true if the thread calling this method is
     * currently inside a connection scope. False if not.
     * @return  true if the thread calling this method is
     * currently inside a connection scope. False if not.
     */
    public synchronized boolean isInsideConnectionScope(){
        return connectionScopes.get(Thread.currentThread()) != null;
    }

    /**
     * Returns true if a connection is currently open inside
     * this connection or transaction scope.
     * @return True if the connection is open internally. False if not.
     */
    public synchronized boolean isConnectionOpen(){
        //if(!isInsideConnectionScope() && !isInsideTransactionScope()) return false;
        if(isInsideConnectionScope()){
            return getConnectionScopeObject() instanceof Connection;
        }
        if(isInsideTransactionScope()){
            return getTransactionScopeObject() instanceof Connection;
        }
        return false;
    }


    private Object getConnectionScopeObject() {
        return this.connectionScopes.get(Thread.currentThread());
    }

    private Object removeConnectionScopeObject() {
        return this.connectionScopes.remove(Thread.currentThread());
    }

    private Object getTransactionScopeObject() {
        return this.transactionScopes.get(Thread.currentThread());
    }
    private Object removeTransactionScopeObject() {
        return this.transactionScopes.remove(Thread.currentThread());
    }

    /**
     * Starts a connection scope for the thread calling this method.
     */
    public synchronized void beginConnectionScope(){
        connectionScopes.put(Thread.currentThread(), "connection");
    }

    /**
     * Ends the connection scope for the tread calling this method.
     * If a connection is open in this connection scope it will be closed.
     */
    public synchronized void endConnectionScope(){
        endConnectionScope(null);
    }

    /**
     * Ends the connection scope for the thread calling this method, and
     * rethrows the given error (Throwable) in a ScopeException.
     * If a connection is open in this connection scope it will be closed.
     * @param error The error that causes this connection scope to end.
     */
    public synchronized void endConnectionScope(Throwable error) {
        Object connectionScopeObject  = getConnectionScopeObject();

        Connection connection        = null;
        Throwable  commitException   = null;
        Throwable  closeException    = null;

        try{
            if(!isInsideScope(connectionScopeObject) || !isConnection(connectionScopeObject)) {
                if(isInsideTransactionScope()) endTransactionScope();
                return;
            }
            connection = (Connection) connectionScopeObject;

            if(isInsideTransactionScope()) endTransactionScope();
        } catch(ScopeException e){
            commitException = e;
        } finally {
            // endTransactionScope() may use the connection scope object. Therefore it cannot be removed until now.
            removeConnectionScopeObject();

            closeException = close(connection);
            if(error != null || commitException != null || closeException != null){
                if(commitException == null && closeException == null){
                    throw new ScopeException("Error occurred in connection scope. Connection scope ended correctly."
                            , error, commitException, null, null, closeException);
                }
                throw new ScopeException("Error ending connection scope", error, commitException, null, null, closeException);
            }
        }
    }

    /**
     * Returns true if the thread calling this method is
     * currently inside a transaction scope. False if not.
     * @return  true if the thread calling this method is
     * currently inside a transaction scope. False if not.
     */
    public synchronized boolean isInsideTransactionScope(){
        return transactionScopes.get(Thread.currentThread()) != null;
    }

    /**
     * Begins a transaction scope for the thread calling this method.
     */
    public synchronized void beginTransactionScope(){
        transactionScopes.put(Thread.currentThread(), "transaction");
    }

    /**
     * Ends the transaction scope for the thread calling this method.
     * If a connection is opened within the transaction scope, the
     * transaction is committed, and the connection closed.
     */
    public synchronized void endTransactionScope() {
        Object     connectionScopeObject  = getConnectionScopeObject();
        Object     transactionScopeObject = removeTransactionScopeObject();

        //if not inside transaction scope just return and ignore.
        if(!isInsideScope(transactionScopeObject))  return;

        //if not inside connection scope and no connection is opened in this transaction scope, just return;
        if(!isInsideScope(connectionScopeObject) && !isConnection(transactionScopeObject)){
            return;
        }

        //if inside connection scope but no connection opened, just return;
        if(isInsideScope(connectionScopeObject) && !isConnection(connectionScopeObject)){
            return;
        }

        Connection connection                  = null;
        Throwable  rootCause                   = null;
        Throwable  rollbackException           = null;
        Throwable  setAutoCommitFalseException = null;
        Throwable  closeException              = null;

        if(isInsideScope(connectionScopeObject)){
            connection = (Connection) connectionScopeObject;
        } else {
            connection = (Connection) transactionScopeObject;
        }

        try {
            connection.commit();
        } catch(Throwable t){
            rootCause = t;
            rollbackException = rollback(connection);
        } finally {
            if(!isInsideScope(connectionScopeObject)) closeException = close(connection);
            else try{ connection.setAutoCommit(true); } catch(Throwable e){setAutoCommitFalseException = e;}

            if(rootCause != null || rollbackException != null || setAutoCommitFalseException != null || closeException != null){
                throw new ScopeException("Error ending transaction scope", null, rootCause, rollbackException, setAutoCommitFalseException, closeException);
            }
        }
    }

    /**
     * Aborts the transaction scope for the thread calling this method.
     * If a transaction is currently running it will be rolled back, and
     * the connection closed. Finally the given rootCause Throwable will
     * be rethrown wrapped in a ScopeException.
     *
     * @param rootCause The exception that is the reason this transaction scope
     *                  should be aborted.
     */
    public synchronized void abortTransactionScope(Throwable rootCause){
        Object     connectionScopeObject  = getConnectionScopeObject();
        Object     transactionScopeObject = removeTransactionScopeObject();

        //if not inside transaction scope just return and ignore.
        if(!isInsideScope(transactionScopeObject))  {
            if(rootCause instanceof ScopeException ) throw (ScopeException) rootCause;
            throw new ScopeException("Transaction Failed. Transaction Scope ended successfully.", rootCause);
        }

        //if not inside connection scope and no connection is opened in this transaction scope, just return;
        if(!isInsideScope(connectionScopeObject) && !isConnection(transactionScopeObject)){
            if(rootCause instanceof ScopeException ) throw (ScopeException) rootCause;
            throw new ScopeException("Transaction Failed. Transaction Scope ended successfully.", rootCause);
        }

        //if inside connection scope but no connection opened, just return;
        if(isInsideScope(connectionScopeObject) && !isConnection(connectionScopeObject)){
            if(rootCause instanceof ScopeException ) throw (ScopeException) rootCause;
            throw new ScopeException("Transaction Failed. Transaction Scope ended successfully.", rootCause);
        }

        Connection connection                  = null;
        Throwable  rollbackException           = null;
        Throwable  setAutoCommitFalseException = null;
        Throwable  closeException              = null;

        if(isInsideScope(connectionScopeObject)){
            connection = (Connection) connectionScopeObject;
        } else {
            connection = (Connection) transactionScopeObject;
        }

        try {
            rollbackException = rollback(connection);
            connection.setAutoCommit(true);
        } catch(Throwable t){
            rootCause = t;

        } finally {
            if(!isInsideScope(connectionScopeObject)) closeException = close(connection);
            else try{ connection.setAutoCommit(true); } catch(Throwable e){setAutoCommitFalseException = e;}

            if(rollbackException != null || setAutoCommitFalseException != null || closeException != null){
                throw new ScopeException("Error during transaction. Transaction aborted (rolled back), but with errors"
                        , rootCause, null, rollbackException, setAutoCommitFalseException, closeException);
            } else {
                throw new ScopeException("Error during transaction. Transaction aborted (rolled back)."
                        , rootCause, null, rollbackException, setAutoCommitFalseException, closeException);
            }
        }
    }


    /**
     * Returns a connection. If the thread calling this method is currently inside a
     * connection or transaction scope, the connections returned by this method will be the same connection,
     * until the connection or transaction scope ends. In other words, connections are reused inside
     * a connection or transaction scope.
     *
     * @return A connection.
     *
     * @throws SQLException If a connection cannot be obtained from the internal DataSource.
     */
    public synchronized Connection getConnection() throws SQLException {
        Object     connectionScopeObject  = getConnectionScopeObject();
        Object     transactionScopeObject = transactionScopes.get(Thread.currentThread());

        Connection connection  = null;

        if(isInsideScope(connectionScopeObject)){
            if(isConnection(connectionScopeObject)) {
                connection = (Connection) connectionScopeObject;
            }
            else connection = getScopingConnection();
            connectionScopes.put(Thread.currentThread(), connection);
        }

        //if is inside a transaction scope
        if(isInsideScope(transactionScopeObject)){

            //if not inside connection scope...
            if(!isInsideScope(connectionScopeObject)){
                //if connection already opened in this transaction scope use that connection.
                if(isConnection(transactionScopeObject)) {
                    connection = (Connection) transactionScopeObject;
                } else {
                    //no connection opened yet for this transaction scope.
                    connection = getScopingConnection();
                    this.transactionScopes.put(Thread.currentThread(), connection);
                }
            }
            if(connection.getAutoCommit()) setAutoCommitFalse(connection);
        }

        if(connectionScopeObject == null && transactionScopeObject == null){
            connection = getScopingConnection();
        }
        return connection;
    }


    /**
     * Returns a connection. If the thread calling this method is currently inside a
     * connection or transaction scope, the connections returned by this method will be the same connection,
     * until the connection or transaction scope ends. In other words, connections are reused inside
     * a connection or transaction scope.

     * @param username  The user name be used when creating the connection initially.
     * @param password  The password be used when creating the connection initially.
     *
     * @return A connection.
     *
     * @throws SQLException If a connection cannot be obtained from the internal DataSource.
     */
    public synchronized Connection getConnection(String username, String password) throws SQLException {
        Object     connectionScopeObject  = getConnectionScopeObject();
        Object     transactionScopeObject = transactionScopes.get(Thread.currentThread());

        Connection connection  = null;

        if(isInsideScope(connectionScopeObject)){
            if(isConnection(connectionScopeObject)) {
                connection = (Connection) connectionScopeObject;
            }
            else connection = getScopingConnection(username, password);
            connectionScopes.put(Thread.currentThread(), connection);
        }

        //if is inside a transaction scope
        if(isInsideScope(transactionScopeObject)){

            //if not inside connection scope...
            if(!isInsideScope(connectionScopeObject)){
                //if connection already opened in this transaction scope use that connection.
                if(isConnection(transactionScopeObject)) {
                    connection = (Connection) transactionScopeObject;
                } else {
                    //no connection opened yet for this transaction scope.
                    connection = getScopingConnection(username, password);
                    this.transactionScopes.put(Thread.currentThread(), connection);
                }
            }
            if(connection.getAutoCommit()){
                setAutoCommitFalse(connection);
            }
        }

        if(connectionScopeObject == null && transactionScopeObject == null){
            connection = getScopingConnection(username, password);
        }
        return connection;
    }

    private void setAutoCommitFalse(Connection connection) throws SQLException {
        if(connection instanceof ScopingConnection){
            ScopingConnection scopingConnection = (ScopingConnection) connection;
            scopingConnection.getOriginalConnection().setAutoCommit(false);
        } else {
            connection.setAutoCommit(false);
        }
    }


    private boolean isInsideScope(Object scopeObject){
        return scopeObject != null;
    }

    private boolean isConnection(Object scopeObject){
        return scopeObject instanceof Connection;
    }


    private Throwable close(Connection connection) {
        if(connection == null) return null;
        try {
            connection.close();
        } catch (Throwable e) {
            return e;
        }
        return null;
    }

    private Throwable rollback(Connection connection) {
        if(connection == null) return null;
        try {
            connection.rollback();
        } catch (Throwable e) {
            return e;
        }
        return null;
    }

    private Connection getScopingConnection() throws SQLException {
        return new ScopingConnection(this.dataSource.getConnection(), this);
    }

    private Connection getScopingConnection(String username, String password) throws SQLException {
        return new ScopingConnection(this.dataSource.getConnection(username, password), this);
    }

    public <T> T unwrap(Class<T> tClass) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
