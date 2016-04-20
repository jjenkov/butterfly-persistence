/**
 * @author Jakob Jenkov,  Jenkov Development
 */
package com.jenkov.db.impl.mapping.method;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A subclass of <code>GetterMapping</code> capable of inserting a <code>Byte</code> into
 * a <code>PreparedStatement</code>.
 *
 * @author Jakob Jenkov, Jenkov Development
 */
public class ByteGetterMapping extends GetterMapping{

    protected void insertObjectDo(Object value, PreparedStatement statement, int index) throws SQLException {
        if(value != null){
            statement.setByte(index, ((Byte) value).byteValue());
        } else {
            statement.setNull(index, java.sql.Types.TINYINT);
        }
    }
}
