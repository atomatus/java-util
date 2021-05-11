package com.atomatus.connection.http;

import com.atomatus.connection.http.exception.URLConnectionException;
import com.atomatus.util.cache.CacheControl;

import java.io.Closeable;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

abstract class ResponseParameter implements Closeable {

    interface Action<I> {
        void action(I i) throws URLConnectionException;
    }

    interface FunctionIO<I, O> {
        O apply(I i) throws URLConnectionException;
    }

    interface Function<O> {
        O apply() throws URLConnectionException;
    }

    private static final int DEFAULT_BUFFER_LENGTH = 1024;

    /**
     * Origin request.
     */
    protected HttpURLConnection con;

    /**
     * Url to request.
     */
    protected URL url;

    /**
     * Origin request function.
     */
    protected FunctionIO<URL, HttpURLConnection> conFun;

    /**
     * Input stream function callback.
     */
    protected FunctionIO<HttpURLConnection, InputStream> inputStreamFun;

    /**
     * Error stream function callback.
     */
    protected FunctionIO<HttpURLConnection, InputStream> errorStreamFun;

    /**
     * Success response function callback.
     */
    protected FunctionIO<HttpConnection.StatusCode, Boolean> successResponseFun;

    /**
     * Function callback, attempt to get cache
     * control when enabled it, otherwise null.
     */
    protected Function<CacheControl> cacheFun;

    /**
     * Action executed on finally response read.
     */
    protected Action<HttpURLConnection> finallyAction;

    /**
     * Charset to parse response content to string.
     */
    protected Charset charset;

    /**
     * Content input stream.
     */
    protected InputStream stream;

    /**
     * Buffer lengh.
     */
    protected int bufferLength;

    /**
     * State of response parameter.
     */
    private boolean isClosed;

    protected ResponseParameter(ResponseParameter other) {
        this.con = other.con;
        this.url = other.url;
        this.conFun = other.conFun;
        this.inputStreamFun = other.inputStreamFun;
        this.errorStreamFun = other.errorStreamFun;
        this.successResponseFun = other.successResponseFun;
        this.cacheFun = other.cacheFun;
        this.finallyAction = other.finallyAction;
        this.charset = other.charset;
        this.bufferLength = other.bufferLength;
        this.stream = other.stream;
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

    /**
     * On close callback.
     */
    protected void onClose() { }

    /**
     * require connection non null
     * @return connection where the request as generated.
     * @throws NullPointerException throws when connection is null.
     * @throws URLConnectionException throws when is not possible open a connection.
     */
    protected HttpURLConnection requireConnection() throws URLConnectionException {
        if(con == null && conFun != null && url != null) {
            con = conFun.apply(url);
        }

        if(con == null) {
            throw new NullPointerException("HttpURLConnection was disposed or not set!");
        }

        return con;
    }

    /**
     * require connection non null
     * @return connection where the request as generated.
     * @throws NullPointerException throws when connection is null.
     */
    protected HttpURLConnection requireConnectionOrFunction(){
        if(con == null) {
            if(conFun != null) {
                if(url == null) {
                    throw new NullPointerException("Url not set to build connection!");
                }
            } else {
                throw new NullPointerException("HttpURLConnection was disposed or not set!");
            }
        }
        return con;
    }

    /**
     * Require current response parameter non closed.
     * @throws UnsupportedOperationException throws when object is disposed.
     */
    protected final void requireNonClosed(){
        if(isClosed) {
            throw new UnsupportedOperationException("Object was closed and disposed!");
        }
    }

    /**
     * Attempt to close.
     * @param c target
     */
    protected void tryClose(Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception ignored) { }
    }

    /**
     * Attempt to close.
     * @param con target
     */
    protected void tryClose(HttpURLConnection con) {
        try {
            if (con != null) {
                con.disconnect();
            }
        } catch (Exception ignored) { }
    }

    @Override
    public final void close()  {
        this.requireNonClosed();
        try {
            this.onClose();
        } finally {
            this.con = null;
            this.url = null;
            this.conFun = null;
            this.inputStreamFun = null;
            this.errorStreamFun = null;
            this.successResponseFun = null;
            this.cacheFun = null;
            this.finallyAction = null;
            this.charset = null;
            this.stream = null;
            this.isClosed = true;
        }
    }
}
