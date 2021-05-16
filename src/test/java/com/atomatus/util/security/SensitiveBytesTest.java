package com.atomatus.util.security;

import com.atomatus.connection.http.HttpConnection;
import com.atomatus.connection.http.exception.URLConnectionException;
import com.atomatus.util.ArrayHelper;
import junit.framework.TestCase;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SensitiveBytesTest extends TestCase {

    public void testSecure() {
        boolean sameCipher = true;
        for(int i=0; i < 5; i++) {
            byte[] str = "This is a sensitive string!".getBytes();
            SensitiveBytes sb0 = SensitiveBytes.of(str);
            Assert.assertArrayEquals(str, sb0.readAll());

            SensitiveBytes sb1 = SensitiveBytes.of(str);
            Assert.assertArrayEquals(str, sb1.readAll());
            sameCipher = sameCipher & ArrayHelper.sequenceEquals(sb0.secure(), sb1.secure());
        }

        assertFalse(sameCipher);
    }

    public void testByteToBytes() {
        SensitiveBytes sb0 = new SensitiveBytes()
                .append((byte) 'A')
                .append((byte) 'B')
                .append((byte) 'C')
                .append((byte) 'D');

        byte[] res = sb0.readAll();
        Assert.assertArrayEquals("ABCD".getBytes(), res);
    }

    public void testMixedAppend() {
        SensitiveBytes sb0 = new SensitiveBytes()
                .append((byte) 'A')
                .append((byte) 'B')
                .append((byte) 'C')
                .append((byte) 'D')
                .append("EFG".getBytes())
                .append((byte)'H')
                .append("IJKLMNO".getBytes())
                .append((byte)'P')
                .append((byte)'Q')
                .append((byte)'R')
                .append("STUVWXY".getBytes())
                .append((byte)'Z');
        byte[] res = sb0.readAll();
        Assert.assertArrayEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes(), res);
    }

    public void testReadIndex() {
        byte[] str = "This is a sensitive string!".getBytes();
        SensitiveBytes sb0 = SensitiveBytes.of(str);
        assertEquals('T', sb0.read(0));
        assertEquals('i', sb0.read(5));
        assertEquals('v', sb0.read(17));
    }

    public void testReadRange() {
        byte[] str = "This is a sensitive string!".getBytes();
        SensitiveBytes sb0 = SensitiveBytes.of(str);
        Assert.assertArrayEquals("This".getBytes(), sb0.read(0, 4));
        Assert.assertArrayEquals("a".getBytes(), sb0.read(8, 9));
        Assert.assertArrayEquals("string".getBytes(), sb0.read(20, 26));
    }

    public void testIterator() {
        SensitiveBytes sb0 = new SensitiveBytes()
                .append((byte) 'A')
                .append((byte) 'B')
                .append((byte) 'C')
                .append((byte) 'D');

        StringBuilder sb = new StringBuilder();
        for (Byte b : sb0) {
            sb.append((char) b.byteValue());
        }

        assertEquals("ABCD", sb.toString());
    }

    public void testInputStream() throws IOException {
        SensitiveBytes sb0 = new SensitiveBytes()
                //block
                .append((byte) 'A')
                .append((byte) 'B')
                .append((byte) 'C')
                .append((byte) 'D')
                //block
                .append("EFG".getBytes())
                //block
                .append((byte)'H')
                //block
                .append("IJKLMNO".getBytes())
                //block
                .append((byte)'P')
                .append((byte)'Q')
                .append((byte)'R')
                //block
                .append("STUVWXY".getBytes())
                //block
                .append((byte)'Z');

        try(InputStream is = sb0.stream()) {
            byte[] arr = new byte[1024];
            int read = is.read(arr);
            byte[] aux = arr;
            arr = new byte[read];
            System.arraycopy(aux, 0, arr, 0, read);
            Assert.assertArrayEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes(), arr);
        }
    }

    public void testStored() throws IOException, URLConnectionException {
        byte[] script = new HttpConnection()
                .getContent("https://raw.githubusercontent.com/chcmatos/nanodegree_py_analyze_srag/main/app/analyze.py")
                .getContentBytes();

        for(int i=0, l=SensitiveBytes.CIPHER_PROXY_LIMIT * 2; i < l; i++) {
            SensitiveBytes sb = new SensitiveBytes();
            if((i + 1) == l) sb.useClearAfterAppend();
            sb.append(script);
            byte[] original = sb.readAll();

            File file = sb.store();
            assertNotNull(file);
            assertEquals(0, sb.length());

            sb.stored(file);
            Assert.assertArrayEquals(original, sb.readAll());
        }
    }
}