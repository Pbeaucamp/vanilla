package bpm.vanilla.repository.ui.connection;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.vanilla.designer.ui.common.preferences.PreferencesConstants;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.repository.ui.Activator;
import bpm.vanilla.repository.ui.Messages;
import bpm.vanilla.repository.ui.viewers.RepositoryLabelProvider;

public class CompositeWelcomeConnectionTab extends Composite{
	private static Font font = new Font(Display.getCurrent(), "Times New Roman", 12, SWT.ITALIC); //$NON-NLS-1$
	private ComboViewer repositories;
	private ComboViewer groups;
	
	private BaseVanillaContext vanillaCtx;
	private Text host, login, password;
	public CompositeWelcomeConnectionTab(Composite parent, int style) {
		super(parent, style);
		createContent();
	}
	
	
	public  IRepositoryContext getRepositoryContext() throws Exception{
		
		BaseRepositoryContext repCtx = new BaseRepositoryContext(vanillaCtx, (Group)(((IStructuredSelection)groups.getSelection()).getFirstElement()), (Repository)(((IStructuredSelection)repositories.getSelection()).getFirstElement()));
		
		if(!new RemoteVanillaPlatform(repCtx.getVanillaContext()).getVanillaSecurityManager().canAccessApp(repCtx.getGroup().getId(), Activator.getDefault().getDesignerActivator().getApplicationId())) {
			throw new Exception("You are not allowed to access this application.");
		}
		
		IPreferenceStore s = bpm.vanilla.designer.ui.common.Activator.getDefault().getPreferenceStore();
		s.setValue(PreferencesConstants.P_VANILLA_LOGIN, login.getText());
		s.setValue(PreferencesConstants.P_VANILLA_URL, host.getText());
		s.setValue(PreferencesConstants.P_VANILLA_PASSWORD, password.getText());
		s.setValue(PreferencesConstants.P_VANILLA_GROUP_ID, repCtx.getGroup().getId());
		s.setValue(PreferencesConstants.P_VANILLA_REP_ID, repCtx.getRepository().getId());

		return repCtx;
	}
	
	
	
	private void createContent(){
		this.setLayout(new GridLayout(2, false));
		this.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite container = this;
		
		Label l3 = new Label(container, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.CompositeWelcomeConnectionTab_0); 
		l3.setFont(font);
		
		host = new Text(container, SWT.BORDER);
		host.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		
		Label l6 = new Label(container, SWT.NONE);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l6.setText(Messages.CompositeWelcomeConnectionTab_1); 
		l6.setFont(font);
		
		login= new Text(container, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
				
		Label l7 = new Label(container, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText(Messages.CompositeWelcomeConnectionTab_2); 
		l7.setFont(font);
		
		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			
		
		Button loadGroups = new Button(container, SWT.PUSH);
		loadGroups.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 2, 1));
		loadGroups.setText(Messages.CompositeWelcomeConnectionTab_3);
		
		loadGroups.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				vanillaCtx = new BaseVanillaContext(host.getText(), login.getText(), password.getText());
				
				
				IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
				
				try{
					User user = vanillaApi.getVanillaSecurityManager().authentify("", vanillaCtx.getLogin(), vanillaCtx.getPassword(), false);
					repositories.setInput(vanillaApi.getVanillaRepositoryManager().getUserRepositories(login.getText()));
					groups.setInput(vanillaApi.getVanillaSecurityManager().getGroups(user));
					
				}catch(Exception ex){
					ex.printStackTrace();
					groups.setInput(Collections.EMPTY_LIST);
					repositories.setInput(Collections.EMPTY_LIST);
					vanillaCtx = null;
					MessageDialog.openError(getShell(), Messages.CompositeWelcomeConnectionTab_4, Messages.CompositeWelcomeConnectionTab_5 + ex.getMessage());
				}
			}
		});

		l7 = new Label(container, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText(Messages.CompositeWelcomeConnectionTab_6); 
		l7.setFont(font);
		
		
		groups = new ComboViewer(container, SWT.READ_ONLY);
		groups.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groups.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		groups.setContentProvider(new ArrayContentProvider());
	
		
		l7 = new Label(container, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText(Messages.CompositeWelcomeConnectionTab_7); 
		l7.setFont(font);
	
		repositories = new ComboViewer(container, SWT.READ_ONLY);
		repositories.setLabelProvider(new RepositoryLabelProvider());
		repositories.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		repositories.setContentProvider(new ArrayContentProvider());
		
		fill();
	}
	
	private void connect() throws Exception{
		vanillaCtx = new BaseVanillaContext(host.getText(), login.getText(), password.getText());
		
		
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);

		User user = vanillaApi.getVanillaSecurityManager().authentify("", vanillaCtx.getLogin(), vanillaCtx.getPassword(), true);
		repositories.setInput(vanillaApi.getVanillaRepositoryManager().getUserRepositories(login.getText()));
		groups.setInput(vanillaApi.getVanillaSecurityManager().getGroups(user));
			

	}
	private void fill(){
		if (Activator.getDefault().getDesignerActivator() != null && Activator.getDefault().getDesignerActivator().getRepositoryContext() != null){
			IRepositoryContext ctx = Activator.getDefault().getDesignerActivator().getRepositoryContext();
			
			login.setText(ctx.getVanillaContext().getLogin());
			password.setText(ctx.getVanillaContext().getPassword());
			host.setText(ctx.getVanillaContext().getVanillaUrl());
			
			try{
				connect();
				if (groups.getInput() != null){
					for(Group g : (List<Group>)groups.getInput()){
						if (g.getId() == ctx.getGroup().getId()){
							groups.setSelection(new StructuredSelection(g));
							break;
						}
					}
				}
				
				if (repositories.getInput() != null){
					for(Repository g : (List<Repository>)repositories.getInput()){
						if (g.getId() == ctx.getRepository().getId()){
							repositories.setSelection(new StructuredSelection(g));
							break;
						}
					}
				}
			}catch(Exception ex){
				
			}
		}
		else{
			IPreferenceStore s = bpm.vanilla.designer.ui.common.Activator.getDefault().getPreferenceStore();
			
			login.setText(s.getString(PreferencesConstants.P_VANILLA_LOGIN));
			password.setText(s.getString(PreferencesConstants.P_VANILLA_PASSWORD));
			host.setText(s.getString(PreferencesConstants.P_VANILLA_URL));
			
			try{
				connect();
				if (groups.getInput() != null){
					for(Group g : (List<Group>)groups.getInput()){
						if (g.getId() == s.getInt(PreferencesConstants.P_VANILLA_GROUP_ID)){
							groups.setSelection(new StructuredSelection(g));
							break;
						}
					}
				}
				
				if (repositories.getInput() != null){
					for(Repository g : (List<Repository>)repositories.getInput()){
						if (g.getId() == s.getInt(PreferencesConstants.P_VANILLA_REP_ID)){
							repositories.setSelection(new StructuredSelection(g));
							break;
						}
					}
				}
			}catch(Exception ex){
				
			}
		}
		
		
		
		
	}
	
}
