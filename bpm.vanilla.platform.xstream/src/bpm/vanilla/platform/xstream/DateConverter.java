package bpm.vanilla.platform.xstream;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class DateConverter implements Converter {
	
	private static final String FORMAT_DATE = "dd/MM/yyyy HH:mm:ss";

	public boolean canConvert(Class clazz) {
		// This converter is only for Date fields.
		return Date.class.isAssignableFrom(clazz);
	}

	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_DATE);
		
		Date date = (Date) value;
		writer.setValue(formatter.format(date));
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_DATE);
			return formatter.parse(reader.getValue());
		} catch (Exception e) {
			e.printStackTrace();
//			throw new ConversionException(e.getMessage(), e);
			return new Date();
		}
	}
}