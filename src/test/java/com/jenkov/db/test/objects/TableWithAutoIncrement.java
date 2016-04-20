package com.jenkov.db.test.objects;

import com.jenkov.db.itf.mapping.AGetterMapping;

/**

 */
public class TableWithAutoIncrement {
    protected long id = -1;

    @AGetterMapping(databaseGenerated = true)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
