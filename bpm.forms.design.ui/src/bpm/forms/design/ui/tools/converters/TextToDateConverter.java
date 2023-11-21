package bpm.forms.design.ui.tools.converters;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.databinding.conversion.Converter;

public class TextToDateConverter extends Converter{

	private SimpleDateFormat sdf;
	
	public TextToDateConverter(String dateFormat) {
		super(String.class, Date.class);
		this.sdf = new SimpleDateFormat(dateFormat);

	}

	@Override
	public Object convert(Object fromObject) {
		if (fromObject == null){
			return null;
		}

		try{
			return sdf.parse((String)fromObject);
		}catch(Exception ex){
			return null;
		}
		
	}

	@Override
	public Object getFromType() {
		
		return super.getFromType();
	}
	@Override
	public Object getToType() {
		
		return super.getToType();
	}
	
}
