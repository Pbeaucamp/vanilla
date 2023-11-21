package bpm.csv.oda.ui.impl.datasource;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.ActionFactory;

import bpm.csv.oda.runtime.impl.Connection;
import bpm.csv.oda.ui.Activator;
import bpm.csv.oda.ui.Messages;
import bpm.vanilla.oda.commons.trees.TreeContentProvider;
import bpm.vanilla.oda.commons.trees.TreeDirectory;
import bpm.vanilla.oda.commons.trees.TreeItem;
import bpm.vanilla.oda.commons.trees.TreeLabelProvider;
import bpm.vanilla.oda.commons.trees.TreeObject;
import bpm.vanilla.oda.commons.trees.TreeParent;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserRep;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ConnectionComposite extends Composite {

	private Text login, password, separator, dateFormat, url;
	private Text path;

	private TreeViewer viewer;

	private ComboViewer groups;
	private ComboViewer repositories;

	private IRepositoryApi repSocket;
	private bpm.vanilla.platform.core.repository.Repository repository;
	private RepositoryItem directoryItem;

	private List<Group> availableGroups;
	private List<Repository> availableRepositories;
	private TreeParent root;

	private CSVDataSourcePage page;
	private IVanillaAPI api;

	public ConnectionComposite(Composite parent, int style, CSVDataSourcePage csvDataSourcePage) {
		super(parent, style);
		this.setLayout(new GridLayout(3, false));
		this.page = csvDataSourcePage;
		page.setPingButton(false);
		buildContent();
		fillData(null);
	}

	private void buildContent() {
		Label l0 = new Label(this, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l0.setText("File path"); //$NON-NLS-1$

		path = new Text(this, SWT.BORDER);
		path.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		Button btnGetFile = new Button(this, SWT.PUSH);
		btnGetFile.setText("..."); //$NON-NLS-1$
		btnGetFile.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		btnGetFile.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell());
				fd.setFilterExtensions(new String[] {"*.csv", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
				path.setText(fd.open());
			}
		});
		
		Label l12 = new Label(this, SWT.NONE);
		l12.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l12.setText("Vanilla Url"); //$NON-NLS-1$

		url = new Text(this, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

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
		_btn.setLayout(new GridLayout(2, false));
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
					MessageDialog.openError(getShell(), Messages.ConnectionComposite_2, Messages.ConnectionComposite_3 + _e.getMessage());
				}
			}
		});

		Button btnSecu = new Button(_btn, SWT.PUSH);
		btnSecu.setText("Define Vanilla's Url"); //$NON-NLS-1$
		btnSecu.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, true, false));
		btnSecu.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ActionFactory.PREFERENCES.create(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow()).run();
			}
		});

		Label l7 = new Label(this, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText("Repository Name"); //$NON-NLS-1$

		repositories = new ComboViewer(this, SWT.READ_ONLY);
		repositories.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true, 2, 1));
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
		l8.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l8.setText("Group Name"); //$NON-NLS-1$

		groups = new ComboViewer(this, SWT.READ_ONLY);
		groups.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true, 2, 1));
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
		btn.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false, 3, 1));

		Button conRep = new Button(btn, SWT.PUSH);
		conRep.setText("Connect to Repository"); //$NON-NLS-1$
		conRep.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, true, false));
		conRep.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object o = ((IStructuredSelection) groups.getSelection()).getFirstElement();
				if(o == null) {
					MessageDialog.openWarning(getShell(), Messages.ConnectionComposite_4, Messages.ConnectionComposite_5);
					return;
				}
				Object _o = ((IStructuredSelection) repositories.getSelection()).getFirstElement();
				if(_o == null) {
					MessageDialog.openWarning(getShell(), Messages.ConnectionComposite_6, Messages.ConnectionComposite_7);
					return;
				}

