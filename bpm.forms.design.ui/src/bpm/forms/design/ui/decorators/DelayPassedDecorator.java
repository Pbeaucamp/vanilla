package bpm.forms.design.ui.decorators;

import java.util.Date;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import bpm.forms.core.design.IForm;
import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.design.ui.Activator;
import bpm.forms.design.ui.icons.IconsNames;

public class DelayPassedDecorator implements ILightweightLabelDecorator{
	public static final String ID = "bpm.forms.design.ui.decorator1"; //$NON-NLS-1$
	@Override
	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof IForm){
			
			
			try{
				for(IFormInstance f : Activator.getDefault().getServiceProvider().getInstanceService().getRunningInstances((IForm)element)){
					Date exp = f.getExpirationDate();
					if (exp != null && !f.isSubmited()){
						Date d = new Date();
						
						if (d.after(exp)){
							ImageDescriptor img = Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.warning);
							
							decoration.addOverlay(img, IDecoration.TOP_RIGHT);
						}
					}
				}
			}catch(Exception ex){
				
			}
		}
		decoration.addOverlay(null, IDecoration.TOP_RIGHT);
		
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		
		
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		
		
	}
	

	

}
