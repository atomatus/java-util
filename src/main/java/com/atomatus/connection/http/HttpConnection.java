package com.atomatus.connection.http;

import com.atomatus.connection.http.Parameter.ParameterType;
import com.atomatus.connection.http.exception.SecureContextCredentialsException;
import com.atomatus.connection.http.exception.URLConnectionException;
import com.atomatus.util.Base64;
import com.atomatus.util.StringUtils;
import com.atomatus.util.cache.CacheControl;
import com.atomatus.util.serializer.Serializer;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Http Connection.
 * @author Carlos Matos {@literal @chcmatos}
 */
@SuppressWarnings({"SpellCheckingInspection", "unused", "UnusedReturnValue"})
public class HttpConnection {

	/**
	 * Http protocol scheme.
	 */
	public static final String SCHEME_HTTP  = "http://";

	/**
	 * Https protocol scheme.
	 */
	public static final String SCHEME_HTTPS = "https://";

	private static final int BUFFER_LENGTH;
	private static final int DEFAULT_CACHE_MAX_AGE_IN_SEC;
	private static final int CACHE_ID;

	private static CookieManager cookieManager;

	private transient List<String> cookies;
	private transient String PROXY_HOST;
	private transient String PROXY_PORT;

	private final Object proxyLock, cookieLock;

	private Charset charset;
	private String username, password;
	private String contentType;
	private boolean isUseCookieBetweenRequest, isKeepAlive, useProxy, useCache, isUseBasicAuth, useSecureContext;

	private int connectionTimeOut;
	private int readTimeOut;
	private int cacheId;
	private long cacheMaxAge;
	private TimeUnit cacheTimeUnit;
	private CacheMode cacheMode;
	private StatusCode[] acceptRespCode;
	private SecureProtocols protocol;
	private SecureContextCredentials secureContextCredentials;

	/**
	 * Http Status codes.
	 */
	public enum StatusCode {

		//region 2XX generally "OK"
		/**
		 * HTTP Status-Code 200: OK.
		 */
		HTTP_OK(200),

		/**
		 * HTTP Status-Code 201: Created.
		 */
		HTTP_CREATED(201),

		/**
		 * HTTP Status-Code 202: Accepted.
		 */
		HTTP_ACCEPTED(202),

		/**
		 * HTTP Status-Code 203: Non-Authoritative Information.
		 */
		HTTP_NOT_AUTHORITATIVE(203),

		/**
		 * HTTP Status-Code 204: No Content.
		 */
		HTTP_NO_CONTENT(204),

		/**
		 * HTTP Status-Code 205: Reset Content.
		 */
		HTTP_RESET(205),

		/**
		 * HTTP Status-Code 206: Partial Content.
		 */
		HTTP_PARTIAL(206),
		//endregion

		//region 3XX: relocation/redirect

		/**
		 * HTTP Status-Code 300: Multiple Choices.
		 */
		HTTP_MULT_CHOICE(300),

		/**
		 * HTTP Status-Code 301: Moved Permanently.
		 */
		HTTP_MOVED_PERM(301),

		/**
		 * HTTP Status-Code 302: Temporary Redirect.
		 */
		HTTP_MOVED_TEMP(302),

		/**
		 * HTTP Status-Code 303: See Other.
		 */
		HTTP_SEE_OTHER(303),

		/**
		 * HTTP Status-Code 304: Not Modified.
		 */
		HTTP_NOT_MODIFIED(304),

		/**
		 * HTTP Status-Code 305: Use Proxy.
		 */
		HTTP_USE_PROXY(305),
		//endregion

		//region 4XX: client error

		/**
		 * HTTP Status-Code 400: Bad Request.
		 */
		HTTP_BAD_REQUEST(400),

		/**
		 * HTTP Status-Code 401: Unauthorized.
		 */
		HTTP_UNAUTHORIZED(401),

		/**
		 * HTTP Status-Code 402: Payment Required.
		 */
		HTTP_PAYMENT_REQUIRED(402),

		/**
		 * HTTP Status-Code 403: Forbidden.
		 */
		HTTP_FORBIDDEN(403),

		/**
		 * HTTP Status-Code 404: Not Found.
		 */
		HTTP_NOT_FOUND(404),

		/**
		 * HTTP Status-Code 405: Method Not Allowed.
		 */
		TTP_BAD_METHOD(405),

		/**
		 * HTTP Status-Code 406: Not Acceptable.
		 */
		HTTP_NOT_ACCEPTABLE(406),

		/**
		 * HTTP Status-Code 407: Proxy Authentication Required.
		 */
		HTTP_PROXY_AUTH(407),

		/**
		 * HTTP Status-Code 408: Request Time-Out.
		 */
		HTTP_CLIENT_TIMEOUT(408),

		/**
		 * HTTP Status-Code 409: Conflict.
		 */
		HTTP_CONFLICT(409),

		/**
		 * HTTP Status-Code 410: Gone.
		 */
		HTTP_GONE(410),

		/**
		 * HTTP Status-Code 411: Length Required.
		 */
		HTTP_LENGTH_REQUIRED(411),

		/**
		 * HTTP Status-Code 412: Precondition Failed.
		 */
		HTTP_PRECON_FAILED(412),

		/**
		 * HTTP Status-Code 413: Request Entity Too Large.
		 */
		HTTP_ENTITY_TOO_LARGE(413),

		/**
		 * HTTP Status-Code 414: Request-URI Too Large.
		 */
		HTTP_REQ_TOO_LONG(414),

		/**
		 * HTTP Status-Code 415: Unsupported Media Type.
		 */
		HTTP_UNSUPPORTED_TYPE(415),
		//endregion

		//region 5XX: server error

		/**
		 * HTTP Status-Code 500: Internal Server Error.
		 * @deprecated   it is misplaced and shouldn't have existed.
		 */
		@Deprecated
		HTTP_SERVER_ERROR(500),

		/**
		 * HTTP Status-Code 500: Internal Server Error.
		 */
		HTTP_INTERNAL_ERROR(500),

