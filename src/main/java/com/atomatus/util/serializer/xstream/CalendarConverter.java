package com.atomatus.util.serializer.xstream;

import com.atomatus.util.DateHelper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.Calendar;

/**
 * Convert xml node value to {@link Calendar}.
 * @author Carlos Matos
 *
 */
public final class CalendarConverter implements Converter {

	@Override
	public boolean canConvert(Class c) {
		return Calendar.class.isAssignableFrom(c);
	}

	@Override
	public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
		Calendar calendar = (Calendar) obj;
		String value = DateHelper.getInstance().getDate(calendar.getTime());
		writer.setValue(value);
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		try{
			String value = reader.getValue();
			return DateHelper.getInstance().parseCalendar(value);
        } catch (Exception e) {
        	throw new ConversionException(e.getMessage(), e);
         }
	}
}