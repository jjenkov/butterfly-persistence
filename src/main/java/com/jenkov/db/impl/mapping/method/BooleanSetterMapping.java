/**
 * @author Jakob Jenkov,  Jenkov Development
 */
package com.jenkov.db.impl.mapping.method;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A subclass of the <code>SetterMapping</code> capable of reading
 * a <code>boolean</code> from a <code>ResultSet</code>
 * instance and insert it into a target object, by calling the target
 * object matching setter method.
 *
 * @author Jakob Jenkov, Jenkov Development
 */
public class BooleanSetterMapping extends SetterMapping{


    protected void insertValueIntoObjectDo(Object target, ResultSet result)
    throws SQLException, InvocationTargetException, IllegalAccessException {
        if(result.getObject(getColumnName()) != null){
            if(getColumnType() == java.sql.Types.BOOLEAN || getColumnType() == java.sql.Types.BIT){
                getObjectMethod().invoke(target, new Object[]{ new Boolean(result.getBoolean(getColumnName()))});
            } else if(isNumberType()){
                getObjectMethod().invoke(target, new Object[]{ new Boolean(result.getInt(getColumnName()) != 0)});
            }
        } 
    }

    protected Object getValueFromResultSetDo(ResultSet result) throws SQLException {
        return new Boolean(result.getBoolean(getColumnName()));
    }

}
