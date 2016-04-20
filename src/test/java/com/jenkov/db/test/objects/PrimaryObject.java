package com.jenkov.db.test.objects;


import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * @author Jakob Jenkov - Copyright 2004-2006 Jenkov Development
 */
public class PrimaryObject {

    protected long   id   = -1;
    protected String text = null;
    protected Set relatedObjects = new LinkedHashSet();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addRelated(RelatedObject object){
        this.relatedObjects.add(object);
    }

    public Set getRelatedObjects() {
        return relatedObjects;
    }
}
