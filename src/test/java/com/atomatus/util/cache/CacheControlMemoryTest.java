package com.atomatus.util.cache;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CacheControlMemoryTest extends TestCase {

    private CacheControl cache;
    private int maxAgeInSec;

    @Override
    protected void setUp() {
        cache = CacheControl
                .memory()
                .maxAge(maxAgeInSec = 2, TimeUnit.SECONDS);
    }

    @Override
    protected void tearDown() {
        cache.clear();
        cache = null;
    }

    public void testSingleton() {
        assertEquals(cache, CacheControl.memory());
    }

    public void testCacheDataInStored() {

        try {
            URL url = new URL("https://test.com");
            String mock = "{\"menu\":{\"id\":\"file\",\"value\":\"File\",\"popup\":{\"menuitem\":[{\"value\":\"New\",\"onclick\":\"CreateNewDoc()\"},{\"value\":\"Open\",\"onclick\":\"OpenDoc()\"},{\"value\":\"Close\",\"onclick\":\"CloseDoc()\"}]}}}";

            CacheData cd = new CacheData.Builder()
                    .id(url)
                    .text(mock)
                    .build();

            //quando memory cache, após add mantem dado na memoria.
            cache.add(cd);
            assertTrue(cd.isMemory());//true
            assertFalse(cd.isStored());//false
            assertEquals(mock, cd.text());

            CacheData cdAux = cache.get(cd.hash());

            assertEquals(cd, cdAux);//compara com a ref added.
            assertTrue(cdAux.isMemory());//true
            assertFalse(cdAux.isStored());//false
            assertEquals(mock, cdAux.text());

            assertTrue(cache.remove(cdAux));
        } catch (Exception e) {
            throw new AssertionFailedError(e.getMessage());
        }
    }

    public void testCacheDataExpired() {
        try {
            UUID id = UUID.randomUUID();
            CacheData cd = new CacheData.Builder()
                    .id(id)
                    .text("teste")
                    .build();

            cache.add(cd);
            Thread.sleep(TimeUnit.SECONDS.toMillis(maxAgeInSec));
            assertTrue(cd.isExpired());
            assertNotSame(cd, cache.get(id));
        } catch (Exception e){
            throw new AssertionFailedError(e.getMessage());
        }
    }
}