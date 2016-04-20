package com.jenkov.db.impl;

import com.jenkov.db.itf.IPreparedStatementManager;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.util.JdbcUtil;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class PreparedStatementManagerBase implements IPreparedStatementManager {

    protected Object[] parameters = null;
    protected boolean isQuery = true;

    public PreparedStatementManagerBase() {
    }

    public PreparedStatementManagerBase(Object[] parameters) {
        this.parameters = parameters;
    }

    void setIsQuery(boolean isQuery){
        this.isQuery = isQuery;
    }

    public PreparedStatement prepare(String sql, Connection connection) throws SQLException, PersistenceException {
        return connection.prepareStatement(sql);
    }

    public void init(PreparedStatement statement)  throws SQLException, PersistenceException{
        if(parameters != null && parameters.length > 0){
            JdbcUtil.insertParameters(statement, parameters);
        }
    }

    public Object execute(PreparedStatement statement) throws SQLException, PersistenceException {
        if(this.isQuery){
            return statement.executeQuery();
        } else {
            return new Integer(statement.executeUpdate());
        }
    }

    public void postProcess(PreparedStatement statement)  throws SQLException, PersistenceException{

    }
}
