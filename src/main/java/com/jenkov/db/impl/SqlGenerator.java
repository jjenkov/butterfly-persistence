package com.jenkov.db.impl;

import com.jenkov.db.itf.mapping.IObjectMapping;
import com.jenkov.db.itf.mapping.IMethodMapping;
import com.jenkov.db.itf.mapping.IGetterMapping;
import com.jenkov.db.itf.ISqlGenerator;
import com.jenkov.db.itf.PersistenceException;

import java.util.Iterator;

/**
 * @author Jakob Jenkov,  Jenkov Development
 */
public class SqlGenerator implements ISqlGenerator{

    public String generateReadByPrimaryKeyStatement(IObjectMapping mapping) throws PersistenceException {
        validateObjectMapping(mapping);
        StringBuffer buffer = new StringBuffer();
        buffer.append("select ");

        Iterator iterator = mapping.getSetterMappings().iterator();
        while(iterator.hasNext()){
            IMethodMapping fieldMapping = (IMethodMapping) iterator.next();
            if(fieldMapping.isTableMapped()){
                if(startsWithNumber(fieldMapping.getColumnName())){
                    buffer.append("\"");
                    buffer.append(fieldMapping.getColumnName());
                    buffer.append("\"");
                } else {
                    buffer.append(fieldMapping.getColumnName());
                }
                if(iterator.hasNext()){
                    buffer.append(", ");
                }
            }
        }
        if(buffer.indexOf(", ", buffer.length()-2) > -1){
            buffer.delete(buffer.length()-2, buffer.length());
        }

        buffer.append(" from ");
        buffer.append(mapping.getTableName());
        buffer.append(" where ");
        appendPrimaryKey(buffer, mapping);
        return buffer.toString();
    }

    private boolean startsWithNumber(String columnName) {
        char firstLetter = columnName.charAt(0);
        if(firstLetter == '0' ||
           firstLetter == '1' ||
           firstLetter == '2' ||
           firstLetter == '3' ||
           firstLetter == '4' ||
           firstLetter == '5' ||
           firstLetter == '6' ||
           firstLetter == '7' ||
           firstLetter == '8' ||
           firstLetter == '9') return true;

        return false;
    }

    private void appendPrimaryKey(StringBuffer buffer, IObjectMapping mapping) {
        Iterator iterator = mapping.getPrimaryKey().getColumns().iterator();
        while(iterator.hasNext()){
            buffer.append((String) iterator.next());
            buffer.append(" = ?");
            if(iterator.hasNext()){
                buffer.append(" and ");
            }
        }
    }

    public String generateReadListByPrimaryKeysStatement(IObjectMapping mapping, int primaryKeyCount) throws PersistenceException {
        if(primaryKeyCount <= 0){
            throw new PersistenceException("The primary key count was " + primaryKeyCount
                    + ". For each primary key a ?-mark will be inserted into the "
                    + "\"where [primaryKeyColumn] in(?,..)\" part of the SQL statement. "
                    + "Therefore a sensible SQL statement "
                    + "cannot be generated unless the primary key count is at least 1 ");
        }
        validateObjectMapping(mapping);

        StringBuffer buffer = new StringBuffer();
        buffer.append("select ");

        Iterator iterator = mapping.getSetterMappings().iterator();
        while(iterator.hasNext()){
            IMethodMapping fieldMapping = (IMethodMapping) iterator.next();
            if(fieldMapping.isTableMapped()){
                buffer.append(fieldMapping.getColumnName());
                if(iterator.hasNext()){
                    buffer.append(", ");
                }
            }
        }

        buffer.append(" from ");
        buffer.append(mapping.getTableName());
        buffer.append(" where ");
        insertPrimaryKeys(buffer, mapping, primaryKeyCount);
        return buffer.toString();
    }

    private void insertPrimaryKeys(StringBuffer buffer, IObjectMapping mapping, int primaryKeyCount) {
        for(int i=0; i<primaryKeyCount; i++){
            buffer.append("(");
            Iterator iterator = mapping.getPrimaryKey().getColumns().iterator();
            while(iterator.hasNext()){
                String column = (String) iterator.next();
                buffer.append(column);
                buffer.append(" = ?");
                if(iterator.hasNext()){
                    buffer.append(" and ");
                }
            }
            buffer.append(")");
            if(i < primaryKeyCount-1){
                buffer.append(" or ");
            }
        }
    }

    public String generateInsertStatement(IObjectMapping mapping)  throws PersistenceException{
        validateObjectMapping(mapping);
        StringBuffer buffer = new StringBuffer();
        StringBuffer valueBuffer = new StringBuffer();

        buffer.append("insert into ");
        buffer.append(mapping.getTableName());
        buffer.append(" (");

        boolean isFirstColumn = true;

        Iterator iterator = mapping.getGetterMappings().iterator();
        while(iterator.hasNext()){
            IGetterMapping fieldMapping = (IGetterMapping) iterator.next();
            if(fieldMapping.isTableMapped() && !fieldMapping.isAutoGenerated()){
                if(!isFirstColumn){
                    buffer.append(", ");
                    valueBuffer.append(", ");
                }
                buffer.append(fieldMapping.getColumnName());
                valueBuffer.append("?");
                isFirstColumn = false;
            }

        }

        buffer.append(") values (");
        buffer.append(valueBuffer.toString());
        buffer.append(')');

        return buffer.toString();
    }

    public String generateUpdateStatement(IObjectMapping mapping) throws PersistenceException {
        validateObjectMapping(mapping);
        validatePrimaryKey(mapping);
        StringBuffer buffer = new StringBuffer();
        buffer.append("update ");
        buffer.append(mapping.getTableName());
        buffer.append(" set ");

        boolean isFirstColumn = true;

        Iterator iterator = mapping.getGetterMappings().iterator();
        while(iterator.hasNext()){
            IGetterMapping fieldMapping = (IGetterMapping) iterator.next();
            if(fieldMapping.isTableMapped() && !fieldMapping.isAutoGenerated()){
                if(!isFirstColumn){
                    buffer.append(", ");
                }
                buffer.append(fieldMapping.getColumnName());
                buffer.append(" = ?");
                isFirstColumn = false;
            }
        }

        buffer.append(" where ");
        appendPrimaryKey(buffer, mapping);

        return buffer.toString();
    }

    public String generateDeleteStatement(IObjectMapping mapping) throws PersistenceException {
        validateObjectMapping(mapping);
        validatePrimaryKey(mapping);

        StringBuffer buffer = new StringBuffer();
        buffer.append("delete from ");
        buffer.append(mapping.getTableName());
        buffer.append(" where ");
        appendPrimaryKey(buffer, mapping);

        return buffer.toString();

    }


    protected void validateObjectMapping(IObjectMapping mapping) throws PersistenceException{
        if(mapping.getTableName() == null) {
            throw new PersistenceException("The object mapping contained no table name");
        }
        if(mapping.getObjectClass() == null) {
            throw new PersistenceException("The object mapping contained no class");
        }
        if(mapping.getGetterMappings().size() == 0){
            throw new PersistenceException("The object mapping contained no getter method mappings");
        }
    }

    protected void validatePrimaryKey(IObjectMapping mapping) throws PersistenceException{
        if(mapping.getPrimaryKey().getColumns().size() == 0){
            throw new PersistenceException("No primary key columns for object mapping");
        }
        Iterator iterator = mapping.getPrimaryKey().getColumns().iterator();
        while(iterator.hasNext()){
            String column = (String) iterator.next();
            if(mapping.getGetterMapping(column) == null){
                throw new PersistenceException("No getter mapping for primary key column " + column);
            }
        }
    }
}
