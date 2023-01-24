package com.atomatus.util.security;

import com.atomatus.util.ArrayHelper;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import java.io.*;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Sensitive Bytes.<br>
 * Implements a bytes matrix manager encrypted using Cipher and DESede algorithm.<br>
 * Each sensitive bytes class instance will generate an unique private key.
 * @author Carlos Matos {@literal @chcmatos}
 */
public class SensitiveBytes implements Iterable<Byte>, Destroyable, Closeable {

    /**
     * Sensitive bytes iterator.
     */
    private static class SensitiveBytesIterator implements Iterator<Byte>, Destroyable {

        @FunctionalInterface
        private interface DecryptCallback {
            byte[] apply(byte[] arr);
        }

        private DecryptCallback decryptFun;
        private byte[][] matrix;
        private final int mOffset;

        private byte[] bytes;
        private int bLength;
        private int mIndex;
        private int bIndex;
        private boolean destroyed;

        SensitiveBytesIterator(DecryptCallback decryptFun,
                               byte[][] matrix, int offset) {
            this.decryptFun = decryptFun;
            this.matrix     = ArrayHelper.copy(matrix);
            this.mOffset    = offset;
        }

        public void reset() throws DestroyFailedException {
            if(destroyed) {
                throw new DestroyFailedException();
            }
            this.bytes = null;
            this.bLength = 0;
            this.mIndex = 0;
            this.bIndex = 0;
        }

        @Override
        public boolean hasNext() {
            if(!destroyed && (bytes == null || bLength == bIndex) && mIndex < mOffset) {
                clearSensitiveData(bytes);
                bytes   = decryptFun.apply(matrix[mIndex++]);
                bLength = bytes.length;
                bIndex  = 0;
                if(bLength == 0) {
                    return hasNext();
                }
            }

            boolean hasNext = !destroyed && mIndex <= mOffset && bIndex < bLength;

            if(!hasNext) {
                destroy();
            }

            return hasNext;
        }

        @Override
        public Byte next() {
            if(destroyed || bIndex >= bLength) {
                destroy();
                throw new NoSuchElementException();
            }
            return bytes[bIndex++];
        }

        @Override
        public boolean isDestroyed() {
            return destroyed;
        }

        @Override
        public void destroy() {
            clearSensitiveData(bytes);
            matrix = null;
            decryptFun = null;
            bytes = null;
            destroyed = true;
        }
    }

    /**
     * Cipher proxy.
     */
    private static class CipherProxy {

        private static class ThreadSafeEncryptorWrapper extends Encryptor {

            private final Encryptor enc;

            public ThreadSafeEncryptorWrapper(Encryptor enc) {
                this.enc = enc;
            }

            @Override
            public synchronized String encrypt(String original) {
                return enc.encrypt(original);
            }

            @Override
            public synchronized byte[] encrypt(byte[] original) {
                return enc.encrypt(original);
            }

            @Override
            public synchronized byte[] encrypt(byte[] original, int offset, int len) {
                return enc.encrypt(original, offset, len);
            }

            @Override
            public synchronized String decrypt(String encrypted) {
                return enc.decrypt(encrypted);
            }

            @Override
            public synchronized byte[] decrypt(byte[] encrypted) {
                return enc.decrypt(encrypted);
            }

            @Override
            public byte[] decrypt(byte[] encrypted, int offset, int len) {
                return enc.decrypt(encrypted, offset, len);
            }
        }

        private final List<WeakReference<Encryptor>> ciphers;
        private final int limit;
        private int count;

        private static final CipherProxy instance;

        /**
         * Create or recover valid cipher encryptor.
         * @return cipher encriptor.
         */
        public synchronized static Encryptor getCipher() {
            return instance.getOrCreate();
        }

        static {
            instance = new CipherProxy(CIPHER_PROXY_LIMIT);
        }

        private CipherProxy(int limit) {
            this.limit   = limit;
            this.ciphers = new ArrayList<>(limit);
        }

        private int randomIndex() {
            return new Random().nextInt(limit);
        }

        private Encryptor getEncryptor(){
            return new ThreadSafeEncryptorWrapper(
                    new Encryptor.Builder()
                            .cipher()
                            .key()
                            .vector()
                            .build());
        }

