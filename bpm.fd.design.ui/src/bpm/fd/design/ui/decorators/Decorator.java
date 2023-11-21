package bpm.fd.design.ui.decorators;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import bpm.fd.api.core.model.IStatuable;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.icons.Icons;

public class Decorator implements ILightweightLabelDecorator {
	public static final String ID = "bpm.fd.design.ui.decorators.Decorator"; //$NON-NLS-1$
	public void decorate(Object element, IDecoration decoration) {
		if (!(element instanceof IStatuable)){
			return;
		}

		ImageDescriptor img = null;
		
		
		if (((IStatuable)element).getStatus() == IStatuable.ERROR){
			img = Activator.getDefault().getImageRegistry().getDescriptor(Icons.error_8);
		}
		else if (((IStatuable)element).getStatus() == IStatuable.UNDEFINED){
			
		}
		
		decoration.addOverlay(img, IDecoration.TOP_RIGHT);
	}

	public void addListener(ILabelProviderListener listener) {
		

	}

	public void dispose() {
		

	}

	public boolean isLabelProperty(Object element, String property) {
		
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		

	}

}
