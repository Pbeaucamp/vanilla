package bpm.forms.design.ui.tools.converters;

import org.eclipse.core.databinding.conversion.Converter;

public class IntToBoolean extends Converter{

	public IntToBoolean() {
		super(int.class, Boolean.class);
		
	}

	@Override
	public Object convert(Object fromObject) {
		if (fromObject == null){
			return false;
		}
		
		if (((Number)fromObject).longValue() == 0){
			return false;
		}
		return true;
	}

}
