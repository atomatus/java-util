package com.atomatus.connection.http;

import com.atomatus.util.Debug;
import com.atomatus.util.StringUtils;
import com.atomatus.util.WrapperHelper;
import com.atomatus.util.serializer.Serializer;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Request Parameter.
 * @author Carlos Matos
 */
public class Parameter {

	private final String name;
	private final ParameterType type;
	private Object content;
	private boolean checked;

	/**
	 * <p>
	 * Build a query parameter no name.
	 * </p>
	 * <p>
	 * Query parameters are a defined set of parameters attached to the end of a url.<br/>
	 * They are extensions of the URL that are used to help define specific content or
	 * actions based on the data being passed.<br/>
	 * To append query params to the end of a URL, a '? ' Is added followed immediately by
	 * a query parameter or inner url separated for '/' (slash) char.
	 * </p>
	 * @param content parameter value
	 * @return new query parameter.
	 */
	public static Parameter buildQuery(Object content) {
		return new Parameter(null, content, ParameterType.QUERY);
	}

	/**
	 * <p>
	 * Build a query named parameter.
	 * </p>
	 * <p>
	 * Query parameters are a defined set of parameters attached to the end of a url.<br/>
	 * They are extensions of the URL that are used to help define specific content or
	 * actions based on the data being passed.<br/>
	 * To append query params to the end of a URL, a '? ' Is added followed immediately by
	 * a query parameter or inner url separated for '/' (slash) char.
	 * </p>
	 * @param name parameter name
	 * @param content parameter value
	 * @return new query parameter.
	 */
	public static Parameter buildQuery(String name, Object content) {
		return new Parameter(name, content, ParameterType.QUERY);
	}

	/**
	 * <p>
	 * Build a body named parameter.
	 * </p>
	 * <p>
	 * The body parameter is defined in the operation's parameters section
	 * and includes the following: in: body. schema that describes the body
	 * data type and structure. The data type is usually an object,
	 * but can also be a primitive (such as a string or number) or an array.
	 * </p>
	 * @param name parameter name
	 * @param content parameter value
	 * @return new body parameter.
	 */
	public static Parameter buildBody(String name, Object content) {
		return new Parameter(name, content, ParameterType.BODY);
	}

	/**
	 * <p>
	 * Build a header parameter.
	 * </p>
	 * <p>
	 * HTTP headers let the client and the server pass additional information with an
	 * HTTP request or response. An HTTP header consists of
	 * its case-insensitive name followed by a colon ( : ),
	 * then by its value. Entity headers contain information about the body of
	 * the resource, like its content length or MIME type.
	 * </p>
	 * @param name parameter name
	 * @param content parameter value
	 * @return new header parameter.
	 */
	public static Parameter buildHeader(String name, Object content) {
		return new Parameter(name, content, ParameterType.HEADER);
	}
	
	/**
	 * Parameter type
	 */
	public enum ParameterType {
		/**
		 * Query parameters are a defined set of parameters attached to the end of a url.<br/>
		 * They are extensions of the URL that are used to help define specific content or
		 * actions based on the data being passed.<br/>
		 * To append query params to the end of a URL, a '? ' Is added followed immediately by
		 * a query parameter.
		 */
		QUERY,

		/**
		 * HTTP headers let the client and the server pass additional information with an
		 * HTTP request or response. An HTTP header consists of
		 * its case-insensitive name followed by a colon ( : ),
		 * then by its value. Entity headers contain information about the body of
		 * the resource, like its content length or MIME type.
		 */
		HEADER,

		/**
		 * The body parameter is defined in the operation's parameters section
		 * and includes the following: in: body. schema that describes the body
		 * data type and structure. The data type is usually an object,
		 * but can also be a primitive (such as a string or number) or an array.
		 */
		BODY
	}
			
	/**
	 * Constructor.
	 * @param name parameter name
	 * @param content parameter value
	 * @param type parameter type
	 */
	public Parameter(String name, Object content, ParameterType type) {
		super();
		this.name		= type == ParameterType.QUERY ? name : StringUtils.requireNonNullOrEmpty(name);
		this.content	= content;
		this.type		= type;
	}

	/**
	 * Parameter constructs with name and value to query type.
	 * @param name query parameter name
	 * @param content query parameter value
	 */
	public Parameter(String name, Object content) {
		this(name, content, ParameterType.QUERY);
	}

	/**
	 * Recover parameter name.
	 * @return parameter name.
	 */
	protected String getName() {
		return name;
	}

	/**
	 * Recover parameter content.
	 * @return parameter content
	 */
	protected Object getContent() {
		return content;
	}

	/**
	 * Format parameter content to URLEncoded.
	 * @return url encoded.
	 */
	protected String getContentURLEncoded() {
		return formatContent();
	}

	/**
	 * Check parameter has name.
	 * @return true, parameter has name, otherwise, false.
	 */
	protected boolean hasName() {
		return !StringUtils.isNullOrEmpty(name);
	}

	/**
	 * Check if content is a BODY content, when true check if
	 * is an object and current connection requests it to send as serialized content-type,
	 * therefore serialize current content. Otherwise, does not nothing.
	 * @param con current target connection owner of parameter.
	 */
	protected void parseContent(HttpConnection con) {
		if(content != null && !checked && (checked = true) &&
				type == ParameterType.BODY &&
				!WrapperHelper.isWrapper(content)) {
			if(content instanceof Serializable) {
				content = Serializer.getInstance(HttpConnection.ContentType
						.fromType(con.getContentType())
						.getSerializerType())
						.serialize((Serializable) content);
			} else if(Debug.isDebugMode()) {
				System.err.printf("Parameter (%1$s) of HttpConnection could no be serialized " +
						"to %2$s because object on content does not " +
						"implements Serializable interface!\n", name, con.getContentType());
			}
		}
	}

	/**
	 * Parameter type
	 * @return type.
	 */
	public ParameterType getType() {
		return type;
	}
		
	private String formatContent() {
		if (content == null) {
			return "";
		} else if (content.getClass().isArray()) {
			int len = Array.getLength(content);
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < len; i++) {
				if (i > 0) {
					builder.append(",");
				}
				builder.append(Array.get(content, i));
			}
			return this.doURLEncode(builder.toString());
		} else {
			return this.doURLEncode(content.toString());
		}
	}
	
	private String doURLEncode(String content){
		if(type == ParameterType.QUERY || type == ParameterType.BODY) {
			String encode = Charset.defaultCharset().displayName();
		
			try {
				content = URLEncoder.encode(content, encode);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return content;
	}

	@Override
	public String toString() {
		return this.hasName() ? this.getName() + "=" + this.formatContent() : this.formatContent();
	}
}