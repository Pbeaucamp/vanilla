package metadata.client.wizards.datasources;

import metadata.client.i18n.Messages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.IDataStream;


public class PageQuery extends WizardPage {

	private Text query, name , description;
	private String value;
	
	protected PageQuery(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		//create main composite & set layout
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl( mainComposite );
		setPageComplete(true);
	}
	
	private void createPageContent(Composite parent){
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));		
		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.PageQuery_0); //$NON-NLS-1$
		
		name = new Text(container, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
			}
			
		});
		
		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false, 1, 2));
		l2.setText(Messages.PageQuery_1); //$NON-NLS-1$
		
		description = new Text(container, SWT.BORDER);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 2));

		
		
		Label l3 = new Label(container, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		l3.setText(Messages.PageQuery_2); //$NON-NLS-1$
		
		query = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		query.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		query.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				value = query.getText();
				getContainer().updateButtons();
			}
			
		});
		
		query.setFocus();
	}

	@Override
	public boolean isPageComplete() {
		return (query != null && !query.getText().equals("")); //$NON-NLS-1$
	}

	public IDataStream getTable() throws Exception{
		AbstractDataSource ds = (AbstractDataSource)((TableWizard)getWizard()).dataSource;
		
		String name = this.name.getText();
		
		return ds.addTableFromQuery(name, value);
	}

	@Override
	protected boolean isCurrentPage() {
		return super.isCurrentPage();
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
	
	
}
