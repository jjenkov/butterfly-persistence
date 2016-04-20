package com.jenkov.db.impl.init;

/**

 */
public class DbInfo {

    protected String name  = null;
    protected String value = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getValueAsInt(){
        return Integer.parseInt(getValue());
    }
}