		/**
		 * HTTP Status-Code 501: Not Implemented.
		 */
		HTTP_NOT_IMPLEMENTED(501),

		/**
		 * HTTP Status-Code 502: Bad Gateway.
		 */
		HTTP_BAD_GATEWAY(502),

		/**
		 * HTTP Status-Code 503: Service Unavailable.
		 */
		HTTP_UNAVAILABLE(503),

		/**
		 * HTTP Status-Code 504: Gateway Timeout.
		 */
		HTTP_GATEWAY_TIMEOUT(504),

		/**
		 * HTTP Status-Code 505: HTTP Version Not Supported.
		 */
		HTTP_VERSION(505);
		//endregion

		final int code;

		StatusCode(int code) {
			this.code = code;
		}

		/**
		 * Http status code value.
		 * @return http status code.
		 */
		public int getCode() {
			return code;
		}

		private static Iterable<StatusCode> filtered(int group) {
			return filtered(group *= 100, group + 99);
		}

		private static Iterable<StatusCode> filtered(int from, int until) {
			return () -> new Iterator<StatusCode>() {

				final Object lock;
				StatusCode[] arr;
				StatusCode curr;
				int i, len;

				{
					lock = new Object();
				}

				private boolean checkNext(){
					if(arr == null) {
						arr = values();
						len = arr.length;
					}

					if(curr == null && i < len) {
						StatusCode aux = arr[i++];
						int code = aux.getCode();
						if(code >= from && code <= until) {
							curr = aux;
						} else {
							return checkNext();
						}
					}

					return curr != null;
				}

				@Override
				public boolean hasNext() {
					synchronized (lock) {
						return checkNext();
					}
				}

				@Override
				public StatusCode next() {
					synchronized (lock) {
						if (checkNext()) {
							StatusCode aux = curr;
							curr = null;
							return aux;
						}
						throw new NoSuchElementException();
					}
				}
			};
		}

		/**
		 * List all status from Group 2XX.
		 * @return status from group 2XX.
		 */
		public static Iterable<StatusCode> list2XX() {
			return filtered(2);
		}

		/**
		 * List all status from Group 3XX.
		 * @return status from group 3XX.
		 */
		public static Iterable<StatusCode> list3XX() {
			return filtered(3);
		}

		/**
		 * List all status from Group 4XX.
		 * @return status from group 4XX.
		 */
		public static Iterable<StatusCode> list4XX() {
			return filtered(4);
		}

		/**
		 * List all status from Group 5XX.
		 * @return status from group 5XX.
		 */
		public static Iterable<StatusCode> list5XX() {
			return filtered(5);
		}

		/**
		 * Parse and convert code to status code type.
		 * @param code status code int.
		 * @return status code enum type.
		 */
		public static StatusCode valueOf(int code) {
			for(StatusCode sc : filtered(code, code)) return sc;
			throw new IllegalArgumentException("Status Code " + code + " Not found ");
		}

		@Override
		public String toString() {
			return code + " - " + name();
		}
	}

	/**
	 * HTTP Method Types.
	 */
	public enum RequestType {

		/**
		 * Read
		 */
		GET,

		/**
		 * Create
		 */
		POST,

		/**
		 * Update/Replace
		 */
		PUT,

		/**
		 * Partial Update/Motiffy
		 */
		PATCH,

		/**
		 * Delete.
		 */
		DELETE
	}

	/**
	 * <strong>Content Type</strong><br>
	 * <p>
	 *    In responses, a Content-Type header tells the client what the content type of the
	 *    returned content actually is. Browsers will do MIME sniffing in some cases and
	 *    will not necessarily follow the value of this header
	 * </p>
	 * see more <a href="https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Headers/Content-Type">here</a>
	 */
	public enum ContentType {
		/**
		 * Content is a form urlenconded.
		 */
		XWWW_FORM_URLENCODED("application/x-www-form-urlencoded"),

		/**
		 * Content is a simple text plan.
		 */
		TEXT("text/plain"),

		/**
		 * Content is bson format.
		 */
		BSON("application/bson"),

		/**
		 * Content is a java archive.
		 */
		JAVA_ARCHIVE("application/java-archive"),

		/**
		 * Content is a javascript format.
		 */
		JAVASCRIPT("application/javascript"),

		/**
		 * Content is a json format.
		 */
		JSON("application/json"),

		/**
		 * Content is a ld+json format.
		 */
		LD_JSON("application/ld+json"),

		/**
		 * Content is a stream.
		 */
		OCTET_STREAM("application/octet-stream"),

		/**
		 * Content is a ogg file format.
		 */
		OGG("application/ogg"),

		/**
		 * Content is a pdf file format.
		 */
		PDF("application/pdf"),

		/**
		 * Content is a xhtml+xml file format.
		 */
		XHTML_XML("application/xhtml+xml"),

		/**
		 * Content is a xml file format.
		 */
		XML("application/xml"),

		/**
		 * Content is a zip file format.
		 */
		ZIP("application/zip");

		final String str;

		/**
		 * Constructor
		 * @param str content type pattern
		 */
		ContentType(String str) {
			this.str = str;
		}

		String getValue() {
			return this.str;
		}

		/**
		 * Generate content type by mime type name.
		 * @param name content type mime type name
		 * @return instance of content type from mime type name.
		 */
		public static ContentType fromType(String name) {
			if(!StringUtils.isNullOrWhitespace(name)) {
				int fIndex = name.indexOf('/');
				int lIndex = name.indexOf(';');
				String part = fIndex != -1 ? name.substring(fIndex + 1,
						lIndex != -1 ? lIndex : name.length() - 1) : null;
				for(ContentType type : values()) {
					String value = type.getValue();
					if(value.equalsIgnoreCase(name) ||
							value.substring(value.indexOf('/') + 1).equalsIgnoreCase(part)) {
						return type;
					}
				}
			}
			return ContentType.XWWW_FORM_URLENCODED;
		}

