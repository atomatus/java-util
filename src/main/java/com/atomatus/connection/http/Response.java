package com.atomatus.connection.http;

import com.atomatus.connection.http.exception.URLConnectionException;
import com.atomatus.util.Debug;
import com.atomatus.util.cache.CacheControl;
import com.atomatus.util.cache.CacheData;
import com.atomatus.util.serializer.Serializer;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Response generated from request HttpConnection.
 * @author Carlos Matos {@literal @chcmatos}
 */
public final class Response extends ResponseParameter implements Closeable {

	/**
	 * Response Builder
	 */
	protected static class Builder extends ResponseParameter {

		Builder useConnection(HttpURLConnection con) {
			this.con = Objects.requireNonNull(con);
			this.url = con.getURL();
			return this;
		}

		Builder useCharset(Charset charset) {
			this.charset = Objects.requireNonNull(charset);
			return this;
		}

		Builder useBufferLength(int bufferLength) {
			if(bufferLength <= 0) throw new IndexOutOfBoundsException();
			this.bufferLength = bufferLength;
			return this;
		}

		Builder useConnectionFun(FunctionIO<URL, HttpURLConnection> conFun){
			this.conFun = Objects.requireNonNull(conFun);
			return this;
		}

		Builder useUrl(URL url) {
			this.url = Objects.requireNonNull(url);
			return this;
		}

		Builder useCacheFun(Function<CacheControl> cacheFun) {
			this.cacheFun = Objects.requireNonNull(cacheFun);
			return this;
		}

		Builder useSuccessResponseFun(FunctionIO<HttpConnection.StatusCode, Boolean> successResponseFun) {
			this.successResponseFun = Objects.requireNonNull(successResponseFun);
			return this;
		}

		Builder useInputStreamFun(FunctionIO<HttpURLConnection, InputStream> inputStreamFun) {
			this.inputStreamFun = Objects.requireNonNull(inputStreamFun);
			return this;
		}

		Builder useErrorStreamFun(FunctionIO<HttpURLConnection, InputStream> errorStreamFun) {
			this.errorStreamFun = Objects.requireNonNull(errorStreamFun);
			return this;
		}

		Builder useFinallyAction(Action<HttpURLConnection> finnalyAction) {
			this.finallyAction = Objects.requireNonNull(finnalyAction);
			return this;
		}

		/**
		 * Build a new response.
		 * @return response.
		 */
		public Response build() {
			this.requireNonClosed();
			this.requireConnectionOrFunction();

			try {
				return new Response(this);
			} finally {
				this.close();
			}
		}
	}

	private boolean success;
	private HttpConnection.StatusCode statusCode;
	private HttpConnection.ContentType contentType;
	private byte[] contentBytes;
	private byte[] errorBytesContent;
	private final Object lock;

	private Response(Builder builder) {
		super(builder);
		lock = new Object();
	}

	private InputStream getInputStream(boolean success) throws URLConnectionException {
		if(stream != null) {
			try {
				stream.reset();
			} catch (IOException e) {
				if(Debug.isDebugMode()) {
					throw new URLConnectionException(e);
				} else {
					try{
						stream.close();
					} catch (IOException ignored) { } finally {
						stream = null;
					}
					return getInputStream(success);
				}
			}
		} else {
			try {
				HttpURLConnection con = requireConnection();
				if(success) {
					stream = inputStreamFun != null ? inputStreamFun.apply(con) : con.getInputStream();
				} else {
					stream = errorStreamFun != null ? errorStreamFun.apply(con) : con.getErrorStream();
				}
			} catch (IOException e) {
				throw new URLConnectionException(e);
			}
		}

		return stream;
	}

	private byte[] readAll(InputStream in) throws URLConnectionException {
		byte[] buffer = new byte[bufferLength];
		int offset = 0;
		int count;

		try {
			while ((count = in.read(buffer, offset, buffer.length - offset)) != -1) {
				if ((offset += count) == buffer.length) {
					byte[] aux = buffer;
					buffer = new byte[buffer.length + bufferLength];
					System.arraycopy(aux, 0, buffer, 0, aux.length);
				}
			}
		} catch (IOException e) {
			throw new URLConnectionException(
					"An error occurred while attempt to read response:\n" + e.getMessage());
		}

		if (offset < buffer.length) {
			byte[] aux = buffer;
			buffer = new byte[offset];
			System.arraycopy(aux, 0, buffer, 0, offset);
		}

		return buffer;
	}

