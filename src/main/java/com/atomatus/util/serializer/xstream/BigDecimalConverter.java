package com.atomatus.util.serializer.xstream;

import java.math.BigDecimal;

import com.atomatus.util.DecimalHelper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Convert xml node value to {@link BigDecimal}.
 * @author Carlos Matos
 *
 */
public class BigDecimalConverter implements Converter {
	
	@Override
	public boolean canConvert(Class c) {
		return BigDecimal.class.isAssignableFrom(c);
	}

	@Override
	public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
		BigDecimal value = (BigDecimal) obj;		
		writer.setValue(DecimalHelper.toDecimal(value));
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		return DecimalHelper.toBigDecimal(reader.getValue());
	}
}
