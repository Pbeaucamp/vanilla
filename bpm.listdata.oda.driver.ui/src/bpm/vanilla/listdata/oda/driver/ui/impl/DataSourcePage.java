package bpm.vanilla.listdata.oda.driver.ui.impl;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import bpm.vanilla.listdata.oda.driver.impl.Connection;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class DataSourcePage extends DataSourceWizardPage {

	private Text login, password;
	private Text vanillaUrl;
	private ComboViewer repositories;
	private ComboViewer groups;
	private Button loadRepositories;
	
	private Properties initialProps;
	
	public DataSourcePage(String pageName) {
		super(pageName);
	}

	public DataSourcePage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public Properties collectCustomProperties() {
		Properties prop = new Properties();
		prop.setProperty(Connection.PROP_VANILLA_URL, vanillaUrl.getText());
		prop.setProperty(Connection.PROP_VANILLA_LOGIN, login.getText());
		prop.setProperty(Connection.PROP_VANILLA_PASSWORD, password.getText());
		prop.setProperty(Connection.PROP_REPOSITORY_ID, "" + ((Repository)((IStructuredSelection)repositories.getSelection()).getFirstElement()).getId());
		prop.setProperty(Connection.PROP_GROUP_ID, "" + ((Group)((IStructuredSelection)groups.getSelection()).getFirstElement()).getId());
		return prop;
	}

	@Override
	public void createPageCustomControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Vanilla Url");
		
		vanillaUrl = new Text(main, SWT.BORDER);
		vanillaUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Vanilla Login");
		
		login = new Text(main, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		login.addModifyListener(new TextListener() );
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Vanilla Password");
		
		password = new Text(main, SWT.PASSWORD | SWT.BORDER);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		password.addModifyListener(new TextListener() );
		
	
		
		loadRepositories = new Button(main, SWT.PUSH);
		loadRepositories.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		loadRepositories.setText("Connect");
		loadRepositories.setEnabled(false);
		loadRepositories.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					loadRepositories();
				}catch(Exception ex){
					MessageDialog.openError(getShell(), "Unable to load repositories", ex.getMessage());
				}
			}
		});
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Group");
		
		groups = new ComboViewer(main, SWT.READ_ONLY);
		groups.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
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
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		
		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Repository");
		
		repositories = new ComboViewer(main, SWT.READ_ONLY);
		repositories.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		repositories.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		
		repositories.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Repository)element).getName();
			}
		});
		fill();
	}

	
	private void fill(){
		if (initialProps == null){
			loadPreferences();
			return;
		}
		vanillaUrl.setText(initialProps.getProperty(Connection.PROP_VANILLA_URL));
		login.setText(initialProps.getProperty(Connection.PROP_VANILLA_LOGIN));
		password.setText(initialProps.getProperty(Connection.PROP_VANILLA_PASSWORD));
		int id = Integer.parseInt(initialProps.getProperty(Connection.PROP_REPOSITORY_ID));
		try{
			loadRepositories();
			for(Repository d : (Collection<Repository>)repositories.getInput()){
				if (d.getId() == id){
					repositories.setSelection(new StructuredSelection(d));
					break;
				}
			}
			try{
				id = Integer.parseInt(initialProps.getProperty(Connection.PROP_GROUP_ID));
				
				for(Group d : (Collection<Group>)groups.getInput()){
					if (d.getId() == id){
						groups.setSelection(new StructuredSelection(d));
						break;
					}
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			
		}catch(Exception ex){
			MessageDialog.openError(getShell(), "Unable to load repositories", ex.getMessage());
		}
		
	}
	@Override
	public void setInitialProperties(Properties dataSourceProps) {
		initialProps = dataSourceProps;
		
		
		

	}

	
	private class TextListener implements ModifyListener{
		public void modifyText(ModifyEvent e) {
			loadRepositories.setEnabled(!"".equals(login.getText()) && !"".equals(password.getText())); 
			
		}
	}
	
	
	private void loadRepositories()throws Exception{
		User vanillaUser = null;
		IVanillaAPI api =  new RemoteVanillaPlatform(vanillaUrl.getText(), login.getText(), password.getText());
		
		 try{
        	vanillaUser = api.getVanillaSecurityManager().getUserByLogin(login.getText());
        }catch(Exception ex){
        	throw new OdaException("Unable to fnd User, " + ex.getMessage());
        }
	        
        if (vanillaUser == null){
        	throw new OdaException("The User " + login.getText() + " does not exist.");
        }
        
//        String pass = password.getText();
        
	        
        repositories.setInput(api.getVanillaRepositoryManager().getUserRepositories(vanillaUser.getLogin()));
        
        groups.setInput(api.getVanillaSecurityManager().getGroups(vanillaUser));
	       
	}
	
	/*
	 * this method try to load preferences from other plugin
	 * to init the fields
	 */
	private void loadPreferences(){
		
		try{
			Bundle bundle = Platform.getBundle("bpm.oda.driver.reader");
			String activator = (String)bundle.getHeaders().get(Constants.BUNDLE_ACTIVATOR);
			Class activatorClass = bundle.loadClass(activator);
			Method method = activatorClass.getMethod("getDefault");
			AbstractUIPlugin plugin = (AbstractUIPlugin)method.invoke(null);
			vanillaUrl.setText(plugin.getPreferenceStore().getString("bpm.oda.driver.reader.preferences.vanillaUrl"));
			login.setText(plugin.getPreferenceStore().getString("bpm.oda.driver.reader.preferences.vanillaLogin"));
			password.setText(plugin.getPreferenceStore().getString("bpm.oda.driver.reader.preferences.vanillaPassword"));
		}catch(Throwable t){
			
		}
		
	}
}
