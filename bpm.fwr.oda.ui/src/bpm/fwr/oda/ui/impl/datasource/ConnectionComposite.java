package bpm.fwr.oda.ui.impl.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fwr.oda.ui.Activator;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ConnectionComposite extends Composite{

	private Text url, login, password;
	private Collection<String> groups;
	private Combo groupNames, repositoryNames;
	private String groupName, repositoryName;
	private List<RepositoryDirectory> rootDirectories;
	private List<Group> listGroup;
	private List<Repository>  listRepositories;
	private Group selectedGroup;
	private Repository selectedRepository;
	private RepositoryItem selectedFwrReport;
	
	protected TreeViewer viewer;
	
	private FwrDataSourcePage page;
	
	public ConnectionComposite(Composite parent, int style, FwrDataSourcePage page) {
		super(parent, style);
		this.setLayout(new GridLayout(2, false));
		this.page = page;
		
		buildContent();
	}
	
	public void buildContent(){
		Label log = new Label(this, SWT.NONE);
		log.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		log.setText(Activator.getResourceString("ConnectionComposite.Username")); //$NON-NLS-1$
		
		login = new Text(this, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		login.setText("system");

		
		Label pass = new Label(this, SWT.NONE);
		pass.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		pass.setText(Activator.getResourceString("ConnectionComposite.Password")); //$NON-NLS-1$
		
		password = new Text(this, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		password.setText("system");
		
		Label vanUrl = new Label(this, SWT.NONE);
		vanUrl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		vanUrl.setText(Activator.getResourceString("ConnectionComposite.VanillaUrl")); //$NON-NLS-1$
		
		url = new Text(this, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		url.setText("http://localhost:7171/VanillaRuntime");
		
		Button connect = new Button(this, SWT.NONE);
		connect.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		connect.setText(Activator.getResourceString("ConnectionComposite.ConnectVanilla")); //$NON-NLS-1$
		connect.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					IVanillaAPI api = new RemoteVanillaPlatform(getUrl(), login.getText(), password.getText());
					User user = api.getVanillaSecurityManager().getUserByLogin(getLogin());
					initRepositories(api, user);
					initGroups(api, user);
				} catch (Exception ex) {
					
					MessageDialog.openError(getShell(), Activator.getResourceString("ConnectionComposite.ErrorConnection"), ex.getMessage()); //$NON-NLS-1$
					ex.printStackTrace();
				}
				
			}
			
		});
		
		Label group = new Label(this, SWT.NONE);
		group.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		group.setText(Activator.getResourceString("ConnectionComposite.10"));  //$NON-NLS-1$
	
		groupNames = new Combo(this, SWT.READ_ONLY);
		groupNames.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groupNames.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				groupName = groupNames.getText();
				
			}
			
		});
		
		Label rep = new Label(this, SWT.NONE);
		rep.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		rep.setText(Activator.getResourceString("ConnectionComposite.12"));  //$NON-NLS-1$
	
		repositoryNames = new Combo(this, SWT.READ_ONLY);
		repositoryNames.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		repositoryNames.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				repositoryName = repositoryNames.getText();
			}
			
		});
		
		Button load = new Button(this, SWT.NONE);
		load.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		load.setText(Activator.getResourceString("ConnectionComposite.LoadRepository")); //$NON-NLS-1$
		load.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectedGroup = null;
				//Get the selected group
				for(int i=0;i<listGroup.size();i++){
					if(groupNames.getText().equals(listGroup.get(i).getName())){
						selectedGroup = listGroup.get(i);
					}
				}
				
				selectedRepository = null;
				//Get the selected group
				for(int i=0;i<listRepositories.size();i++){
					if(repositoryNames.getText().equals(listRepositories.get(i).getName())){
						selectedRepository = listRepositories.get(i);
					}
				}
				
				if(selectedGroup!=null && selectedRepository != null){
					loadData();
				}
			}
		});
		
		viewer = new TreeViewer(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		viewer.setContentProvider(new ITreeContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			@Override
			public void dispose() {
				
				
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
					List l = (List) inputElement;
					return l.toArray(new Object[l.size()]);
			}
			
			@Override
			public boolean hasChildren(Object element) {
				if (element instanceof RepositoryDirectory){
					try {
						if (repo.getChildDirectories((RepositoryDirectory) element).size() == 0 && (repo.getItems((RepositoryDirectory) element).size() == 0)){
							return false;
						}
						else{
							return true;
						}
					} catch (Exception e) {
						
						e.printStackTrace();
					}
				}
				return false;
			}
			
			@Override
			public Object getParent(Object element) {
				
				return null;
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {
				List l = new ArrayList<Object>();
				if (parentElement instanceof RepositoryDirectory){
					try{
						l.addAll(repo.getChildDirectories((RepositoryDirectory) parentElement));
						l.addAll(repo.getItems((RepositoryDirectory) parentElement));
					}catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				return l.toArray(new Object[l.size()]);
			}
		});
		
		viewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof RepositoryDirectory)
				{
					RepositoryDirectory dir = (RepositoryDirectory) element;
					return dir.getName();
				}
				else{
					RepositoryItem item = (RepositoryItem) element;
					return item.getItemName();
				}
			}
			@Override
			public Image getImage(Object obj) {
				if (obj instanceof RepositoryDirectory){
					return Activator.getDefault().getImageRegistry().get("directory"); //$NON-NLS-1$
				}
				if (obj instanceof RepositoryItem){
					return Activator.getDefault().getImageRegistry().get("directoryItem"); //$NON-NLS-1$
				}
				return null;
			}
			
		});
	}
	
	private void initRepositories(IVanillaAPI api, User user) {
		try {
			listRepositories = api.getVanillaRepositoryManager().getUserRepositories(user.getLogin());
	
			String[] items = new String[listRepositories.size()];
			int i = 0;
			
			for(Repository c : listRepositories){
				items[i++] = c.getName();
			}
						
			repositoryNames.setItems(items);
			
			i=0;
//			for(Repository c : listRepositories){
//				if((c.getName()).equals("axis")){
//					repositoryNames.select(i);
//				}
//				i++;
//			}
		} catch (Exception ex2) {
			
			ex2.printStackTrace();
		}
	}
	
	private void initGroups(IVanillaAPI api, User user) {
		try {
			listGroup = api.getVanillaSecurityManager().getGroups(user);
			String[] groups = new String[listGroup.size()];
			int i = 0;
			
			for(Group c : listGroup){
				groups[i++] = c.getName();
			}
			
			groupNames.setItems(groups);
			
			i=0;
			for(Group c : listGroup){
				if((c.getName()).equals("System")){
					groupNames.select(i);
				}
				i++;
			}
		} catch (Exception ex3) {
			
			ex3.printStackTrace();
		}
	}
	
	private IRepository repo;
	
	public void loadData(){
		
		IVanillaContext vanillaContext = new BaseVanillaContext(url.getText(), login.getText(), password.getText());
		
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaContext, selectedGroup, selectedRepository);
		
		IRepositoryApi connection = new RemoteRepositoryApi(ctx);
		try {
			repo = new bpm.vanilla.platform.core.repository.Repository(connection, IRepositoryApi.FWR_TYPE);
			rootDirectories = repo.getRootDirectories();
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}

		viewer.setInput(rootDirectories);
	}
	
	public String getUrl(){
		return url.getText();
	}
	
	public String getLogin(){
		return login.getText();
	}
	
	public String getPassword(){
		return password.getText();
	}
	
	public int getSelectedGroupId(){
		return selectedGroup.getId();
	}
	
	public String getSelectedGroupName(){
		return selectedGroup.getName();
	}
	
	public String getSelectedRepositoryUrl(){
		return selectedRepository.getUrl();
	}
	
	public int getFWReportId() throws Exception{
		IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
		if(ss.isEmpty() || !(ss.getFirstElement() instanceof RepositoryItem)){
			throw new Exception("No FreeWebReport has been select.");
		}
		return ((RepositoryItem)ss.getFirstElement()).getId();
	}
}
