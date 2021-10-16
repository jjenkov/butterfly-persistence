package com.jenkov.db.test;

import com.jenkov.db.PersistenceManager;
import com.jenkov.db.itf.IDaos;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Jakob Jenkov - Copyright 2004-2006 Jenkov Development
 */
public class MapDaoTest {

    @Test
    public void testReadMap() throws Exception {
        PersistenceManager manager = new PersistenceManager(Environment.getDataSource());

        IDaos daos = daos = manager.createDaos();;
        try {
            daos.getJdbcDao().update("delete from persistent_object");
            daos.getJdbcDao().update("insert into persistent_object(id, name) values (33, 'aName')");

            Map map = daos.getMapDao().readMap("select * from persistent_object where id = 33");

            if(Environment.isHsqldb() || Environment.isH2() || Environment.isFirebird() || Environment.isDerby()){
                assertEquals(new Long(33), map.get("ID"));
                assertEquals("aName", map.get("NAME"));
            } else {
                assertEquals(new Long(33), map.get("id"));
                assertEquals("aName", map.get("name"));
            }

            map = daos.getMapDao().readMap("select * from persistent_object where id = ?", new Object[]{new Long(33)});

            if(Environment.isHsqldb() || Environment.isH2() || Environment.isFirebird() || Environment.isDerby() ){
                assertEquals(new Long(33), map.get("ID"));
                assertEquals("aName", map.get("NAME"));
            } else {
                assertEquals(new Long(33), map.get("id"));
                assertEquals("aName", map.get("name"));
            }
        } finally {
            daos.getJdbcDao().update("delete from persistent_object where id=33");
            assertNull(daos.getMapDao().readMap("select * from persistent_object where id=33"));
            daos.getConnection().close();
        }
    }

    public void testReadMapList() throws Exception {
        PersistenceManager manager = new PersistenceManager(Environment.getDataSource());
        IDaos daos = manager.createDaos();
        try {
            List mapList = daos.getMapDao().readMapList("select * from persistent_object order by id");
            assertEquals(0, mapList.size());

            daos.getJdbcDao().update("insert into persistent_object(id, name) values (33, 'aName')");
            daos.getJdbcDao().update("insert into persistent_object(id, name) values (34, 'aName2')");

            mapList = daos.getMapDao().readMapList("select * from persistent_object order by id");

            assertMapListCorrect(mapList);

            mapList = null;
            mapList = daos.getMapDao().readMapList("select * from persistent_object where id > ?",
                            new Object[]{new Long(1)});
            assertMapListCorrect(mapList);
        } finally {
            daos.getJdbcDao().update("delete from persistent_object");
            daos.getConnection().close();
        }
    }

    private void assertMapListCorrect(List mapList) {
        assertEquals(2, mapList.size());
        Map map1 = (Map) mapList.get(0);
        if(Environment.isHsqldb() || Environment.isH2() || Environment.isFirebird() || Environment.isDerby()){
            assertEquals(new Long(33), map1.get("ID"));
            assertEquals("aName"     , map1.get("NAME"));
        } else {
            assertEquals(new Long(33), map1.get("id"));
            assertEquals("aName"     , map1.get("name"));
        }

        Map map2 = (Map) mapList.get(1);
        if(Environment.isHsqldb() || Environment.isH2() || Environment.isFirebird() || Environment.isDerby()){
            assertEquals(new Long(34), map2.get("ID"));
            assertEquals("aName2"     , map2.get("NAME"));
        } else {
            assertEquals(new Long(34), map2.get("id"));
            assertEquals("aName2"     , map2.get("name"));
        }
    }
}