//				IPreferenceStore store = bpm.vanilla.oda.commons.Activator.getDefault().getPreferenceStore();
				String vanillaurl = url.getText();

				if(vanillaurl == null || vanillaurl.equalsIgnoreCase("")) { //$NON-NLS-1$
					MessageDialog.openInformation(getShell(), Messages.ConnectionComposite_9, Messages.ConnectionComposite_10);
					return;
				}
				repSocket = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(vanillaurl, login.getText(), password.getText()), (Group) o, (Repository) _o));
				try {
					createTreeContent();
				} catch(Exception e1) {
					MessageDialog.openError(getShell(), "Connection Error", e1.getMessage()); //$NON-NLS-1$
					e1.printStackTrace();
				}

			}

		});

		Button publish = new Button(btn, SWT.PUSH);
		publish.setText("Publish file on repository"); //$NON-NLS-1$
		publish.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, true, false));
		publish.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(path.getText() == null || path.getText().equalsIgnoreCase("")) { //$NON-NLS-1$
					MessageDialog.openWarning(getShell(), Messages.ConnectionComposite_12, Messages.ConnectionComposite_13);
					return;
				}
				if(viewer.getSelection().isEmpty()) {
					MessageDialog.openWarning(getShell(), Messages.ConnectionComposite_14, Messages.ConnectionComposite_15);
					return;
				}
				Object selected = ((IStructuredSelection) viewer.getSelection()).getFirstElement();

				if(!(selected instanceof TreeDirectory)) {
					return;
				}
				String p = path.getText();
				String name = p.substring(p.lastIndexOf(File.separator) + 1);
				try {
					repSocket.getRepositoryService().addExternalDocumentWithDisplay(((TreeDirectory) selected).getDirectory(), name, "", "", "", new FileInputStream(p), false, p.substring(p.lastIndexOf(".") + 1)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					createTreeContent();
				} catch(Exception e1) {
					e1.printStackTrace();
				}

			}

		});

		viewer = new TreeViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setContentProvider(new TreeContentProvider());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				if(viewer.getSelection().isEmpty()) {
					directoryItem = null;
					if(page != null) {
						page.setPingButton(false);
					}

					return;
				}

				Object selected = ((IStructuredSelection) viewer.getSelection()).getFirstElement();

				if(selected instanceof TreeItem) {
					directoryItem = ((TreeItem) selected).getItem();
					if(page != null) {
						page.setPingButton(true);
					}
				}
				else {
					directoryItem = null;
					if(page != null) {
						page.setPingButton(false);
					}

				}

				page.updateButtons();

			}

		});
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {

			}

		});

		Label ls = new Label(this, SWT.NONE);
		ls.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		ls.setText("Column separator"); //$NON-NLS-1$

		separator = new Text(this, SWT.BORDER);
		separator.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		separator.setText(";"); //$NON-NLS-1$

		Label ld = new Label(this, SWT.NONE);
		ld.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		ld.setText("Date Format"); //$NON-NLS-1$

		dateFormat = new Text(this, SWT.BORDER);
		dateFormat.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		dateFormat.setText("yyyy/MM/dd HH:mm:ss"); //$NON-NLS-1$

	}

	private void fillInfos() throws Exception {
//		IPreferenceStore store = bpm.vanilla.oda.commons.Activator.getDefault().getPreferenceStore();
		String vanillaurl = url.getText();

		if(vanillaurl == null || vanillaurl.equalsIgnoreCase("")) { //$NON-NLS-1$
			MessageDialog.openInformation(getShell(), Messages.ConnectionComposite_23, Messages.ConnectionComposite_24);
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

	public void fillData(Properties properties) {

		if(properties != null) {
			password.setText(properties.getProperty(Connection.PASSWORD));
			login.setText(properties.getProperty(Connection.USER));

			separator.setText(properties.getProperty(Connection.CSV_SEPARATOR));
			dateFormat.setText(properties.getProperty(Connection.DATE_FORMAT));

			try {
				fillInfos();

				Group g = api.getVanillaSecurityManager().getGroupById(new Integer(properties.getProperty(Connection.GROUP_ID)));

				for(Group _g : availableGroups) {
					if(g.getId() == _g.getId())
						groups.setSelection(new StructuredSelection(_g));
				}

				Repository rep = api.getVanillaRepositoryManager().getRepositoryById(new Integer(properties.getProperty(Connection.REPOSITORY_ID)));

				for(Repository _rep : availableRepositories) {
					if(rep.getId() == _rep.getId())
						repositories.setSelection(new StructuredSelection(_rep));
				}
				
				String vanillaurl = url.getText();

				if(vanillaurl == null || vanillaurl.equalsIgnoreCase("")) { //$NON-NLS-1$
					MessageDialog.openInformation(getShell(), Messages.ConnectionComposite_26, Messages.ConnectionComposite_27);
					return;
				}

				repSocket = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(vanillaurl, login.getText(), password.getText()), g, rep));

				try {
					createTreeContent();

					directoryItem = repository.getItem(new Integer(properties.getProperty(Connection.DIRECTORY_ITEM_ID)));

					findTreeItem(root);

				} catch(Exception e1) {
					MessageDialog.openError(getShell(), "Connection Error", e1.getMessage()); //$NON-NLS-1$
					e1.printStackTrace();
				}

			} catch(Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void findTreeItem(TreeParent _root) {
		for(TreeObject o : _root.getChildren()){
			if (o instanceof TreeItem && ((TreeItem)o).getItem().getId() == directoryItem.getId()){
				viewer.setSelection(new StructuredSelection((TreeItem) o));
			}
			else if(o instanceof TreeDirectory) {
				findTreeItem((TreeParent) o);
			}
		}

	}

	private void createTreeContent() throws Exception {
		repository = new bpm.vanilla.platform.core.repository.Repository(repSocket, IRepositoryApi.EXTERNAL_DOCUMENT);
		List<RepositoryDirectory> list = repository.getRootDirectories();
		root = new TreeParent("root"); //$NON-NLS-1$

		StringBuffer buf = new StringBuffer();

		for(RepositoryDirectory d : list) {
			TreeDirectory tp = new TreeDirectory(d);
			root.addChild(tp);
			buildChilds(tp);

			try {
				for(RepositoryItem di : repository.getItems(d)) {
					TreeItem ti = new TreeItem(di);
					tp.addChild(ti);
				}
			} catch(Exception ex) {
				buf.append(Messages.ConnectionComposite_28 + d.getName() + Messages.ConnectionComposite_29 + ex.getMessage() + "\n"); //$NON-NLS-3$
			}

		}

		viewer.setInput(root);

		if(buf.toString().length() > 0) {
			MessageDialog.openWarning(getShell(), Messages.ConnectionComposite_31, Messages.ConnectionComposite_32 + buf.toString());
		}
	}

	private void buildChilds(TreeDirectory parent) {

		RepositoryDirectory dir = ((TreeDirectory) parent).getDirectory();

		try {

			for(RepositoryDirectory d : repository.getChildDirectories(dir)) {
				TreeDirectory td = new TreeDirectory(d);
				parent.addChild(td);
				for(RepositoryItem di : repository.getItems(d)) {
					TreeItem ti = new TreeItem(di);
					td.addChild(ti);
				}
				buildChilds(td);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}

	}

	public int getRepositoryId() {
		Object o = ((IStructuredSelection) repositories.getSelection()).getFirstElement();
		return ((Repository) o).getId();
	}

	public String getUsername() {
		return login.getText();
	}

	public String getPassword() {
		return password.getText();
	}

	public RepositoryItem getDirectoryItem() {
		return directoryItem;
	}

	public String getSeparator() {
		return separator.getText();
	}

	public int getGroupId() {
		Object o = ((IStructuredSelection) groups.getSelection()).getFirstElement();
		return ((Group) o).getId();
	}

	public String getDateFormat() {
		return dateFormat.getText();
	}

	public boolean isPageComplete() {
		return directoryItem != null && !dateFormat.getText().equalsIgnoreCase("") && !separator.getText().equalsIgnoreCase(""); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
