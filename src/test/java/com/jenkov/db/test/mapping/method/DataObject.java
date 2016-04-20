package com.jenkov.db.test.mapping.method;

import com.jenkov.db.impl.mapping.method.AsciiStream;
import com.jenkov.db.impl.mapping.method.CharacterStream;
import com.jenkov.db.impl.mapping.method.BinaryStream;

import java.math.BigDecimal;
import java.sql.*;
import java.net.URL;

/**
 * @author Jakob Jenkov - Copyright 2005 Jenkov Development
 */
public class DataObject {

    private byte[]             byteArray       = null;
    private AsciiStream        asciiStream     = null;
    private CharacterStream    characterStream = null;
    private BinaryStream       binaryStream    = null;

    private BigDecimal         bigDecimal      = null;
    private Blob               blob            = null;
    private boolean            booleanData     = false;
    private byte               byteData        = -1;
    private Clob               clob            = null;
    private double             doubleData      = -1;
    private float              floatData       = -1;
    private int                intData         = -1;
    private long                longData       = -1;
    private short              shortData       = -1;

    private java.sql.Date      sqlDate         = null;
    private java.sql.Time      time            = null;
    private java.sql.Timestamp timestamp       = null;

    private URL                url             = null;

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public AsciiStream getAsciiStream() {
        return asciiStream;
    }

    public void setAsciiStream(AsciiStream asciiStream) {
        this.asciiStream = asciiStream;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    public BinaryStream getBinaryStream() {
        return binaryStream;
    }

    public void setBinaryStream(BinaryStream binaryStream) {
        this.binaryStream = binaryStream;
    }

    public Blob getBlob() {
        return blob;
    }

    public void setBlob(Blob blob) {
        this.blob = blob;
    }

    public boolean isBoolean() {
        return booleanData;
    }

    public void setBoolean(boolean booleanData) {
        this.booleanData = booleanData;
    }

    public byte getByteData() {
        return byteData;
    }

    public void setByteData(byte byteData) {
        this.byteData = byteData;
    }

    public CharacterStream getCharacterStream() {
        return characterStream;
    }

    public void setCharacterStream(CharacterStream characterStream) {
        this.characterStream = characterStream;
    }

    public Clob getClob() {
        return clob;
    }

    public void setClob(Clob clob) {
        this.clob = clob;
    }


    public double getDouble() {
        return doubleData;
    }

    public void setDouble(double doubleData) {
        this.doubleData = doubleData;
    }

    public double getFloatData() {
        return floatData;
    }

    public void setFloatData(float floatData) {
        this.floatData = floatData;
    }

    public int getIntData() {
        return intData;
    }

    public void setIntData(int intData) {
        this.intData = intData;
    }

    public long getLongData() {
        return longData;
    }

    public void setLongData(long longData) {
        this.longData = longData;
    }

    public short getShortData() {
        return shortData;
    }

    public void setShort(short data){
        this.shortData = data;
    }

    public Date getSqlDate() {
        return sqlDate;
    }

    public void setSqlDate(Date sqlDate) {
        this.sqlDate = sqlDate;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
