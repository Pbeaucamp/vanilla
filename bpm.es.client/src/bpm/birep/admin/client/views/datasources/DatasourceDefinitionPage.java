package bpm.birep.admin.client.views.datasources;

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
import bpm.vanilla.platform.core.repository.DataSource;

public class DatasourceDefinitionPage extends WizardPage {

	private DataSource datasource;
	
	private Text txtName, txtDescription;
	
	private ModifyListener modifyListener = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			if(e.getSource() == txtName) {
				datasource.setName(txtName.getText());
			}
			else if(e.getSource() == txtDescription) {
				datasource.setDescription(txtDescription.getText());
			}
			
			getContainer().updateButtons();
		}
	};

	public DatasourceDefinitionPage(String pageName, DataSource datasource) {
		super(pageName);
		this.datasource = datasource;
	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		Label l = new Label(mainComposite, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DatasourceDefinitionPage_0);

		txtName = new Text(mainComposite, SWT.BORDER);
		txtName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l1 = new Label(mainComposite, SWT.NONE);
		l1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l1.setText(Messages.DatasourceDefinitionPage_1);

		txtDescription = new Text(mainComposite, SWT.BORDER);
		txtDescription.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		setControl(mainComposite);
		setPageComplete(true);
		
		if(datasource != null) {
			txtName.setText(datasource.getName() != null ? datasource.getName() : ""); //$NON-NLS-1$
			txtDescription.setText(datasource.getDescription() != null ? datasource.getDescription() : ""); //$NON-NLS-1$
		}

		txtName.addModifyListener(modifyListener);
		txtDescription.addModifyListener(modifyListener);
	}

	@Override
	public boolean isPageComplete() {
		return !txtName.getText().isEmpty();
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}

	public void updateWizardButtons() {
		getContainer().updateButtons();
	}
}
