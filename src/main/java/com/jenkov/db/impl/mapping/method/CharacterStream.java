package com.jenkov.db.impl.mapping.method;

import java.io.Reader;

/**
 * @author Jakob Jenkov,  Jenkov Development
 */
public class CharacterStream {
    protected Reader    reader = null;
    protected int       length = 0;

    public CharacterStream(Reader reader) {
        this.reader = reader;
    }

    public CharacterStream(Reader reader, int length) {
        this.reader = reader;
        this.length = length;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof CharacterStream)) return false;

        CharacterStream otherStream = (CharacterStream) obj;
        if(getLength() != otherStream.getLength()) return false;

        return getReader().equals(otherStream.getReader());
    }
}
