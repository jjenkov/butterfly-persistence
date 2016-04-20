package com.jenkov.db.impl;

import com.jenkov.db.itf.IMapDao;
import com.jenkov.db.itf.IPreparedStatementManager;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.IDaos;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class MapDao implements IMapDao {

    protected IDaos daos = null;

    public MapDao(IDaos daos) {
        this.daos = daos;
    }

    public Map readMap(String sql) throws PersistenceException{
        return readMap(sql, new PreparedStatementManagerBase());
    }

    public Map readMap(String sql, final Object ... parameters) throws PersistenceException{
        return readMap(sql, new PreparedStatementManagerBase(parameters));
    }

    public Map readMap(String sql, IPreparedStatementManager statementManager) throws PersistenceException {
        return (Map) this.daos.getJdbcDao().read(sql, statementManager, new ResultSetProcessorBase(){

            ResultSetMetaData metaData = null;

            public void init(ResultSet result, IDaos daos) throws SQLException, PersistenceException {
                this.metaData = result.getMetaData();
            }

            public void process(ResultSet result, IDaos daos) throws SQLException, PersistenceException {
                setResult(readRecordIntoMap(result, this.metaData, (Map) getResult()));
            }

        });
    }

    private Map readRecordIntoMap(ResultSet result, ResultSetMetaData metaData, Map map) throws SQLException {
        if(map == null) map = new HashMap();
        for(int i=1, n=metaData.getColumnCount(); i<=n; i++){
            map.put(metaData.getColumnName(i), result.getObject(i));
        }
        return map;
    }

    public List readMapList(String sql) throws PersistenceException {
        return readMapList(sql, new PreparedStatementManagerBase());
    }

    public List readMapList(String sql, final Object ... parameters) throws PersistenceException {
        return readMapList(sql, new PreparedStatementManagerBase(parameters));
    }

    public List readMapList(String sql, IPreparedStatementManager statementManager) throws PersistenceException{
        return (List) this.daos.getJdbcDao().read(sql, statementManager,  new ResultSetProcessorBase(){
            ResultSetMetaData metaData = null;

            public void init(ResultSet result, IDaos daos) throws SQLException, PersistenceException {
                setResult(new ArrayList());
                this.metaData = result.getMetaData();
            }

            public void process(ResultSet result, IDaos daos) throws SQLException, PersistenceException {
                ((List) getResult()).add(readRecordIntoMap(result, this.metaData, null));
            }

        });
    }
}
