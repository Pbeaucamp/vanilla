package bpm.forms.design.ui.wizards.form;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import bpm.forms.core.design.IForm;
import bpm.forms.design.ui.Activator;
import bpm.forms.design.ui.Messages;

public class WizardForm extends Wizard{

	private FormPage formPage;
	
	private IForm form;
	
	@Override
	public void addPages() {
		formPage = new FormPage(Messages.WizardForm_0);
		formPage.setTitle(Messages.WizardForm_1);
		formPage.setDescription(Messages.WizardForm_2);
		
		addPage(formPage);
	}
	
	@Override
	public boolean performFinish() {
		form = formPage.getForm();
		
		if (form != null){
			try{
				Activator.getDefault().getServiceProvider().getDefinitionService().saveForm(form);
				return true;
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(getShell(), Messages.WizardForm_3, Messages.WizardForm_4 + ex.getMessage());
			}
		}
		
		return false;
		
		
	}

	public IForm getForm(){
		return form;
	}
	
}
