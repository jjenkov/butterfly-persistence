/**
 * @author Jakob Jenkov,  Jenkov Development
 */
package com.jenkov.db.impl.mapping.method;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A subclass of <code>GetterMapping</code> capable of inserting a <code>Boolean</code> into
 * a <code>PreparedStatement</code>.
 *
 * @author Jakob Jenkov, Jenkov Development
 */
public class BooleanGetterMapping extends GetterMapping{

 
    protected void insertObjectDo(Object value, PreparedStatement statement, int index) throws SQLException {
        if(value != null){
            if(getColumnType() == java.sql.Types.BOOLEAN || getColumnType() == java.sql.Types.BIT){
                statement.setBoolean(index, ((Boolean) value).booleanValue());
            } else if(isNumberType()){
                statement.setInt(index, ((Boolean) value).booleanValue() ? 1 : 0);
            }
        } else {
            statement.setNull(index, getColumnType());
        }
    }
}