	private void checkCache(boolean fillBuffer)  throws URLConnectionException {
		if((contentBytes == null || !fillBuffer) && cacheFun != null) {
			try {
				CacheControl cache = cacheFun.apply();
				if(cache != null) {
					CacheData data = cache.get(url);

					if (data.exists()) {
						contentBytes 	= fillBuffer ? data.bytes() : contentBytes;
						stream 			= !fillBuffer ? data.stream() : null;
						success 		= true;
					}
				}
			} catch (Exception e) {
				if(Debug.isDebugMode()) {
					throw new URLConnectionException(e);
				}
			}
		}
	}

	private byte[] checkAddCache(byte[] buffer) throws URLConnectionException {
		if(cacheFun != null) {
			try {
				CacheControl cache = cacheFun.apply();
				if (cache != null) {
					cache.add(new CacheData.Builder()
							.id(url)
							.bytes(buffer)
							.build());
				}
			} catch (Exception e) {
				if(Debug.isDebugMode()) {
					throw new URLConnectionException(e);
				}
			}
		}
		return buffer;
	}

	private void checkReadResponse(boolean fillBuffer) throws URLConnectionException {
		synchronized (lock) {
			requireNonClosed();
			checkCache(fillBuffer);
			if (contentBytes == null) {
				success = success || successResponseFun.apply(getStatusCodeLocal());
				if (fillBuffer) {
					InputStream in = null;
					try {
						errorBytesContent = contentBytes = new byte[0];
						if(getStatusCodeLocal() != HttpConnection.StatusCode.HTTP_NO_CONTENT) {
							byte[] buffer = readAll(in = getInputStream(success));
							if (success) {
								contentBytes = checkAddCache(buffer);
							} else {
								errorBytesContent = buffer;
							}
						}
					} finally {
						try {
							if(finallyAction != null) {
								finallyAction.action(requireConnection());
							}
						} finally {
							this.tryClose(in);
							this.tryClose(con);
						}
					}
				}
			}
		}
	}

	private void checkReadResponseFilling() throws URLConnectionException {
		checkReadResponse(true);
	}

	private void checkReadResponseOnlyState() throws URLConnectionException {
		checkReadResponse(false);
	}

	private HttpConnection.StatusCode getStatusCodeLocal() throws URLConnectionException {
		if(statusCode == null) {
			try {
				HttpURLConnection con = this.requireConnection();
				statusCode = HttpConnection.StatusCode.valueOf(con.getResponseCode());
				contentType = HttpConnection.ContentType.fromType(con.getContentType());
			} catch (IOException e) {
				throw new URLConnectionException(e);
			}
		}

		return statusCode;
	}

	private Serializer.Type getSerializerType() throws URLConnectionException {
		return getContentType().getSerializerType();
	}

	/**
	 * Get response content as stream.
	 * @return response in stream
	 * @throws URLConnectionException throws when is not possible get response content.
	 */
	public InputStream getContentStream() throws URLConnectionException {
		synchronized (lock) {
			requireNonClosed();
			checkCache(false);
			success = success || successResponseFun.apply(getStatusCodeLocal());
			return getInputStream(success);
		}
	}

	/**
	 * Get response content as byte array.
	 * @return respose in byte array
	 * @throws URLConnectionException throws when is not possible get response content.
	 */
	public byte[] getContentBytes() throws URLConnectionException {
		checkReadResponseFilling();
		return contentBytes;
	}

	/**
	 * Get response content as string.
	 * @return respose in string
	 * @throws URLConnectionException throws when is not possible get response content.
	 */
	public String getContent() throws URLConnectionException {
		checkReadResponseFilling();
		return new String(contentBytes, charset);
	}