		@Override
		public String toString() {
			return str;
		}

		Serializer.Type getSerializerType(){
			switch (this) {
				case BSON:
					return Serializer.Type.BSON;
				case JSON:
					return Serializer.Type.JSON;
				case XML:
					return Serializer.Type.XML;
				case OCTET_STREAM:
					return Serializer.Type.BASE64;
				default:
					throw new UnsupportedOperationException("Content type \""+ this + "\" does not supports serialization!");
			}
		}
	}

	/**
	 * Secure protocols for HTTPS connections.
	 */
	public enum SecureProtocols {

		/**
		 * Transport Layer Security (TLS) is a protocol that can be used with
		 * other protocols like UDP to provide security between applications
		 * communicating over an IP network.<br>
		 * <p>
		 * The first version TLS 1.0 was defined in 1999 and it built on
		 * previous work on Secure Socket Layer (SSL). <br><br>
		 * TLS 1.1 was then released in 2006. A common TLS versions used on
		 * the Internet today is TLS 1.2 (defined in IETF RFC 5246 from 2008)
		 * but support for TLS 1.3 (defined in IETF RFC 8446 from 2018) is
		 * becoming more common. <br><br>
		 * TLS is used in the 5GC to protect the HTTP-based interfaces.
		 * 3GPP allows TLS 1.1, TLS 1.2 and TLS 1.3 to be used, even though the use
		 * of <i>TLS 1.1 is not recommended</i>.
		 * </p><br>
		 * <a href="https://www.sciencedirect.com/topics/computer-science/transport-layer-security">more here</a>
		 */
		TLS,

		/**
		 * Transport Layer Security (TLS) provide security between applications
		 * communicating over an IP network.<br>
		 * The first version TLS 1.0 was defined in 1999 and it built on previous
		 * work on Secure Socket Layer (SSL).<br><br>
		 * <i>Warning: the use of TLS 1.1 is not recommended</i><br><br>
		 * <a href="https://www.sciencedirect.com/topics/computer-science/transport-layer-security">more here</a>
		 */
		TLSv1,

		/**
		 * Transport Layer Security (TLS) provide security between applications
		 * communicating over an IP network.<br>
		 * TLS 1.1 was then released in 2006<br>
		 * <a href="https://www.sciencedirect.com/topics/computer-science/transport-layer-security">more here</a>
		 */
		TLSv1_1,

		/**
		 * Transport Layer Security (TLS) provide security between applications
		 * communicating over an IP network.<br>
		 * A common TLS versions used on the Internet today is
		 * TLS 1.2 (defined in IETF RFC 5246 from 2008).<br>
		 * <a href="https://www.sciencedirect.com/topics/computer-science/transport-layer-security">more here</a>
		 */
		TLSv1_2,

		/**
		 * Transport Layer Security (TLS) provide security between applications
		 * communicating over an IP network.<br>
		 * Support for TLS 1.3 (defined in IETF RFC 8446 from 2018) is becoming more common.
		 * TLS is used in the 5GC to protect the HTTP-based interfaces.
		 * 3GPP allows TLS 1.1, TLS 1.2 and TLS 1.3 to be used, even though the use of
		 * <i> TLS 1.1 is not recommended</i>.<br>
		 * <a href="https://www.sciencedirect.com/topics/computer-science/transport-layer-security">more here</a>
		 */
		TLSv1_3,

		/**
		 * Secure Sockets Layer (SSL) was designed to protect HTTP (Hypertext Transfer Protocol)
		 * data: HTTPS uses TCP port 443.<br>
		 * SSL was developed for the Netscape Web browser in the 1990s.<br><br>
		 * <i>TLS (Transport Layer Security) is the latest version of SSL,
		 * equivalent to SSL version 3.1</i><br><br>
		 * <a href="https://www.sciencedirect.com/topics/computer-science/transport-layer-security">more here</a>
		 */
		SSL,

		/**
		 * Secure Sockets Layer (SSL) was designed to protect HTTP (Hypertext Transfer Protocol)
		 * data: HTTPS uses TCP port 443.<br>
		 * SSL was developed for the Netscape Web browser in the 1990s.<br><br>
		 * SSL 2.0 was the first released version.<br><br>
		 * <a href="https://www.sciencedirect.com/topics/computer-science/transport-layer-security">more here</a>
		 */
		SSLv2,

		/**
		 * Secure Sockets Layer (SSL) was designed to protect HTTP (Hypertext Transfer Protocol)
		 * data: HTTPS uses TCP port 443.<br>
		 * SSL was developed for the Netscape Web browser in the 1990s.<br><br>
		 * <i>TLS (Transport Layer Security) is the latest version of SSL,
		 * equivalent to SSL version 3.1</i><br><br>
		 * <a href="https://www.sciencedirect.com/topics/computer-science/transport-layer-security">more here</a>
		 */
		SSLv3;

		/**
		 * Check if current secure protocol is a TLS type.
		 * @return true, protocol is TLS, otherwise, false.
		 */
		public boolean isTLS(){
			switch (this){
				case TLS:
				case TLSv1_1:
				case TLSv1_2:
					return true;
				default:
					return false;
			}
		}

		/**
		 * Get protocol.
		 * @return protocol type and version.
		 */
		public String getProtocol() {
			return name().replace('_', '.');
		}
	}

	/**
	 * Cache modes.
	 */
	public enum CacheMode {
		/**
		 * Keep cache data in memory
		 */
		MEMORY,

		/**
		 * Keep cache data stored releasing memory space.
		 */
		STORED
	}

	static  {
		BUFFER_LENGTH = 2048;
		DEFAULT_CACHE_MAX_AGE_IN_SEC = 3600;
		CACHE_ID = UUID.randomUUID().hashCode();
	}

	{
		connectionTimeOut = readTimeOut = 5000;
		proxyLock = new Object();
		cookieLock = new Object();
	}

