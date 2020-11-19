package com.atomatus.connection.http;

import com.atomatus.connection.http.exception.URLConnectionException;
import com.atomatus.util.security.KeyGenerator;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class HttpConnectionTest extends TestCase {

    public void testGetContent() {
        try(Response resp = new HttpConnection()
                .getContent("https://httpbin.org/get")) {
            assertTrue(resp.isSuccess());
        } catch (URLConnectionException e) {
            throw new AssertionFailedError(e.getMessage());
        }
    }

    public void testPostContent() {
        String key;
        try(Response resp = new HttpConnection()
                .useBasicAuth()
                .setCredentials("test", "123456")
                .postContent("https://httpbin.org/post",
                        Parameter.buildBody("param0", key = KeyGenerator.generateRandomKeyHex(10)))) {
            assertTrue(resp.isSuccess());
            String json = resp.getContent();
            assertTrue(json.contains(key));
        } catch (URLConnectionException e) {
            throw new AssertionFailedError(e.getMessage());
        }
    }
}