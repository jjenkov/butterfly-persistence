package com.jenkov.db.impl;

import com.jenkov.db.itf.ISqlCache;

import java.util.Map;
import java.util.HashMap;

public class SqlCache implements ISqlCache {

    protected Map sqlStatements = new HashMap();

    public boolean containsStatement(Object mappingKey) {
        return this.sqlStatements.containsKey(mappingKey);
    }

    public String getStatement(Object mappingKey) {
        return (String) this.sqlStatements.get(mappingKey);
    }

    public void storeStatement(Object mappingKey, String insertStatement) {
        this.sqlStatements.put(mappingKey, insertStatement);
    }

    public void removeStatement(Object mappingKey) {
        this.sqlStatements.remove(mappingKey);
    }

    public void clear() {
        this.sqlStatements.clear();
    }

    public int size() {
        return this.sqlStatements.size();
    }
}
