package bpm.datasource.vanilla.oda.ui.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserRep;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.DataSource;

public class ConnectionComposite extends Composite {

	private Text login, password, vanillaUrl;

	private ComboViewer groups;
	private ComboViewer repositories;
	private ComboViewer datasources;
	
	private VanillaDatasourceOdaPage page;
	private IVanillaAPI api;

	private List<Group> availableGroups;
	private List<Repository> availableRepositories;
	
	private DataSource selectedDatasource;

	public ConnectionComposite(Composite parent, int style, VanillaDatasourceOdaPage csvDataSourcePage) {
		super(parent, style);
		this.setLayout(new GridLayout(3, false));
		this.page = csvDataSourcePage;
		page.setPingButton(false);
		buildContent();
	}

	private void buildContent() {
		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText("Vanilla Url"); //$NON-NLS-1$
		
		vanillaUrl = new Text(this, SWT.BORDER);
		vanillaUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false,2,1));

		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("User name"); //$NON-NLS-1$

		login = new Text(this, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText("Password"); //$NON-NLS-1$

		password = new Text(this, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Composite _btn = new Composite(this, SWT.NONE);
		_btn.setLayout(new GridLayout());
		_btn.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false, 3, 1));

		Button btnInfos = new Button(_btn, SWT.PUSH);
		btnInfos.setText("Load User's Informations"); //$NON-NLS-1$
		btnInfos.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, true, false));
		btnInfos.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					fillInfos();
				} catch(Exception _e) {
					_e.printStackTrace();
					MessageDialog.openError(getShell(), "Load Groups", "Error while loading groups;\r\n" + _e.getMessage());
				}
			}
		});

		Label l7 = new Label(this, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		l7.setText("Repository Name"); //$NON-NLS-1$

		repositories = new ComboViewer(this, SWT.READ_ONLY);
		repositories.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		repositories.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if(element instanceof Repository) {
					return ((Repository) element).getName();
				}
				return super.getText(element);
			}

		});

		repositories.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			public void dispose() {

			}

			public Object[] getElements(Object inputElement) {
				List<Repository> rep = (List<Repository>) inputElement;
				return rep.toArray(new Repository[rep.size()]);
			}
		});

		Label l8 = new Label(this, SWT.NONE);
		l8.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		l8.setText("Group Name"); //$NON-NLS-1$

		groups = new ComboViewer(this, SWT.READ_ONLY);
		groups.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		groups.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if(element instanceof Group) {
					return ((Group) element).getName();
				}
				return super.getText(element);
			}

		});

		groups.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			public void dispose() {}

			public Object[] getElements(Object inputElement) {
				List<Group> groups = (List<Group>) inputElement;
				return groups.toArray(new Group[groups.size()]);
			}
		});

		Composite btn = new Composite(this, SWT.NONE);
		btn.setLayout(new GridLayout(2, false));
		btn.setLayoutData(new GridData(GridData.CENTER, GridData.BEGINNING, false, false, 3, 1));

		Button conRep = new Button(btn, SWT.PUSH);
		conRep.setText("Connect to Repository"); //$NON-NLS-1$
		conRep.setLayoutData(new GridData(GridData.CENTER, GridData.BEGINNING, true, false));
		conRep.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object o = ((IStructuredSelection) groups.getSelection()).getFirstElement();
				if(o == null) {
					MessageDialog.openWarning(getShell(), "Group Selection", "Please select a group before to connect");
					return;
				}
				Object _o = ((IStructuredSelection) repositories.getSelection()).getFirstElement();
				if(_o == null) {
					MessageDialog.openWarning(getShell(), "Repository Selection", "Please select a reppository to connect");
					return;
				}
				
				try {
					IRepositoryApi repositoryApi = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(api.getVanillaUrl(), login.getText(), password.getText()), (Group) o, (Repository) _o));
	
					List<DataSource> ds = repositoryApi.getImpactDetectionService().getAllDatasources();
					datasources.setInput(ds);
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(getShell(), "Datasources Error", "Unable to get all the datasources : " + ex.getMessage());
				}
				
			}
		});
		
		Label l9 = new Label(this, SWT.NONE);
		l9.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		l9.setText("Datasources"); //$NON-NLS-1$

		datasources = new ComboViewer(this, SWT.READ_ONLY);
		datasources.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		datasources.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if(element instanceof DataSource) {
					return ((DataSource) element).getName();
				}
				return super.getText(element);
			}

		});
		datasources.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

			public void dispose() {}

			public Object[] getElements(Object inputElement) {
				List<DataSource> datasources = (List<DataSource>) inputElement;
				return datasources.toArray(new DataSource[datasources.size()]);
			}
		});
		datasources.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object o = ((IStructuredSelection) datasources.getSelection()).getFirstElement();
				if(o == null) {
					return;
				}
				
				selectedDatasource = (DataSource) o;
				page.updateButtons();
			}
		});
		
		fillData();
	}
	
	private void fillData(){
		String url = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
		String login = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
		
		if (url != null){
			this.vanillaUrl.setText(url);
		}
		
		if (login != null){
			this.login.setText(login);
		}
		
		if (password != null){
			this.password.setText(password);
		}
	}
	
	private void fillInfos() throws Exception {
		
		String vanillaurl = vanillaUrl.getText();

		if(vanillaurl == null || vanillaurl.equalsIgnoreCase("")) {
			MessageDialog.openInformation(getShell(), "Vanilla Server", "Vanilla Security Server's url is not set. \n Use Window -> Preference to define a default value");
			return;
		}

		api = new RemoteVanillaPlatform(vanillaurl, login.getText(), password.getText());

		User user = api.getVanillaSecurityManager().getUserByLogin(login.getText());

		availableGroups = api.getVanillaSecurityManager().getGroups(user);
		availableRepositories = new ArrayList<Repository>();
		for(UserRep rr : api.getVanillaRepositoryManager().getUserRepByUserId(user.getId())) {
			availableRepositories.add(api.getVanillaRepositoryManager().getRepositoryById(rr.getRepositoryId()));
		}

		repositories.setInput(availableRepositories);
		groups.setInput(availableGroups);
	}
	
	public DataSource getDatasource() {
		return selectedDatasource;
	}

	public boolean isPageComplete() {
		return selectedDatasource != null;
	}
}
