/**
 * @author Jakob Jenkov,  Jenkov Development
 */
package com.jenkov.db.impl.mapping.method;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * A subclass of <code>GetterMapping</code> capable of inserting a <code>BigDecimal</code> into
 * a <code>PreparedStatement</code>.
 *
 * @author Jakob Jenkov, Jenkov Development
 */

public class BigDecimalGetterMapping extends GetterMapping{

    protected void insertObjectDo(Object value, PreparedStatement statement, int index) throws SQLException {
        statement.setBigDecimal(index, (BigDecimal) value);
    }
}
