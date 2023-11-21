package bpm.profiling.ui.connection.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class DefinitionTypePage extends WizardPage {
	
	protected static final int FMDT_TYPE = 0;
	protected static final int USER_DEFINED = 1;
	
	private Button fmdt, userDefined;
	
	protected DefinitionTypePage(String pageName) {
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
		setPageComplete(false);

	}

	private void createPageContent(Composite parent){
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout());
		
		fmdt = new Button(composite, SWT.RADIO);
		fmdt.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		fmdt.setText("FreeMetaData");;
		fmdt.addSelectionListener(new SelectionAdapter(){

			public void widgetSelected(SelectionEvent e) {
				getContainer().updateButtons();
				
			}
			
		});
		
		userDefined = new Button(composite, SWT.RADIO);
		userDefined.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		userDefined.setText("User Defined Connection");;
		userDefined.addSelectionListener(new SelectionAdapter(){

			public void widgetSelected(SelectionEvent e) {
				getContainer().updateButtons();
				
			}
			
		});
		
	}
	
	protected int getType(){
		if (fmdt.getSelection()){
			return FMDT_TYPE;
		}
		else if (userDefined.getSelection()){
			return USER_DEFINED;
		}
		
		return -1;
	}

	@Override
	public boolean isPageComplete() {
		return getType() >= 0; 
	}
	
	
}
