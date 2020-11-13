package com.atomatus.connection.http;

import com.atomatus.connection.http.exception.URLConnectionException;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Response generated from request HttpConnection.
 * @author Carlos Matos
 */
public final class Response extends ResponseParameter implements Closeable {

	static class ResponseBuilder extends ResponseParameter {

		ResponseBuilder useConnection(HttpURLConnection con) {
			this.con =  Objects.requireNonNull(con);
			return this;
		}

		ResponseBuilder useCharset(Charset charset) {
			this.charset = Objects.requireNonNull(charset);
			return this;
		}

		ResponseBuilder useBufferLength(int bufferLength) {
			if(bufferLength <= 0) throw new IndexOutOfBoundsException();
			this.bufferLength = bufferLength;
			return this;
		}

		ResponseBuilder useSuccessResponseFun(Function<HttpConnection.StatusCode, Boolean> successResponseFun) {
			this.successResponseFun = Objects.requireNonNull(successResponseFun);
			return this;
		}

		ResponseBuilder useInputStreamFun(Function<HttpURLConnection, InputStream> inputStreamFun) {
			this.inputStreamFun = Objects.requireNonNull(inputStreamFun);
			return this;
		}

		ResponseBuilder useErrorStreamFun(Function<HttpURLConnection, InputStream> errorStreamFun) {
			this.errorStreamFun = Objects.requireNonNull(errorStreamFun);
			return this;
		}

		ResponseBuilder useFinallyAction(Action<HttpURLConnection> finnalyAction) {
			this.finallyAction = Objects.requireNonNull(finnalyAction);
			return this;
		}

		public Response build() {
			this.requireNonClosed();
			this.requireConnection();
			try {
				return new Response(this);
			} finally {
				this.close();
			}
		}
	}

	public static ResponseBuilder builder() {
		return new ResponseBuilder();
	}

	private boolean success;
	private HttpConnection.StatusCode statusCode;
	private byte[] bytesContent;
	private byte[] errorBytesContent;
	private final Object lock;

	private Response(ResponseBuilder builder) {
		super(builder);
		lock = new Object();
	}

	private void tryClose(Closeable c) {
		try {
			if (c != null) {
				c.close();
			}
		} catch (Exception ignored) { }
	}

	private void tryClose(HttpURLConnection con) {
		try {
			if (con != null) {
				con.disconnect();
			}
		} catch (Exception ignored) { }
	}

	private InputStream getInputStream(HttpURLConnection con, boolean success) throws URLConnectionException {
		try {
			if(success) {
				return inputStreamFun != null ? inputStreamFun.apply(con) : con.getInputStream();
			} else {
				return errorStreamFun != null ? errorStreamFun.apply(con) : con.getErrorStream();
			}
		} catch (IOException e) {
			throw new URLConnectionException(e);
		}
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

	private void checkReadResponse(boolean fillBuffer) throws URLConnectionException {
		synchronized (lock) {
			requireNonClosed();
			if (bytesContent == null) {
				success = success || successResponseFun.apply(getStatusCodeLocal());
				if (fillBuffer) {
					InputStream in = null;
					try {
						errorBytesContent = bytesContent = new byte[0];
						if(getStatusCodeLocal() != HttpConnection.StatusCode.HTTP_NO_CONTENT) {
							byte[] buffer = readAll(in = getInputStream(requireConnection(), success));
							if (success) {
								bytesContent = buffer;
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

	private HttpConnection.StatusCode getStatusCodeLocal(){
		if(statusCode == null) {
			try {
				statusCode = HttpConnection.StatusCode.valueOf(this.requireConnection().getResponseCode());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return statusCode;
	}

	public byte[] getBytesContent() throws URLConnectionException {
		checkReadResponseFilling();
		return bytesContent;
	}

	public String getContent() throws URLConnectionException {
		checkReadResponseFilling();
		return new String(bytesContent, charset);
	}
	
	public byte[] getErrorBytesContent() throws URLConnectionException {
		checkReadResponseFilling();
		return errorBytesContent;
	}
	
	public String getErrorContent() throws URLConnectionException {
		checkReadResponseFilling();
		return new String(errorBytesContent, this.charset);
	}

	public HttpConnection.StatusCode getStatusCode() throws URLConnectionException {
		checkReadResponseOnlyState();
		return statusCode;
	}

	public boolean isSuccess() throws URLConnectionException {
		checkReadResponseOnlyState();
		return success;
	}

	@Override
	protected void onClose() {
		this.tryClose(con);
		this.bytesContent = null;
		this.errorBytesContent = null;
		this.charset = null;
	}
}