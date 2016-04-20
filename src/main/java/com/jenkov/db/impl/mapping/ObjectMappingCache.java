package com.jenkov.db.impl.mapping;

import com.jenkov.db.itf.mapping.IObjectMappingCache;
import com.jenkov.db.itf.mapping.IObjectMapping;

import java.util.*;

public class ObjectMappingCache implements IObjectMappingCache{

    protected Map objectMappings = new HashMap();



    public boolean containsObjectMapping(Object mappingKey){
        return this.objectMappings.containsKey(mappingKey);
    }

    public IObjectMapping getObjectMapping(Object mappingKey) {
        return (IObjectMapping) this.objectMappings.get(mappingKey);
    }

    public void storeObjectMapping(Object mappingKey, IObjectMapping mapping) {
        this.objectMappings.put(mappingKey, mapping);
    }

    public void removeObjectMapping(Object mappingKey) {
        this.objectMappings.remove(mappingKey);
    }

    public void clear() {
        this.objectMappings.clear();
    }

    public int size() {
        return this.objectMappings.size();
    }

}
