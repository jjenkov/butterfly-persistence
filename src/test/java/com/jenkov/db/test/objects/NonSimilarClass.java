package com.jenkov.db.test.objects;

/**
 * Created by IntelliJ IDEA.
 * User: Jakob Jenkov
 * Date: 10-Jun-2004
 * Time: 00:54:21
 * To change this template use File | Settings | File Templates.
 */
public class NonSimilarClass {

    private long theId = -1;
    private String firstName = null;

    public long getTheId() {
        return theId;
    }

    public void setTheId(long theId) {
        this.theId = theId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
