package bpm.profiling.ui.connection.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import bpm.profiling.runtime.core.Connection;

public class JdbcPage extends WizardPage {

	private CompositeConnectionSql composite;
	private Listener listener;
	protected JdbcPage(String pageName) {
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
	
	@Override
	protected boolean isCurrentPage() {
		return super.isCurrentPage();
	}

	private void createPageContent(Composite parent){
		composite = new CompositeConnectionSql(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		listener = new Listener(){

			public void handleEvent(Event event) {
				if (event.widget == composite){
					getContainer().updateButtons();
				}
			}
			
		};
		
		composite.addListener(SWT.SELECTED, listener);
	}

	
	@Override
	public boolean isPageComplete() {
		return composite.isFilled();
	}

	protected Connection getConnection(){
		return composite.getConnection();
	}
}
