package bpm.fd.design.ui.viewer.labelprovider;

import org.eclipse.jface.viewers.LabelProvider;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.datas.ParameterDescriptor;
import bpm.fd.api.internal.ILabelable;
import bpm.vanilla.map.core.design.MapVanilla;

public class DatasLabelProvider extends LabelProvider{

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof ParameterDescriptor){
			return ((ParameterDescriptor)element).getLabel();
		}
		if (element instanceof ILabelable){
			return ((ILabelable)element).getLabel();
		}
		if (element instanceof IBaseElement){
			return ((IBaseElement)element).getName();
		}
		if(element instanceof FdModel) {
			return ((FdModel)element).getName();
		}
		if(element instanceof MapVanilla) {
			return ((MapVanilla)element).getName();
		}
		
		return super.getText(element);
	}

	
}
