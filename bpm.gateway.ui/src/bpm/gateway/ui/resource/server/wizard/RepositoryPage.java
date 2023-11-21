package bpm.gateway.ui.resource.server.wizard;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.ui.i18n.Messages;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;



public class RepositoryPage extends WizardPage {

	protected Text login, url, password;
	protected ComboViewer groups;
	protected Button test;
	private SelectionListener testListener;
	protected ModifyListener listener = new ModifyListener(){

		public void modifyText(ModifyEvent e) {
			getContainer().updateButtons();
			if (isPageComplete()){
				test.setEnabled(true);
			}
			else{
				test.setEnabled(false);
			}
		}
		
	};
	
	protected RepositoryPage(String pageName) {
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
	
	private Composite createPageContent(Composite composite){
		Composite container = new Composite(composite, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Label l0 = new Label(container, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l0.setText(Messages.RepositoryPage_0);
		
		url = new Text(container, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		url.addModifyListener(listener);
		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.RepositoryPage_1);
		
		login = new Text(container, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		login.addModifyListener(listener);
		
		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.RepositoryPage_2);
		
		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		createCustomWidgets(container);
		
		test = new Button(container, SWT.PUSH);
		test.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		test.setText(Messages.RepositoryPage_3);
		test.setEnabled(false);
		
		testListener = new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				
			}
			
		};
		test.addSelectionListener(testListener);
		
		return container;
	}

	protected void createCustomWidgets(Composite parent){
		Button loadGroups = new Button(parent, SWT.PUSH);
		loadGroups.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 2, 1));
		loadGroups.setText(Messages.RepositoryPage_4);
		
		loadGroups.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<Group> l = new ArrayList<Group>();
				
				try{
					IVanillaContext ctx = new BaseVanillaContext(url.getText(), login.getText(), password.getText());
					IVanillaSecurityManager mng = new RemoteVanillaPlatform(ctx).getVanillaSecurityManager();
					l.addAll(mng.getGroups());
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.RepositoryPage_5, Messages.RepositoryPage_6 + ex.getMessage());
				}
				
				
				groups.setInput(l.toArray(new Group[l.size()]));
				if (!l.isEmpty()){
					groups.getCombo().select(0);
				}
			}
		});
		
		
		Label l  = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.RepositoryPage_7); 

		
		
		groups = new ComboViewer(parent, SWT.READ_ONLY);
		groups.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groups.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});

	}
	

	/**
	 * register a listener on the TestButton
	 * must be overridden for a specific listener
	 */
	protected void replaceTestListener(SelectionListener listener){
		test.removeSelectionListener(this.testListener);
		this.testListener = listener;
		test.addSelectionListener(testListener);
		
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		boolean b = !(url.getText().equals("") || login.getText().equals(""));  //$NON-NLS-1$ //$NON-NLS-2$
		return b;
	}


	public Properties getValues() {
		Properties p = new Properties();
		p.setProperty("url", url.getText()); //$NON-NLS-1$
		p.setProperty("login", login.getText()); //$NON-NLS-1$
		p.setProperty("password", password.getText()); //$NON-NLS-1$
		try{
			IStructuredSelection ss = (IStructuredSelection)groups.getSelection();
			
			if (!ss.isEmpty() && (ss.getFirstElement() instanceof Group)){
				p.setProperty("groupId", ((Group)ss.getFirstElement()).getId() + ""); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return p;
	}

	
//	private Integer getGroupId(){
//		try{
//			IVanillaContext ctx = new BaseVanillaContext(url.getText(), login.getText(), password.getText());
//			IVanillaSecurityManager mng = new RemoteVanillaPlatform(ctx).getVanillaSecurityManager();
//			return mng.getGroupByName(groups.getText()).geti;
//		}catch(Exception ex){
//			ex.printStackTrace();
//			MessageDialog.openError(getShell(), Messages.RepositoryPage_9, Messages.RepositoryPage_10 + ex.getMessage());
//			return null;
//		}
//	}
	
	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
	
	public void initUrl(String url){
		
		if (this.url != null && !this.url.isDisposed()){
			this.url.removeModifyListener(listener);
			this.url.setText(url);
			this.url.addModifyListener(listener);
		}
		
		
	}
	
}

