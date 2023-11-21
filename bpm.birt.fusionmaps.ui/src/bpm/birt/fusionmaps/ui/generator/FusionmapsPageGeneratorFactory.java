package bpm.birt.fusionmaps.ui.generator;

import org.eclipse.birt.report.designer.ui.views.IPageGenerator;
import org.eclipse.core.runtime.IAdapterFactory;

public class FusionmapsPageGeneratorFactory implements IAdapterFactory{

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		return new FusionmapsPageGenerator( );
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[]{
				IPageGenerator.class
			};
	}
}
