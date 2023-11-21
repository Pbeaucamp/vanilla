package bpm.gateway.ui.views.property;

import org.eclipse.ui.views.properties.tabbed.ITypeMapper;

public class ModelTypeMapper implements ITypeMapper {

	public Class mapType(Object object) {
		
		return object.getClass();
	}

}
