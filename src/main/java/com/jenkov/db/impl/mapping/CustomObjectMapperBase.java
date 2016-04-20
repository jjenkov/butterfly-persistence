package com.jenkov.db.impl.mapping;

import com.jenkov.db.itf.mapping.ICustomObjectMapper;
import com.jenkov.db.itf.mapping.IObjectMapping;
import com.jenkov.db.itf.PersistenceException;

/**
 * An abstract base class for easy implementation of custom object mappers. Extend
 * this class and override the methods you need.
 *
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */


///CLOVER:OFF
public abstract class CustomObjectMapperBase implements ICustomObjectMapper{
    public IObjectMapping getObjectMapping(Object objectMappingKey) throws PersistenceException {
        return null;
    }

    public String getTableName(Object objectMappingKey) throws PersistenceException {
        return null;
    }

    public void modify(Object objectMappingKey, IObjectMapping mapping) throws PersistenceException {
    }
}
///CLOVER:ON