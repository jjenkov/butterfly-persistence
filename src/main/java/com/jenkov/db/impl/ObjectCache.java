package com.jenkov.db.impl;

import com.jenkov.db.itf.mapping.IKeyValue;

import java.util.Map;
import java.util.HashMap;

/**
 *
 * Copyright Jenkov Aps
 */
public class ObjectCache {

    protected Map<Object, Map<IKeyValue, Object>> map = new HashMap<Object, Map<IKeyValue, Object>>();


    public synchronized Object get(Object objectMappingKey, IKeyValue pkKeyValue){
        Map<IKeyValue, Object> objectMap = map.get(objectMappingKey);
        if(objectMap != null){
            return objectMap.get(pkKeyValue);
        }
        return null;
    }

    public synchronized void store(Object objectMappingKey, IKeyValue pkKeyValue, Object object){
        Map<IKeyValue, Object> objectMap = new HashMap<IKeyValue, Object>();
        objectMap.put(pkKeyValue, object);
        this.map.put(objectMappingKey, objectMap);
    }

    public synchronized void clear(){
        for(Map objectMap : this.map.values()){
            objectMap.clear();
        }
        this.map.clear();
    }
}
