package com.jenkov.db.test.mapping.method;

import java.sql.Blob;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class BlobMock implements Blob{
    public long length() throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void truncate(long len) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public byte[] getBytes(long pos, int length) throws SQLException {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int setBytes(long pos, byte[] bytes) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public long position(byte pattern[], long start) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public InputStream getBinaryStream() throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public OutputStream setBinaryStream(long pos) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public long position(Blob pattern, long start) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void free() throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public InputStream getBinaryStream(long l, long l1) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
