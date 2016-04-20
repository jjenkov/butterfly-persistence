package com.jenkov.db.test.mapping.method;

import java.sql.Clob;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class ClobMock implements Clob{

    public long length() throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void truncate(long len) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public InputStream getAsciiStream() throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public OutputStream setAsciiStream(long pos) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Reader getCharacterStream() throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Writer setCharacterStream(long pos) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getSubString(long pos, int length) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int setString(long pos, String str) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int setString(long pos, String str, int offset, int len) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public long position(String searchstr, long start) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public long position(Clob searchstr, long start) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void free() throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Reader getCharacterStream(long l, long l1) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
