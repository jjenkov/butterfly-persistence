package com.jenkov.db.impl;

import com.jenkov.db.impl.mapping.KeyValue;
import com.jenkov.db.itf.IObjectDao;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.itf.mapping.IKeyValue;
import com.jenkov.db.itf.mapping.IObjectMapping;

import java.sql.ResultSet;

/**
 * @author Jakob Jenkov - Copyright 2004-2006 Jenkov Development
 */
public class ObjectDaoCaching {

    protected ObjectCache cache     = new ObjectCache();
    protected IObjectDao  objectDao = null;

    public ObjectDaoCaching(IObjectDao objectDao) {
        this.objectDao = objectDao;
    }

    public Object read(Object objectMappingKey, ResultSet result) throws PersistenceException {
        IObjectMapping objectMapping = this.objectDao.getConfiguration().getObjectMappingCache().getObjectMapping(objectMappingKey);
        IKeyValue keyValue = objectMapping.getPrimaryKeyValueForRecord(result, new KeyValue());
        Object object = this.cache.get(objectMappingKey, keyValue);
        if(object == null){
            object = this.objectDao.read(objectMappingKey, result);
            if(object != null)
            this.cache.store(objectMappingKey, keyValue, object);
        }
        return object;
    }


}
