package bpm.gateway.ui.resource.service.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.ui.i18n.Messages;
import bpm.vanilla.platform.core.beans.service.ServiceTransformationDefinition;

public class ServiceDefinitionPage extends WizardPage {

	private ServiceTransformationDefinition service;
	private Text name, xmlRoot, xmlRow;
	
	private ModifyListener modifyListener = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			getContainer().updateButtons();
		}
	};

	protected ServiceDefinitionPage(String pageName, ServiceTransformationDefinition service) {
		super(pageName);
		this.service = service;
	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		Label l = new Label(mainComposite, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.ServiceDefinitionPage_0);

		name = new Text(mainComposite, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l1 = new Label(mainComposite, SWT.NONE);
		l1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l1.setText(Messages.ServiceDefinitionPage_1);

		xmlRoot = new Text(mainComposite, SWT.BORDER);
		xmlRoot.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l2 = new Label(mainComposite, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.ServiceDefinitionPage_2);

		xmlRow = new Text(mainComposite, SWT.BORDER);
		xmlRow.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		if(service != null){
			name.setText(service.getName() != null ? service.getName() : ""); //$NON-NLS-1$
			xmlRoot.setText(service.getXmlRoot() != null ? service.getXmlRoot() : ""); //$NON-NLS-1$
			xmlRow.setText(service.getXmlRow() != null ? service.getXmlRow() : ""); //$NON-NLS-1$
			
//			getContainer().updateButtons();
		}

		name.addModifyListener(modifyListener);
		xmlRoot.addModifyListener(modifyListener);
		xmlRow.addModifyListener(modifyListener);
		
		setControl(mainComposite);
		setPageComplete(true);
	}

	@Override
	public boolean isPageComplete() {
		return !name.getText().isEmpty() && !xmlRoot.getText().isEmpty() && !xmlRow.getText().isEmpty();
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}

	public void updateWizardButtons() {
		getContainer().updateButtons();
	}

	public String getServiceName() {
		return name.getText();
	}

	public String getXmlRoot() {
		return xmlRoot.getText();
	}

	public String getXmlRow() {
		return xmlRow.getText();
	}
}
