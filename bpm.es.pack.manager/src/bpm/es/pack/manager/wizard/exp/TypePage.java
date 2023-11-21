package bpm.es.pack.manager.wizard.exp;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import bpm.es.pack.manager.I18N.Messages;

public class TypePage extends WizardPage {
	
	private Button all, select;
	
	protected TypePage(String pageName) {
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
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout());
		
		select = new Button(c, SWT.RADIO);
		select.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		select.setText(Messages.TypePage_0);
		select.setSelection(true);
		
		all = new Button(c, SWT.RADIO);
		all.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		all.setText(Messages.TypePage_1);

	}
	
	protected boolean isFullExport(){
		return all.getSelection();
	}

}