	/**
	 * Default construtor.
	 */
	public HttpConnection() {
		this.charset 		= Charset.defaultCharset();
		this.contentType 	= ContentType.XWWW_FORM_URLENCODED.getValue();
		this.acceptRespCode = toArray(StatusCode.list2XX());
	}

	/**
	 * Enable proxy usage.
	 * @return current instance.
	 */
	public HttpConnection useProxy() {
		this.useProxy = true;
		return this;
	}

	/**
	 * Enable basic authentication Bearer.
	 * @return current instance.
	 */
	public HttpConnection useBasicAuth() {
		this.isUseBasicAuth = true;
		return this;
	}

	/**
	 * Enable cache for requests (GET method)
	 * with default max age ({@link #DEFAULT_CACHE_MAX_AGE_IN_SEC}).
	 * @return current instance.
	 */
	public HttpConnection useCache() {
		this.useCache = true;
		if(cacheMode == null) cacheMode = CacheMode.MEMORY;
		if(cacheId == 0) cacheId = CACHE_ID;
		return cacheTimeUnit != null ? this :
				this.setCacheMaxAge(DEFAULT_CACHE_MAX_AGE_IN_SEC, TimeUnit.SECONDS);
	}

	/**
	 * Enable secure context connection (SSL/TLS).
	 * @return current instance
	 */
	public HttpConnection useSecureContext() {
		this.useSecureContext = true;
		if(protocol == null) protocol = SecureProtocols.TLS;
		if(secureContextCredentials == null) secureContextCredentials = SecureContextCredentials.builder().build();
		return this;
	}

	/**
	 * Enable ssl/tls protocol for https connection.
	 * @param protocol protocol enabled.
	 * @return current instance.
	 */
	public HttpConnection setSecureProtocol(SecureProtocols protocol) {
		this.protocol = Objects.requireNonNull(protocol);
		return this;
	}

	/**
	 * Secure context credentials to start a SSL/TLS connection.
	 * @param keystoreFile keystore file full path.
	 * @param password keystore file password.
	 * @return current instance.
	 */
	public HttpConnection setSecureCredentials(String keystoreFile, String password) {
		this.secureContextCredentials = SecureContextCredentials.builder()
				.addClientKeyStore(keystoreFile)
				.addClientPassword(password)
				.build();
		return this;
	}

	/**
	 * Secure context credentials to start a SSL/TLS connection.
	 * @param keystoreFile keystore file full path.
	 * @return current instance.
	 */
	public HttpConnection setSecureCredentials(String keystoreFile) {
		return setSecureCredentials(keystoreFile, null);
	}

	/**
	 * Secure context credentials to start a SSL/TLS connection.
	 * @param keystore keystore file.
	 * @param password keystore file password.
	 * @return current instance.
	 */
	public HttpConnection setSecureCredentials(KeyStore keystore, String password) {
		this.secureContextCredentials = SecureContextCredentials.builder()
				.addClientKeyStore(keystore)
				.addClientPassword(password)
				.build();
		return this;
	}

	/**
	 * Secure context credentials to start a SSL/TLS connection.
	 * @param keystore keystore file.
	 * @param trustStore server keystore we trust.
	 * @param password keystore file password.
	 * @return current instance.
	 */
	public HttpConnection setSecureCredentials(KeyStore keystore, KeyStore trustStore, String password) {
		this.secureContextCredentials = SecureContextCredentials.builder()
				.addClientKeyStore(keystore)
				.addClientPassword(password)
				.addClientTrustStore(trustStore)
				.build();
		return this;
	}

	/**
	 * Secure context credentials to start a SSL/TLS connection.
	 * @param keystore keystore file.
	 * @return current instance.
	 */
	public HttpConnection setSecureCredentials(KeyStore keystore) {
		return setSecureCredentials(keystore, null);
	}

	/**
	 * Set credential for basic or proxy authentication.
	 * @param username user name.
	 * @param password user password.
	 * @return current instance.
	 */
	public HttpConnection setCredentials(String username, String password) {
		Authenticator.setDefault(new ProxyAuthenticator(
				this.username = StringUtils.requireNonNullOrWhitespace(username, "Authentication: User name not set!"),
				this.password = StringUtils.requireNonNullOrEmpty(password, "Authentication: Password not set!")));
		return this;
	}

	/**
	 * <p>Set cache id.</p>
	 * <i>Warning: Use cache id whether you have to isolate cache, otherwise
	 * does not set it to use shared cache for all HttpConnection cached.</i>
	 * @param cacheId cache id
	 * @return current instance.
	 * @throws IllegalArgumentException throws if cache id is equals 0 (zero).
	 */
	public HttpConnection setCacheId(int cacheId) {
		if(cacheId == 0) throw new IllegalArgumentException("Cache id can not be equals 0 (zero)!");
		this.cacheId = cacheId;
		return this;
	}

	/**
	 * Set cache max age by target request.
	 * @param cacheMaxAge cache max age.
	 * @param cacheTimeUnit cache max age time unit.
	 * @return current instance.
	 * @throws IllegalArgumentException throws when cache max age is less or equals zero.
	 * @throws NullPointerException throws when cache time unit is null.
	 */
	public HttpConnection setCacheMaxAge(long cacheMaxAge, TimeUnit cacheTimeUnit) {
		if(cacheMaxAge <= 0) throw new IllegalArgumentException("Max age can not be equals or less then 0!");
		if(cacheTimeUnit == null) throw new NullPointerException();
		this.cacheMaxAge = cacheMaxAge;
		this.cacheTimeUnit = cacheTimeUnit;
		return this;
	}

	/**
	 * Set cache mode.
	 * @param mode cache mode
	 * @return current instance.
	 * @throws NullPointerException throws when cache mode is null.
	 */
	public HttpConnection setCacheMode(CacheMode mode) {
		this.cacheMode = Objects.requireNonNull(mode);
		return this;
	}

	/**
	 * List all cookies.
	 * @return all cookies stored.
	 */
	public List<String> getCookies() {
		return cookies;
	}

