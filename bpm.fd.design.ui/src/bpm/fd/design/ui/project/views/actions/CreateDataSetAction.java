package bpm.fd.design.ui.project.views.actions;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.edition.EditionDataSetWizard;
import bpm.fd.design.ui.edition.MultiPageWizardDialog;
import bpm.fd.design.ui.wizard.OdaDataSetWizard;

public class CreateDataSetAction extends Action implements IViewActionDelegate{

	
	
	public CreateDataSetAction(String label, String id){
		super(label);
		setId(id);
	
		
	}
	@Override
	public boolean isEnabled() {
		
		return super.isEnabled();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		
		
		
		Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		try {
			OdaDataSetWizard wiz = new OdaDataSetWizard(null);
//			wiz.init(Activator.getDefault().getWorkbench(), (IStructuredSelection)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection());
			WizardDialog d = new WizardDialog(shell, wiz);
			if (d.open() == WizardDialog.OK){
				
				try{
				//here we re-edit the dataset to allow cyhaning parameter names
					OdaDataSetWizard wiz2 = new EditionDataSetWizard(wiz.getDataSet());
	//				wiz.init(Activator.getDefault().getWorkbench(), (IStructuredSelection)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection());
					WizardDialog d2 = new MultiPageWizardDialog(shell, wiz2);
					if (d2.open() == WizardDialog.OK){
						for(IComponentDefinition def : Activator.getDefault().getProject().getDictionary().getComponents()){
							if (def.getDatas() != null && def.getDatas().getDataSet() == wiz.getDataSet()){
								def.firePropertyChange(IComponentDefinition.PARAMETER_CHANGED, null, wiz.getDataSet());
							}
						}
						
						Activator.getDefault().getProject().getDictionary().firePropertyChange(Dictionary.PROPERTY_DATASET_CHANGED, null, wiz.getDataSet());				
						
					}
				} catch (OdaException e) {
					e.printStackTrace();
				}
				
			}
		} catch (OdaException e) {
			e.printStackTrace();
		}
	}
	public void init(IViewPart view) {
		
		
	}
	public void run(IAction action) {
		run();
		
	}
	public void selectionChanged(IAction action, ISelection selection) {
		
		
	}
	
	
}
