package com.atomatus.util.security;

import com.atomatus.util.ArrayHelper;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;

public class SensitiveCharsTest extends TestCase {

    public void testSecure() {
        String str = "This is a sensitive string!";
        SensitiveChars sb0 = SensitiveChars.of(str);
        assertEquals(str, sb0.toString());

        SensitiveChars sb1 = SensitiveChars.of(str);
        assertEquals(str, sb1.toString());

        assertFalse(ArrayHelper.sequenceEquals(sb0.secure(), sb1.secure()));
    }

    public void testCharToCharSequence() {
        SensitiveChars sb0 = new SensitiveChars()
                .append('A')
                .append('B')
                .append('C')
                .append('D');

        assertEquals("ABCD", sb0.toString());
        assertEquals("ABCD", sb0.subSequence(0, 4));
    }

    public void testMixedAppend() {
        SensitiveChars sb0 = new SensitiveChars()
                .append('A')
                .append('B')
                .append('C')
                .append('D')
                .append("EFG")
                .append('H')
                .append("IJKLMNO")
                .append('P')
                .append('Q')
                .append('R')
                .append("STUVWXY")
                .append('Z');
        String res = sb0.toString();
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", res);
    }

    public void testCharAt() {
        byte[] str = "This is a sensitive string!".getBytes();
        SensitiveChars sb0 = SensitiveChars.of(str);
        assertEquals('T', sb0.charAt(0));
        assertEquals('i', sb0.charAt(5));
        assertEquals('v', sb0.charAt(17));
    }

    public void testSubSequence() {
        SensitiveChars sb0 = SensitiveChars.of("This is a sensitive string!");
        assertEquals("This", sb0.subSequence(0, 4));
        assertEquals("a", sb0.subSequence(8, 9));
        assertEquals("string", sb0.subSequence(20, 26));
    }

    public void testIterator() {
        SensitiveChars sb0 = new SensitiveChars()
                .append('A')
                .append('B')
                .append('C')
                .append('D');

        StringBuilder sb = new StringBuilder();
        for (Byte b : sb0) {
            sb.append((char) b.byteValue());
        }

        assertEquals("ABCD", sb.toString());
    }

    public void testInputStream() throws IOException {
        SensitiveChars sb0 = new SensitiveChars()
                //block
                .append('A')
                .append('B')
                .append('C')
                .append('D')
                //block
                .append("EFG")
                //block
                .append('H')
                //block
                .append("IJKLMNO")
                //block
                .append('P')
                .append('Q')
                .append('R')
                //block
                .append("STUVWXY")
                //block
                .append('Z');

        try(InputStream is = sb0.stream()) {
            byte[] arr = new byte[1024];
            int read = is.read(arr);
            byte[] aux = arr;
            arr = new byte[read];
            System.arraycopy(aux, 0, arr, 0, read);
            assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", new String(arr));
        }
    }
}