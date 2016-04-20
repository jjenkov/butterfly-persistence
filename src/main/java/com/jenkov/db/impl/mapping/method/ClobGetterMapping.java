/**
 * @author Jakob Jenkov,  Jenkov Development
 */
package com.jenkov.db.impl.mapping.method;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A subclass of <code>GetterMapping</code> capable of inserting a <code>Clob</code> into
 * a <code>PreparedStatement</code>.
 *
 * @author Jakob Jenkov, Jenkov Development
 */
public class ClobGetterMapping extends GetterMapping{

 
    protected void insertObjectDo(Object value, PreparedStatement statement, int index) throws SQLException {
        statement.setClob(index, (Clob) value);
    }
}
