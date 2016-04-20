/**
 * @author Jakob Jenkov,  Jenkov Development
 */
package com.jenkov.db.impl.mapping.method;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A subclass of the <code>SetterMapping</code> capable of reading
 * a <code>Object</code> from a <code>ResultSet</code>
 * instance and insert it into a target object, by calling the target
 * object matching setter method.
 *
 * @author Jakob Jenkov, Jenkov Development
 */

public class ObjectSetterMapping extends SetterMapping{


    protected void insertValueIntoObjectDo(Object target, ResultSet result)
    throws SQLException, InvocationTargetException, IllegalAccessException {
        getObjectMethod().invoke(target, new Object[]{ result.getObject(getColumnName())});
    }

    protected Object getValueFromResultSetDo(ResultSet result) throws SQLException {
        return result.getObject(getColumnName());
    }
}
