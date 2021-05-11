package com.atomatus.connection.http;

import com.atomatus.connection.http.exception.URLConnectionException;
import com.atomatus.util.Stopwatch;
import com.atomatus.util.security.KeyGenerator;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HttpConnectionTest extends TestCase {

    public void testGetContent() {
        try(Response resp = new HttpConnection()
                .getContent("https://httpbin.org/get")) {
            assertTrue(resp.isSuccess());
            assertTrue(resp.getContent().length() > 0);
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

    public void testGetContentCached() {
        long cacheMaxAge = 2L;
        int cacheId = UUID.randomUUID().hashCode();
        String url  = "https://httpbin.org/get";
        String content;

        //region request from server and add on cache
        Stopwatch s = Stopwatch.startNew();
        try(Response resp0 = new HttpConnection()
                .useCache()
                .setCacheMaxAge(cacheMaxAge, TimeUnit.SECONDS)
                .setCacheId(cacheId)
                .getContent(url)) {
            assertTrue(resp0.isSuccess());
            assertTrue((content = resp0.getContent()).length() > 0);
        } catch (URLConnectionException e) {
            throw new AssertionFailedError(e.getMessage());
        }
        s.stop();
        long firstRequestTime = s.getElapsedInMillis();
        //endregion

        //region request from cache
        s.restart();
        try(Response resp1 = new HttpConnection()
                .useCache()
                .setCacheId(cacheId)
                .getContent(url)) {
            assertTrue(resp1.isSuccess());
            assertEquals(content, resp1.getContent());
        } catch (URLConnectionException e) {
            throw new AssertionFailedError(e.getMessage());
        }
        s.stop();

        long secondRequestTime = s.getElapsedInMillis();
        assertTrue(firstRequestTime > secondRequestTime);
        //endregion

        //region request from server again, cache data expired
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(cacheMaxAge));
        } catch (InterruptedException e) {
            throw new AssertionFailedError(e.getMessage());
        }

        s.restart();
        try(Response resp2 = new HttpConnection()
                .useCache()
                .setCacheId(cacheId)
                .getContent(url)) {
            assertTrue(resp2.isSuccess());
            assertNotSame(content, resp2.getContent());
        } catch (URLConnectionException e) {
            throw new AssertionFailedError(e.getMessage());
        }
        s.stop();

        long thirdRequestTime = s.getElapsedInMillis();
        assertTrue(thirdRequestTime > secondRequestTime);
        //endregion

    }
}