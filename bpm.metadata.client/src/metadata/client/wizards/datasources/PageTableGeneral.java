package metadata.client.wizards.datasources;

import metadata.client.i18n.Messages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class PageTableGeneral extends WizardPage {

	private boolean fromQuery = false;
	private Button query, base;
	
	protected PageTableGeneral(String pageName) {
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
	
				
		
		Group g = new Group(container, SWT.NONE);
		g.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		g.setLayout(new GridLayout());
		g.setText(Messages.PageTableGeneral_0); //$NON-NLS-1$
		
		base = new Button(g, SWT.RADIO);
		base.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		base.setText(Messages.PageTableGeneral_1); //$NON-NLS-1$
		base.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (base.getSelection()) {
					fromQuery = false;
				}
				else{
					fromQuery = true;
				}
			}
			
		});
		
		query = new Button(g, SWT.RADIO);
		query.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		query.setText(Messages.PageTableGeneral_2); //$NON-NLS-1$
		base.setSelection(true);
		query.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (query.getSelection()) {
					fromQuery = true;
				}
				else{
					fromQuery = false;
				}
				
			}
			
		});
		
	}
	
	public boolean isFromQuery(){
		return fromQuery;
	}

	@Override
	public boolean canFlipToNextPage() {
		return true;
	}
	
	
}