	/**
	 * Get charset
	 * @return current charset.
	 */
	public Charset getCharset() {
		return charset;
	}

	/**
	 * Update charset
	 * @param charset new charset
	 * @return current http connection reference.
	 */
	public HttpConnection setCharset(Charset charset) {
		this.charset = Objects.requireNonNull(charset);
		return this;
	}

	/**
	 * Get current content type
	 * @return content type
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Set content type
	 * @param contentType new content type
	 * @return current http connection reference.
	 */
	public HttpConnection setContentType(String contentType) {
		this.contentType = StringUtils.requireNonNullOrWhitespace(contentType);
		return this;
	}

	/**
	 * Set content type
	 * @param contentType new content type
	 * @return current http connection reference.
	 */
	public HttpConnection setContentType(ContentType contentType) {
		this.contentType = contentType.getValue();
		return this;
	}

	/**
	 * Set charset by name.
	 * @param charsetName charset name
	 * @return current http connection reference.
	 */
	public HttpConnection setCharsetForName(String charsetName) {
		this.charset = Charset.forName(charsetName);
		return this;
	}

	/**
	 * Update connection timeout
	 * @param connectionTimeOut timeout in millis
	 * @return current http connection reference.
	 */
	public HttpConnection changeConnectionTimeOut(int connectionTimeOut) {
		this.connectionTimeOut = connectionTimeOut;
		return this;
	}

	/**
	 * Update read response timeout
	 * @param readTimeOut timeout in millis
	 * @return current http connection reference.
	 */
	public HttpConnection changeReadTimeOut(int readTimeOut) {
		this.readTimeOut = readTimeOut;
		return this;
	}

	/**
	 * Check if is keep alive session.
	 * @return inform whether keep session alive.
	 */
	public boolean isKeepAlive() {
		return this.isKeepAlive;
	}

	/**
	 * Set keep alive (session).
	 * @param isKeepAlive true for keep alive.
	 * @return current http connection reference.
	 */
	public HttpConnection setKeepAlive(boolean isKeepAlive) {
		this.isKeepAlive = isKeepAlive;
		return this;
	}

	/**
	 * Recover if use cookis between requests ({@link CookieManager}).
	 * @return true for use cookis between request.
	 */
	public boolean isUseCookieBetweenRequest() {
		return isUseCookieBetweenRequest;
	}

	/**
	 * Set wheter have to reuse cookies ({@link CookieManager}) between requests,
	 * must when desire keep session.
	 * @param isUseCookieBetweenRequest true for use cookis between request.
	 * @return current http connection reference.
	 */
	public HttpConnection setUseCookieBetweenRequest(boolean isUseCookieBetweenRequest) {
		this.isUseCookieBetweenRequest = isUseCookieBetweenRequest;
		return this;
	}

	/**
	 * Set http response code to be accept how sucess response.
	 * @param code http response code.
	 * @return current http connection reference.
	 */
	public HttpConnection setAcceptHttpResponseCode(StatusCode code) {
		if(!containsHttpResponseCode(code)) {
			StatusCode[] aux = acceptRespCode;
			acceptRespCode = new StatusCode[acceptRespCode.length + 1];
			System.arraycopy(aux, 0, acceptRespCode, 0, aux.length);
			acceptRespCode[aux.length] = code;
		}
		return this;
	}

	private StatusCode[] toArray(Iterable<StatusCode> it) {
		ArrayList<StatusCode> list = new ArrayList<>();
		for(StatusCode sc : it) {
			list.add(sc);
		}
		return list.toArray(new StatusCode[0]);
	}

	private boolean containsHttpResponseCode(StatusCode code) {
		for(StatusCode c : acceptRespCode) {
			if(c == code) {
				return true;
			}
		}
		return false;
	}

	private String[] proxyHostAndPort(String url) {
		String regex = "(http[s]?://|ftp[s]?://|url://)?((?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)|\\D*):[0-9]*";
		String aux = url.replaceAll(regex, "");// techo a ser removido;
		url = url.replace(aux, "");
		url = url.replaceAll("(http[s]?://|ftp://|url://)", "");

		if (url.trim().length() > 0) {
			return url.trim().split(":");
		}

		return null;
	}

	private void proxyAuthentication(URL url) throws URLConnectionException {
		synchronized (proxyLock) {
			if (useProxy && PROXY_HOST == null && PROXY_PORT == null) {
				String[] var;
				if ((var = this.proxyHostAndPort(url.toString())) != null) {
					PROXY_HOST = var[0];
					PROXY_PORT = var[1];

					System.setProperty("http.proxyHost", PROXY_HOST);
					System.setProperty("http.proxyPort", String.valueOf(PROXY_PORT));
				} else {
					throw new URLConnectionException("Was not possible discovery proxy HOST and PORT!");
				}
			} else {
				System.clearProperty("http.proxyHost");
				System.clearProperty("http.proxyPort");
			}
		}
	}

	/**
	 * Clear proxy properties.<br>
	 * <i>This method will be always called after close connection.
	 * Prevent conflits with another connections.</i>
	 */
	private void clearProxy() {
		synchronized (proxyLock) {
			System.clearProperty("http.proxyHost");
			System.clearProperty("http.proxyPort");
		}
	}

	/**
	 * Set HEADER parameters.
	 * @param con current connection.
	 * @param params all parameters to be filtered.
	 */
	private void setHeaderParameters(HttpURLConnection con, Parameter[] params) {
		params = this.filterParameters(params, ParameterType.HEADER);
		if(params != null) {
			for(Parameter p : params) {
				con.setRequestProperty(p.getName(), String.valueOf(p.getContent()));
			}
		}
	}

	private String getBase64Auth() {
		return "basic " + new String(Base64.getEncoder().encode((username + ":" + password).getBytes()), charset);
	}

