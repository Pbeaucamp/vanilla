package bpm.es.gedmanager.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ImportDocumentTypePage extends WizardPage {

	public static int NEW_DOCUMENT = 0;
	public static int NEW_VERSION = 1;
	
	private Button rbNewDoc;
	private Button rbNewVersion;
	
	protected ImportDocumentTypePage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(1, false));
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		rbNewDoc = new Button(main, SWT.RADIO);
		rbNewDoc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		rbNewDoc.setText("Create a new document");
		rbNewDoc.addSelectionListener(listener);
		
		rbNewVersion = new Button(main, SWT.RADIO);
		rbNewVersion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		rbNewVersion.setText("Add a new version to an existing document");
		rbNewVersion.addSelectionListener(listener);
		
		setControl(main);
	}

	@Override
	public boolean isPageComplete() {
		return rbNewDoc.getSelection() || rbNewVersion.getSelection();
	}
	
	public int getSelection() {
		if(rbNewDoc.getSelection()) {
			return NEW_DOCUMENT;
		}
		return NEW_VERSION;
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}

	public void updateWizardButtons() {
		getContainer().updateButtons();
	}
	
	SelectionAdapter listener = new SelectionAdapter() {
		public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			updateWizardButtons();
		};
	};
}
