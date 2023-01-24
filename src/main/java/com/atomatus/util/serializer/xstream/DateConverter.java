package com.atomatus.util.serializer.xstream;

import com.atomatus.util.DateHelper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.Date;

/**
 * Convert xml node value to {@link Date}.
 * @author Carlos Matos {@literal @chcmatos}
 *
 */
public final class DateConverter implements Converter {

	@Override
	public boolean canConvert(Class c) {
		return Date.class.isAssignableFrom(c);
	}

	@Override
	public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
		Date date = (Date) obj;
		String value = DateHelper.getInstance().toISO8601(date);
		writer.setValue(value);
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		try{
			String value = reader.getValue();
			return DateHelper.getInstance().parseDate(value);
        } catch (Exception e) {
        	throw new ConversionException(e.getMessage(), e);
         }
	}
}