/**
 * @author Jakob Jenkov,  Jenkov Development
 */
package com.jenkov.db.impl.mapping.method;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 * A subclass of <code>GetterMapping</code> capable of inserting a <code>java.util.Date</code> into
 * a <code>PreparedStatement</code>.
 *
 * @author Jakob Jenkov, Jenkov Development
 */
public class DateGetterMapping extends GetterMapping{

    protected void insertObjectDo(Object value, PreparedStatement statement, int index) throws SQLException {
        if(value != null){
            statement.setTimestamp(index, new java.sql.Timestamp( ((Date) value).getTime()));
        } else {
            statement.setTimestamp(index, null);
        }
    }
}
