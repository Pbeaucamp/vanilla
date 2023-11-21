package bpm.fd.design.ui.rcp.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;

import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.editors.FdProjectEditorInput;
import bpm.fd.design.ui.rcp.Messages;

public class ActionCloseActiveProject extends Action{
	public ActionCloseActiveProject(){
		super("Close Active Project"); //$NON-NLS-1$
		setText(Messages.ActionCloseActiveProject_1);
	}
	
	public void run(){
		FdProject proj = Activator.getDefault().getProject();
		
		
		if (proj == null){
			return;
		} 
		List<IEditorReference> toClose = new ArrayList<IEditorReference>();
		
		for (IEditorReference ref : Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences()){
			
			try{
				if (ref.getEditorInput() instanceof FdProjectEditorInput){
					FdProjectEditorInput input = (FdProjectEditorInput)ref.getEditorInput();
					
					
					if (proj instanceof MultiPageFdProject){
						if (input.getModel() == proj.getFdModel() || ((MultiPageFdProject)proj).getPagesModels().contains(input.getModel())){
							toClose.add(ref);
						}
					}
					else{
						if (input.getModel() == proj.getFdModel()){
							toClose.add(ref);
						}
								
					}
					
				}
			}catch(PartInitException ex){
				ex.printStackTrace();
			}
			
		}
		
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditors(toClose.toArray(new IEditorReference[toClose.size()]), true);
	}
}
