/**
 * User: Administrator
 */
package com.jenkov.db.impl.mapping.method;

import com.jenkov.db.itf.mapping.IGetterMapping;
import com.jenkov.db.itf.mapping.IMethodMapping;
import com.jenkov.db.itf.mapping.ISetterMapping;
import com.jenkov.db.util.ClassUtil;

import java.lang.reflect.Method;

public class MethodMapping implements IMethodMapping, Comparable {

    //protected IObjectMapping objectMapping  = null;
    protected String  columnName   = null;
    protected int     columnType   = java.sql.Types.NULL;
    protected Method  objectMethod = null;

    protected boolean isTableMapped = false;

    //protected String         referencesTable = null;

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String fieldName) {
        this.columnName = fieldName;
    }

    public int getColumnType() {
        return columnType;
    }

    public void setColumnType(int columnType) {
        this.columnType = columnType;
    }

    protected boolean isNumberType(){
        return this.columnType == java.sql.Types.BIGINT ||
                this.columnType == java.sql.Types.DECIMAL ||
                this.columnType == java.sql.Types.DOUBLE ||
                this.columnType == java.sql.Types.FLOAT ||
                this.columnType == java.sql.Types.INTEGER ||
                this.columnType == java.sql.Types.NUMERIC ||
                this.columnType == java.sql.Types.REAL ||
                this.columnType == java.sql.Types.SMALLINT ||
                this.columnType == java.sql.Types.TINYINT;
    }

    protected boolean isStringType(){
        return this.columnType == java.sql.Types.CHAR ||
                this.columnType == java.sql.Types.CLOB ||
                this.columnType == java.sql.Types.LONGVARCHAR ||
                this.columnType == java.sql.Types.VARCHAR;
    }


    public Method getObjectMethod() {
        return this.objectMethod;
    }

    public void setObjectMethod(Method member) {
        this.objectMethod = member;
    }

    /*
    public boolean isForeignKey() {
        return this.isForeignKey;
    }

    public void setForeignKey(boolean isForeignKey) {
        this.isForeignKey = isForeignKey;
    }*/

    public boolean isTableMapped() {
        return this.isTableMapped;
    }

    public void setTableMapped(boolean isTableMapped){
        this.isTableMapped = isTableMapped;
    }


/*
    public String referencesTable() {
        return this.referencesTable;
    }
*/






   public boolean equals(Object o){
        if(! (o instanceof IMethodMapping)) return false;
        IMethodMapping fieldMapping = (IMethodMapping) o;

        if(!ClassUtil.areEqual(getObjectMethod()  , fieldMapping.getObjectMethod())) return false;
        if(!ClassUtil.areEqual(getColumnName()   , fieldMapping.getColumnName())) return false;

        //if(isForeignKey() != fieldMapping.isForeignKey()) return false;
        if(isTableMapped() != fieldMapping.isTableMapped()) return false;

        return true;
    }

    public int hashCode(){
        if(getColumnName() != null)  return getColumnName().hashCode();
        if(getObjectMethod() != null) return getObjectMethod().getName().hashCode();
        return super.hashCode();
    }


    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(getObjectMethod().getName());
        if(this instanceof IGetterMapping){
            buffer.append("  -->  ");
        } else if(this instanceof ISetterMapping){
            buffer.append("  <--  ");
        } else {
            buffer.append("  <-?->  ");
        }
        if(getColumnName() != null){
            buffer.append(getColumnName());
        } else {
            buffer.append("[no method]");
        }

        buffer.append("     (");
        buffer.append(ClassUtil.classNameWithoutPackage(getClass()));
        buffer.append(')');
        return buffer.toString();
    }


    public int compareTo(Object o) {
        IMethodMapping fieldMapping = (IMethodMapping) o;

        if(this instanceof IGetterMapping && ! (fieldMapping instanceof IGetterMapping)) return -1;
        if(this instanceof ISetterMapping && ! (fieldMapping instanceof ISetterMapping)) return 1;

        if(isTableMapped() && !fieldMapping.isTableMapped()) return -1;
        if(!isTableMapped() && fieldMapping.isTableMapped()) return 1;

        int comparison = ClassUtil.compare(getObjectMethod(), fieldMapping.getObjectMethod());
        if(comparison != 0) return comparison;

        comparison = ClassUtil.compare(getColumnName(), fieldMapping.getColumnName());
        if(comparison != 0) return comparison;

        return 0;
    }
}
