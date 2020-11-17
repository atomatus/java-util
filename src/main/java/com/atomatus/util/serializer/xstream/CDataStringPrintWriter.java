package com.atomatus.util.serializer.xstream;

import com.atomatus.util.Debug;
import com.atomatus.util.RegExp;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

import java.io.Writer;

/**
 * Xml writer with CData specification.
 * Write as PrettyPrintWriter when debugging otherwise use CompactWriter.
 * @author Carlos Matos
 */
public final class CDataStringPrintWriter extends PrettyPrintWriter {

	private boolean isStringClass;

	private static final String PREFIX;
	private static final String SUFFIX;

	static {
		PREFIX = "<![CDATA[";
		SUFFIX = "]]>";
	}

	public CDataStringPrintWriter(Writer writer) {
		super(writer);
	}
	
	public CDataStringPrintWriter(Writer writer, XmlFriendlyNameCoder coder) {
		super(writer, coder);
	}

	@Override
	public void startNode(String name, Class clazz) {
		super.startNode(name, clazz);
		isStringClass = String.class.isAssignableFrom(clazz);
	}
	
	@Override
	protected void writeText(QuickWriter writer, String text) {
		if(text != null){
			if(isStringClass && !RegExp.isValidCDataString(text) && RegExp.isNeedCDataEncapsulationString(text)){
				writer.write(PREFIX);
				writer.write(text);
				writer.write(SUFFIX);
			}
			else{
				super.writeText(writer, text);
			}
		}
	}

	@Override
	protected void endOfLine() {
		// override parent: don't write anything at end of line when release mode.
		if(Debug.isDebugMode()){
			super.endOfLine();
		}
	}
}
