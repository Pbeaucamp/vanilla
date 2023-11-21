package bpm.birep.admin.client.views.validation;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import adminbirep.Messages;
import bpm.vanilla.platform.core.beans.alerts.Alert;

public class AlertInformationsPage extends WizardPage {
	private Alert alert;

	private Text name;
	private Text description;
	
	private ModifyListener listener = new ModifyListener(){

		public void modifyText(ModifyEvent e) {
			getContainer().updateButtons();
			if (e.getSource().equals(name)) {
				alert.setName(name.getText());
			}
			else if (e.getSource().equals(description)) {
				alert.setDescription(description.getText());
			}
		}
	};

	protected AlertInformationsPage(String pageName, Alert alert) {
		super(pageName);
		this.alert = alert;
	}
	
	public void createControl(Composite parent) {
		//create main composite & set layout
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl(mainComposite);
		setPageComplete(false);
	}

	private void createPageContent(Composite parent){
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.AlertInformationsPage_0);
		
		name = new Text(main, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.setText(alert.getName());
		name.addModifyListener(listener);
		
		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true, 1, 2));
		l2.setText(Messages.AlertInformationsPage_1);
		
		description = new Text(main, SWT.BORDER);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
		description.setText(alert.getDescription());
		description.addModifyListener(listener);
		
		Composite fileBar = new Composite(main, SWT.NONE);
		fileBar.setLayout(new GridLayout(3, false));
		fileBar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
	}

	@Override
	public boolean isPageComplete() {
		return !(name.getText().trim().equals("")); //$NON-NLS-1$
	}
}
