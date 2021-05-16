package com.atomatus.util.cache;

import junit.framework.TestCase;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class CacheControlStoredTest extends TestCase {

    private CacheControl cache;

    @Override
    protected void setUp() {
        cache = CacheControl
                .stored()
                .maxAge(20, TimeUnit.SECONDS);
    }

    @Override
    protected void tearDown() {
        cache.clear();
        cache = null;
    }

    public void testSingleton() {
        assertEquals(cache, CacheControl.stored());
    }

    public void testCacheDataInStored() {
        try {
            URL url = new URL("https://test.com");
            String mock = "{\"menu\":{\"id\":\"file\",\"value\":\"File\",\"popup\":{\"menuitem\":[{\"value\":\"New\",\"onclick\":\"CreateNewDoc()\"},{\"value\":\"Open\",\"onclick\":\"OpenDoc()\"},{\"value\":\"Close\",\"onclick\":\"CloseDoc()\"}]}}}";

            CacheData cd = new CacheData.Builder()
                    .id(url)
                    .text(mock)
                    .build();

            //nao armazenado em nenhum cache, sempre na memoria.
            assertTrue(cd.isMemory());//true
            assertFalse(cd.isStored());//false

            //quando stored cache, após add nao mantem dado na memoria.
            cache.add(cd);
            //porém, ao solicitar o dado o mesmo é recuperado.
            assertFalse(cd.isMemory());//false
            assertTrue(cd.isStored());//true
            assertEquals(mock, cd.text());//recupera dado armazenado

            CacheData cdAux = cache.get(url);
            assertEquals(cd, cdAux);//compara com a ref added.
            assertFalse(cdAux.isMemory());//false
            assertTrue(cdAux.isStored());//true
            assertEquals(mock, cdAux.text());//recupera dado armazenado

            assertTrue(cache.remove(cdAux));
        } catch (IOException e){
            throw new AssertionError(e.getMessage(), e);
        }
    }
}