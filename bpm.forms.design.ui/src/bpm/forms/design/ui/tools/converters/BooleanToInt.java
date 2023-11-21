package bpm.forms.design.ui.tools.converters;

import org.eclipse.core.databinding.conversion.Converter;

public class BooleanToInt extends Converter{

	public BooleanToInt() {
		super(Boolean.class, int.class);
		
	}

	@Override
	public Object convert(Object fromObject) {
		if (fromObject == null){
			return 0;
		}
		
		if ((Boolean)fromObject){
			return 1;
		}
		return 0;
	}

}
