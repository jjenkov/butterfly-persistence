package com.jenkov.db.test.objects;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class CompoundPkObject {

    protected long id  = 0;
    protected long id2 = 0;

    protected String name = null;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId2() {
        return id2;
    }

    public void setId2(long id2) {
        this.id2 = id2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
