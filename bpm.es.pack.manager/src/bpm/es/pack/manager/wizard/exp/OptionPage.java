package bpm.es.pack.manager.wizard.exp;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import bpm.es.pack.manager.I18N.Messages;

public class OptionPage extends WizardPage {

	private Button includeHistorics;
	private Button includeGroups;
	private Button includeRoles;
	private Button includeGrants;
	
	protected OptionPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new FillLayout());

		createPageContent(mainComposite);		
		
		setControl(mainComposite);
		setPageComplete(true);
	}

	
	private void createPageContent(Composite parent){
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		Group generalComposite = new Group(container, SWT.NONE);
		generalComposite.setLayout(new GridLayout());
		generalComposite.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		generalComposite.setText(bpm.es.pack.manager.I18N.Messages.OptionPage_18);
		
		includeHistorics = new Button (generalComposite, SWT.CHECK);
		includeHistorics.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		includeHistorics.setText(bpm.es.pack.manager.I18N.Messages.OptionPage_19);
		
		Group securityComposite = new Group(container, SWT.NONE);
		securityComposite.setLayout(new GridLayout());
		securityComposite.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		securityComposite.setText(Messages.OptionPage_0);
		
		includeGroups = new Button (securityComposite, SWT.CHECK);
		includeGroups.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		includeGroups.setText(Messages.OptionPage_1);
		includeGroups.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				includeRoles.setEnabled(includeGroups.getSelection());
				includeGrants.setEnabled(includeGroups.getSelection());
			}
		});

		includeRoles = new Button (securityComposite, SWT.CHECK);
		includeRoles.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		includeRoles.setText(Messages.OptionPage_2);
		includeRoles.setEnabled(false);
		
		includeGrants = new Button (securityComposite, SWT.CHECK);
		includeGrants.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		includeGrants.setText(Messages.OptionPage_3);
		includeGrants.setEnabled(false);
	}

	@Override
	public boolean isPageComplete() {
		return true;
	}

	@Override
	protected boolean isCurrentPage() {
		return super.isCurrentPage();
	}
	
	public boolean includeHistorics() {
		return includeHistorics.getSelection();
	}

	public boolean includeGroups(){
		return includeGroups.getSelection();
	}

	public boolean includeGrants(){
		return includeGrants.getSelection();
	}

	public boolean includeRoles(){
		return includeRoles.getSelection();
	}
}