        private synchronized Encryptor getOrCreate() {
            if(count < limit) {
                Encryptor e = getEncryptor();
                WeakReference<Encryptor> ref = new WeakReference<>(e);
                ciphers.add(ref);
                count++;
                return e;
            }

            int i = randomIndex();
            WeakReference<Encryptor> ref = ciphers.get(i);
            Encryptor e = ref.get();

            if(e == null) {
                ref = new WeakReference<>(e = getEncryptor());
                ciphers.remove(i);
                ciphers.add(ref);
            }

            return e;
        }
    }

    /**
     * Limit ciphers types instance from proxy.
     */
    protected static final int CIPHER_PROXY_LIMIT;

    /**
     * Buffer length.
     */
    protected static final int MATRIX_LENGTH;

    private final String FILE_PREFIX;

    private File[] tmpFiles;
    private byte[][] matrix;
    private int index, length, count;
    private final Object lock;
    private Encryptor cipher;
    private boolean lastInputSingle, clearAfterAppend;

    static {
        MATRIX_LENGTH = 512;
        CIPHER_PROXY_LIMIT = 10;
    }

    {
        FILE_PREFIX = "~" + KeyGenerator.generateRandomKeyHex(4);
    }

    //region constructors
    /**
     * Constructs inputing initial value.
     * @param bytes initial value.
     */
    protected SensitiveBytes(byte[] bytes) {
        Objects.requireNonNull(bytes);
        this.lock   = new Object();
        this.cipher = CipherProxy.getCipher();
        this.init().append(bytes);
    }

    /**
     * Constructs empty.
     */
    public SensitiveBytes() {
        this(new byte[0]);
    }
    //endregion

    //region local
    /**
     * Check if must resize matrix.
     * @return current instance.
     */
    protected final SensitiveBytes resize() {
        if((index + 1) == length) {
            byte[][] aux = matrix;
            matrix = new byte[length = matrix.length + MATRIX_LENGTH][];
            System.arraycopy(aux, 0, matrix, 0, aux.length);
        }
        return this;
    }

    /**
     * Appends the byte array to secure context.
     * @param args target content to be stored in secure context.
     * @param start start index to read
     * @param end array max length to read.
     * @return current instance.
     */
    private SensitiveBytes putInternal(byte[] args, int start, int end) {
        int len = args.length;
        if (len > 0) {
            if(start >= end || end > len || end > (len - start)) {
                throw new IndexOutOfBoundsException();
            }
            resize();
            matrix[++index] = cipher.encrypt(args, start, end);
            lastInputSingle = false;
            count += (end - start);
            if(clearAfterAppend) {
                clearSensitiveData(args, start, end);
            }
        }
        return this;
    }

    /**
     * Appends the byte array to secure context.
     * @param args target content to be stored in secure context.
     * @param start start index to read
     * @param end array max length to read.
     * @return current instance.
     */
    protected final SensitiveBytes put(byte[] args, int start, int end) {
        synchronized (lock) {
            this.requireNonDestroyed();
            return this.putInternal(args, start, end);
        }
    }

    /**
     * Appends the byte to secure context.
     * @param b target byte.
     * @return current instance.
     */
    protected final SensitiveBytes put(byte b) {
        synchronized (lock) {
            this.requireNonDestroyed();
            this.resize();
            byte[] arr = matrix[lastInputSingle ? index : ++index];
            matrix[index] = arr != null ?
                    cipher.encrypt(ArrayHelper.add(cipher.decrypt(arr), b)) :
                    cipher.encrypt(new byte[] { b });
            lastInputSingle = true;
            count++;
            return this;
        }
    }

    /**
     * Read all bytes stored non ciphered.
     * @return original data (non ciphered).
     */
    private byte[] peekInternal() {
        byte[] empty = new byte[0];
        return index == -1 ? empty : ArrayHelper.reduceI(matrix,
                (acc, curr, i) -> i > index ? acc : ArrayHelper.join(acc, cipher.decrypt(curr)),
                empty);
    }

