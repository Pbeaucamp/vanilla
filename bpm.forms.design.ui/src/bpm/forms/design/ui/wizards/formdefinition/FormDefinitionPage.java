package bpm.forms.design.ui.wizards.formdefinition;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import bpm.forms.core.design.IForm;
import bpm.forms.core.design.IFormDefinition;
import bpm.forms.design.ui.Activator;
import bpm.forms.design.ui.composite.CompositeFormDefinition;

public class FormDefinitionPage extends WizardPage{

	private CompositeFormDefinition formDefinitionControl;
	
	protected FormDefinitionPage(String pageName) {
		super(pageName);
		
	}

	@Override
	public void createControl(Composite parent) {
		formDefinitionControl = new CompositeFormDefinition(null);
		formDefinitionControl.createContent(parent);
		
		IFormDefinition fd = Activator.getDefault().getFactoryModel().createFormDefinition();		
		if (getWizard() instanceof WizardFormDefintion){
			IForm f = ((WizardFormDefintion)getWizard()).getForm();
			
			int max = 0;
			for(IFormDefinition _fd : Activator.getDefault().getServiceProvider().getDefinitionService().getFormDefinitionVersions(f.getId())){
				if (max < _fd.getVersion()){
					max = _fd.getVersion();
				}
			}
			
			fd.setVersion(max + 1);
		}
		
		formDefinitionControl.setInput(fd);
		
		formDefinitionControl.getClient().addListener(SWT.Modify, new Listener(){
			@Override
			public void handleEvent(Event event) {
				getContainer().updateButtons();
				
			}
		});
		
		setControl(formDefinitionControl.getClient());
	}

	public IFormDefinition getFormDefinition() {
		return formDefinitionControl.getInput();
	}
}
