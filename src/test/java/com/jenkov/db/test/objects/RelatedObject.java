package com.jenkov.db.test.objects;

import com.jenkov.db.itf.mapping.AClassMapping;
import com.jenkov.db.itf.mapping.AGetterMapping;
import com.jenkov.db.itf.mapping.ASetterMapping;

/**
 * @author Jakob Jenkov - Copyright 2004-2006 Jenkov Development
 */
@AClassMapping(tableName = "RELATEDOBJECTS")
public class RelatedObject {

    protected long   id        = -1;
    protected String aValue    = null;
    protected long   primaryId = -1;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @AGetterMapping(columnName="A_VALUE")
    public String getAValue() {
        return aValue;
    }

    @ASetterMapping(columnName="A_VALUE")
    public void setAValue(String aValue) {
        this.aValue = aValue;
    }

    public long getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(long primaryId) {
        this.primaryId = primaryId;
    }
}