    /**
     * Read all bytes stored non ciphered.
     * @return original data (non ciphered).
     */
    protected final byte[] peek() {
        synchronized (lock) {
            requireNonDestroyed();
            return peekInternal();
        }
    }

    /**
     * Read valid at index.
     * @param index target index
     * @return value at index.
     * @exception IndexOutOfBoundsException throws when index is invalid.
     */
    protected final byte peek(int index) {
        if(index > -1 && index < count) {
            synchronized (lock) {
                requireNonDestroyed();
                for(int i=0, diff=index; i <= this.index; i++) {
                    byte[] block = cipher.decrypt(matrix[i]);
                    if(block == null) {
                        break;
                    } else if(diff >= block.length) {
                        diff -= block.length;
                    } else {
                        return block[diff];
                    }
                }
            }
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * Read the byte array from range.
     * @param start start index to read.
     * @param end array max length to read.
     * @return range array.
     */
    protected final byte[] peek(int start, int end) {
        if(index > -1 && index < count && count >= (end - start)) {
            synchronized (lock) {
                requireNonDestroyed();
                byte[] res = new byte[(end - start)];
                for(int i=0, offset=0, s=start, diff=res.length; i <= this.index; i++) {
                    byte[] block = cipher.decrypt(matrix[i]);
                    if(block == null) {
                        break;
                    } else if(s < block.length) {
                        System.arraycopy(block, s, res, offset,
                                offset += Math.min(block.length, diff - offset));
                        diff -= offset;
                        s = 0;
                        if(diff == 0){
                            return res;
                        }
                    } else {
                        s -= block.length;
                    }
                }
            }
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * Read the byte array block by append's request index.
     * @param index append's request index.
     * @return range array.
     */
    protected final byte[] peekAt(int index) {
        synchronized (lock) {
            requireNonDestroyed();
            if(this.index < index) {
                throw new IndexOutOfBoundsException();
            }
            return cipher.decrypt(matrix[index]);
        }
    }

    /**
     * Initialize matrix.
     * @return current instance.
     */
    private SensitiveBytes init() {
        this.matrix = new byte[length = MATRIX_LENGTH][];
        this.index  = -1;
        this.count  = 0;
        return this;
    }

    /**
     * Reset sensitve bytes storing.
     * @return current instance
     */
    private SensitiveBytes resetInternal() {
        byte[][] old;
        old = this.matrix;
        this.init();
        if(old != null) {
            for(byte[] arr : old) {
                clearSensitiveData(arr);
            }
        }
        return this;
    }

    /**
     * Reset sensitve bytes storing.
     * @return current instance
     */
    protected final SensitiveBytes reset() {
        synchronized (lock) {
            this.requireNonDestroyed();
            return this.resetInternal();
        }
    }

    /**
     * Iterator to read all stored data (apply decipher when request each read block).
     * @return iterator to read secured data (decipher it).
     */
    private SensitiveBytesIterator getIterator() {
        synchronized (lock) {
            requireNonDestroyed();
            return new SensitiveBytesIterator(cipher::decrypt, matrix, index + 1);
        }
    }

    /**
     * Clear and dispose all.
     */
    protected final void purge() {
        byte[][] old;
        synchronized (lock) {
            old = this.matrix;
            this.matrix = null;
            this.cipher = null;
            this.length = 0;
            this.count = 0;
            this.index = -1;
            this.purgeTmpFiles();
        }

        if (old != null) {
            for (byte[] arr : old) {
                clearSensitiveData(arr);
            }
        }
    }

    /**
     * Require current instance non destroyed.
     */
    protected final void requireNonDestroyed() {
        if (matrix == null) {
            throw new UnsupportedOperationException("Object destroyed!");
        }
    }

    /**
     * Char array to byte array.
     * @param chars target
     * @param charset characteres encoding type.
     * @return byte array.
     */
    protected static byte[] fromChars(char[] chars, Charset charset) {
        Objects.requireNonNull(chars);
        Objects.requireNonNull(charset);
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = charset.encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                byteBuffer.position(), byteBuffer.limit());
        clearSensitiveData(byteBuffer.array());
        return bytes;
    }

    /**
     * Check if current instance requeted to store sensitive data in temp file.
     * @return true, current instance stored senstitive data in temp file,
     * otherwise false.
     */
    private boolean isStoredInternal() {
        return count == 0 && tmpFiles != null && tmpFiles.length > 0;
    }
    //endregion

    //region behavior
    /**
     * Enabled to clear input data
     * after "append" requests.
     * @return current instance.
     */
    public SensitiveBytes useClearAfterAppend() {
        synchronized (lock) {
            this.clearAfterAppend = true;
            return this;
        }
    }

    /**
     * Check if current instance requeted to store sensitive data in temp file.
     * @return true, current instance stored senstitive data in temp file,
     * otherwise false.
     */
    public final boolean isStored() {
        synchronized (lock) {
            return isStoredInternal();
        }
    }
    //endregion

    //region append
    /**
     * Appends the byte array to secure context.
     * @param args content to be stored in secure context.
     * @return current instance.
     */
    public SensitiveBytes append(byte[] args) {
        return this.put(Objects.requireNonNull(args), 0, args.length);
    }

    /**
     * Appends the byte array to secure context.
     * @param args target content to be stored in secure context.
     * @param start start index to read
     * @param end array max length to read.
     * @return current instance.
     */
    public SensitiveBytes append(byte[] args, int start, int end) {
        return this.put(Objects.requireNonNull(args), start, end);
    }

    /**
     * Appends the byte to secure context.
     * @param b target byte.
     * @return current instance.
     */
    public SensitiveBytes append(byte b) {
        return this.put(b);
    }
    //endregion

    //region read data
    /**
     * Read secure and ciphered data.
     * @return ciphered data.
     */
    protected byte[] secure(){
        synchronized (lock) {
            requireNonDestroyed();
            byte[] empty = new byte[0];
            return index == -1 ? empty : ArrayHelper.reduceI(matrix,
                    (acc, curr, i) -> i > index ? acc : ArrayHelper.join(acc, curr),
                    empty);
        }
    }

    /**
     * Count of element stored are sensitive bytes.
     * @return count of elements stored.
     */
    public int length() {
        return count;
    }

    /**
     * Read valid at index.
     * @param index target index
     * @return value at index.
     * @exception IndexOutOfBoundsException throws when index is invalid.
     */
    public byte read(int index) {
        return peek(index);
    }

    /**
     * Read the byte array from range.
     * @param start start inclusive index to read.
     * @param end end exclusive index to read.
     * @return range array.
     */
    public byte[] read(int start, int end) {
        return peek(start, end);
    }

    /**
     * Read all bytes stored.
     * @return original data (non ciphered).
     */
    public byte[] readAll() {
        return peek();
    }

    /**
     * Read the byte array block by append's request index.
     * @param index append's request index.
     * @return range array.
     */
    public byte[] readAt(int index) {
        return peekAt(index);
    }

    /**
     * Iterator to read all stored data (apply decipher when request each read block).
     * @return iterator to read secured data (decipher it).
     */
    @Override
    public Iterator<Byte> iterator() {
        return getIterator();
    }

    /**
     * Input stream to read all stored data (apply decipher when request each read block).
     * @return input stream to read secured data (decipher it).
     */
    public InputStream stream() {
        return new InputStream() {
            final SensitiveBytesIterator it = getIterator();

            @Override
            public synchronized void reset() throws IOException {
                try {
                    it.reset();
                } catch (DestroyFailedException e) {
                    throw new IOException(e);
                }
            }

            @Override
            public int read() {
                return it.hasNext() ? it.next() : -1;
            }
        };
    }
    //endregion

    //region store

    //region tmpfile
    private void pushTmpFile(File file) {
        tmpFiles = tmpFiles == null ?
                new File[] { file } :
                ArrayHelper.contains(tmpFiles, file) ? tmpFiles :
                ArrayHelper.add(tmpFiles, file);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void purgeTmpFiles() {
        if(tmpFiles != null) {
            for(File tmp : tmpFiles) {
                tmp.delete();
            }
            tmpFiles = null;
        }
    }

    private void purgeTmpFile(File file) throws FileNotFoundException {
        if(tmpFiles != null && tmpFiles.length > 0) {

            if(file == null /*generated file*/ && tmpFiles.length == 1) {
                tmpFiles = null;
                return;
            }

            int index = ArrayHelper.indexOf(tmpFiles, file);
            if(index == -1) {
                String name =  this.getClass().getSimpleName();
                throw new FileNotFoundException("Is not allowed to reload sensitive data " +
                        "stored by other " + name + " instance!");
            } if(tmpFiles.length == 1) {
                tmpFiles = null;
            } else {
                tmpFiles = index == 0 ?
                        ArrayHelper.jump(tmpFiles, index + 1) :
                        ArrayHelper.join(
                            ArrayHelper.take(tmpFiles, index),
                            ArrayHelper.jump(tmpFiles, index + 1));
            }
        }
    }

    private File requireValidTmpFile(File file) throws FileNotFoundException {
        String name =  this.getClass().getSimpleName();
        if(tmpFiles == null || tmpFiles.length == 0) {
            throw new FileNotFoundException("Can not reload file that is not " +
                    "stored before by this " + name + " instance!\n" +
                    "You must request store(File), store(String) or store(), " +
                    "least one time before request it!\n" +
                    "Is not allowed to reload sensitive data stored by other " + name + " instance!");

        } else if(file == null) {
            file = tmpFiles[0];
        } else if(!ArrayHelper.contains(tmpFiles, file)) {
            throw new FileNotFoundException("Is not allowed to reload sensitive data " +
                    "stored by other " + name + " instance!");
        }

        if(!Objects.requireNonNull(file).exists()) {
            throw new FileNotFoundException(file.getAbsolutePath() +
                    " (file does not exists).");
        }

        return file;
    }
    //endregion

    //region store
    /**
     * To store current ciphered  data to target temp file.<br><br>
     * <i>Warning: After do it, current instance is clean ciphered data from memory.
     * So to do data reload, request {@link #stored(File)}.</i><br><br>
     * <i>Warning: The file is marked to be deleted after process close or reloaded
     * by request {@link #stored(File)}.</i>
     * @param file target temp file.
     * @throws FileNotFoundException throws if the file exists but is a directory
     * rather than a regular file, does not exist but cannot be created,
     * or cannot be opened for any other reason
     * @throws IOException throws if an I/O error occurs.
     */
    public final void store(File file) throws FileNotFoundException, IOException {
        synchronized (lock) {
            requireNonDestroyed();
            Objects.requireNonNull(file).deleteOnExit();
            try (FileOutputStream fos = new FileOutputStream(file);
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                int len = index + 1;
                byte[][] aux = length == len ? matrix : ArrayHelper.resize(matrix, index + 1);
                oos.writeObject(aux);
            }
            pushTmpFile(file);
            clear();
        }
    }

    /**
     * To store current ciphered  data to target temp file.<br><br>
     * <i>Warning: After do it, current instance is clean ciphered data from memory.
     * So to do data reload, request {@link #stored(File)}.</i><br><br>
     * <i>Warning: The file is marked to be deleted after process close or reloaded
     * by request {@link #stored(File)}.</i>
     * @param filename target temp file name.
     * @return return file generated.
     * @throws FileNotFoundException throws if the file exists but is a directory
     * rather than a regular file, does not exist but cannot be created,
     * or cannot be opened for any other reason
     * @throws IOException throws if an I/O error occurs.
     */
    public final File store(String filename) throws FileNotFoundException, IOException {
        File file = File.createTempFile(filename, ".tmp");
        store(file);
        return file;
    }

    /**
     * To store current ciphered data to target temp file.<br><br>
     * <i>Warning: After do it, current instance is clean ciphered data from memory.
     * So to do data reload, request {@link #stored(File)}.</i><br><br>
     * <i>Warning: The file is marked to be deleted after process close or reloaded
     * by request {@link #stored(File)}.</i>
     * @return return file generated.
     * @throws FileNotFoundException throws if the file exists but is a directory
     * rather than a regular file, does not exist but cannot be created,
     * or cannot be opened for any other reason
     * @throws IOException throws if an I/O error occurs.
     */
    public final File store() throws IOException {
        return store(FILE_PREFIX);
    }
    //endregion

    //region stored
    /**
     * Reload back data stored in file.<br><br>
     * <i>Warning: The file is marked to be deleted after data is reloaded.</i>
     * @param file target temp file generated from {@link #store(File)} or {@link #store(String)}
     * @return current instance.
     * @exception FileNotFoundException if the file does not exist,
     * is a directory rather than a regular file, or for some other
     * reason cannot be opened for reading.
     * @throws IOException throws if an I/O error occurs.
     */
    public SensitiveBytes stored(File file) throws IOException {
        return reloadStored(file);
    }

    /**
     * Reload back last data stored in file.<br><br>
     * <i>Warning: The file is marked to be deleted after data is reloaded.</i>
     * @return current instance.
     * @exception FileNotFoundException if the file does not exist,
     * is a directory rather than a regular file, or for some other
     * reason cannot be opened for reading.
     * @throws IOException throws if an I/O error occurs.
     */
    public SensitiveBytes stored() throws IOException  {
        return reloadStored(null);
    }

    /**
     * Reload back data stored in file.<br><br>
     * <i>Warning: The file is marked to be deleted after data is reloaded.</i>
     * @param file target temp file generated from {@link #store(File)} or {@link #store(String)}
     * @return current instance.
     * @exception FileNotFoundException if the file does not exist,
     * is a directory rather than a regular file, or for some other
     * reason cannot be opened for reading.
     * @throws IOException throws if an I/O error occurs.
     */
    private SensitiveBytes reloadStored(File file) throws IOException {
        synchronized (lock) {
            try {
                byte[][] blocks = readStoredInternal(file);
                //region assert
                this.resetInternal();
                for(byte[] block : blocks) {
                    byte[] sb = cipher.decrypt(block, 0, block.length);
                    this.putInternal(sb, 0, sb.length);
                    clearSensitiveData(sb);
                }
                //endregion
                this.purgeTmpFile(file);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
        return this;
    }
    //endregion

    //region peekStored
    /**
     * <p>
     *      To peek content from stored temp file.<br>
     *      But does not keep data in memory back.
     * </p>
     * <i>
     *      Warning: This will work only whether sensitive bytes is stored before it.
     * </i>
     * @param file target temp file generated from {@link #store(File)} or {@link #store(String)}
     * @return original data (non ciphered).
     * @throws IOException throws when is not possivle read data from stored file.
     */
    public byte[] peekStored(File file) throws IOException {
        return peekStoredInternal(file);
    }

    /**
     * <p>
     *      To peek content from stored temp file (generated before by {@link #store()}).<br>
     *      But does not keep data in memory back.
     * </p>
     * <i>
     *      Warning: This will work only whether sensitive bytes is stored before it.
     * </i>
     * @return original data (non ciphered).
     * @throws IOException throws when is not possivle read data from stored file.
     */
    public byte[] peekStored() throws IOException {
        return peekStoredInternal(null);
    }

    /**
     * <p>
     *      To peek content from stored temp file.<br>
     *      But does not keep data in memory back.
     * </p>
     * <i>
     *      Warning: This will work only whether sensitive bytes is stored before it.
     * </i>
     * @param file target temp file generated from {@link #store(File)} or {@link #store(String)}
     * @return original data (non ciphered).
     * @throws IOException throws when is not possivle read data from stored file.
     */
    private byte[] peekStoredInternal(File file) throws IOException {
        synchronized (lock) {
            try {
                byte[][] blocks = readStoredInternal(file);
                return ArrayHelper.reduce(blocks,
                        (acc, curr) -> ArrayHelper.join(acc,
                                        cipher.decrypt(curr, 0, curr.length)),
                        new byte[0]);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    /**
     * Read stored ciphered data
     * @param file temp file.
     * @return matrix
     * @throws IOException throws when is not possible read of find temp file.
     */
    private byte[][] readStoredInternal(File file) throws IOException {
        requireNonDestroyed();
        try (FileInputStream fis = new FileInputStream(requireValidTmpFile(file));
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            try {
                return (byte[][]) ois.readObject();
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }
    //endregion

    //region streamStored
    /**
     * <p>
     *     Open input stream to read sensitive bytes stored.<br>
     * </p>
     * <i>
     *      Warning: This will work only whether sensitive bytes is stored before it.
     * </i>
     * @return input stream to read stored bytes.
     */
    public InputStream streamStored() {
        return streamStoredInternal(null);
    }

    /**
     * <p>
     *     Open input stream to read sensitive bytes stored.<br>
     * </p>
     * <i>
     *      Warning: This will work only whether sensitive bytes is stored before it.
     * </i>
     * @param file target temp file generated from {@link #store(File)} or {@link #store(String)}
     * @return input stream to read stored bytes.
     */
    public InputStream streamStored(File file) {
        return streamStoredInternal(file);
    }

    /**
     * <p>
     *     Open input stream to read sensitive bytes stored.<br>
     * </p>
     * <i>
     *      Warning: This will work only whether sensitive bytes is stored before it.
     * </i>
     * @param file target temp file generated from {@link #store(File)} or {@link #store(String)}
     * @return input stream to read stored bytes.
     */
    private InputStream streamStoredInternal(File file) {
        return new InputStream() {
            Iterator<Byte> it;

            @Override
            public synchronized void reset() {
                it = null;
            }

            @Override
            public int read() throws IOException {
                if(it == null) {
                    synchronized (lock) {
                        byte[][] tmp = readStoredInternal(file);
                        it = new SensitiveBytesIterator(cipher::decrypt, tmp, tmp.length);
                    }
                }
                return it.hasNext() ? it.next() : -1;
            }
        };
    }
    //endregion

    //endregion

    //region clear
    /**
     * Clear and remove all data.
     */
    public void clear() {
        this.reset();
    }
    //endregion

    //region destroy and close
    @Override
    public boolean isDestroyed() {
        synchronized (lock) {
            return matrix == null;
        }
    }

    @Override
    public void destroy() throws DestroyFailedException {
        try {
            purge();
        } catch (Exception ex) {
            throw new DestroyFailedException(ex.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        try {
            purge();
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }
    //endregion

    //region SensitiveBytes.of
    /**
     * Create an instance of sensitive byte from byte array.
     * @param bytes target byte array to be stored (ciphered) in secure context.
     * @return current instance.
     */
    public static SensitiveBytes of(byte[] bytes) {
        return new SensitiveBytes(bytes);
    }

    /**
     * Create an instance of sensitive byte from chars.
     * @param chars target chars array to be stored (ciphered) in secure context.
     * @param charset charset for target chars.
     * @return current instance.
     */
    public static SensitiveBytes of(char[] chars, Charset charset) {
        return of(fromChars(chars, charset));
    }

    /**
     * Create an instance of sensitive byte from chars.
     * @param chars target chars array to be stored (ciphered) in secure context.
     * @return current instance.
     */
    public static SensitiveBytes of(char[] chars) {
        return of(chars, Charset.defaultCharset());
    }

    /**
     * Create an instance of sensitive byte from string.
     * @param str target strin to be stored (ciphered) in secure context.
     * @return current instance.
     */
    public static SensitiveBytes of(String str) {
        return of(Objects.requireNonNull(str)
                .getBytes());
    }

    /**
     * Create an instance of sensitive byte from string.
     * @param str target strin to be stored (ciphered) in secure context.
     * @param charset charset for target string.
     * @return current instance.
     */
    public static SensitiveBytes of(String str, Charset charset) {
        return of(Objects.requireNonNull(str)
                .getBytes(Objects.requireNonNull(charset)));
    }
    //endregion

    //region SensitiveBytes.clearSensitiveData
    /**
     * Clear sensitive data.
     * @param arr target array.
     * @param start start index.
     * @param length max length to clear.
     */
    public static void clearSensitiveData(byte[] arr, int start, int length) {
        if(arr != null) {
            byte zero = (byte) 0;
            for (int i = start; i < length; i++) {
                arr[i] = zero;
            }
        }
    }

    /**
     * Clear sensitive data.
     * @param arr target array.
     */
    public static void clearSensitiveData(byte[] arr) {
        if(arr != null) {
            clearSensitiveData(arr, 0, arr.length);
        }
    }
    //endregion
}
