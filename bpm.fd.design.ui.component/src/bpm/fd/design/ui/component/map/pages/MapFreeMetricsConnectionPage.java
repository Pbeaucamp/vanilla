package bpm.fd.design.ui.component.map.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fd.design.ui.component.Messages;

public class MapFreeMetricsConnectionPage extends WizardPage {

	public static final String PAGE_NAME = "bpm.fd.design.ui.component.map.pages.MapFreeMetricsConnectionPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.MapFreeMetricsConnectionPage_1;
	public static final String PAGE_DESCRIPTION = Messages.MapFreeMetricsConnectionPage_2;
	
	private Text txtUser;
	private Text txtPass;
	private Button btnTestConnection;
	
	public MapFreeMetricsConnectionPage() {
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected MapFreeMetricsConnectionPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2,false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lblUser = new Label(mainComposite, SWT.NONE);
		lblUser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblUser.setText(Messages.MapFreeMetricsConnectionPage_3);
		
		txtUser = new Text(mainComposite, SWT.BORDER);
		txtUser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Label lblPass = new Label(mainComposite, SWT.NONE);
		lblPass.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblPass.setText(Messages.MapFreeMetricsConnectionPage_4);
		
		txtPass = new Text(mainComposite, SWT.BORDER|SWT.PASSWORD);
		txtPass.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
//		
//		btnTestConnection = new Button(mainComposite, SWT.PUSH);
//		btnTestConnection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
//		btnTestConnection.setText("Test Connection");
//		btnTestConnection.addSelectionListener(new SelectionListener() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				
//			}
//			
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				
//			}
//		});
		
		setControl(mainComposite);
	}

	@Override
	public boolean isPageComplete() {
		if(txtUser.getText() != null && txtPass.getText() != null) {
			return true;
		}
		return false;
	}	
	
	public String getUser() {
		return txtUser.getText();
	}
	
	public String getPassword() {
		return txtPass.getText();
	}
}