	private void configSecureContext(HttpURLConnection con) throws SecureContextCredentialsException {
		if(useSecureContext && con instanceof HttpsURLConnection) {
			((HttpsURLConnection) con).setSSLSocketFactory(
					secureContextCredentials
							.initContext(protocol)
							.getSocketFactory());
		}
	}

	/**
	 * Open a new Http connection.
	 * @param url target url
	 * @param type request type.
	 * @param postDataLength for POST/PUT set data length.
	 * @param parms request parameters
	 * @return a new instnace of HttpURLConnection.
	 * @throws URLConnectionException throws when found connection error.
	 */
	private HttpURLConnection openConnection(URL url, RequestType type, int postDataLength, Parameter... parms)
			throws URLConnectionException {
		HttpURLConnection con;

		if (isUseCookieBetweenRequest && cookieManager == null) {
			CookieHandler.setDefault(cookieManager = new CookieManager());
		} else if (!isUseCookieBetweenRequest) {
			cookieManager = null;
			CookieHandler.setDefault(null);
		}

		this.proxyAuthentication(url);

		try {
			con = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			throw new URLConnectionException("Connection attempt error:\n" + e.getMessage());
		}

		try {

			configSecureContext(con);
			String agent = System.getProperty("http.agent");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; pt-BR; rv:1.9.2.7; Linux; "
					+ "U; Android 2.2.1; en-us; Nexus One Build/FRG83) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1 "
					+ (agent != null ? agent : ""));
			con.setRequestProperty("Accept-Encoding", "gzip,deflate");
			con.setRequestProperty("Accept", "application/xhtml+xml,application/xml,application/json,application/x-www-form-urlencoded,text/plain,text/html,text/xml,text/json,text/x-www-form-urlencoded;q=0.9,*/*;q=0.8");
			con.setRequestProperty("Accept-Language", "pt-br,pt;q=0.8,en-us;q=0.5,en;q=0.3");
			con.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
			con.setRequestProperty("Content-Type", this.contentType + "; charset=" + this.charset.name());
			con.setRequestProperty("Connection", isKeepAlive ? "keep-alive" : "close");

			if(isUseBasicAuth) {
				StringUtils.requireNonNullOrWhitespace(username, "AuthBase64: User name not set or invalid!");
				StringUtils.requireNonNullOrEmpty(password, "AuthBase64: User name not set or invalid!");

				String base64Auth = getBase64Auth();
				con.setRequestProperty("Authorization", base64Auth);

				if(useProxy) {
					con.setRequestProperty("Proxy-Authorization", base64Auth);
				}
			}

			if (useProxy) {
				con.setRequestProperty("Proxy-Connection", isKeepAlive ? "keep-alive" : "close");
			}

			if (type.equals(RequestType.POST)) {
				con.setRequestProperty("Content-Length", String.valueOf(postDataLength));
				con.setDoOutput(true);
			}

			con.setDoInput(true);
			con.setRequestMethod(type.toString());
			con.setConnectTimeout(connectionTimeOut);
			con.setReadTimeout(readTimeOut);

		} catch (ProtocolException e) {
			con.disconnect();
			throw new URLConnectionException("Protocol Error:\n" + e.getMessage());
		} catch (Exception ex) {
			throw new URLConnectionException("Connection Error:\n" + ex.getMessage());
		}

