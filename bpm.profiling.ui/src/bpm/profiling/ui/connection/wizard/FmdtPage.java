package bpm.profiling.ui.connection.wizard;

import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.MetaData;
import bpm.metadata.MetaDataBuilder;
import bpm.metadata.digester.MetaDataDigester;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.physical.IConnection;
import bpm.profiling.runtime.core.Connection;
import bpm.profiling.ui.Activator;
import bpm.profiling.ui.preferences.PreferenceConstants;
import bpm.profiling.ui.repository.DialogRepositoryContent;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FmdtPage extends WizardPage {

	@Override
	protected boolean isCurrentPage() {
		return super.isCurrentPage();
	}


	private Text txtVanillaUrl, txtLogin, txtPassword, model;
	private ComboViewer dataSource, connection, cbGroups, cbRepository;
	private RepositoryItem item;
	private Button fmdtBrowser;
	
	private MetaData fmdt;
	private IConnection connectionFmdt;
	private IDataSource dataSourceFmdt;
	
	private Integer groupId;
	
	protected FmdtPage(String pageName) {
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
		composite.setLayout(new GridLayout(3, false));
		
		Label l = new Label(composite, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Vanilla Url");
		
		txtVanillaUrl = new Text(composite, SWT.BORDER);
		txtVanillaUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		
		Label l2 = new Label(composite, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText("Login");
		
		txtLogin = new Text(composite, SWT.BORDER);
		txtLogin.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		
		Label l3 = new Label(composite, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText("Password");
		
		txtPassword = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		
		Button btnConnection = new Button(composite, SWT.PUSH);
		btnConnection.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 3, 1));
		btnConnection.setText("Connection");
		btnConnection.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				String vanillaRuntimeUrl = txtVanillaUrl.getText();
				String login = txtLogin.getText();
				String password = txtPassword.getText();
				
				IVanillaContext vanillaCtx = new BaseVanillaContext(vanillaRuntimeUrl, login, password);
				IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
				
				try {
					List<Group> groups = vanillaApi.getVanillaSecurityManager().getGroups();
					cbGroups.setInput(groups);
					cbGroups.getCombo().select(0);
				} catch (Exception e1) {
					MessageDialog.openError(getShell(), "Error when loading groups", e1.getMessage()); //$NON-NLS-1$
					e1.printStackTrace();
				}
				
				try {
					List<Repository> repositories = vanillaApi.getVanillaRepositoryManager().getRepositories();
					cbRepository.setInput(repositories);
					cbRepository.getCombo().select(0);
				} catch (Exception e1) {
					MessageDialog.openError(getShell(), "Error when loading repositories", e1.getMessage()); //$NON-NLS-1$
					e1.printStackTrace();
				}
			}
		});
		
		
		l3 = new Label(composite, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText("Group Name");
		
		cbGroups = new ComboViewer(composite, SWT.READ_ONLY);
		cbGroups.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		cbGroups.setContentProvider(new ArrayContentProvider());
		cbGroups.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		
		
		l3 = new Label(composite, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText("Repository");
		
		cbRepository = new ComboViewer(composite, SWT.READ_ONLY);
		cbRepository.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		cbRepository.setContentProvider(new ArrayContentProvider());
		cbRepository.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Repository)element).getName();
			}
		});
		
		
		Composite bar = new Composite(composite, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		bar.setLayout(new GridLayout(3, false));
		
		Label l4 = new Label(bar, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText("Choose your Fmdt");
		
		model = new Text(bar, SWT.BORDER );
		model.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		model.setEnabled(false);
		
		fmdtBrowser = new Button(bar, SWT.PUSH);
		fmdtBrowser.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		fmdtBrowser.setText("...");
		fmdtBrowser.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(((IStructuredSelection)cbGroups.getSelection()).getFirstElement() == null
						&& ((IStructuredSelection)cbRepository.getSelection()).getFirstElement() == null){
					return;
				}
				
				Group gr = (Group)((IStructuredSelection)cbGroups.getSelection()).getFirstElement();
				Repository repo = (Repository)((IStructuredSelection)cbRepository.getSelection()).getFirstElement();
				
				IVanillaContext vanillaContext = new BaseVanillaContext(txtVanillaUrl.getText(), txtLogin.getText(), txtPassword.getText()); 
				
				IRepositoryContext ctx = new BaseRepositoryContext(vanillaContext, gr, repo);
				
				IRepositoryApi sock = new RemoteRepositoryApi(ctx);
				
				DialogRepositoryContent d = new DialogRepositoryContent(getShell(), sock);
				if (d.open() == DialogRepositoryContent.OK){
					RepositoryItem it = d.getItem();
					
					String xml;
					try {
						xml = sock.getRepositoryService().loadModel(it);
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(getShell(), "Error while loading Fmdt", e1.getMessage());
						return;
					}
					
					try {
						fmdt = new MetaDataDigester(IOUtils.toInputStream(xml), 
								new MetaDataBuilder(sock)).getModel(null);
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(getShell(), "Error while parsing Fmdt xml", e1.getMessage());
						return;
					} 
					model.setText(it.getItemName());
					item = it;
					dataSource.setInput(fmdt.getDataSources());
					getContainer().updateButtons();
				}
			}
			
		});
		
		Label l5 = new Label(composite, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l5.setText("Datasources");
		
		dataSource = new ComboViewer(composite, SWT.READ_ONLY);
		dataSource.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		dataSource.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((IDataSource)element).getName();
			}
			
		});
		dataSource.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				Collection<IDataSource> c = (Collection<IDataSource>)inputElement;
				return c.toArray(new IDataSource[c.size()]);
			}

			public void dispose() { }

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
		});
		dataSource.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IDataSource ds = (IDataSource)((IStructuredSelection)dataSource.getSelection()).getFirstElement();
				connection.setInput(ds.getConnections(null));
			}
			
		});
		
		
		Label l6 = new Label(composite, SWT.NONE);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l6.setText("Connection");
		
		
		connection = new ComboViewer(composite, SWT.READ_ONLY);
		connection.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		connection.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((IConnection)element).getName();
			}
			
		});
		connection.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				Collection<IConnection> c = (Collection<IConnection>)inputElement;
				return c.toArray(new IConnection[c.size()]);
			}

			public void dispose() { }

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
		});
		connection.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				if (connection.getSelection().isEmpty()){
					connection = null;
				}
				else{
					connectionFmdt = (IConnection)((IStructuredSelection)connection.getSelection()).getFirstElement();
				}
				if (dataSource.getSelection().isEmpty()){
					dataSource = null;
				}
				else{
					dataSourceFmdt = (IDataSource)((IStructuredSelection)dataSource.getSelection()).getFirstElement();
				}
				
				getContainer().updateButtons();
			}
			
		});
	
		fillData();
	}
	
	private void fillData(){
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		try{
			String s = store.getString(PreferenceConstants.P_BPM_REPOSITORY_URL);
			if (s.equals("")){
				txtVanillaUrl.setText(store.getDefaultString(PreferenceConstants.P_BPM_REPOSITORY_URL));
			}
			else{
				txtVanillaUrl.setText(s);
			}
			
			s = store.getString(PreferenceConstants.P_BPM_REPOSITORY_LOGIN);
			if (s.equals("")){
				txtLogin.setText(store.getDefaultString(PreferenceConstants.P_BPM_REPOSITORY_LOGIN));
			}
			else{
				txtLogin.setText(s);
			}
			
			s = store.getString(PreferenceConstants.P_BPM_REPOSITORY_PASSWORD);
			if (s.equals("")){
				txtPassword.setText(store.getDefaultString(PreferenceConstants.P_BPM_REPOSITORY_PASSWORD));
			}
			else{
				txtPassword.setText(s);	
			}
		}catch(Throwable t){
			t.printStackTrace();
		}
	}
	
	
	protected IConnection getFmdtConnection(){
		return connectionFmdt;
	}
	
	protected IDataSource getFmdtDataSource(){
		return dataSourceFmdt;
	}

	
	protected void setConnection(Connection c){
		c.setIsFromRepository(true);
		c.setDirectoryItemId(item.getId());
		c.setHost(txtVanillaUrl.getText());
		c.setLogin(txtLogin.getText());
		c.setPassword(txtPassword.getText());
		c.setFmdtDataSourceName(dataSourceFmdt.getName());
		c.setFmdtConnectionName(connectionFmdt.getName());
		c.setName(dataSourceFmdt.getName());
		c.setVanillaGroupId(groupId);
	}
	

	@Override
	public boolean isPageComplete() {
		return connectionFmdt != null && dataSourceFmdt != null;
	}
}
