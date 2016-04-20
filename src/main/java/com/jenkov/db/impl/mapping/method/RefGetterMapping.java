/**
 * @author Jakob Jenkov,  Jenkov Development
 */
package com.jenkov.db.impl.mapping.method;

import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.SQLException;

/**
 * A subclass of <code>GetterMapping</code> capable of inserting a <code>Ref</code> into
 * a <code>PreparedStatement</code>.
 *
 * @author Jakob Jenkov, Jenkov Development
 */
public class RefGetterMapping extends GetterMapping{

    protected void insertObjectDo(Object value, PreparedStatement statement, int index) throws SQLException {
        statement.setRef(index, (Ref) value);
    }
}
