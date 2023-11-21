package bpm.fd.design.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import bpm.fd.api.core.model.FdModel;

public class FdProjectEditorInput implements IEditorInput{

	private FdModel model ;
	
	

	
	public FdProjectEditorInput(FdModel model){
		this.model = model;
	}
	
	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		if (model != null){
			try {
				return model.getProject().getProjectDescriptor().getProjectName();
			} catch(Throwable e) {
				return model.getName();
			}
		}
		return null;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return model.getName();
	}

	public Object getAdapter(Class adapter) {
		if (IActionDelegate.class.isAssignableFrom(adapter)){
			return model;
		}
		return null;
	}

	public FdModel getModel(){
		return model;
	}
}
