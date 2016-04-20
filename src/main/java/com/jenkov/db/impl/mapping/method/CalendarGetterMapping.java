package com.jenkov.db.impl.mapping.method;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * @author Jakob Jenkov - Copyright 2004-2006 Jenkov Development
 */
public class CalendarGetterMapping extends GetterMapping{

    protected void insertObjectDo(Object value, PreparedStatement statement, int index) throws SQLException {
        if(value != null){
            statement.setTimestamp(index, new java.sql.Timestamp( ((Calendar) value).getTimeInMillis()));
        } else {
            statement.setTimestamp(index, null);
        }
    }

}
