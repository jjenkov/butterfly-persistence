package com.jenkov.db.test.objects;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class OverloadedSetters {

    protected int    age  = 0;
    protected String name = null;

    protected int    height = 0;
    protected long   width = 0;


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAge(String age){
        this.age = Integer.parseInt(age);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setName(Double name){
        this.name = name.toString();
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setHeight(long height) {
        this.height = (int) height;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(StringBuffer width){
        
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setWidth(short width) {
        this.width = width;
    }

}
