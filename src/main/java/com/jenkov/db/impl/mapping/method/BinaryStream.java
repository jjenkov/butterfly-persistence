package com.jenkov.db.impl.mapping.method;

import java.io.InputStream;

/**
 * @author Jakob Jenkov,  Jenkov Development
 */
public class BinaryStream {
    protected InputStream   inputStream = null;
    protected int           length = 0;

    public BinaryStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public BinaryStream(InputStream inputStream, int length) {
        this.inputStream = inputStream;
        this.length = length;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof BinaryStream)) return false;

        BinaryStream otherStream = (BinaryStream) obj;
        if(getLength() != otherStream.getLength()) return false;

        return(getInputStream().equals(otherStream.getInputStream()));
    }
}
