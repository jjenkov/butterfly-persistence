package com.jenkov.db.itf;

import java.util.Map;
import java.util.List;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public interface IMapDao {

    public Map readMap(String sql) throws PersistenceException;
    public Map readMap(String sql, Object ... parameters) throws PersistenceException;
    public Map readMap(String sql, IPreparedStatementManager statementManager) throws PersistenceException;

    public List readMapList(String sql) throws PersistenceException;
    public List readMapList(String sql, Object ... parameters) throws PersistenceException;
    public List readMapList(String sql, IPreparedStatementManager statementManager) throws PersistenceException;
}
