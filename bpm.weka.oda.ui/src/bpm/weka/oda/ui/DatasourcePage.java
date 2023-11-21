package bpm.weka.oda.ui;

import java.util.Collections;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.weka.oda.runtime.Driver;

public class DatasourcePage extends DataSourceWizardPage {

	private Properties properties;
	
	private Text url, login, password;
	private TreeViewer viewer;
	private ComboViewer groupNames, repositories;
	
	private Button radioRep, radioLocal;
	
	private Composite root, localComp;

	private Text txtLocal;
	
	public DatasourcePage(String pageName) {
		super(pageName);
	}

	@Override
	public Properties collectCustomProperties() {
		properties = new Properties();
		
		try {
			properties.put(Driver.PROP_USER, login.getText());
			properties.put(Driver.PROP_PASSWORD, password.getText());
			properties.put(Driver.PROP_URL, url.getText());
			
			Repository rep = (Repository) ((IStructuredSelection)repositories.getSelection()).getFirstElement();
			properties.put(Driver.PROP_REPID, rep.getId() + "");
			
			Group grp = (Group) ((IStructuredSelection)groupNames.getSelection()).getFirstElement();
			properties.put(Driver.PROP_GROUPID, grp.getId() + "");
			
			RepositoryItem item = (RepositoryItem) ((IStructuredSelection)viewer.getSelection()).getFirstElement();
			properties.put(Driver.PROP_ITEMID, item.getId() + "");
		} catch (Exception e) {
		}
		
		properties.put(Driver.PROP_LOCAL_FILE, txtLocal.getText());
		
		return properties;
	}

	@Override
	public void createPageCustomControl(Composite arg0) {
		
		arg0.setLayout(new GridLayout());
		arg0.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		radioRep = new Button(arg0, SWT.RADIO);
		radioRep.setText("From Repository");
		radioRep.setSelection(true);
		radioRep.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enableComposite(root);
				disableComposite(localComp);
			}
		});
		
		root = new Composite(arg0, SWT.NONE);
		root.setLayout(new GridLayout(3, false));
		root.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label l = new Label(root, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Login"); //$NON-NLS-1$
		
		login = new Text(root, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1 ));

		Label l2 = new Label(root, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText("Password"); //$NON-NLS-1$
		
		password = new Text(root, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1 ));
		
		Label l3 = new Label(root, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText("Runtime url"); //$NON-NLS-1$
		
		url = new Text(root, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1 ));
		
		Button conRep = new Button(root, SWT.PUSH);
		conRep.setText("Connection"); //$NON-NLS-1$
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
					MessageDialog.openError(getShell(), "Error while authentifying user", "An error occured during the authentifying process" + ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
				}
				
			}
			
		});
		
		
		Label l7 = new Label(root, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText("Group");  //$NON-NLS-1$
	
		groupNames = new ComboViewer(root, SWT.READ_ONLY);
		groupNames.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		groupNames.setContentProvider(new ArrayContentProvider());
		groupNames.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		
		
		l7 = new Label(root, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText("Repository"); //$NON-NLS-1$
		
		
		repositories = new ComboViewer(root, SWT.READ_ONLY);
		repositories.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		repositories.setContentProvider(new ArrayContentProvider());
		repositories.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Repository)element).getName();
			}
		});
		
		Button loadRep = new Button(root, SWT.PUSH);
		loadRep.setText("Load Repository"); //$NON-NLS-1$
		loadRep.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		loadRep.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				IRepositoryApi api = new RemoteRepositoryApi(
						new BaseRepositoryContext(
						new BaseVanillaContext(url.getText(), login.getText(), password.getText()),
						(Group) ((IStructuredSelection)groupNames.getSelection()).getFirstElement(), 
						(Repository) ((IStructuredSelection)repositories.getSelection()).getFirstElement()));
				
				IRepository rep = new bpm.vanilla.platform.core.repository.Repository(api, IRepositoryApi.EXTERNAL_DOCUMENT);
				
				viewer.setInput(rep);
				
			}
			
		});
		
		viewer = new TreeViewer(root, SWT.BORDER  | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true,3 , 1));
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setContentProvider(new TreeContentProvider());
		
		radioLocal = new Button(arg0, SWT.RADIO);
		radioLocal.setText("From local fileSystem");
		radioLocal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enableComposite(localComp);
				disableComposite(root);
			}
		});
		
		localComp = new Composite(arg0, SWT.NONE);
		localComp.setLayout(new GridLayout(3, false));
		localComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label locallabel = new Label(localComp, SWT.NONE);
		locallabel.setText("Local file");
		locallabel.setLayoutData(new GridData(SWT.FILL,SWT.FILL, false, false));
		
		txtLocal = new Text(localComp, SWT.BORDER);
		txtLocal.setLayoutData(new GridData(SWT.FILL,SWT.FILL, true, false));
		
		Button browse = new Button(localComp, SWT.PUSH);
		browse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		browse.setText("...");
		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				FileDialog dial = new FileDialog(getShell());
				String filePath = dial.open();
				txtLocal.setText(filePath);
			}
		});
		
		
	}

	private void enableComposite(Composite comp) {
		for(Control c : comp.getChildren()) {
			c.setEnabled(true);
		}
		
	}

	private void disableComposite(Composite comp) {
		for(Control c : comp.getChildren()) {
			c.setEnabled(false);
		}
	}

	@Override
	public void setInitialProperties(Properties arg0) {
		properties = arg0;
	}

}