		this.setCookiesOnRequest(con);// add cookies.
		this.setHeaderParameters(con, parms);
		return con;
	}

	/**
	 * Insert current cookies to new connection.
	 * @param con new connection.
	 */
	private void setCookiesOnRequest(HttpURLConnection con) {
		synchronized (cookieLock) {
			if (cookies == null) {
				return;
			}

			StringBuilder allCookies = new StringBuilder();
			for (String cookie : cookies) {
				allCookies.append(cookie).append(";");
			}
			con.setRequestProperty("Cookie", allCookies.toString());
		}
	}

	/**
	 * Update cookies from current connection to be
	 * used for another connection request.
	 * @param con current connection.
	 */
	private void updateCookies(HttpURLConnection con) {
		synchronized (cookieLock) {
			try {
				//get cookies from header.
				if (con.getHeaderField("Set-Cookie") != null) {
					String[] newCookies = con.getHeaderField("Set-Cookie").split(";");

					if (cookies != null) {
						//update current cookies
						for (String cookie : cookies) {
							for (int i = 0; i < newCookies.length; i++) {
								if (cookie.equals(newCookies[i])) {
									cookie = newCookies[i];
									newCookies[i] = null;
								}
							}
						}
					} else {
						cookies = new ArrayList<>();
					}
					//add new cookies.
					for (String newCookie : newCookies) {
						if (newCookie != null) {
							cookies.add(newCookie);
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Filter input parameters, returning only requested type.
	 * @param params all parameters
	 * @param type type to filter
	 * @return filted parameters.
	 */
	private Parameter[] filterParameters(Parameter[] params, ParameterType type) {
		if(params == null) {
			return null;
		}

		Parameter[] result = new Parameter[params.length];

		int i=0;
		for(Parameter p : params) {
			if(p.getType() == type) {
				p.parseContent(this);
				result[i++] = p;
			}
		}

		if(i < params.length) {
			Parameter[] aux = result;
			result = new Parameter[i];
			System.arraycopy(aux, 0, result, 0, i);
		}

		return result;
	}

	/**
	 * Add QUERY parameters for URL.
	 * @param url target url
	 * @param params all parameters
	 * @return return new url with QUERY parameters.
	 * @throws MalformedURLException throws when URL is malformed.
	 */
	private URL addParameters(URL url, Parameter... params) throws MalformedURLException {
		params = this.filterParameters(params, ParameterType.QUERY);
		String urlStr = url.toExternalForm();
		StringBuilder queryParams = null;

		for(int i=0, l = params.length; i < l; i++) {
			Parameter p = params[i];
			boolean hasName = p.hasName();
			String pName = "{" + (hasName ? p.getName() : i ) + "}";
			int j = urlStr.indexOf(pName);

			if (j != -1) /*URL Parameter*/ {
				urlStr = urlStr.replace(pName, p.getContentURLEncoded());
			} else if(hasName) /*Query Parameter*/ {
				if (queryParams == null) {
					(queryParams = new StringBuilder()).append(p);
				} else {
					queryParams.append('&').append(p);
				}
			}
		}

		if(queryParams != null) {
			urlStr = queryParams.insert(0, '?').insert(0, urlStr).toString();
		}

		return new URL(urlStr);
	}

	//region getResponse
	private String getParametersFormData(Parameter[] params) {
		StringBuilder builder = new StringBuilder();

		char separator = '&';
		for (int i = 0, c = params.length, l = c - 1; i < c; i++) {
			builder.append(params[i]);

			if (i < l) {
				builder.append(separator);
			}
		}

		return builder.toString();
	}

	private InputStream resolveContentEncoding(String contentEncoding, InputStream in) throws IOException {
		if("gzip".equalsIgnoreCase(contentEncoding)) {
			return new GZIPInputStream(in);
		} else if("deflate".equalsIgnoreCase(contentEncoding)) {
			return new InflaterInputStream(in, new Inflater(true));
		} else {
			return in;
		}
	}

	private InputStream resolveInputStream(HttpURLConnection con) throws URLConnectionException {
		try {
			return resolveContentEncoding(con.getContentEncoding(), con.getInputStream());
		} catch (IOException e) {
			throw new URLConnectionException(
					"An error occurred while attempt to access the page content:\n" + e.getMessage());
		}
	}

	private InputStream resolveErrorStream(HttpURLConnection con) throws URLConnectionException {
		try {
			return resolveContentEncoding(con.getContentEncoding(), con.getErrorStream());
		} catch (IOException e) {
			throw new URLConnectionException(
					"An error occurred while attempt to access the page content:\n" + e.getMessage());
		}
	}

	private void finallyHttpUrlConn(HttpURLConnection con) {
		this.clearProxy();
		this.updateCookies(con);
	}

	private Response.Builder getResponseBuilder() {
		return new Response.Builder()
				.useCharset(this.charset)
				.useBufferLength(BUFFER_LENGTH)
				.useSuccessResponseFun(this::containsHttpResponseCode)
				.useInputStreamFun(this::resolveInputStream)
				.useErrorStreamFun(this::resolveErrorStream)
				.useFinallyAction(this::finallyHttpUrlConn);
	}

	private Response getResponse(HttpURLConnection con) {
		return getResponseBuilder()
				.useConnection(con)
				.build();
	}

	private Response getResponseCached(URL url,
									   ResponseParameter.FunctionIO<URL, HttpURLConnection> conFun) {
		return getResponseBuilder()
				.useUrl(url)
				.useConnectionFun(conFun)
				.useCacheFun(this::getCache)
				.build();
	}
	//endregion

	//region cached Response
	private CacheControl getCache() {
		if(useCache) {
			switch (cacheMode) {
				case MEMORY:
					return CacheControl.memory(cacheId)
							.maxAge(cacheMaxAge, cacheTimeUnit);
				case STORED:
					return CacheControl.stored(cacheId)
							.maxAge(cacheMaxAge, cacheTimeUnit);
				default:
					throw new UnsupportedOperationException();
			}
		}
		return null;
	}
	//endregion

	//region get and send
	private Response get(URL url, Parameter... params) throws URLConnectionException {

		//region prepare url
		try {
			url = addParameters(url, params);
		} catch (MalformedURLException e) {
			throw new URLConnectionException(
					"An error occurred while attempt to add parameters to URL:\n" + e.getMessage());
		}
		//endregion

		//cached or new request
		return this.getResponseCached(url, (u) -> {
			//region new request
			HttpURLConnection con;
			try {
				con = this.openConnection(u, RequestType.GET, 0, params);
				con.connect();
				return con;
			} catch (IOException e) {
				throw new URLConnectionException(
						"An error occurred while attempt to connect on \"" + u + "\" :\n" + e.getMessage());
			}
		});
		//endregion
	}

	private Response get(String url, Parameter... params) throws URLConnectionException {
		try {
			return get(new URL(url), params);
		} catch (MalformedURLException e) {
			throw new URLConnectionException(e);
		}
	}

	private Response send(URL url, RequestType type, byte[] data, Parameter... params) throws URLConnectionException {

		if (data == null || data.length == 0) {
			Parameter[] bodyParams = this.filterParameters(params, ParameterType.BODY);

			if (bodyParams == null) {
				throw new URLConnectionException("Invalid parameters!");
			} else if (bodyParams.length < 1) {
				throw new URLConnectionException("Can not post data, no one parameter was set on Body request!");
			}

			String builder = this.getParametersFormData(bodyParams);
			data = builder.getBytes(this.charset);
		}

		HttpURLConnection con = null;
		OutputStream out;

		try {

			url = addParameters(url, params);
			con = this.openConnection(url, type, data.length, params);

			out = con.getOutputStream();
			out.write(data);
			out.flush();
			out.close();

			return this.getResponse(con);
		} catch (IOException e) {
			if(con != null) con.disconnect();
			throw new URLConnectionException(
					"An error occurred while attempt to post data:\n" + e.getMessage());
		}
	}

	private Response send(String url, RequestType type, byte[] data, Parameter... params) throws URLConnectionException {
		try {
			return send(new URL(url), type, data, params);
		} catch (MalformedURLException e) {
			throw new URLConnectionException(e);
		}
	}

	private Response send(URL url, RequestType type, Parameter... params) throws URLConnectionException {
		return send(url, type, null, params);
	}

	private Response send(String url, RequestType type, Parameter... params) throws URLConnectionException {
		try {
			return send(new URL(url), type, params);
		} catch (MalformedURLException e) {
			throw new URLConnectionException(e);
		}
	}
	//endregion

	//region getContent
	/**
	 * Do a request (GET Method) on {@link URL} with {@link Parameter}
	 * set and recover a {@link Response} with result.
	 * @param url target url
	 * @param params parameters
	 * @return a response with request result.
	 * @throws URLConnectionException throws when some connection error is found.
	 */
	public Response getContent(URL url, Parameter... params) throws URLConnectionException {
		return get(url, params);
	}

	/**
	 * Do a request (GET Method) on {@link URL} with {@link Parameter}
	 * set and recover a {@link Response} with result.
	 * @param url target url
	 * @param params parameters
	 * @return a response with request result.
	 * @throws URLConnectionException throws when some connection error is found.
	 */
	public Response getContent(String url, Parameter... params) throws URLConnectionException {
		return get(url, params);
	}
	//endregion

	//region post, put, patch, delete - data on body
	/**
	 * Send data (POST Method)
	 * @param url target url
	 * @param data data to be send
	 * @return response with post result.
	 * @throws URLConnectionException throws when some connection error is found.
	 */
	public Response postContent(URL url, byte[] data) throws URLConnectionException {
		return this.send(url, RequestType.POST, data);
	}

	/**
	 * Update data (PUT Method)
	 * @param url target url
	 * @param data data to be updated
	 * @return response with put result.
	 * @throws URLConnectionException throws when some connection error is found.
	 */
	public Response putContent(URL url, byte[] data) throws URLConnectionException {
		return this.send(url, RequestType.PUT, data);
	}

	/**
	 * Send data (PATCH Method)
	 * @param url target url
	 * @param data data to be updated
	 * @return response with result.
	 * @throws URLConnectionException throws when some connection error is found.
	 */
	public Response patchContent(URL url, byte[] data) throws URLConnectionException {
		return this.send(url, RequestType.PATCH, data);
	}

	/**
	 * Delete data (DELETE Method)
	 * @param url target url
	 * @param data data to be deleted
	 * @return response with result.
	 * @throws URLConnectionException throws when some connection error is found.
	 */
	public Response deleteContent(URL url, byte[] data) throws URLConnectionException {
		return this.send(url, RequestType.DELETE, data);
	}

	/**
	 * Send data (POST Method)
	 * @param url target url
	 * @param data data to be send
	 * @return response with post result.
	 * @throws URLConnectionException throws when some connection error is found.
	 */
	public Response postContent(String url, byte[] data) throws URLConnectionException {
		return this.send(url, RequestType.POST, data);
	}

	/**
	 * Update data (PUT Method)
	 * @param url target url
	 * @param data data to be updated
	 * @return response with put result.
	 * @throws URLConnectionException throws when some connection error is found.
	 */
	public Response putContent(String url, byte[] data) throws URLConnectionException {
		return this.send(url, RequestType.PUT, data);
	}

	/**
	 * Send data (PATCH Method)
	 * @param url target url
	 * @param data data to be updated
	 * @return response with result.
	 * @throws URLConnectionException throws when some connection error is found.
	 */
	public Response patchContent(String url, byte[] data) throws URLConnectionException {
		return this.send(url, RequestType.PATCH, data);
	}

	/**
	 * Delete data (DELETE Method)
	 * @param url target url
	 * @param data data to be deleted
	 * @return response with result.
	 * @throws URLConnectionException throws when some connection error is found.
	 */
	public Response deleteContent(String url, byte[] data) throws URLConnectionException {
		return this.send(url, RequestType.DELETE, data);
	}
	//endregion

	//region post, put, patch, delete - parameters
	/**
	 * Send parameters data
	 * @param url target url
	 * @param params optional parameters
	 * @return response with post result.
	 * @throws URLConnectionException throws when some connection error is found.
	 */
	public Response postContent(URL url, Parameter... params) throws URLConnectionException {
		return this.send(url, RequestType.POST, params);
	}

	/**
	 * Update data (PUT Method)
	 * @param url target url
	 * @param params optional parameters
	 * @return response with put result.
	 * @throws URLConnectionException throws when some connection error is found.
	 */
	public Response putContent(URL url, Parameter... params) throws URLConnectionException {
		return this.send(url, RequestType.PUT, params);
	}

	/**
	 * Send data (PATCH Method)
	 * @param url target url
	 * @param params parameters
	 * @return response with result.
	 * @throws URLConnectionException throws when some connection error is found.
	 */
	public Response patchContent(URL url, Parameter... params) throws URLConnectionException {
		return this.send(url, RequestType.PATCH, params);
	}

	/**
	 * Delete data (DELETE Method)
	 * @param url target url
	 * @param params optional parameters
	 * @return response with result.
	 * @throws URLConnectionException throws when some connection error is found.
	 */
	public Response deleteContent(URL url, Parameter... params) throws URLConnectionException {
		return this.send(url, RequestType.DELETE, params);
	}

	/**
	 * Send parameters data
	 * @param url target url
	 * @param params optional parameters
	 * @return response with post result.
	 * @throws URLConnectionException throws when some connection error is found.
	 */
	public Response postContent(String url, Parameter... params) throws URLConnectionException {
		return this.send(url, RequestType.POST, params);
	}

	/**
	 * Update data (PUT Method)
	 * @param url target url
	 * @param params optional parameters
	 * @return response with put result.
	 * @throws URLConnectionException throws when some connection error is found.
	 */
	public Response putContent(String url, Parameter... params) throws URLConnectionException {
		return this.send(url, RequestType.PUT, params);
	}

	/**
	 * Send data (PATCH Method)
	 * @param url target url
	 * @param params parameters
	 * @return response with result.
	 * @throws URLConnectionException throws when some connection error is found.
	 */
	public Response patchContent(String url, Parameter... params) throws URLConnectionException {
		return this.send(url, RequestType.PATCH, params);
	}

	/**
	 * Delete data (DELETE Method)
	 * @param url target url
	 * @param params parameters
	 * @return response with result.
	 * @throws URLConnectionException throws when some connection error is found.
	 */
	public Response deleteContent(String url, Parameter... params) throws URLConnectionException {
		return this.send(url, RequestType.DELETE, params);
	}
	//endregion
}