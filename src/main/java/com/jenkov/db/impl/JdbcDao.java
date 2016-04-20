package com.jenkov.db.impl;

import com.jenkov.db.itf.IJdbcDao;
import com.jenkov.db.itf.IPreparedStatementManager;
import com.jenkov.db.itf.IResultSetProcessor;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.IDaos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class JdbcDao implements IJdbcDao {

    protected IDaos daos = null;

    public JdbcDao(IDaos daos) {
        this.daos = daos;
    }

    public Long readLong(String sql) throws PersistenceException {
        return (Long) readLong(sql, null);
    }

    public Long readLong(String sql, Object ... parameters) throws PersistenceException {
        return (Long) read(sql, new ResultSetProcessorBase(){
            private Long theLong = null;

            public void process(ResultSet result, IDaos daos) throws SQLException {
                this.theLong = new Long(result.getLong(1));
            }

            public Object getResult() {
                return this.theLong;
            }
        }, parameters);
    }

    public String readIdString(String sql) throws PersistenceException {
        return readIdString(sql, (Object[]) null);
    }

    public String readIdString(String sql, Object ... parameters) throws PersistenceException {
        return readIdString(sql, new PreparedStatementManagerBase(parameters));
    }

    public String readIdString(String sql, IPreparedStatementManager manager) throws PersistenceException {
        return (String) read(sql, manager, new ResultSetProcessorBase(){
            public void init(ResultSet result, IDaos daos) throws SQLException, PersistenceException {
                setResult(new StringBuffer("("));
            }

            public void process(ResultSet result, IDaos daos) throws SQLException, PersistenceException {
                ((StringBuffer) this.result).append(result.getString(1));
                ((StringBuffer) this.result).append(",");
            }

            public Object getResult() throws PersistenceException {
                StringBuffer buffer = (StringBuffer) this.result;
                if(buffer.length() <= 1) return "";
                buffer.delete(buffer.length()-1, buffer.length());
                buffer.append(")");
                return buffer.toString();
            }
        });
    }



    public Object read(String sql, IResultSetProcessor processor) throws PersistenceException{
        return read(sql, new PreparedStatementManagerBase(), processor);
    }

    public Object read(String sql, IResultSetProcessor processor, Object ... parameters) throws PersistenceException{
        return read(sql, new PreparedStatementManagerBase(parameters), processor);
    }

    public Object read(String sql, IPreparedStatementManager statementManager, IResultSetProcessor processor) throws PersistenceException {
        PreparedStatement statement = null;
        ResultSet         result    = null;
        SQLException      exception = null;
        try {
            if(statementManager instanceof PreparedStatementManagerBase){
                ((PreparedStatementManagerBase) statementManager).setIsQuery(true);
            }

            statement = statementManager.prepare(sql, daos.getConnection());
            if(statement == null) {
                statement = daos.getConnection().prepareStatement(sql);
            }
            statementManager.init(statement);

            result = (ResultSet) statementManager.execute(statement);

            processor.init(result, this.daos);

            while(result.next()){
                processor.process(result, this.daos);
            }
            statementManager.postProcess(statement);

            return processor.getResult();
        } catch(SQLException e){
            exception = e;
            return null; // won't happen. exception will be rethrown from finally clause.
        }
        finally {
            SQLException resultSetCloseException = null;
            if(result != null){
                try {
                    result.close();
                } catch (SQLException e) {
                    resultSetCloseException = e;
                }
            }

            //todo improve exception handling here?
            if(statement != null){
                try {
                    statement.close();
                } catch (SQLException e) {
                    if (exception               != null) throw new PersistenceException("Error during read operation",exception);
                    if (resultSetCloseException != null) throw new PersistenceException("Error closing ResultSet", resultSetCloseException);
                    throw new PersistenceException("Error closing PreparedStatement", e);
                }
            }
            if(exception != null) throw new PersistenceException("Error during read operation",exception);
            if(resultSetCloseException != null) throw new PersistenceException("Error closing ResultSet", resultSetCloseException);
        }
    }


    public int update(String sql) throws PersistenceException{
        return update(sql, new PreparedStatementManagerBase());
    }

    public int update(String sql, final Object ... parameters) throws PersistenceException{
        return update(sql, new PreparedStatementManagerBase(parameters));
    }

    public int update(String sql, IPreparedStatementManager statementManager) throws PersistenceException{
        PreparedStatement statement = null;

        PersistenceException persistenceException = null;
        SQLException         sqlException         = null;
        try {
            if(statementManager instanceof PreparedStatementManagerBase){
                ((PreparedStatementManagerBase) statementManager).setIsQuery(false);
            }
            statement = statementManager.prepare(sql, daos.getConnection());
            statementManager.init(statement);
            int affectedRows = ((Integer) statementManager.execute(statement)).intValue();
            statementManager.postProcess(statement);
            return affectedRows;
        } catch (SQLException e) {
            sqlException = e;
            return 0; //never happens, exception is rethrown in finally clause
        } catch (PersistenceException e) {
            persistenceException = e;
            return 0; //never happens, exception is rethrown in finally clause
        } finally {
            if(statement != null){
                try {
                    statement.close();
                } catch (SQLException e) {
                    if(sqlException != null) throw new PersistenceException("Error executing update", sqlException);
                    if(persistenceException != null) throw persistenceException;
                    throw new PersistenceException("Error closing PreparedStatement", e);
                }
            }
            if(sqlException != null)throw new PersistenceException("Error executing update", sqlException);
            if(persistenceException != null) throw persistenceException;
        }
    }
}
