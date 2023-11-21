package bpm.forms.design.ui.wizards.formdefinition;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.forms.core.design.IForm;
import bpm.forms.core.design.IFormDefinition;

import bpm.forms.design.ui.Activator;
import bpm.forms.design.ui.Messages;

public class WizardFormDefintion extends Wizard implements INewWizard{

	private FormDefinitionPage formDefPage;
	private IFormDefinition formDef;
	
	private IForm form;
	
	@Override
	public void addPages() {
		formDefPage = new FormDefinitionPage(Messages.WizardFormDefintion_0);
		formDefPage.setTitle(Messages.WizardFormDefintion_1);
		formDefPage.setDescription(Messages.WizardFormDefintion_2);
		
		addPage(formDefPage);
	}
	
	@Override
	public boolean performFinish() {
		formDef = formDefPage.getFormDefinition();
		
		if (formDef != null){
			try{
				formDef.setFormId(form.getId());
				Activator.getDefault().getServiceProvider().getDefinitionService().saveFormDefinition(formDef);
				return true;
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(getShell(), Messages.WizardFormDefintion_3, Messages.WizardFormDefintion_4 + ex.getMessage());
			}
		}
		
		return false;
	}
	
	public IFormDefinition getFormDefinition(){
		return formDef;
	}
	
	

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		if (!selection.isEmpty()){
			if (((IStructuredSelection)selection).getFirstElement() instanceof IForm){
				form = (IForm)((IStructuredSelection)selection).getFirstElement() ;
			}
		}
		
		
	}

	protected IForm getForm(){
		return form;
	}
}
