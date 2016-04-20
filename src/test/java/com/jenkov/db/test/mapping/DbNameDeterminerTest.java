/**
 * User: Administrator
 */
package com.jenkov.db.test.mapping;

import junit.framework.TestCase;
import com.jenkov.db.impl.mapping.DbNameGuesser;
import com.jenkov.db.impl.mapping.DbNameDeterminer;
import com.jenkov.db.itf.PersistenceException;
import com.jenkov.db.test.objects.PersistentObject;
import com.jenkov.db.test.objects.AmbiguousTableName;
import com.jenkov.db.test.objects.NoTableName;
import com.jenkov.db.test.Environment;

import java.sql.Connection;
import java.util.Collection;
import java.lang.reflect.Method;

public class DbNameDeterminerTest extends TestCase{

    protected DbNameGuesser    nameGuesser    = new DbNameGuesser();
    protected DbNameDeterminer nameDeterminerOld = new DbNameDeterminer();
    protected Connection connection = null;

    public DbNameDeterminerTest(String test){
        super(test);
    }


    public void setUp() throws Exception{
        this.connection = Environment.getConnection();
    }

    public void tearDown() throws Exception{
        this.connection.close();
    }



    public void testDetermineTableName() throws Exception{
        Collection names = this.nameGuesser.getPossibleTableNames(PersistentObject.class);
        String name = this.nameDeterminerOld.determineTableName(names, this.connection);
        assertEquals("wrong name determined", "persistent_object", name.toLowerCase());

        names  = this.nameGuesser.getPossibleTableNames(AmbiguousTableName.class);
        try{
            name   = this.nameDeterminerOld.determineTableName(names, "db", this.connection);
            fail("There should be more than one matching table name");
        } catch (PersistenceException e){
            //ignore, an exception is epected.
        }

        names = this.nameGuesser.getPossibleTableNames(NoTableName.class);
        try {
            name = this.nameDeterminerOld.determineTableName(names, this.connection );
            fail("There should be no table matching");
        } catch (PersistenceException e) {
            // ignore, an exception is expected.
        }
    }

    public void testDetermineFieldName() throws Exception{
//        String tableName = "persistent_object";
        String tableName = "persistent_object".toUpperCase();
        if(Environment.isPostgreSQL()){
            tableName = tableName.toLowerCase();
        }


        Method method = PersistentObject.class.getMethod("getObjectValue", new Class[] {});
        Collection names = this.nameGuesser.getPossibleColumnNames(method);

        String name = this.nameDeterminerOld.determineColumnName(names, tableName, this.connection);

        assertEquals("wrong name determined.", "objectvalue", name.toLowerCase());
    }

}
