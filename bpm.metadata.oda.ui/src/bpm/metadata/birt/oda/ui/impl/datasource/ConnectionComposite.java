package bpm.metadata.birt.oda.ui.impl.datasource;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.MetaDataReader;
import bpm.metadata.birt.oda.runtime.ConnectionPool;
import bpm.metadata.birt.oda.runtime.impl.Connection;
import bpm.metadata.birt.oda.ui.Activator;
import bpm.metadata.birt.oda.ui.preferences.Messages;
import bpm.metadata.birt.oda.ui.trees.TreeContentProvider;
import bpm.metadata.birt.oda.ui.trees.TreeDirectory;
import bpm.metadata.birt.oda.ui.trees.TreeItem;
import bpm.metadata.birt.oda.ui.trees.TreeLabelProvider;
import bpm.metadata.birt.oda.ui.trees.TreeObject;
import bpm.metadata.birt.oda.ui.trees.TreeParent;
import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.UnitedOlapBusinessPackage;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
//import bpm.repository.api.utils.Securizer;
/**
 * This class provide a composite to show a FMDT connection from
 * a repository
 * @author LCA
 *
 */
public class ConnectionComposite extends Composite {

	private Text url, login, password;
	private TreeViewer viewer;
	private ComboViewer businessModel, businessPackage;
	
	private Combo connectionsName;
	private ComboViewer groupNames, repositories;
	
	private IRepositoryApi repSocket;
	private bpm.vanilla.platform.core.repository.Repository repository;
	private RepositoryItem directoryItem;
	private IBusinessPackage pack;
	private IBusinessModel model;
	private String groupName, conName;
	
	private FmdtDataSourcePage page;
	
	private String odaDatasourceId;
	
	public ConnectionComposite(Composite parent, int style, FmdtDataSourcePage page, String odaDatasourceId) {
		super(parent, style);
		this.setLayout(new GridLayout(3, false));
		this.page = page;
		
		this.odaDatasourceId = odaDatasourceId;
		
		buildContent();
		fillData(null);
	}
	
	private void buildContent(){
		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.getString("ConnectionComposite.Username")); //$NON-NLS-1$
		
