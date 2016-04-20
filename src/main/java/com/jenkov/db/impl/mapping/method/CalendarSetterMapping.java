package com.jenkov.db.impl.mapping.method;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.GregorianCalendar;

/**
 * @author Jakob Jenkov - Copyright 2004-2006 Jenkov Development
 */
public class CalendarSetterMapping extends SetterMapping{

    protected void insertValueIntoObjectDo(Object target, ResultSet result)
    throws SQLException, InvocationTargetException, IllegalAccessException {
        Timestamp timestamp = result.getTimestamp(getColumnName());
        if(timestamp != null){
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(timestamp.getTime());
            getObjectMethod().invoke(target, new Object[]{calendar});
        } else {
            getObjectMethod().invoke(target, new Object[]{null});
        }
    }

    protected Object getValueFromResultSetDo(ResultSet result) throws SQLException {
        return result.getTimestamp(getColumnName());
    }


}
