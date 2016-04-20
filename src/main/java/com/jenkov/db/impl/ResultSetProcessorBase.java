package com.jenkov.db.impl;

import com.jenkov.db.itf.IResultSetProcessor;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.IDaos;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class ResultSetProcessorBase implements IResultSetProcessor {

    protected Object result = null;

    public void init(ResultSet result, IDaos daos) throws SQLException, PersistenceException {
    }

    public void process(ResultSet result, IDaos daos) throws SQLException, PersistenceException {
    }

    protected void setResult(Object object){
        this.result = object;
    }

    public Object getResult() throws PersistenceException {
        return this.result;
    }
}
