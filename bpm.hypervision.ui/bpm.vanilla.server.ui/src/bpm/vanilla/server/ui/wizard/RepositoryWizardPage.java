package bpm.vanilla.server.ui.wizard;

import java.util.Collection;
import java.util.Properties;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.birep.admin.connection.AdminAccess;
import bpm.birep.admin.datas.vanilla.Group;
import bpm.birep.admin.datas.vanilla.RepositoryDefinition;
import bpm.repository.api.model.FactoryRepository;
import bpm.repository.api.model.IRepositoryConnection;
import bpm.vanilla.server.ui.Activator;

public class RepositoryWizardPage extends WizardPage{

	private ComboViewer repository;
	private ComboViewer groups;
	private Text repositoryPassword;
	private Text repositoryLogin;
	private String vanillaUrl;
	
	
	public RepositoryWizardPage(String pageName) throws Exception {
		super(pageName);
		vanillaUrl = Activator.getDefault().getServerRemote().getServerConfig().getValue("url");
		this.setDescription("Define the repository connection.");
	}

	public void createControl(Composite parent) {
		Composite connCmp = new Composite(parent, SWT.NONE);
		connCmp.setLayout(new GridLayout(3, false));
		connCmp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
//		Label l = new Label(connCmp, SWT.NONE);
//		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		l.setText("VanillaServerUrl");
//		
//		vanillaUrl = new Text(connCmp, SWT.BORDER);
//		vanillaUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		vanillaUrl.setText("http://localhost:8080/vanilla");
//		
//		Button b = new Button(connCmp, SWT.PUSH);
//		b.setText("Connect");
//		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
//		b.addSelectionListener(new SelectionAdapter(){
//
//			/* (non-Javadoc)
//			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
//			 */
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				try {
//					repository.setInput(new AdminAccess(vanillaUrl.getText()).getRepositoryDefinitions());
//				} catch (Exception e1) {
//					e1.printStackTrace();
//					repository.setInput(new ArrayList());
//					MessageDialog.openError(getShell(), "Connection Problem", "Unable to connect to Vanilla Server:" + e1.getMessage());
//				}
//			}
//			
//		});
//		
		Label l = new Label(connCmp, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Login");
		
		repositoryLogin = new Text(connCmp, SWT.BORDER);
		repositoryLogin.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		repositoryLogin.setText("system");
		repositoryLogin.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
		});
		
		l = new Label(connCmp, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Password");
		
		repositoryPassword = new Text(connCmp, SWT.BORDER | SWT.PASSWORD);
		repositoryPassword.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		repositoryPassword.setText("system");
		repositoryPassword.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
		});
		
		
		
		l = new Label(connCmp, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Group");
		
		groups = new ComboViewer(connCmp, SWT.BORDER | SWT.READ_ONLY);
		groups.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		groups.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		groups.setLabelProvider(new LabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				
				return ((Group)element).getName();
			}
			
		});
		
		l = new Label(connCmp, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Repository");
		
		repository = new ComboViewer(connCmp, SWT.BORDER | SWT.READ_ONLY);
		repository.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		repository.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		repository.setLabelProvider(new LabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				
				return ((RepositoryDefinition)element).getName();
			}
			
		});
		
		repository.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();
				
			}
		});
		
		
		setControl(connCmp);
		
		/*
		 * load Repository
		 */
		AdminAccess a = new AdminAccess(vanillaUrl);
		try {
			repository.setInput(a.getRepositoryDefinitions());
			groups.setInput(a.getGroups(a.getUserByName(repositoryLogin.getText())));
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
	}

	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return !("".equals(repository.getCombo().getText().trim()) || "".equals(repositoryLogin.getText()));
	}

	public IRepositoryConnection getRepositoryConnection(){
		return FactoryRepository.getInstance().getConnection(FactoryRepository.AXIS_CLIENT, ((RepositoryDefinition)((IStructuredSelection)repository.getSelection()).getFirstElement()).getUrl(), repositoryLogin.getText(), repositoryPassword.getText(), null, ((Group)((IStructuredSelection)groups.getSelection()).getFirstElement()).getId());
	}
	
	public Properties getProperties(){
		Properties p = new Properties();
		p.setProperty("repositoryId", ((RepositoryDefinition)((IStructuredSelection)repository.getSelection()).getFirstElement()).getId()+ "");
		p.setProperty("login", repositoryLogin.getText());
		p.setProperty("password", repositoryPassword.getText());
		p.setProperty("encrypted", "false");
		p.setProperty("groupId", ((Group)((IStructuredSelection)groups.getSelection()).getFirstElement()).getId()+ "");
		
		return p;
	}
	
	
}
