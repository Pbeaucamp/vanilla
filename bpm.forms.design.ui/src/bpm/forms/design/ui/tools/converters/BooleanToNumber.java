package bpm.forms.design.ui.tools.converters;

import org.eclipse.core.databinding.conversion.Converter;

public class BooleanToNumber extends Converter{

	public BooleanToNumber() {
		super(Boolean.class, String.class);
		
	}

	@Override
	public Object convert(Object fromObject) {
		if (fromObject == null){
			return ""; //$NON-NLS-1$
		}
		Boolean d = (Boolean)fromObject;
		
		if (d){
			return 1 + ""; //$NON-NLS-1$
		}
		return 0 + ""; //$NON-NLS-1$
	}
	
}
