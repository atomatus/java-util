package com.atomatus.connection.http;

import com.atomatus.connection.http.exception.URLConnectionException;

import java.io.Closeable;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;

abstract class ResponseParameter implements Closeable {

    interface Action<I> {
        void action(I i) throws URLConnectionException;
    }

    interface Function<I, O> {
        O apply(I i) throws URLConnectionException;
    }

    private static final int DEFAULT_BUFFER_LENGTH = 1024;

    protected HttpURLConnection con;
    protected Function<HttpURLConnection, InputStream> inputStreamFun, errorStreamFun;
    protected Function<HttpConnection.StatusCode, Boolean> successResponseFun;
    protected Action<HttpURLConnection> finallyAction;
    protected Charset charset;
    protected int bufferLength;
    private boolean isClosed;

    protected ResponseParameter(ResponseParameter other) {
        this.con = other.con;
        this.inputStreamFun = other.inputStreamFun;
        this.errorStreamFun = other.errorStreamFun;
        this.successResponseFun = other.successResponseFun;
        this.finallyAction = other.finallyAction;
        this.charset = other.charset;
        this.bufferLength = other.bufferLength;
        this.isClosed = other.isClosed;
    }

    protected ResponseParameter() {
        this.charset = Charset.defaultCharset();
        this.bufferLength = DEFAULT_BUFFER_LENGTH;
        this.successResponseFun = this::acceptAll2xx;
    }

    private Boolean acceptAll2xx(HttpConnection.StatusCode statusCode) {
        int code = statusCode.getCode();
        return code >= 200 && code <= 299;
    }

    protected void onClose() { }

    protected HttpURLConnection requireConnection(){
        if(con == null) {
            throw new NullPointerException("HttpURLConnection was disposed or not set!");
        }
        return con;
    }

    protected final void requireNonClosed(){
        if(isClosed) {
            throw new UnsupportedOperationException("Object was closed and disposed!");
        }
    }

    @Override
    public final void close()  {
        this.requireNonClosed();
        try {
            this.onClose();
        } finally {
            this.con = null;
            this.inputStreamFun = null;
            this.errorStreamFun = null;
            this.successResponseFun = null;
            this.finallyAction = null;
            this.charset = null;
            this.isClosed = true;
        }
    }
}
