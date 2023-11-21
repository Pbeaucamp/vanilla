package bpm.forms.design.ui.tools.converters;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.databinding.conversion.Converter;

public class DateToTextConverter extends Converter{

	
	private SimpleDateFormat sdf;
	
	public DateToTextConverter(String dateFormat) {
		super(Date.class, String.class);
		sdf = new SimpleDateFormat(dateFormat);
	}

	@Override
	public Object convert(Object fromObject) {
		if (fromObject == null){
			return ""; //$NON-NLS-1$
		}
		Date d = (Date)fromObject;
		
		return sdf.format(d);

	} 

}
