package bpm.forms.design.ui.wizards.form;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import bpm.forms.core.design.IForm;
import bpm.forms.design.ui.Activator;
import bpm.forms.design.ui.composite.CompositeForm;

public class FormPage extends WizardPage{

	private CompositeForm formControl;
	
	protected FormPage(String pageName) {
		super(pageName);
		
	}

	@Override
	public void createControl(Composite parent) {
		formControl = new CompositeForm(null);
		formControl.createContent(parent);
		
		formControl.setInput(Activator.getDefault().getFactoryModel().createForm());
		
		formControl.getClient().addListener(SWT.Modify, new Listener(){
			@Override
			public void handleEvent(Event event) {
				getContainer().updateButtons();
				
			}
		});
		
		setControl(formControl.getClient());
		
	}

	public IForm getForm() {
		return formControl.getInput();
	}
	
	@Override
	public boolean isPageComplete() {
		return formControl.getInput().getName() != null && !formControl.getInput().getName().trim().equals("");  //$NON-NLS-1$
		
	}

}
