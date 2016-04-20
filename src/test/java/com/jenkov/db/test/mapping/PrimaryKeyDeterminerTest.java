package com.jenkov.db.test.mapping;

import junit.framework.TestCase;
import com.jenkov.db.itf.mapping.IDbPrimaryKeyDeterminer;
import com.jenkov.db.itf.mapping.IKey;
import com.jenkov.db.impl.mapping.DbPrimaryKeyDeterminer;
import com.jenkov.db.test.Environment;
import com.jenkov.db.util.JdbcUtil;

import java.sql.Connection;
import java.util.Iterator;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class PrimaryKeyDeterminerTest extends TestCase{

    IDbPrimaryKeyDeterminer pkdeterminer = null;
    Connection              connection   = null;

    protected void setUp() throws Exception {
        this.pkdeterminer = new DbPrimaryKeyDeterminer();
        this.connection   = Environment.getConnection();

    }

    protected void tearDown() throws Exception {
        this.pkdeterminer = null;
        JdbcUtil.closeIgnore(connection);
    }

    public void testSingleColumnKey() throws Exception{
        IKey key = null;
        if(Environment.isPostgreSQL()){
            key = this.pkdeterminer.getPrimaryKeyMapping("PERSISTENT_OBJECT".toLowerCase(),null , this.connection);
        } else {
            key = this.pkdeterminer.getPrimaryKeyMapping("PERSISTENT_OBJECT",null , this.connection);
        }


        assertEquals("1 column",1 ,key.getColumns().size());
        assertEquals("should be id", "id" ,key.getColumn().toLowerCase());
    }

    public void testCompoundKey() throws Exception{
        IKey key = null;
        if(Environment.isPostgreSQL()){
            key = this.pkdeterminer.getPrimaryKeyMapping("COMPOUND_PK_OBJECTS".toLowerCase(),null , this.connection);
        } else {
            key = this.pkdeterminer.getPrimaryKeyMapping("COMPOUND_PK_OBJECTS",null , this.connection);
        }

        Iterator iterator = key.getColumns().iterator();
        assertEquals("2 column"     ,     2 , key.getColumns().size());
        assertEquals("should be id" ,   "id", ((String) iterator.next()).toLowerCase());
        assertEquals("should be id2",  "id2", ((String) iterator.next()).toLowerCase());
    }

}
