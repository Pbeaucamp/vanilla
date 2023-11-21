package bpm.fd.design.ui.component.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import bpm.fd.design.ui.actions.ActionGenerateLocalisationFiles;
import bpm.fd.design.ui.component.Messages;

public class ActionCreateFilter implements IViewActionDelegate{
	public static final String ID = "bpm.fd.design.ui.component.actions.ActionCreateFilterComponent"; //$NON-NLS-1$
	public static final String NAME = Messages.ActionCreateFilter_1;
	
	private IViewPart viewPart;
	
	public ActionCreateFilter(){
		
	}
	
	

	public void init(IViewPart view) {
		this.viewPart = view;
		
	}

	public void run(IAction action) {
		DialogListComponentType dial = new DialogListComponentType(viewPart.getSite().getShell());
		if (dial.open() == DialogListComponentType.OK){
			WizardDialog d = new WizardDialog(
					viewPart.getSite().getShell(),
					dial.getWizard());
			
			if (d.open() == WizardDialog.OK){
				new ActionGenerateLocalisationFiles().run();
				
			}
		}
		
		
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		
		
	}
}
