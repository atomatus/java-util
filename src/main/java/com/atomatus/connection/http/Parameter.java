package com.atomatus.connection.http;

import com.atomatus.util.StringUtils;

import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Request Parameter.
 * @author Carlos Matos
 */
public class Parameter {

	private final String name;
	private final Object content;
	private final ParameterType type;

	public static Parameter buildQuery(Object content) {
		return new Parameter(null, content, ParameterType.QUERY);
	}

	public static Parameter buildQuery(String name, Object content) {
		return new Parameter(name, content, ParameterType.QUERY);
	}

	public static Parameter buildBody(String name, Object content) {
		return new Parameter(name, content, ParameterType.BODY);
	}
	
	public static Parameter buildHeader(String name, Object content) {
		return new Parameter(name, content, ParameterType.HEADER);
	}
	
	/**
	 * Parameter type
	 */
	public enum ParameterType {
		QUERY,
		HEADER,
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
	
	public Parameter(String name, Object content) {
		this(name, content, ParameterType.QUERY);
	}
		
	protected String getName() {
		return name;
	}
		
	protected Object getContent() {
		return content;
	}

	protected String getContentURLEncoded() {
		return formatContent();
	}

	protected boolean hasName() {
		return !StringUtils.isNullOrEmpty(name);
	}

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