/**
 * @author Jakob Jenkov,  Jenkov Development
 */
package com.jenkov.db.impl.mapping.method;

import java.io.InputStream;

public class AsciiStream {

    protected InputStream   inputStream = null;
    protected int           length = 0;

    public AsciiStream(InputStream inputStream){
        this.inputStream = inputStream;
    }

    public AsciiStream(InputStream inputStream, int length) {
        this.inputStream = inputStream;
        this.length = length;
    }

    public InputStream getInputStream(){
        return this.inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public int getLength(){
        return this.length;
    }

    public void setLength(int length){
        this.length = length;
    }

    public boolean equals(Object obj) {
        if (obj == null                  ) return false;
        if (!(obj instanceof AsciiStream)) return false;

        AsciiStream otherStream = (AsciiStream) obj;
        if(getLength() != otherStream.getLength()) return false;

        return getInputStream().equals(otherStream.getInputStream());
    }
}
