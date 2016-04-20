package com.jenkov.db.test;

import com.jenkov.db.itf.IReadFilter;
import com.jenkov.db.itf.PersistenceException;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 15-02-2004
 * Time: 18:07:59
 * To change this template use File | Settings | File Templates.
 */
public class AcceptEveryOtherFilter implements IReadFilter {
    protected boolean acceptNext = true;
    protected int     maxRecords = -1;
    protected int     recordsAccepted = 0;

    public AcceptEveryOtherFilter(int maxRecords){
        this.maxRecords = maxRecords;
    }

    public void init(ResultSet result) throws SQLException, PersistenceException {

    }

    public boolean accept(ResultSet result) throws SQLException, PersistenceException {
        boolean acceptThis = this.acceptNext;
        if(acceptThis){
            this.recordsAccepted++;
        }
        this.acceptNext = !this.acceptNext;
        return acceptThis;
    }

    public boolean acceptMore() {
        return this.maxRecords > recordsAccepted;
    }

    public void acceptedByAllFilters(boolean wasAcceptedByAllFilters) {

    }

    public void clear() {

    }

}
