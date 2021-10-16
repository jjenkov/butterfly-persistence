package com.jenkov.db.test.mapping;

import com.jenkov.db.itf.mapping.IDbPrimaryKeyDeterminer;
import com.jenkov.db.itf.mapping.IKey;
import com.jenkov.db.impl.mapping.DbPrimaryKeyDeterminer;
import com.jenkov.db.test.Environment;
import com.jenkov.db.util.JdbcUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class PrimaryKeyDeterminerTest {

    IDbPrimaryKeyDeterminer pkdeterminer = null;
    Connection              connection   = null;

    @BeforeEach
    protected void setUp() throws Exception {
        this.pkdeterminer = new DbPrimaryKeyDeterminer();
        this.connection   = Environment.getConnection();

    }

    @AfterEach
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


        assertEquals(1 ,key.getColumns().size(), "1 column");
        assertEquals("id" ,key.getColumn().toLowerCase(), "1 column");
    }

    @Test
    public void testCompoundKey() throws Exception{
        IKey key = null;
        if(Environment.isPostgreSQL()){
            key = this.pkdeterminer.getPrimaryKeyMapping("COMPOUND_PK_OBJECTS".toLowerCase(),null , this.connection);
        } else {
            key = this.pkdeterminer.getPrimaryKeyMapping("COMPOUND_PK_OBJECTS",null , this.connection);
        }

        Iterator iterator = key.getColumns().iterator();
        assertEquals(2 , key.getColumns().size(), "2 column");
        assertEquals("id", ((String) iterator.next()).toLowerCase(), "should be id");
        assertEquals("id2", ((String) iterator.next()).toLowerCase(), "should be id2");
    }

}