		login = new Text(this, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1 ));
		login.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.Modify, new Event());
				
			}
			
		});

		
		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.getString("ConnectionComposite.Password")); //$NON-NLS-1$
		
		password = new Text(this, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1 ));
		password.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.Modify, new Event());
				
			}
			
		});
		
		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.getString("ConnectionComposite.BiRepUrl")); //$NON-NLS-1$
		
		url = new Text(this, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1 ));
		url.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.Modify, new Event());
				
			}
			
		});
		
		Button conRep = new Button(this, SWT.PUSH);
		conRep.setText(Messages.getString("ConnectionComposite.0")); //$NON-NLS-1$
		conRep.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		conRep.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IVanillaAPI api = new RemoteVanillaPlatform(url.getText(), login.getText(), password.getText());
				try{
					User user = api.getVanillaSecurityManager().authentify("", login.getText(), password.getText(), false);
					groupNames.setInput(api.getVanillaSecurityManager().getGroups(user));
					repositories.setInput(api.getVanillaRepositoryManager().getUserRepositories(user.getLogin()));
				}catch(Exception ex){
					ex.printStackTrace();
					groupNames.setInput(Collections.EMPTY_LIST);
					repositories.setInput(Collections.EMPTY_LIST);
					MessageDialog.openError(getShell(), Messages.getString("ConnectionComposite.1"), Messages.getString("ConnectionComposite.2") +ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
				}
				
			}
			
		});
		
		
		Label l7 = new Label(this, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText(Messages.getString("ConnectionComposite.10"));  //$NON-NLS-1$
	
		groupNames = new ComboViewer(this, SWT.READ_ONLY);
		groupNames.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		groupNames.setContentProvider(new ArrayContentProvider());
		groupNames.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		groupNames.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (groupNames.getSelection().isEmpty()){
					groupName = null;
				}
				else{
					groupName = ((Group)((IStructuredSelection)groupNames.getSelection()).getFirstElement()).getName();
				}
				notifyListeners(SWT.Modify, new Event());
				
			}
		});
		
		
		
		
		
		l7 = new Label(this, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText(Messages.getString("ConnectionComposite.3")); //$NON-NLS-1$
		
		
		repositories = new ComboViewer(this, SWT.READ_ONLY);
		repositories.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		repositories.setContentProvider(new ArrayContentProvider());
		repositories.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Repository)element).getName();
			}
		});
		repositories.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				bpm.vanilla.platform.core.beans.Repository repdef = (bpm.vanilla.platform.core.beans.Repository)((IStructuredSelection)repositories.getSelection()).getFirstElement();
				
				if (groupNames.getSelection().isEmpty()){
					return;
				}
				Group group = (Group)((IStructuredSelection)groupNames.getSelection()).getFirstElement();
				
				repSocket = new RemoteRepositoryApi(new BaseRepositoryContext(
						new BaseVanillaContext(
								url.getText(), 
								login.getText(), 
								password.getText()), group, repdef)) ;
					

								
				try {
					repository = new  bpm.vanilla.platform.core.repository.Repository(repSocket, IRepositoryApi.FMDT_TYPE);
					createTreeContent();
				} catch (Exception e1) {
					MessageDialog.openError(getShell(), Messages.getString("ConnectionComposite.ErrorConnection"), e1.getMessage()); //$NON-NLS-1$
					e1.printStackTrace();
				}
			
			}
		});

		
		
		
		viewer = new TreeViewer(this, SWT.BORDER  | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true,3 , 1));
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setContentProvider(new TreeContentProvider());
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				if (viewer.getSelection().isEmpty()){
					directoryItem = null;
					if (page != null){
						page.setPingButton(false);
					}
					notifyListeners(SWT.Modify, new Event());
					return;
				}
				
				
				Object selected = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
				
				if (selected instanceof TreeItem){
					directoryItem = ((TreeItem)selected).getItem();
					if (page != null){
						page.setPingButton(true);
					}
				}
				else{
					directoryItem = null;
					if (page != null){
						page.setPingButton(false);
					}

				}
				notifyListeners(SWT.Modify, new Event());
			}
			
		});
		viewer.addDoubleClickListener(new IDoubleClickListener(){

			public void doubleClick(DoubleClickEvent event) {
				
				businessModel.getCombo().removeAll(); // FP IDEA
				businessPackage.getCombo().removeAll(); // FP IDEA
				connectionsName.removeAll(); // FP IDEA
				
				if (viewer.getSelection().isEmpty()){
					notifyListeners(SWT.Modify, new Event());
					return;
				}
				
				final Object selected = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
				
				if (selected instanceof TreeItem){
					String xmlModel = null;
					try {
						
						
						try{
							repSocket = new RemoteRepositoryApi(new BaseRepositoryContext(
									new BaseVanillaContext(url.getText(), login.getText(), password.getText()),
									((Group)((IStructuredSelection)groupNames.getSelection()).getFirstElement()),
									((Repository)((IStructuredSelection)repositories.getSelection()).getFirstElement())
							)); 
						}catch(Exception ex){
							
						}
						
						xmlModel = repSocket.getRepositoryService().loadModel(((TreeItem)selected).getItem());
						
						String groupName = groupNames.getCombo().getText();
						Collection<IBusinessModel> models = MetaDataReader.read(groupName, IOUtils.toInputStream(xmlModel, "UTF-8"), null, true); //$NON-NLS-1$
						
						businessModel.setInput(models);
						// FP IDEA
						if (models.size() > 0) {
							ISelection selection = new StructuredSelection(models.toArray()[0]);
							businessModel.setSelection(selection, true);
						}
						// FP IDEA
					}catch (FileNotFoundException e) { // FP IDEA
							MessageDialog.openError(getShell(), Messages.getString("ConnectionComposite.Error"), Activator.getResourceString("ConnectionComposite.ResourcesError")); //$NON-NLS-1$ //$NON-NLS-2$ $NON-NLS-2$ FP IDEA	
					// FP IDEA
					} catch (Throwable e) {
						e.printStackTrace();
						
					}
					notifyListeners(SWT.Modify, new Event());
				}
				
			}
			
		});
		
		
	
		Label l4 = new Label(this, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.getString("ConnectionComposite.BusinessModel")); //$NON-NLS-1$
		
		businessModel = new ComboViewer(this, SWT.READ_ONLY);
		businessModel.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false,2 , 1));
		businessModel.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				
				if (element instanceof IBusinessModel){
					return ((IBusinessModel)element).getName();
				}
				return "error"; //$NON-NLS-1$
			}
			
		});
		businessModel.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				Collection<IBusinessModel> c = (Collection<IBusinessModel>)inputElement;
				
				return c.toArray(new IBusinessModel[c.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
		});
		businessModel.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				if (businessModel.getSelection().isEmpty()){
					model = null;
					notifyListeners(SWT.Modify, new Event());
					return;
				}
				notifyListeners(SWT.Modify, new Event());
				model = (IBusinessModel)((IStructuredSelection)businessModel.getSelection()).getFirstElement();
				
				Collection<IBusinessPackage> packs = model.getBusinessPackages(groupName);
				Collection<IBusinessPackage> packsToshow = new ArrayList<IBusinessPackage>();
				for(IBusinessPackage pack : packs) {
					if(odaDatasourceId.equals("bpm.metadata.birt.oda.runtime.olap") && pack instanceof UnitedOlapBusinessPackage) { //$NON-NLS-1$
						packsToshow.add(pack);
					}
					else if (odaDatasourceId.equals("bpm.metadata.birt.oda.runtime") && pack instanceof BusinessPackage) { //$NON-NLS-1$
						packsToshow.add(pack);
					}
				}
				
				businessPackage.setInput(packsToshow);
				if (packsToshow.size() > 0) {
					ISelection selection = new StructuredSelection(packsToshow.toArray()[0]);
					businessPackage.setSelection(selection, true);
				}
			}
			
		});
		
		
		Label l5 = new Label(this, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l5.setText(Messages.getString("ConnectionComposite.BusinessPackage")); //$NON-NLS-1$
		
		
		businessPackage = new ComboViewer(this, SWT.READ_ONLY);
		businessPackage.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false,2 , 1));
		businessPackage.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof IBusinessPackage){
					return ((IBusinessPackage)element).getName();
				}
				return Messages.getString("ConnectionComposite.11"); //$NON-NLS-1$
			}
			
		});
		businessPackage.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				Collection<IBusinessPackage> c = (Collection<IBusinessPackage>)inputElement;
				
				return c.toArray(new IBusinessPackage[c.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
		});
		businessPackage.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				if (businessPackage.getSelection().isEmpty()){
					notifyListeners(SWT.Modify, new Event());
					pack = null;
				}
				
				pack = (IBusinessPackage)((IStructuredSelection)businessPackage.getSelection()).getFirstElement();
				List<String> cons = pack.getConnectionsNames("none"); //$NON-NLS-1$
				connectionsName.setItems(cons.toArray(new String[cons.size()]));
				connectionsName.select(0);
				conName = connectionsName.getItem(0); 
				notifyListeners(SWT.Modify, new Event());
			}
			
		});

	
		Label l6 = new Label(this, SWT.NONE);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l6.setText(Messages.getString("ConnectionComposite.ConnectionName")); //$NON-NLS-1$
	
		connectionsName = new Combo(this, SWT.READ_ONLY);
		connectionsName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		connectionsName.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				conName = connectionsName.getText();
				notifyListeners(SWT.Modify, new Event());
			}
			
		});
		
		
		
	}

	
	public void fillData(Properties properties){
		
		if (properties != null){
			
			try{
				repSocket = ConnectionPool.getRepositoryConnection(properties);
			}catch(Exception ex){
				ex.printStackTrace();
				return;
			}
			
			password.setText(properties.getProperty(Connection.PASSWORD));
			login.setText(properties.getProperty(Connection.USER));
			if (properties.getProperty(Connection.VANILLA_URL) == null){
				try {
					url.setText(repSocket.getContext().getVanillaContext().getVanillaUrl());
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(getShell(), Messages.getString("ConnectionComposite.13"), Messages.getString("ConnectionComposite.14") + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}
			}
			else{
				url.setText(properties.getProperty(Connection.VANILLA_URL));
			}
			
			groupName = properties.getProperty(Connection.GROUP_NAME);
			conName = properties.getProperty(Connection.CONNECTION_NAME);
			
			IVanillaAPI api = new RemoteVanillaPlatform(url.getText(), login.getText(), password.getText());
			User user = null;
			try{
				user = api.getVanillaSecurityManager().authentify("", login.getText(), password.getText(), true);
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), Messages.getString("ConnectionComposite.15"),  e.getMessage()); //$NON-NLS-1$
				return;
			}
			try {
				repository = new bpm.vanilla.platform.core.repository.Repository(repSocket, IRepositoryApi.FMDT_TYPE);
				List<Group> l = api.getVanillaSecurityManager().getGroups(user);
				groupNames.setInput(l);
				for(Group g : l){
					if (g.getName().equals(groupName)){
						groupNames.setSelection(new StructuredSelection(g));
						break;
					}
				}
				
			} catch (Exception e) {
				MessageDialog.openError(getShell(), Messages.getString("ConnectionComposite.Error"), e.getMessage()); //$NON-NLS-1$
				e.printStackTrace();
			}
			
			try{
				List<Repository> reps = api.getVanillaRepositoryManager().getUserRepositories(user.getLogin());
				repositories.setInput(reps);
				for(Repository g : reps){
					if (properties.getProperty(Connection.REPOSITORY_ID) != null){
						if ((g.getId() + "").equals(properties.getProperty(Connection.REPOSITORY_ID) )){ //$NON-NLS-1$
							repositories.setSelection(new StructuredSelection(g));
							break;
						}
					}
					else{
						if (properties.getProperty(Connection.URL).equals(g.getUrl())){
							repositories.setSelection(new StructuredSelection(g));
							break;
						}
					}
					
				}
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(getShell(), Messages.getString("ConnectionComposite.17"),  Messages.getString("ConnectionComposite.18") + ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
			
			
			
				
				try {
					createTreeContent();
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(getShell(), Messages.getString("ConnectionComposite.17"),  e.getMessage());
					return;
				}
				
				//set the selection in the tree
				setTreeSelection((TreeParent)viewer.getInput(), 
								Integer.parseInt(properties.getProperty(Connection.DIRECTORY_ITEM_ID)), 
								properties.getProperty(Connection.BUSINESS_MODEL), 
								properties.getProperty(Connection.BUSINESS_PACKAGE), groupName);
			
			
		}
		else{
			
			String url = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
			String login = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
			String password = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
			
//			String url = Activator.getDefault().getPreferenceStore().getString(CommonPreferenceConstants.P_BPM_VANILLA_URL);
//			String login = Activator.getDefault().getPreferenceStore().getString(CommonPreferenceConstants.P_BPM_REPOSITORY_LOGIN);
//			String password = Activator.getDefault().getPreferenceStore().getString(CommonPreferenceConstants.P_BPM_REPOSITORY_PASSWORD);
//			
			if (url != null){
				this.url.setText(url);
			}
			
			if (login != null){
				this.password.setText(password);
			}
			if (password != null){
				this.login.setText(login);
			}
//			
//			
//			
//			
//			HashMap<String, String> commonPref;
//			try {
//				commonPref = PreferenceReader.readCommonsProperties();
//				url = commonPref.get(CommonPreferenceConstants.P_BPM_VANILLA_URL).replace("\\:", ":");; //$NON-NLS-1$ //$NON-NLS-2$
//				login = commonPref.get(CommonPreferenceConstants.P_BPM_REPOSITORY_LOGIN).replace("\\:", ":");; //$NON-NLS-1$ //$NON-NLS-2$
//				password = commonPref.get(CommonPreferenceConstants.P_BPM_REPOSITORY_PASSWORD).replace("\\:", ":");; //$NON-NLS-1$ //$NON-NLS-2$
//
//				
//				if (url == null || url.equals("")){ //$NON-NLS-1$
//					this.url.setText(url);
//				}
//				
//				if (password == null || password.equals("")){ //$NON-NLS-1$
//					this.password.setText(password);
//				}
//				
//				if (login == null || login.equals("")){ //$NON-NLS-1$
//					this.login.setText(login);
//				}
//				
//				
//				
//			}catch(Exception e){
//				e.printStackTrace();
//				this.url.setText(Activator.getDefault().getPreferenceStore().getString(CommonPreferenceConstants.P_BPM_VANILLA_URL));
//				this.login.setText(Activator.getDefault().getPreferenceStore().getString(CommonPreferenceConstants.P_BPM_REPOSITORY_LOGIN));
//				this.password.setText(Activator.getDefault().getPreferenceStore().getString(CommonPreferenceConstants.P_BPM_REPOSITORY_PASSWORD));
//
//			}
		}
		
		
	}
	
	
	private void setTreeSelection(TreeParent root, int i, String businessModelName, String businesspackName, String groupName){
		for(TreeObject o : root.getChildren()){
			if (o instanceof TreeItem && ((TreeItem)o).getItem().getId() == i){
				
				viewer.setSelection(new StructuredSelection(o));
				
				//fillCombos
				
				try{
					String xml = repSocket.getRepositoryService().loadModel(((TreeItem)o).getItem());
					Collection<IBusinessModel> c = MetaDataReader.read(groupName, IOUtils.toInputStream(xml, "UTF-8"), null); //$NON-NLS-1$
					businessModel.setInput(c);
					
					for(IBusinessModel m : c){
						if (m.getName().equals(businessModelName)){
							businessModel.setSelection(new StructuredSelection(m));
							model = m;
							
							Collection<IBusinessPackage> packs = model.getBusinessPackages(groupName);
							Collection<IBusinessPackage> packsToshow = new ArrayList<IBusinessPackage>();
							for(IBusinessPackage pack : packs) {
								if(odaDatasourceId.equals("bpm.metadata.birt.oda.runtime.olap") && pack instanceof UnitedOlapBusinessPackage) { //$NON-NLS-1$
									packsToshow.add(pack);
								}
								else if (odaDatasourceId.equals("bpm.metadata.birt.oda.runtime") && pack instanceof BusinessPackage) { //$NON-NLS-1$
									packsToshow.add(pack);
								}
							}
							
							businessPackage.setInput(packsToshow);
							
							for(IBusinessPackage p : (Collection<IBusinessPackage>)businessPackage.getInput()){
								if (p.getName().equals(businesspackName)){
									businessPackage.setSelection(new StructuredSelection(p));
									pack = p;
									break;
								}
							}
							
							break;
						}
						
						
					}

				}catch(Exception e){
					e.printStackTrace();	
				}
								
				return;
			}
			
			if (o instanceof TreeDirectory){
				setTreeSelection((TreeParent)o, i, businessModelName, businesspackName, groupName);
			}
		}
	}
	
	private void createTreeContent() throws Exception{
		
		List<RepositoryDirectory> list = repository.getRootDirectories();
		TreeParent root = new TreeParent("root"); //$NON-NLS-1$
		
		StringBuffer buf = new StringBuffer();
		
		for(RepositoryDirectory d : list){
			TreeDirectory tp = new TreeDirectory(d);
			root.addChild(tp);
			buildChilds(tp);
			
			try{
				for(RepositoryItem di : repository.getItems(d)){
					TreeItem ti = new TreeItem(di);
						tp.addChild(ti);
				}
			}catch(Exception ex){
				buf.append(Messages.getString("ConnectionComposite.25") + d.getName() + Messages.getString("ConnectionComposite.26") + ex.getMessage() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			
		}
		
		viewer.setInput(root);
		
		if (buf.toString().length() > 0){
			MessageDialog.openWarning(getShell(), Messages.getString("ConnectionComposite.28"), Messages.getString("ConnectionComposite.29") + buf.toString()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	private void buildChilds(TreeDirectory parent){

		RepositoryDirectory dir = ((TreeDirectory)parent).getDirectory();
		
		try{
		
			for(RepositoryDirectory d : repository.getChildDirectories(dir)){
				TreeDirectory td = new TreeDirectory(d);
				parent.addChild(td);
				for(RepositoryItem di : repository.getItems(d)){
					TreeItem ti = new TreeItem(di);
					td.addChild(ti);
				}
				buildChilds(td);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}	
			
	}
	
	public boolean isFilled(){
		boolean filled = url != null && !url.getText().isEmpty() && login != null && !login.getText().isEmpty() && conName != null && !conName.isEmpty() && groupNames!= null && !groupNames.getSelection().isEmpty() && repositories != null && !repositories.getSelection().isEmpty();
		filled = filled && model != null & pack != null;
		return filled;
	}
	
	
	public void setProperties(Properties props) throws Exception{
		props.put(Connection.USER, login.getText());
		props.put(Connection.PASSWORD, password.getText());
		props.put(Connection.VANILLA_URL, url.getText());
		

		props.put(Connection.BUSINESS_PACKAGE, ((IBusinessPackage)((IStructuredSelection)businessPackage.getSelection()).getFirstElement()).getName());
		props.put(Connection.BUSINESS_MODEL, ((IBusinessModel)((IStructuredSelection)businessModel.getSelection()).getFirstElement()).getName());
		props.put(Connection.GROUP_NAME, ((Group)((IStructuredSelection)groupNames.getSelection()).getFirstElement()).getName());
		props.put(Connection.REPOSITORY_ID, ((Repository)((IStructuredSelection)repositories.getSelection()).getFirstElement()).getId() + ""); //$NON-NLS-1$
		props.put(Connection.CONNECTION_NAME, connectionsName.getText());

		props.put(Connection.DIRECTORY_ITEM_ID, directoryItem.getId() + "");	 //$NON-NLS-1$
	}
}