	/**
	 * Get error response content as byte array.
	 * @return error respose in byte array
	 * @throws URLConnectionException throws when is not possible get response content.
	 */

	public byte[] getErrorBytesContent() throws URLConnectionException {
		checkReadResponseFilling();
		return errorBytesContent;
	}

	/**
	 * Get error response content as string.
	 * @return error respose in string
	 * @throws URLConnectionException throws when is not possible get response content.
	 */
	public String getErrorContent() throws URLConnectionException {
		checkReadResponseFilling();
		return new String(errorBytesContent, this.charset);
	}

	/**
	 * Response status code.
	 * @return status code.
	 * @throws URLConnectionException throws when is not possible get response.
	 */
	public HttpConnection.StatusCode getStatusCode() throws URLConnectionException {
		checkReadResponseOnlyState();
		return statusCode;
	}

	/**
	 * Response content type.
	 * @return content type.
	 * @throws URLConnectionException throws when is not possible get response.
	 */
	public HttpConnection.ContentType getContentType() throws URLConnectionException {
		checkReadResponseOnlyState();
		return contentType;
	}

	/**
	 * Inform whether response is a success status code.
	 * @return true, response sucessfully.
	 * @throws URLConnectionException throws when is not possible get response.
	 */
	public boolean isSuccess() throws URLConnectionException {
		checkReadResponseOnlyState();
		return success;
	}

	/**
	 * Inform when current response in success contains a content.
	 * @return true, response contains content, otherwise false.
	 * @throws URLConnectionException throws when is not possible get response.
	 */
	public boolean hasContent() throws URLConnectionException {
		checkReadResponseFilling();
		return success && contentBytes != null && contentBytes.length != 0;
	}

	/**
	 * Inform when current response in error contains a content.
	 * @return true, response in error contains content, otherwise false.
	 * @throws URLConnectionException throws when is not possible get response.
	 */
	public boolean hasErrorContent() throws URLConnectionException {
		checkReadResponseFilling();
		return !success && errorBytesContent != null && errorBytesContent.length != 0;
	}

	/**
	 * Parse and convert response content to target serializable type
	 * using response content type serializer.
	 * @param rootElement response content root element, usage for xml.
	 * @param type target class type
	 * @param <T> target type
	 * @return deserialized content from response content.
	 * @throws URLConnectionException throws when is not possible get response.
	 */
	public <T extends Serializable> T parse(String rootElement, Class<T> type) throws URLConnectionException {
		checkReadResponseFilling();
		return success ? Serializer
				.getInstance(getSerializerType())
				.deserialize(contentBytes, rootElement, type) : null;
	}

	/**
	 * Parse and convert response content error to target serializable type
	 * using response content type serializer.
	 * @param rootElement response content root element, usage for xml.
	 * @param type target class type
	 * @param <T> target type
	 * @return deserialized content from response content.
	 * @throws URLConnectionException throws when is not possible get response.
	 */
	public <T extends Serializable> T parseError(String rootElement, Class<T> type) throws URLConnectionException {
		checkReadResponseFilling();
		return !success ? Serializer
				.getInstance(getSerializerType())
				.deserialize(errorBytesContent, rootElement, type) : null;
	}

	/**
	 * Parse and convert response content to target serializable type
	 * using response content type serializer.
	 * @param type target class type
	 * @param <T> target type
	 * @return deserialized content from response content.
	 * @throws URLConnectionException throws when is not possible get response.
	 */
	public <T extends Serializable> T parse(Class<T> type) throws URLConnectionException {
		return parse(null, type);
	}

	/**
	 * Parse and convert response content error to target serializable type
	 * using response content type serializer.
	 * @param type target class type
	 * @param <T> target type
	 * @return deserialized content from response content.
	 * @throws URLConnectionException throws when is not possible get response.
	 */
	public <T extends Serializable> T parseError(Class<T> type) throws URLConnectionException {
		return parseError(null, type);
	}

	@Override
	protected void onClose() {
		this.tryClose(stream);
		this.tryClose(con);
		this.contentBytes = null;
		this.errorBytesContent = null;
		this.statusCode = null;
		this.contentType = null;
		this.charset = null;
	}
}